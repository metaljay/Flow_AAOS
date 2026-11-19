// Tests for scripts/aaos/play-upload.mjs against a local fake Google server. No real network, no real key.
import { test, before, after, beforeEach } from "node:test";
import assert from "node:assert/strict";
import crypto from "node:crypto";
import fs from "node:fs";
import http from "node:http";
import os from "node:os";
import path from "node:path";
import { execFile } from "node:child_process";
import { fileURLToPath } from "node:url";

const SCRIPT = path.join(path.dirname(fileURLToPath(import.meta.url)), "..", "play-upload.mjs");
const TOKEN = "ya29.FAKE_ACCESS_TOKEN_1234567890";
const PKG = "com.JF_Flow";

let server, base, tmp, keyPath, bundlePath, privatePem;
let log, state, requests;

before(async () => {
  tmp = fs.mkdtempSync(path.join(os.tmpdir(), "play-upload-test-"));
  const { privateKey } = crypto.generateKeyPairSync("rsa", { modulusLength: 2048 });
  privatePem = privateKey.export({ type: "pkcs8", format: "pem" });
  bundlePath = path.join(tmp, "app.aab");
  fs.writeFileSync(bundlePath, crypto.randomBytes(300 * 1024));
  server = http.createServer((req, res) => {
    const chunks = [];
    req.on("data", (c) => chunks.push(c));
    req.on("end", () => handle(req, res, Buffer.concat(chunks)));
  });
  await new Promise((r) => server.listen(0, "127.0.0.1", r));
  base = `http://127.0.0.1:${server.address().port}`;
  keyPath = path.join(tmp, "key.json");
  fs.writeFileSync(keyPath, JSON.stringify({ client_email: "t@test.iam", private_key: privatePem, private_key_id: "keyid1234567890", token_uri: `${base}/token` }), { mode: 0o600 });
  fs.chmodSync(keyPath, 0o600);
});
after(() => {
  server.closeAllConnections?.();
  server.close();
  fs.rmSync(tmp, { recursive: true, force: true });
});
beforeEach(() => {
  log = [];
  requests = [];
  state = { tracks: [{ track: "qa", releases: [{ versionCodes: ["3"], status: "completed" }] }], failUpload: false, uploaded: 0 };
});

function json(res, code, obj, headers = {}) {
  res.writeHead(code, { "content-type": "application/json", ...headers });
  res.end(JSON.stringify(obj));
}
function handle(req, res, body) {
  const u = new URL(req.url, base);
  const p = u.pathname;
  requests.push(p);
  const key = `${req.method} ${p.replace(`/androidpublisher/v3/applications/${PKG}`, "").replace(/edits\/[^/:]+/, "edits/E")}`;
  log.push(key);
  if (p.startsWith("/androidpublisher/") && !p.startsWith(`/androidpublisher/v3/applications/${PKG}/`)) return json(res, 404, { error: { message: "wrong package" } });
  if (p.startsWith("/upload/") && !p.startsWith(`/upload/androidpublisher/v3/applications/${PKG}/`)) return json(res, 404, { error: { message: "wrong package" } });
  if (p === "/token") return json(res, 200, { access_token: TOKEN });
  if (p !== "/session/1" && req.headers.authorization !== `Bearer ${TOKEN}`) return json(res, 401, { error: { message: "no auth" } });
  if (key === "POST /edits") return json(res, 200, { id: "E1" });
  if (key === "GET /edits/E/tracks") return json(res, 200, { tracks: state.tracks });
  if (key.startsWith("POST /upload") || p.endsWith("/bundles")) {
    if (req.method === "POST") return json(res, 200, {}, { location: `${base}/session/1` });
  }
  if (p === "/session/1") {
    if (state.failUpload) return json(res, 400, { error: { message: "bad bundle" } });
    const m = /bytes (\d+)-(\d+)\/(\d+)/.exec(req.headers["content-range"]);
    if (Number(m[2]) + 1 < Number(m[3])) {
      res.writeHead(308, { range: `bytes=0-${m[2]}` });
      return res.end();
    }
    return json(res, 200, { versionCode: state.returnCode ?? 4 });
  }
  if (req.method === "PUT" && /\/tracks\//.test(p)) return json(res, 200, {});
  if (key.endsWith(":validate")) return json(res, 200, {});
  if (key.endsWith(":commit")) return json(res, 200, {});
  if (req.method === "DELETE") return json(res, 204, {});
  return json(res, 404, { error: { message: "unknown " + key } });
}

function run(args, env = {}) {
  return new Promise((resolve) => {
    execFile("node", [SCRIPT, ...args], { env: { ...process.env, CLAUDE_CAR_PLAY_KEY: keyPath, PLAY_API_BASE: base, PLAY_UPLOAD_BASE: base, ...env } }, (err, stdout, stderr) => {
      resolve({ code: err ? err.code : 0, stdout, stderr });
    });
  });
}
const apiLog = () => log.filter((l) => !l.includes("/token"));
const noSecrets = (r) => {
  const all = r.stdout + r.stderr;
  assert.ok(!all.includes(TOKEN), "token leaked");
  assert.ok(!all.includes("PRIVATE KEY"), "key leaked");
  assert.ok(!all.includes("keyid1234567890"), "key id leaked");
};

test("real upload: insert, upload, tracks.update, commit in order; token and key never printed", async () => {
  const r = await run(["--bundle", bundlePath, "--version-code", "4"]);
  assert.equal(r.code, 0, r.stderr);
  const l = apiLog();
  const order = ["POST /edits", "GET /edits/E/tracks", "POST /edits/E/bundles", "PUT /session/1", "PUT /edits/E/tracks/qa", "POST /edits/E:commit"];
  let at = -1;
  for (const step of order) {
    const i = l.findIndex((x, idx) => idx > at && x.startsWith(step.replace("POST /edits/E/bundles", "POST /upload")) || (idx > at && x.includes(step)));
    assert.ok(i > at, `missing or out of order: ${step} in ${l.join(" | ")}`);
    at = i;
  }
  assert.ok(!l.some((x) => x.startsWith("DELETE")));
  assert.match(r.stdout, /Released version 4/);
  noSecrets(r);
});

test("prefers the automotive internal track when it is listed", async () => {
  state.tracks.push({ track: "automotive:qa", releases: [] });
  const r = await run(["--bundle", bundlePath, "--version-code", "4"]);
  assert.equal(r.code, 0, r.stderr);
  assert.ok(apiLog().some((x) => x.includes("PUT /edits/E/tracks/automotive:qa")));
  assert.ok(!apiLog().some((x) => x.includes("PUT /edits/E/tracks/qa")));
});

test("Play listing production, beta, alpha, internal: picks internal", async () => {
  state.tracks = ["production", "beta", "alpha", "internal"].map((track) => ({ track, releases: [] }));
  const r = await run(["--bundle", bundlePath, "--version-code", "4"]);
  assert.equal(r.code, 0, r.stderr);
  assert.ok(apiLog().some((x) => x.includes("PUT /edits/E/tracks/internal")));
});

test("preference order: automotive:internal beats automotive:qa, internal, qa", async () => {
  state.tracks = ["qa", "internal", "automotive:qa", "automotive:internal"].map((track) => ({ track, releases: [] }));
  const r = await run(["--bundle", bundlePath, "--version-code", "4"]);
  assert.equal(r.code, 0, r.stderr);
  assert.ok(apiLog().some((x) => x.includes("PUT /edits/E/tracks/automotive:internal")));
});

test("only internal tracks are written: other names refused before any request", async () => {
  for (const t of ["production", "beta", "alpha", "automotive:production", "automotive:beta"]) {
    log = [];
    const r = await run(["--bundle", bundlePath, "--version-code", "4", "--track", t]);
    assert.notEqual(r.code, 0);
    assert.equal(log.length, 0, `request made for ${t}`);
    assert.match(r.stderr, /Refused/);
  }
});

test("a used version code is refused (any track) and the edit is deleted", async () => {
  state.tracks.push({ track: "production", releases: [{ versionCodes: ["7"] }] });
  const r = await run(["--bundle", bundlePath, "--version-code", "7"]);
  assert.notEqual(r.code, 0);
  assert.match(r.stderr, /already used/);
  assert.ok(apiLog().some((x) => x.startsWith("DELETE")));
  assert.ok(!apiLog().some((x) => x.includes(":commit")));
});

test("a failed upload deletes the edit and exits non-zero", async () => {
  state.failUpload = true;
  const r = await run(["--bundle", bundlePath, "--version-code", "4"]);
  assert.notEqual(r.code, 0);
  assert.ok(apiLog().some((x) => x.startsWith("DELETE")));
  assert.ok(!apiLog().some((x) => x.includes(":commit")));
  noSecrets(r);
});

test("--dry-run validates and deletes, never commits", async () => {
  const r = await run(["--dry-run", "--bundle", bundlePath, "--version-code", "4"]);
  assert.equal(r.code, 0, r.stderr);
  const l = apiLog();
  assert.ok(l.some((x) => x.includes(":validate")));
  assert.ok(l.some((x) => x.startsWith("DELETE")));
  assert.ok(!l.some((x) => x.includes(":commit")));
});

test("--check makes no writes: only opens an edit, lists tracks, deletes", async () => {
  const r = await run(["--check"]);
  assert.equal(r.code, 0, r.stderr);
  assert.deepEqual(apiLog(), ["POST /edits", "GET /edits/E/tracks", "DELETE /edits/E"]);
  assert.match(r.stdout, /Highest version code on Play: 3/);
  noSecrets(r);
});

test("missing key file: plain message, no requests", async () => {
  const r = await run(["--check"], { CLAUDE_CAR_PLAY_KEY: path.join(tmp, "nope.json") });
  assert.notEqual(r.code, 0);
  assert.match(r.stderr, /key file is missing/);
  assert.equal(log.length, 0);
});

test("key file readable by others is refused", async () => {
  const open = path.join(tmp, "open.json");
  fs.writeFileSync(open, fs.readFileSync(keyPath));
  fs.chmodSync(open, 0o644);
  const r = await run(["--check"], { CLAUDE_CAR_PLAY_KEY: open });
  assert.notEqual(r.code, 0);
  assert.match(r.stderr, /chmod 600/);
  assert.equal(log.length, 0);
  noSecrets(r);
});

test("errors from Google never reveal the token or key", async () => {
  state.returnCode = 99;
  const r = await run(["--bundle", bundlePath, "--version-code", "4"]);
  assert.notEqual(r.code, 0);
  noSecrets(r);
});

test("neither internal track listed: refused, no upload, edit deleted", async () => {
  state.tracks = [{ track: "production", releases: [] }, { track: "beta", releases: [] }];
  const r = await run(["--bundle", bundlePath, "--version-code", "4"]);
  assert.notEqual(r.code, 0);
  assert.match(r.stderr, /Tracks Play returned: production, beta/);
  const l = apiLog();
  assert.ok(!l.some((x) => x.includes("bundles") || x.startsWith("PUT")));
  assert.ok(l.some((x) => x.startsWith("DELETE")));
});

test("--check prints the track names Play returned", async () => {
  const r = await run(["--check"]);
  assert.match(r.stdout, /Tracks Play returned: qa/);
});

test("every request goes to com.JF_Flow and no other package", async () => {
  const r = await run(["--bundle", bundlePath, "--version-code", "4"]);
  assert.equal(r.code, 0, r.stderr);
  const paths = requests.filter((x) => x !== "/token" && !x.startsWith("/session/"));
  assert.ok(paths.length >= 5, paths.join(" | "));
  for (const x of paths) assert.ok(x.includes(`/applications/${PKG}/`), `request outside ${PKG}: ${x}`);
});

#!/usr/bin/env node
// Uploads a signed .aab to the INTERNAL TESTING track of com.JF_Flow on Google Play and releases it there.
// Built-ins only (no npm packages). Run via scripts/aaos/release.sh (see AAOS_RELEASE.md).
//
//   node scripts/aaos/play-upload.mjs --check
//   node scripts/aaos/play-upload.mjs --dry-run --bundle <file.aab> --version-code <n>
//   node scripts/aaos/play-upload.mjs          --bundle <file.aab> --version-code <n>
//
// Key file: ~/.config/claude-car-play/key.json (mode 600), or the path in env CLAUDE_CAR_PLAY_KEY.
// Test hooks: env PLAY_API_BASE / PLAY_UPLOAD_BASE point the Google URLs at a fake server.
// The key, the signed login token and the access token are never printed (see redact()).

import crypto from "node:crypto";
import fs from "node:fs";
import os from "node:os";
import path from "node:path";

const PACKAGE_NAME = "com.JF_Flow";
const SCOPE = "https://www.googleapis.com/auth/androidpublisher";

// Track ids. Source: https://developers.google.com/android-publisher/tracks
// The docs name the internal testing track "qa", and form-factor tracks are the form-factor prefix plus the default name
// ("automotive" is the Android Automotive OS prefix). But the API listed "internal" (with production, beta, alpha) for the car app
// on 2026-11-19: older apps keep the classic names. So the first of these that tracks.list returns is used, in this order.
// Nothing else is ever written: any other name is refused before the first network call.
const ALLOWED_TRACKS = ["automotive:internal", "automotive:qa", "internal", "qa"];

const CHUNK_SIZE = 32 * 256 * 1024; // 8 MiB: a multiple of 256 KiB, as resumable uploads require
const CHUNK_TRIES = 4;

const secrets = new Set();
function addSecret(s) {
  if (typeof s === "string" && s.length >= 8) secrets.add(s);
}
function redact(text) {
  let out = String(text);
  for (const s of secrets) out = out.split(s).join("[hidden]");
  out = out.replace(/-----BEGIN [A-Z ]*PRIVATE KEY-----[\s\S]*?-----END [A-Z ]*PRIVATE KEY-----/g, "[hidden]");
  out = out.replace(/\bya29\.[\w.-]+/g, "[hidden]");
  return out;
}
class Fail extends Error {}
const say = (m) => process.stdout.write(redact(m) + "\n");
const fail = (m) => {
  throw new Fail(m);
};

function parseArgs(argv) {
  const o = { mode: "upload", bundle: null, versionCode: null, track: null };
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    if (a === "--check") o.mode = "check";
    else if (a === "--dry-run") o.mode = "dry-run";
    else if (a === "--bundle") o.bundle = argv[++i];
    else if (a === "--version-code") o.versionCode = Number(argv[++i]);
    else if (a === "--track") o.track = argv[++i];
    else fail(`Unknown option: ${a}`);
  }
  return o;
}

function loadKey() {
  const file = process.env.CLAUDE_CAR_PLAY_KEY || path.join(os.homedir(), ".config", "claude-car-play", "key.json");
  let st;
  try {
    st = fs.statSync(file);
  } catch {
    fail(`The Play key file is missing (${file}). Run scripts/aaos/setup-release.sh and put the key there.`);
  }
  if (st.mode & 0o077) fail(`The Play key file must be private. Run: chmod 600 "${file}"`);
  let key;
  try {
    key = JSON.parse(fs.readFileSync(file, "utf8"));
  } catch {
    fail("The Play key file is not valid JSON (contents not shown).");
  }
  if (!key.client_email || !key.private_key || !key.token_uri) fail("The Play key file is missing client_email, private_key or token_uri.");
  addSecret(key.private_key);
  addSecret(key.private_key_id);
  return key;
}

const b64u = (b) => Buffer.from(b).toString("base64url");

async function signIn(key) {
  const now = Math.floor(Date.now() / 1000);
  const head = b64u(JSON.stringify({ alg: "RS256", typ: "JWT" }));
  const claim = b64u(JSON.stringify({ iss: key.client_email, scope: SCOPE, aud: key.token_uri, iat: now, exp: now + 3000 }));
  let sig;
  try {
    sig = crypto.sign("RSA-SHA256", Buffer.from(`${head}.${claim}`), key.private_key).toString("base64url");
  } catch {
    fail("The private key in the Play key file could not be used to sign in.");
  }
  const jwt = `${head}.${claim}.${sig}`;
  addSecret(jwt);
  const res = await fetch(key.token_uri, {
    method: "POST",
    headers: { "content-type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({ grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer", assertion: jwt }),
  });
  const body = await res.json().catch(() => ({}));
  if (!res.ok || !body.access_token) fail(`Google refused the sign-in (HTTP ${res.status}${body.error ? `, ${body.error}` : ""}).`);
  addSecret(body.access_token);
  return body.access_token;
}

function makeApi(token) {
  const api = (process.env.PLAY_API_BASE || "https://androidpublisher.googleapis.com").replace(/\/$/, "");
  const up = (process.env.PLAY_UPLOAD_BASE || "https://androidpublisher.googleapis.com/upload").replace(/\/$/, "");
  const root = `/androidpublisher/v3/applications/${PACKAGE_NAME}`;
  const auth = { authorization: `Bearer ${token}` };
  async function call(method, p, body, what) {
    const res = await fetch(api + root + p, {
      method,
      headers: { ...auth, ...(body ? { "content-type": "application/json" } : {}) },
      body: body ? JSON.stringify(body) : undefined,
    });
    const text = await res.text();
    let json = {};
    try {
      json = text ? JSON.parse(text) : {};
    } catch {}
    if (!res.ok) fail(`Google Play refused "${what}" (HTTP ${res.status}${json.error?.message ? `: ${json.error.message}` : ""}).`);
    return json;
  }
  return { call, up, root, auth };
}

async function uploadBundle(a, editId, file) {
  const size = fs.statSync(file).size;
  const start = await fetch(`${a.up}${a.root}/edits/${editId}/bundles?uploadType=resumable`, {
    method: "POST",
    headers: { ...a.auth, "x-upload-content-type": "application/octet-stream", "x-upload-content-length": String(size), "content-type": "application/json" },
    body: "{}",
  });
  const session = start.headers.get("location");
  if (!start.ok || !session) fail(`Google Play would not start the upload (HTTP ${start.status}).`);
  const fh = fs.openSync(file, "r");
  try {
    let offset = 0;
    while (offset < size) {
      const len = Math.min(CHUNK_SIZE, size - offset);
      const buf = Buffer.alloc(len);
      fs.readSync(fh, buf, 0, len, offset);
      let res = null;
      for (let t = 1; t <= CHUNK_TRIES; t++) {
        try {
          res = await fetch(session, {
            method: "PUT",
            headers: { "content-length": String(len), "content-range": `bytes ${offset}-${offset + len - 1}/${size}` },
            body: buf,
          });
          if (res.status < 500) break;
        } catch {
          res = null;
        }
        await new Promise((r) => setTimeout(r, 200 * t));
      }
      if (!res) fail("The upload kept failing (network). Nothing was released.");
      if (res.status >= 500) fail(`The upload kept failing (Google HTTP ${res.status}). Nothing was released.`);
      offset += len;
      if (offset >= size) {
        if (!res.ok) fail(`Google Play rejected the bundle (HTTP ${res.status}: ${(await res.text()).slice(0, 300)}).`);
        return await res.json();
      }
      if (res.status !== 308) fail(`Unexpected answer during the upload (HTTP ${res.status}).`);
    }
    fail("The bundle file is empty.");
  } finally {
    fs.closeSync(fh);
  }
}

function highestCode(tracks) {
  let max = 0;
  for (const t of tracks) for (const r of t.releases || []) for (const c of r.versionCodes || []) max = Math.max(max, Number(c) || 0);
  return max;
}

async function main() {
  const o = parseArgs(process.argv.slice(2));
  // Refused before any network call:
  if (o.track !== null && !ALLOWED_TRACKS.includes(o.track)) fail(`Refused: "${o.track}" is not an internal-testing track. Only ${ALLOWED_TRACKS.join(" or ")} can be written.`);
  if (o.mode !== "check") {
    if (!o.bundle || !fs.existsSync(o.bundle)) fail("Give the signed bundle with --bundle <file.aab> (file not found).");
    if (!Number.isInteger(o.versionCode) || o.versionCode < 1) fail("Give the version code with --version-code <number>.");
  }
  const key = loadKey();
  const token = await signIn(key);
  const a = makeApi(token);
  const edit = await a.call("POST", "/edits", {}, "open an edit");
  const editId = edit.id;
  let committed = false;
  try {
    const listed = await a.call("GET", `/edits/${editId}/tracks`, null, "list tracks");
    const tracks = listed.tracks || [];
    const names = tracks.map((t) => t.track);
    const track = o.track ?? (ALLOWED_TRACKS.find((t) => names.includes(t)) ?? ALLOWED_TRACKS[0]);
    const highest = highestCode(tracks);
    const listedNames = names.length ? names.join(", ") : "(none)";
    if (!names.includes(track)) fail(`Play does not list an internal testing track (${ALLOWED_TRACKS.join(", ")}). Tracks Play returned: ${listedNames}. Nothing was uploaded.`);
    if (o.mode === "check") {
      say(`Signed in. Internal testing track: ${track}. Tracks Play returned: ${listedNames}. Highest version code on Play: ${highest}. Nothing was changed.`);
      return;
    }
    if (o.versionCode <= highest) fail(`Version code ${o.versionCode} is already used (Play has ${highest}). Raise versionCode in app/build.gradle.kts, commit and push, and run again.`);
    const uploaded = await uploadBundle(a, editId, o.bundle);
    if (Number(uploaded.versionCode) !== o.versionCode) fail(`Play says the bundle is version ${uploaded.versionCode}, expected ${o.versionCode}. Nothing was released.`);
    await a.call("PUT", `/edits/${editId}/tracks/${track}`, { track, releases: [{ versionCodes: [String(o.versionCode)], status: "completed" }] }, "release to the track");
    if (o.mode === "dry-run") {
      await a.call("POST", `/edits/${editId}:validate`, null, "validate the edit");
      say(`Dry run OK: version ${o.versionCode} would go to ${track}. Nothing was released.`);
      return;
    }
    await a.call("POST", `/edits/${editId}:commit`, null, "commit the edit");
    committed = true;
    say(`Released version ${o.versionCode} to internal testing (${track}).`);
  } finally {
    if (!committed) {
      try {
        await a.call("DELETE", `/edits/${editId}`, null, "delete the edit");
      } catch {}
    }
  }
}

main().catch((e) => {
  const msg = e instanceof Fail ? e.message : `Unexpected problem: ${e?.cause?.code || e?.message || e}`;
  process.stderr.write(redact(msg) + "\n");
  process.exit(1);
});

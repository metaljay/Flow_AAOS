# Flow AAOS fork: rules and customisations

> Fork-owned file (the parent project does not have it, so parent updates never overwrite it). Sections marked **(shared)** are word for word the same in the Flow and Nuvio forks: change them in both repos in the same change. Everything else is specific to this app. If reality changes, fix this file in the same change.

## 1. What this repo is

The owner's fork of [Flow](https://github.com/A-EDev/Flow), a YouTube client, customised so it is readable and usable on the owner's Polestar 3 running Android Automotive OS (AAOS). It reaches the car only through Google Play **Internal testing**. The sister fork is Nuvio, next to this repo in the AAOS folder.

| Fork fact | Value |
| --- | --- |
| Parent project (upstream) | https://github.com/A-EDev/Flow, branch `main` (the only parent branch ever synced) |
| Our fork (origin) | https://github.com/metaljay/Flow_AAOS, branch `main` |
| Play application ID (release) | `com.JF_Flow` |
| Code namespace | `io.github.aedev.flow`, the parent's (never confuse it with the application ID) |
| Debug and nightly builds | `com.JF_Flow.debug` and `com.JF_Flow.nightly`: emulator only, never on the car |
| Release facts | `AAOS_RELEASE.md` (module, build variant, version file, last upload) |
| Parent update facts | `AAOS_UPSTREAM_SYNC.md` (last merged parent commit, checks, hotspots) |

## 2. The jobs (shared)

Every change is meant for the owner's car, so there are only these jobs. The owner starts each one by pasting a prompt from `README.md`.

| Job | Recipe | Ends with |
| --- | --- | --- |
| Update from the parent | `.github/skills/aaos-sync/SKILL.md` | the approval report in `AAOS_UPSTREAM_SYNC.md`, the owner's `approve sync`, then a release |
| Tweak or fix for the car | `.github/skills/aaos-tweak/SKILL.md` | a release |
| Release | `.github/skills/aaos-release/SKILL.md` | a bundle the owner signs and uploads (also runs on its own) |
| Record an upload (optional) | `.github/skills/aaos-uploaded/SKILL.md` | "Last uploaded to Play" updated in `AAOS_RELEASE.md` |

The recipes are plain instructions any AI agent can follow. Claude Code and GitHub Copilot also list them as `/aaos-sync`, `/aaos-tweak`, `/aaos-release` and `/aaos-uploaded`.

## 3. Terminology (shared)

| Term | Meaning |
| --- | --- |
| upstream, parent | The original project this fork follows (section 1). Read-only to us. Only the parent branch named in section 1 is ever synced. |
| origin | The owner's GitHub fork (section 1). |
| `main` | Parent code plus our customisations. The only long-lived branch; always buildable and releasable. |
| sync branch | Temporary `sync/upstream-<date>` branch used to review a parent update before it reaches `main`. Deleted afterwards. |
| customisation | A deliberate change we keep. Each has an ID (C1, C2, ...) in section 7 and a detail block in Part 2. Every customisation must survive every parent update unless the owner decides otherwise. |
| Play version code | The number Play Console requires to rise on every upload. Independent of the parent's version. See `AAOS_RELEASE.md`. |
| release bundle | The signed `.aab` file uploaded to Play. |
| debug build | Emulator-only. Never installed on the car. |

## 4. Golden rules (shared)

1. The only target is the owner's Polestar 3 (AAOS), delivered via Play Internal testing. Android Auto (phone projection) is out of scope; do not add Android Auto metadata.
2. `main` is the only long-lived branch. Parent updates go through a `sync/` branch first (`AAOS_UPSTREAM_SYNC.md`).
3. Never push to the parent. Parent changes flow in one way.
4. Port, don't replay: redo each customisation in the parent's current code instead of cherry-picking old commits.
5. Keep edits to parent-owned files as small as possible (fewer merge conflicts). Fork-only additions go in clearly separate places or files.
6. Every release build raises the version automatically (`AAOS_RELEASE.md`); the parent's version numbers are ignored.
7. Before touching the manifest, the media card, how the app launches, or anything near the screen edges, read `AAOS_CAR_NOTES.md` (facts learned on the real car).
8. If a product decision is unclear (for example removing a customisation), ask the owner instead of guessing.

## 5. Permissions and safety rails (shared)

Agents may edit files, build, run tests, commit, push to `origin/main` once the work is verified, and create and delete `sync/` branches. Agents must **never**:

- force-push or rewrite history on `main`;
- push to the parent or merge anything towards it;
- commit, print, copy or paste `Key.jks`, any keystore, `local.properties` contents or any password;
- delete branches other than `sync/*`;
- change the application ID, namespace or signing setup unless asked;
- say something is "verified" without stating exactly what was run and what was not (compiled / unit-tested / emulator / real car);
- put personal names, usernames, emails or absolute paths into any file, commit message or log.

Commit with small, clear messages (`type(scope): description`). Stage files by name; do not use `git add -A`.

## 6. Talking to the owner (shared)

The owner has no coding experience, copy-pastes messages between chats, and depends on the agent to do and check the work. Therefore:

1. Never tell the owner to run a command unless you give the complete command in a copy-paste code block with every value filled in. No placeholders such as <date> or <file>: look the value up yourself first.
2. Run commands yourself wherever you can. Ask the owner to act only for things only they can do: sign the bundle in Android Studio, upload in Play Console, test on the car, change GitHub settings, or approve a decision.
3. Offer decisions as copy-paste replies, for example: reply `approve sync` or `cancel sync`. One decision at a time.
4. Explain results in plain English. Explain any jargon in one short phrase (for example: "merge means combining the parent's changes with ours").
5. End every task with a report in this order: What I did / What I checked (the exact commands and results) / What I did NOT check / What you need to do next (the exact message to paste, or "nothing").
6. If something fails, stop and say what failed in plain English. Give the owner one message to paste back to you or to another AI assistant. Do not attempt risky fixes.
7. Do not ask the owner to read raw code, diffs or logs; summarise them. Offer the raw output only if asked.

## 7. Customisations (must survive every parent update)

| ID | Customisation | In one line |
| --- | --- | --- |
| C1 | Play identity | Release application ID stays exactly `com.JF_Flow`; namespace stays the parent's. |
| C2 | AAOS manifest | Automotive features, car launcher entry, automotive app descriptor and `distractionOptimized` stay. |
| C3 | Camera optional | Both camera features stay `required="false"` so the car can install the app. |
| C4 | Bottom navigation | Navigation stays at the bottom at every window size; no side rail. |
| C5 | Large-display legibility | Larger type, 72 dp bars, larger Home logo, player, Shorts and music controls. |
| C6 | Display safe area | Touch targets at least 16 dp from the left and right window edges. |
| C7 | Launcher icon | Red YouTube play mark, 48 x 32 dp on the 108 dp grid. |
| C8 | Car media card | The car's home screen card shows Flow's last video or song; no `androidx.car.app.launchable`. |
| C9 | Bundle packaging | ABI-split workaround for bundle tasks stays. |
| C10 | Fork docs | README, AGENTS fork block, agent entry points, recipes and `AAOS_*.md` files stay. |

Each one has a detail block in Part 2: what and why, where it lives, how to redo it after a parent update, and how to check it.

## 8. Deliberately NOT customised

- **Video quality defaults**: Flow's own defaults apply (1080p Wi-Fi, 480p cellular) and users can change them in settings. An earlier "maximum resolution" default was removed because the Polestar 3 display cannot show it. Never raise the defaults and never overwrite a saved user choice (`data/local/PlayerPreferences.kt`; stream selection stays bounded by the streams actually available).
- **Target SDK and architecture**: follow the parent (currently target SDK 36). The old SDK 35 downgrade is not a requirement.
- **Media3 player wiring**: do not change it as part of display-size work.

## 9. README contract (shared)

`README.md` is written for the owner, a person with no coding experience. Every merge or refresh keeps these sections, in this order:

1. Banner: `🚗 Android Automotive OS (AAOS) Fork`, the app's logo and name, and links to the parent project and the license.
2. **What this is**: an AAOS fork of the parent app for the owner's Polestar 3, not Android Auto.
3. **What's different in the car**: an accurate plain-English summary, one row per customisation, each linking to its Part 2 block in `AAOS_FORK.md`.
4. **Getting it on the car**: Google Play Internal testing only; no public downloads.
5. **What do you want to do?**: one section per job in section 2, each with a copy-paste prompt and what happens next, plus help for when something goes wrong.
6. **Behind the scenes**: which file holds what, for the curious.
7. **License**.

Keep code, file paths and jargon out of sections 1 to 5 apart from the prompts. Only use screenshots of this fork that exist and are accurate. On a README conflict in a parent update, keep ours (the parent's README describes the parent app).

## 10. Keeping these docs current (shared)

- **After every verified change**: add a dated entry to `AAOS_LOG.md`; if a customisation was added, changed or removed, update section 7, its Part 2 block, the hotspot table in `AAOS_UPSTREAM_SYNC.md` and, if the owner would notice it, the README summary; after a confirmed upload, update "Last uploaded to Play" in `AAOS_RELEASE.md`. The recipes make these updates.
- **One home per fact.** Rules and customisations live here; release facts and steps in `AAOS_RELEASE.md`; parent-update steps and the approval report in `AAOS_UPSTREAM_SYNC.md`; car facts in `AAOS_CAR_NOTES.md`; history in `AAOS_LOG.md`. Other files link to these instead of repeating them.
- **Both repos together.** Flow and Nuvio share the same car and the same doc skeleton. Car facts go into `AAOS_CAR_NOTES.md` in **both** repos in the same change (the file is identical in both). A change to a **(shared)** section, the README layout or a recipe is made in both repos too.
- **Keep the log short.** `AAOS_LOG.md` keeps the newest 15 entries. When adding an entry would make more, move the oldest entries (unchanged) to the top of `AAOS_LOG_ARCHIVE.md`. A release-preparation entry is at most three lines.
- **No contradictions, no stale text.** When a number or a rule changes, search `README.md`, `AGENTS.md`, the recipes and every `AAOS_*.md` file for the old wording and update every mention. Delete obsolete text rather than adding a correction next to it.

## 11. Environment notes

Shared (both repos):

- Builds run on the owner's Mac with Android Studio. The AAOS folder holds both repos, the release key `Key.jks` (outside both repos) and the `For upload to Play Console` folder for signed bundles (outside git).
- Emulator used for checks: `Automotive_Large_Portrait` (1280 x 1606). It does not prove Polestar 3 behaviour. If `adb` or the emulator is unavailable, say so; never claim device verification from a compile.
- If tests fail with "Unsupported class file major version 71", the default Java is too new: rerun with Android Studio's bundled Java (`JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"` in front). It is an environment problem, not real failures.

Flow only:

- `AGENTS.md` below the fork block holds the parent's own coding rules (Material 3, no "AI slop" UI, performance, file structure). Follow them too.
- `graphify` (mentioned in those rules) may not be installed; if it is missing, say so and carry on.

---

# Part 2: Customisation details

Each block uses the same four headings. **After a parent update** is what the sync recipe must do to keep the customisation working in the parent's new code; **Check** is how to prove it.

### C1. Play identity

- **What and why:** the car installs Flow from our existing Play listing, which only accepts updates carrying the application ID `com.JF_Flow`. The code namespace stays the parent's `io.github.aedev.flow`.
- **Where:** `app/build.gradle.kts` (`applicationId`, version code and name).
- **After a parent update:** keep our application ID and version numbers on any conflict. Component names follow the namespace; runtime package lookups use the application ID. Do not conflate them when porting code.
- **Check:** the merged **release** manifest shows `com.JF_Flow` (debug and nightly suffixes are expected on other variants).

### C2. AAOS manifest

- **What and why:** lets the car recognise Flow as a car app, list it in the car's app grid and run its screens under the car's driving rules.
- **Where:** `app/src/main/AndroidManifest.xml` (automotive feature declarations, car launcher entry, MediaBrowser metadata, `distractionOptimized="true"` on the required activities and aliases, and the explanatory `KEEP` note); `app/src/main/res/xml/automotive_app_desc.xml`.
- **After a parent update:** keep every AAOS entry and the `KEEP` note. Give any new parent activity or launcher alias the same `distractionOptimized` metadata.
- **Check:** in the merged release manifest the automotive feature is optional, the automotive app descriptor and car launcher entry are present, and every launcher activity and alias carries `distractionOptimized`.

### C3. Camera optional

- **What and why:** QR sync may ask for the camera, but a camera must not be a Play requirement, otherwise the car cannot install the app.
- **Where:** `app/src/main/AndroidManifest.xml`: `android.hardware.camera` and `android.hardware.camera.any` with `android:required="false"`.
- **After a parent update:** keep both optional, including on any camera feature the parent adds.
- **Check:** merged release manifest.

### C4. Bottom navigation at every window size

- **What and why:** the car window is wide, and the parent switches to a side rail on wide screens. We keep Home, Shorts, Music, Subscriptions and Library at the bottom, with a 72 dp minimum bar height above the system navigation inset and larger icons and labels. The overflow destination still works when more tabs are enabled than fit.
- **Where:** `ui/components/layout/navigation/FlowNavigationChrome.kt`, `FlowNavigationBar.kt`.
- **After a parent update:** the parent's adaptive side rail must not come back; re-apply the forced bottom bar to the parent's new navigation code.
- **Check:** emulator: navigation at the bottom on every screen; overflow destination reachable.

### C5. Large-display legibility

- **What and why:** makes Flow readable at a glance and easy to tap on the car display: enlarged Material 3 type; 72 dp minimum top and bottom bars through the shared `FlowTopBar` components, with larger titles, actions and readable action menus; a larger Home wordmark and search, with the 44 dp Home logo inset 12 dp from the start edge (so it sits about 16 dp in, like the right-hand icons); larger player, Shorts and music-player controls, touch targets and player and comment text; the video minimise control at a 60 dp hit size, offset 6 dp to the right of the parent's position; top bar controls 16 to 20 dp from the display edges.
- **Where:** `ui/theme/Type.kt`; `ui/components/layout/topbar/` (`FlowTopBar.kt`, `FlowTopBarDefaults.kt`, `FlowTopBarActions.kt`); `ui/screens/home/HomeScreen.kt`; `ui/components/videoplayer/` (including `VideoPlayerTopBar.kt`), `ui/components/shorts/`, `ui/components/musicplayer/`, and the shared comment and description components.
- **After a parent update:** re-apply our sizes on top of the parent's new values; keep screens on the shared top bar component rather than restoring custom bars; keep the parent's component structure, motion and Media3 wiring.
- **Check:** emulator: text and buttons visibly larger than the parent's; bars at least 72 dp; the Home logo and the settings icon sit about the same distance from their edges.

### C6. Display safe area

- **What and why:** the Polestar 3 screen's rounded corners and bezel cover the outer edge of the app window (`AAOS_CAR_NOTES.md`). Every touch target (not just its icon) sits at least 16 dp from the left and right window edges, and icons about 24 to 28 dp in. The top bar's trailing actions end with a 12 dp inset (`FlowTopBarDefaults.ActionsEndInset`, applied once in `FlowTopBar` so every screen gets it); the settings cog was only partly tappable at 4 dp (owner report 2026-10-05).
- **Where:** `ui/components/layout/topbar/FlowTopBarDefaults.kt`, `FlowTopBar.kt`; any new edge control.
- **After a parent update:** check new or moved top bars, side buttons and player controls against the rule.
- **Check:** emulator screenshot: the settings cog sits about 32 px from the right edge (it was about 20 px).

### C7. Launcher icon

- **What and why:** the owner wants the traditional red rounded YouTube play mark with a white triangle, the same visual size as Nuvio's icon. It uses the official mark's curves and triangle proportions, scaled by 0.706 to 48 x 32 dp on the 108 dp viewport.
- **Where:** `app/src/main/res/drawable/ic_launcher_foreground.xml`, `ic_launcher_dynamic_foreground.xml`, theme colours. Adaptive and dynamic assets stay consistent.
- **After a parent update:** keep ours.
- **Check:** emulator app grid: one red Flow icon, about the size of Nuvio's.

### C8. Car media card

- **What and why:** the car's home screen card (bottom left) shows what Flow is playing, with title, channel and picture. AAOS only treats an app as a media source if it exposes a `MediaBrowserService`, and the card reads the session that service hands out. Flow plays videos and music in two separate sessions, so one session mirrors whichever played last and forwards the car's controls to that player. The last item (kind, title, channel or artist, artwork, position, duration) is saved and restored as paused (never `STATE_NONE`, which hides the card). The card only shows artwork from a local `content://` address, so a provider serves the saved picture. Pressing play with Flow closed: music resumes its saved queue; video shows the prompt "Open Flow to continue watching" with an "Open Flow" button (Android blocks a background app from opening itself), then returns to paused after 15 s.
- **Never** add `androidx.car.app.launchable` to the service: on the Polestar it makes the app icon open the car's media screen instead of Flow. Known, accepted limit: after a full car restart the card stays blank (`AAOS_CAR_NOTES.md`).
- **Where:** `service/FlowCarMediaSession.kt` (observes `GlobalPlayerState.currentVideo`, `EnhancedPlayerManager.playerState`, `EnhancedMusicPlayerManager.currentTrack` and `playerState`); `service/FlowCarMediaBrowserService.kt`, declared **before** `Media3MusicService` in `app/src/main/AndroidManifest.xml` with the `android.media.browse.MediaBrowserService` intent filter so the car picks it; `service/FlowCarMediaArtworkProvider.kt` (authority `com.JF_Flow.carmediaart`); one `FlowCarMediaSession.startObserving` call in `FlowApplication.onCreate`; texts in `res/values/aaos_car_media_strings.xml`; saved state in shared preferences `flow_car_media_card` and `files/car_media_card_artwork.png`.
- **After a parent update:** if the parent renames the observed players or states, re-point the observers; keep the service order in the manifest and the `startObserving` call.
- **Check:** follow "Testing like the car" in `AAOS_CAR_NOTES.md`: open Flow from the app grid (one icon that opens Flow), play something, check the card, reboot the emulator, check the icon and the card again.

### C9. Bundle packaging

- **What and why:** with ABI splits on, App Bundle builds fail (AGP "multiple shrunk-resource" error). The workaround turns the splits off for bundle tasks only; APK builds keep their per-ABI and universal outputs, and Play delivers the right ABI from the bundle anyway.
- **Where:** `app/build.gradle.kts`.
- **After a parent update:** keep it; re-test before removing.
- **Check:** `./gradlew :app:bundleGithubRelease` passes.

### C10. Fork docs

- **What and why:** these files are the memory that survives between agent chats and the owner's only way to steer agents.
- **Where:** `README.md` (contract in section 9); the fork block at the top of `AGENTS.md`; `CLAUDE.md`, `GEMINI.md`; `.claude/skills` (a link to `.github/skills`); `.github/skills/aaos-*`; every `AAOS_*.md` file.
- **After a parent update:** keep ours; in `AGENTS.md` keep our block at the top and take the parent's text below it. The parent's `.gitignore` ignores `.claude/` and `claude.md`; these are tracked anyway, never delete them.
- **Check:** the files exist and the README banner is intact.

# Flow AAOS fork: rules every agent must follow

> Fork-owned file. It does not exist in the parent project, so upstream merges never overwrite it. If reality changes, fix this file in the same change.

## 1. What this repo is

This is the owner's fork of [Flow](https://github.com/A-EDev/Flow), customised so the app is readable and usable on the Polestar 3's Android Automotive OS (AAOS) display. It reaches the car only through Google Play **Internal testing**. The owner has no coding experience, so explain in plain English and never assume the owner can spot a code problem. The sister fork (Nuvio) follows identical docs.

AAOS is an Android OS built into the car that runs normal Android apps. **Android Auto (phone projection) is out of scope**; do not add Android Auto metadata.

## 2. Terminology (identical in both repos)

| Term | Meaning |
| --- | --- |
| upstream | The parent project (https://github.com/A-EDev/Flow). Read-only to us. Only `upstream/main` is ever synced; ignore its other branches. |
| origin | The owner's GitHub fork (https://github.com/metaljay/Flow_AAOS). |
| `main` | Upstream code plus our customisations. The only long-lived branch; always buildable and releasable. |
| sync branch | Temporary `sync/upstream-<date>` branch used to review an upstream update before it reaches `main`. Deleted afterwards. |
| customisation | A deliberate change we keep (sections 5 and Part 2). |
| invariant | A customisation that must survive every upstream merge (section 5). |
| Play version code | The number Play Console requires to rise on every upload. Independent of upstream's version. See `AAOS_RELEASE.md`. |
| release bundle | The signed `.aab` file uploaded to Play. |
| debug build | Emulator-only. Never installed on the car. |

## 3. Golden rules

1. The only target is the Polestar 3 (AAOS), delivered via Play Internal testing.
2. `main` is the only long-lived branch. Upstream updates go through a `sync/` branch first (`AAOS_UPSTREAM_SYNC.md`).
3. Never push to upstream. We only control our fork. Parent changes flow in one way.
4. Port, don't replay: adapt each required behaviour into the current upstream code instead of cherry-picking old commits.
5. Keep edits to upstream-owned files as small as possible (fewer merge conflicts). Fork-only additions go in clearly separate places or files.
6. Every release bundle needs a Play version code higher than the last upload, whatever upstream's version says (`AAOS_RELEASE.md`).
7. If a product decision is unclear (for example removing a customisation), ask the owner instead of guessing.

## 4. Permissions and safety rails

Agents may edit files, build, run tests, commit, push to `origin/main` once the work is verified, and create and delete `sync/` branches. Agents must **never**:

- force-push or rewrite history on `main`;
- push to upstream or merge anything towards it;
- commit, print, copy or paste `Key.jks`, any keystore, `local.properties` contents or any password;
- delete branches other than `sync/*`;
- change the `applicationId`, namespace or signing setup unless asked;
- say something is "verified" without stating exactly what was run and what was not (compiled / unit-tested / emulator / real car).

Commit with small, clear messages (`type(scope): description`). Stage files by name; do not use `git add -A`.

## 4b. Talking to the owner (the owner is not a coder)

The owner copy-pastes messages between chats and has little or no coding experience. Therefore:
1. Never tell the owner to run a command unless you give the complete command in a copy-paste code block with every value filled in. No placeholders such as <date> or <file>: look the value up yourself first.
2. Run commands yourself wherever you can. Ask the owner to act only for things only they can do: sign the bundle in Android Studio, upload in Play Console, change GitHub settings, or approve a decision.
3. Offer decisions as copy-paste replies, for example: reply `approve sync` or `cancel sync`. One decision at a time.
4. Explain results in plain English. Explain any jargon in one short phrase (for example: "merge means combining the parent's changes with ours").
5. End every task with a report in this order: What I did / What I checked (the exact commands and results) / What I did NOT check / What you need to do next (the exact message to paste, or "nothing").
6. If something fails, stop and say what failed in plain English. Give the owner one message to paste back to you or to another AI assistant. Do not attempt risky fixes.
7. Do not ask the owner to read raw code, diffs or logs; summarise them. Offer the raw output only if asked.

## 5. Invariants: must survive every upstream merge

1. **Play identity**: the release `applicationId` is exactly `com.JF_Flow`. The code namespace stays `io.github.aedev.flow` as upstream; never confuse the two.
2. **AAOS manifest**: automotive feature declarations, `automotive_app_desc.xml`, the car launcher entry, MediaBrowser service metadata, and `distractionOptimized="true"` on the required activities/aliases. Keep the `KEEP` note in the manifest.
3. **Camera optional**: `android.hardware.camera` and `android.hardware.camera.any` stay `required="false"`.
4. **Bottom navigation at every window size**: no side rail (`FlowNavigationChrome.kt`, `FlowNavigationBar.kt`); the overflow destination still works.
5. **Large-display legibility**: enlarged Material 3 type (`ui/theme/Type.kt`), 72 dp minimum top and bottom bars (shared `FlowTopBar` components), larger player, Shorts and music controls, video minimise button 60 dp hit size offset 8 dp.
6. **Launcher icon**: traditional red rounded YouTube play mark with a white triangle (red mark 46 x 32 dp on the 108 dp viewport) in both foreground assets.
7. **Bundle packaging**: the ABI-split workaround for bundle tasks in `app/build.gradle.kts` (re-test before removing).
8. **Fork docs**: the README banner/contract and the `AAOS_*.md` files.

Details and file locations are in Part 2 below.

## 6. Deliberately NOT customised

- **Video quality defaults**: Flow's own defaults apply (1080p Wi-Fi, 480p cellular), and users can change them in settings. An earlier "maximum resolution" default was removed because the Polestar 3 display cannot show it. Never raise the defaults and never overwrite a saved user choice.
- **Target SDK and architecture**: follow upstream (currently target SDK 36). The old SDK 35 downgrade is not a requirement.
- **Media3 player wiring**: do not change it as part of display-size work.
- **Android Auto**: out of scope.

## 7. README contract

`README.md` is part of the fork's identity. Every merge or refresh must keep: the `🚗 Android Automotive OS (AAOS) Fork` banner at the top; Flow branding and the link to the upstream project; the explanation that this is an AAOS fork for the Polestar 3; an accurate feature summary; the Google Play Internal testing path (no public GitHub downloads); links to `AAOS_FORK.md` and `AAOS_RELEASE.md`; a 'Keeping this fork up to date' section consistent with the AAOS_*.md files and .github/skills. Only use screenshots of this fork that exist and are accurate. On a README conflict, merge deliberately; do not accept either side blindly.

## 8. Keeping these docs current

After every verified change: add a dated entry to `AAOS_LOG.md` (what changed, commands run, what was and was not verified); update Part 2 if behaviour changed; update the release-state table in `AAOS_RELEASE.md` after any version bump or confirmed upload. The `AAOS_*.md` files are the memory that survives between agent chats; if it is not written here, the next agent will not know it. After any change or bug fix, run the `.github/skills/aaos-log-change` recipe, which makes all of these updates.

## 9. Environment notes (one Mac, Android Studio)

- Builds run on the owner's Mac. The apps sit in the AAOS folder that contains both repos; the release key `Key.jks` is in that folder, outside both repos.
- Signed bundles for upload go in the `For upload to Play Console` folder in the AAOS folder, next to both repos (outside git).
- Emulator used for checks: `Automotive_Large_Portrait` (1280 x 1606). It does not prove every Polestar 3 configuration. If `adb` or the emulator is unavailable, say so; never claim device verification from a compile.
- Robolectric tests need Android Studio's bundled JBR; the default JDK 27 fails on the test runner's class parser (environment issue, not a test failure).
- `graphify` (see the upstream rules in `AGENTS.md`) may not be installed; if it is missing, say so and carry on.

---

# Part 2: Customisation inventory (detail)

## Product requirements

These are the behaviors to preserve when the corresponding upstream code changes.

| Area | Required behavior | Where to inspect |
| --- | --- | --- |
| AAOS identity and launch | Keep AAOS automotive feature declarations, automotive app metadata, the car launcher entry, and the MediaBrowser service metadata. Preserve the required launcher/activity entries and their `distractionOptimized="true"` metadata, including the explanatory `KEEP` warning in the manifest. | `app/src/main/AndroidManifest.xml`, merged manifest for the affected variant |
| Camera-less vehicles | QR sync may request camera permission, but a camera must not be a Play Store device requirement. Keep both `android.hardware.camera` and `android.hardware.camera.any` optional (`android:required="false"`). | `app/src/main/AndroidManifest.xml`, Play device catalogue if eligibility changes |
| App navigation | Home, Shorts, Music, Subscriptions, and Library navigation remains at the bottom at every app window size. Do not restore upstream's adaptive left navigation rail. The overflow destination remains available when the enabled tab count exceeds the bar limit. | `ui/components/layout/navigation/FlowNavigationChrome.kt`, `FlowNavigationBar.kt` |
| Large-screen legibility | Preserve the enlarged Material 3 type, touch targets, player controls, titles/descriptions/comments, and Shorts controls. Keep the larger top bar and Home logo/search and the 72 dp minimum top and bottom app-bar heights. Keep the video minimize control at a 60 dp hit size and offset 8 dp to the right. | `ui/theme/Type.kt`; `ui/components/layout/topbar/`; `ui/screens/home/`; `ui/components/videoplayer/`; `ui/components/shorts/`; `ui/components/musicplayer/` |
| Video playback quality | Preserve the app's standard unset defaults (1080p on Wi‑Fi and 480p on cellular); users can change these preferences in settings. Do not overwrite a user's saved quality. | `data/local/PlayerPreferences.kt`, quality settings UI, playback stream selection |
| Launcher branding | Keep the traditional red rounded YouTube play mark with a white triangle as the default launcher icon. Size the red mark to match the approximate 46 dp longest visible dimension of the Nuvio adaptive launcher foreground on its 108 dp viewport; use the official YouTube mark's centered triangle proportions. | `app/src/main/res/drawable/ic_launcher_foreground.xml`, `ic_launcher_dynamic_foreground.xml`, theme colors |
| README and fork identity | Preserve the README contract above: AAOS notice, fork/upstream attribution, accurate feature and Play Internal testing guidance, and this migration-guide link. Do not include unrepresentative screenshots. | `README.md` |
| Play identity | Release `applicationId` remains exactly `com.JF_Flow`, preserving continuity with the existing Play listing. Debug/nightly suffixes are expected; validate the release variant rather than comparing its ID to a debug package. | `app/build.gradle.kts`, resolved release variant |
| Upstream platform baseline | Keep upstream's current target SDK and architecture unless a demonstrated AAOS regression requires a deliberate change. The old fork's target SDK 35 downgrade is not a requirement. | Root/app Gradle configuration and merged manifest |
| APK/App Bundle packaging | Preserve ABI-specific/universal APK outputs for APK workflows. For App Bundle tasks, keep the ABI split workaround if it is still needed to avoid AGP's multiple shrunk-resource failure; bundles already describe supported ABIs for Play delivery. Re-test before changing it. | `app/build.gradle.kts`, APK and bundle tasks |

### Quality preference behavior

Video quality follows the upstream default settings (1080p on Wi‑Fi and 480p on cellular) unless the user changes them. This is not a fork-specific customisation.

## Customization inventory

This lists the intent and the notable implementation areas so a future merge can find the right
surface without treating these paths as a patch to apply blindly.

### AAOS, manifest, and app identity

- `app/src/main/AndroidManifest.xml`: optional automotive and camera features, automotive app
  descriptor, car launcher entry, MediaBrowser metadata, and distraction-optimized metadata for
  required activities/aliases. Preserve the nearby `KEEP` note when resolving manifest conflicts.
- `app/src/main/res/xml/automotive_app_desc.xml`: automotive app descriptor referenced by the
  manifest.
- `app/build.gradle.kts`: release application ID, build identity, APK splits, and the App Bundle
  ABI-split workaround. Keep release versioning under owner control.
- Runtime component names follow the Kotlin/manifest namespace `io.github.aedev.flow`; runtime
  package selection uses the application ID where appropriate. Do not conflate namespace and
  application ID when porting components.

### Display sizing and navigation

- `ui/components/layout/navigation/FlowNavigationChrome.kt` forces bottom navigation at every
  window size and reserves a 72 dp minimum bar height above the system navigation inset.
- `ui/components/layout/navigation/FlowNavigationBar.kt` uses larger icons and labels and keeps
  overflow destinations usable.
- `ui/components/layout/topbar/FlowTopBar.kt`, `FlowTopBarDefaults.kt`, and
  `FlowTopBarActions.kt` centralize a 72 dp minimum top bar, larger title/action affordances, and
  readable action menus. Keep screens on this shared component rather than restoring custom bars.
- `ui/screens/home/HomeScreen.kt` enlarges the Home wordmark, logo, and search action.
- `ui/theme/Type.kt` carries the larger shared Material typography. Related fixed player/comment
  text sizing lives in the relevant player, info, and shared comment/description components.
- Player and Shorts controls, plus music-player controls, have larger touch targets and icon sizes.
  Preserve their existing component architecture and motion; do not replace Media3 player wiring
  as part of a display-size port.

### Playback and branding

- `data/local/PlayerPreferences.kt` preserves the standard 1080p Wi‑Fi and 480p cellular defaults
  for unset video-quality preferences; the quality settings surfaces use matching initial values.
- Stream selection remains bounded by actual available streams and existing codec/stream rules.
  Preserve those rules when porting the preference default.
- The default launcher foreground assets use the red play mark. Keep adaptive/dynamic icon assets
  consistent with the default identity. Its red outer mark is 46 x 32 dp on the 108 dp viewport,
  matching the Nuvio launcher foreground's approximately 46 dp longest visible dimension. The
  white play triangle follows the official YouTube mark's centered proportions.

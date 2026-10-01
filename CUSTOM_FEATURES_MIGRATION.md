# Polestar 3 / AAOS upstream migration guide

Updated: 2026-10-01

## Start here: agent workflow

This guide is the acceptance checklist for work on the Flow AAOS fork. Before editing code,
merging upstream, or updating project configuration:

1. Read this guide and check the live branch, remotes, worktree, and build configuration. Values
   in the migration snapshot and validation record are historical; verify them before relying on
   them.
2. Preserve the AAOS and product requirements below while porting behavior into the current
   upstream architecture. Do not replay old fork commits wholesale.
3. `main` is the primary active custom branch on `origin`.
4. Validate the affected build and device paths, then update this guide with the change and
   evidence. Clearly distinguish builds/tests from emulator or vehicle verification.

The target is Android Automotive OS (AAOS), with the UI tuned for legibility and touch on a large
vehicle display across supported window sizes and orientations. Preserve AAOS driver-distraction
requirements; larger controls do not imply that every app action is appropriate while driving.
The `Automotive_Large_Portrait` emulator at 1280 x 1606 was used for earlier checks, but that does
not verify every Polestar 3 hardware or software configuration.

Do not commit, push, publish, or create a release unless explicitly asked.

## README preservation contract

`README.md` is part of the fork's product identity, not disposable upstream documentation. Every
upstream merge, README refresh, or branding change must preserve and re-check these requirements:

- Keep the **🚗 Android Automotive OS (AAOS) Fork** notice at the top.
- Keep the Flow-for-AAOS identity, attribution/link to the upstream Flow project, and the
  explanation that this is an AAOS-focused fork for the Polestar 3.
- Retain the existing Flow logo artwork when it is present and valid.
- Keep the feature summary accurate to this guide, including the AAOS-specific changes and their
  constraints; update it when implemented behavior changes.
- Explain the intended Google Play Internal testing distribution path. Do not advertise public
  GitHub releases or direct downloads as the official distribution channel.
- Keep the direct link to this guide so users and future agents can find the rationale and full
  customization inventory.
- Retain only branding artwork and screenshots that exist and are accurate. Do not add or restore
  unrepresentative app screenshots; only use current, verified captures of this fork and label
  them accurately.

Before accepting a README change, compare it with this contract and the live build/distribution
configuration. Preserve correct content across upstream README conflicts rather than blindly
accepting either side.

## Product requirements

These are the behaviors to preserve when the corresponding upstream code changes.

| Area | Required behavior | Where to inspect |
| --- | --- | --- |
| AAOS identity and launch | Keep AAOS automotive feature declarations, automotive app metadata, the car launcher entry, and the MediaBrowser service metadata. Preserve the required launcher/activity entries and their `distractionOptimized="true"` metadata, including the explanatory `KEEP` warning in the manifest. | `app/src/main/AndroidManifest.xml`, merged manifest for the affected variant |
| Camera-less vehicles | QR sync may request camera permission, but a camera must not be a Play Store device requirement. Keep both `android.hardware.camera` and `android.hardware.camera.any` optional (`android:required="false"`). | `app/src/main/AndroidManifest.xml`, Play device catalogue if eligibility changes |
| App navigation | Home, Shorts, Music, Subscriptions, and Library navigation remains at the bottom at every app window size. Do not restore upstream's adaptive left navigation rail. The overflow destination remains available when the enabled tab count exceeds the bar limit. | `ui/components/layout/navigation/FlowNavigationChrome.kt`, `FlowNavigationBar.kt` |
| Large-screen legibility | Preserve the enlarged Material 3 type, touch targets, player controls, titles/descriptions/comments, and Shorts controls. Keep the larger top bar and Home logo/search and the 72 dp minimum top and bottom app-bar heights. Keep the video minimize arrow enlarged and offset 4 dp to the right. | `ui/theme/Type.kt`; `ui/components/layout/topbar/`; `ui/screens/home/`; `ui/components/videoplayer/`; `ui/components/shorts/`; `ui/components/musicplayer/` |
| Video playback quality | When no Wi-Fi or cellular video-quality preference has been saved, default to 2160p (the highest setting exposed by the app). If the video has no 2160p stream, use the best available stream under the existing stream-selection behavior. Do not overwrite a user's saved quality. This requirement is for video, not music audio quality. | `data/local/PlayerPreferences.kt`, quality settings UI, playback stream selection |
| Launcher branding | Keep the traditional red rounded YouTube play mark with a white triangle as the default launcher icon. | `app/src/main/res/drawable/ic_launcher_foreground.xml`, `ic_launcher_dynamic_foreground.xml`, theme colors |
| README and fork identity | Preserve the README contract above: AAOS notice, fork/upstream attribution, accurate feature and Play Internal testing guidance, and this migration-guide link. Do not include unrepresentative screenshots. | `README.md` |
| Play identity | Release `applicationId` remains exactly `com.JF_Flow`, preserving continuity with the existing Play listing. Debug/nightly suffixes are expected; validate the release variant rather than comparing its ID to a debug package. | `app/build.gradle.kts`, resolved release variant |
| Upstream platform baseline | Keep upstream's current target SDK and architecture unless a demonstrated AAOS regression requires a deliberate change. The old fork's target SDK 35 downgrade is not a requirement. | Root/app Gradle configuration and merged manifest |
| APK/App Bundle packaging | Preserve ABI-specific/universal APK outputs for APK workflows. For App Bundle tasks, keep the ABI split workaround if it is still needed to avoid AGP's multiple shrunk-resource failure; bundles already describe supported ABIs for Play delivery. Re-test before changing it. | `app/build.gradle.kts`, APK and bundle tasks |

### Important quality-default detail

The 2160p setting is the default only when the corresponding DataStore preference is absent. It
does not rewrite a preference that was previously saved, including a value saved by the older
1080p/480p defaults. To change an existing installation, the user can select a new quality in
settings; do not silently migrate saved values unless the owner explicitly requests that behavior.

## Upstream merge and verification procedure

1. Identify the exact upstream commit intended for the merge and record its full SHA below before
   integrating it. Confirm the working branch and worktree; do not overwrite `my-custom-features`.
2. Review the upstream diff against the current migration branch and this guide. Use the old custom
   commits as historical evidence of intent only: they include inherited divergence and are not
   safe to replay wholesale.
3. Port each still-required behavior into the current upstream implementation. Prefer adapting
   shared components/settings over adding parallel AAOS-only implementations. Do not assume an
   upstream replacement is equivalent until its behavior has been checked on the large display.
4. For manifest or build changes, inspect the merged release manifest and resolved release
   application ID. Check every relevant activity and launcher alias, camera feature requirement,
   automotive metadata, MediaBrowser entry, target SDK, and flavor-specific value.
5. Recheck UI behavior when navigation, typography, Compose/Material, player, or window-size code
   changes. In particular verify bottom navigation on both compact and expanded windows and test
   that larger controls still fit without obscuring content.
6. Run focused tests first, then relevant flavor-prefixed checks. Kotlin/Gradle changes require
   `./gradlew ktlintCheck`; use tasks such as `:app:compileGithubDebugKotlin`,
   `:app:compileFossDebugKotlin`, `:app:testGithubDebugUnitTest`, and
   `:app:assembleGithubDebug` as applicable. Never use bare `assembleDebug` or
   `compileDebugKotlin`.
7. Exercise the affected AAOS flow on an emulator/device when available: launch/onboarding,
   bottom navigation, Home/search, video playback and controls, Shorts, and any manifest or
   background-playback path touched by the merge. Record device/emulator and exact outcomes.
8. Review `README.md` against the README preservation contract. Resolve upstream conflicts without
   losing the AAOS notice, fork attribution, accurate Play Internal testing guidance, or this
   guide's link. Do not add unrepresentative screenshots.
9. Update this guide's migration record and validation notes. Clearly separate code/build/test
   evidence from on-device verification and list blocked checks. Do not claim device verification
   from compilation alone.

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

- `data/local/PlayerPreferences.kt` supplies the 2160p default only for unset Wi-Fi/cellular video
  quality values; the quality settings surfaces use matching initial values.
- Stream selection remains bounded by actual available streams and existing codec/stream rules.
  Preserve those rules when porting the preference default.
- The default launcher foreground assets use the red play mark. Keep adaptive/dynamic icon assets
  consistent with the default identity.

## Historical migration snapshot — verify before use

This dated section records one migration checkpoint. It is not the current branch or release
status; check live refs, configuration, and Play Console before acting on these values.

- Working branch: `aaos-upstream-migration`, based on `upstream/main`. At the last check, HEAD and
  `upstream/main` both resolved to `10f588961d3467a2210a302d3698e9262c3ea201`.
- `my-custom-features` remains the separate reference branch.
- Common base recorded for the original comparison:
  `79937a30ce64fcf7649cd83c368d787b1bce3c04`.
- Historical custom commits: `25ce9fbe` (`My custom tweaks and features`) and `6cac82a4`
  (`Updated custom features`). Their intended changes include AAOS metadata, larger AAOS controls
  and typography, app/build identity, and launcher branding. Do not cherry-pick these commits
  wholesale.
- The custom branch had two commits beyond the common base at the time of the initial comparison.
  Upstream had 170 commits beyond it at that time; re-count before using this as current history.
- No commits or pushes had been made at the last worktree check. Do not assume later sessions have
  the same worktree state.
- The release configuration now uses application ID `com.JF_Flow`, target SDK 36, version code 31,
  and version name `2.2.12`. Play rejected code 30 as already used when the owner attempted the
  upload on 2026-10-01, so code 31 is the next upload candidate. Code 28/name `2.2.9` had also
  been rejected as used; code 29/name `2.2.10` preceded the camera compatibility update. Before
  every upload, confirm the code is still unused in Play Console and increment it again if needed.
  The owner manages release bumps; never bump versions as incidental merge cleanup.

## Known decisions

- Confirm whether this product must support Android Auto in addition to AAOS. They are separate
  targets; do not add Android Auto metadata just because AAOS support is required.
- Re-evaluate whether each existing AAOS launch intent/metadata entry remains required when
  upstream changes the app's activities or launch structure. Do not remove required entries based
  only on a successful compile.

## Validation record

### Earlier AAOS migration checks

- The release bundle task `:app:bundleGithubRelease` passed after disabling ABI splits for bundle
  tasks. It produced an unsigned AAB because the release keystore was unavailable; packaging
  success did not mean the artifact was ready for Play upload.
- The merged release manifest was checked at that time: min SDK 26, target SDK 36, version code
  30/name `2.2.11`, and both camera features optional. The merged GitHub debug manifest contained
  19 `distractionOptimized=true` entries.
- `ktlintCheck`, GitHub/FOSS debug unit tests, and serial GitHub/FOSS debug Kotlin compilation
  passed during the migration. Serial compilation was needed on that machine because concurrent
  flavor compiles exhausted the default Kotlin compiler heap.
- `:app:assembleGithubDebug` passed and the arm64 APK was installed on the
  `Automotive_Large_Portrait` Android Automotive emulator (1280 x 1606). Onboarding completed;
  Home, Shorts, Music, Subscriptions, and Library navigation appeared at the bottom; Music,
  Subscriptions, and Library opened; Home video playback controls and Shorts playback were
  exercised. No crash or ANR was observed in the captured logcat window. This was emulator
  coverage, not confirmation on every physical Polestar 3 configuration.
- The debug package used the expected `.debug` suffix and is not evidence of the release package
  ID. Release identity was checked separately.
- Resource parsing and manifest merging passed after stale generated output was removed. AAPT2
  9.4.1 was fetched for that validation.
- A root `build.gradle.kts` AGP change from 9.3.1 to 9.4.1 appeared separately during validation;
  it was not attributed to the migration work. Inspect its current diff and keep/revert it
  deliberately rather than silently assuming it belongs.

### 2026-10-01 display and quality follow-up

- Passed `ktlintCheck`, `:app:compileGithubDebugKotlin`, `:app:compileFossDebugKotlin`,
  `:app:assembleGithubDebug`, and the focused `ServicePlaybackStreamSelectorTest` and
  `FlowNavigationChromeTest` unit tests.
- Robolectric tests passed with Android Studio's bundled JBR. The default JDK 27 failed because the
  test runner's ASM parser did not support class-file major version 71; this was an environment
  compatibility issue, not a test assertion failure.
- No device/emulator check was possible for this follow-up because `adb` was unavailable.
- `graphify update .` could not run because graphify was unavailable and `graphify-out/` did not
  exist.
- The first upload attempt for code 30 was rejected by Play as already used. The release version
  was then advanced to code 31/name `2.2.12`; verify the resolved release artifact and confirm code
  31 is unused before uploading. Increment again if Play reports it has already been consumed.
- `:app:bundleGithubRelease` passed with code 31/name `2.2.12`; the merged release manifest confirms
  those values. Gradle reported the release keystore was unavailable, so this bundle is unsigned
  and is not upload-ready until signed with the correct key.

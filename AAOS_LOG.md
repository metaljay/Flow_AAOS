# Flow AAOS log

Newest entry first. One entry per verified change, upstream sync or upload. Format:

```
## YYYY-MM-DD: short title
- What changed and why
- Commands run and results
- Verified: compile / unit tests / emulator / real car (state which; list anything NOT verified)
```

Agents read this file only when they need evidence. Rules live in `AAOS_FORK.md`.

## 2026-10-04: Workflow simplified to two jobs (parent update, tweak), both ending in a release that raises the version automatically; aaos-log-change replaced by aaos-tweak.

## 2026-10-03: Upload confirmed by the owner in Play Console
- The owner confirmed the accepted upload for version code 34 (version name 2.2.15) in Play Console.
- Updated the release-state table in `AAOS_RELEASE.md` to record the confirmed upload and noted that the repo version must be raised before the next release.
- Commands run and results: `git diff --check` passed.
- Verified: no compile, unit-test, emulator or real-car check was run for this status update; the only verification here is the owner's Play Console confirmation. No device or vehicle claim is made.

## 2026-10-03: Raised the Play version for a safety buffer before upload
- Increased the release version code from 33 to 34 and the version name from 2.2.14 to 2.2.15 to keep a clear safety buffer above the last confirmed Play upload (32 / 2.2.13).
- Updated the release-state table in `AAOS_RELEASE.md` to reflect the current repo version and the new signed-bundle naming convention.
- Commands run and results: `./gradlew ktlintCheck` passed; `./gradlew :app:assembleGithubDebug` passed.
- Verified: Kotlin formatting and the GitHub debug APK build passed. NOT verified: signing for Play upload, Play Console upload, emulator launch, and real car installation.

## 2026-10-03: Prepared release bundle for version code 33 (2.2.14)
- Prepared release bundle for Play Console upload with versionCode 33 and versionName 2.2.14.
- Verified merged release manifest: application ID `com.JF_Flow`, version code 33, version name 2.2.14, target SDK 36, automotive metadata present, camera features optional (`required="false"`).
- Commands run and results: `./gradlew :app:bundleGithubRelease` passed.
- Verified: release bundle build and merged manifest inspection passed. NOT verified: signing, Play Console upload, emulator or real car installation (owner will sign and upload).

## 2026-10-03: Fixed stale launcher size notes in AAOS_FORK.md.
- Updated Part 2 of `AAOS_FORK.md` to record that the red launcher mark is 68 x 45 dp on the 108 dp viewport, using the official YouTube mark's Bezier curves and centred triangle proportions.
- Commands run and results: none (docs-only change).
- Verified: documentation diff checked; no compile, unit tests, emulator or real-car checks run (docs-only change).

## 2026-10-03: Official YouTube launcher icon and player top bar spacing update
- Updated launcher foreground assets (`ic_launcher_foreground.xml` and `ic_launcher_dynamic_foreground.xml`) to use the official YouTube play button vector path (68 x 45 dp on a 108 dp viewport), matching its official smooth Bezier curves and centered triangle proportions.
- Updated video player top bar controls in `PlayerControlsOverlay.kt` and `VideoPlayerTopBar.kt`: set horizontal padding to 16 dp (normal) / 20 dp (portrait) and shifted the minimize chevron button offset to +12 dp, moving the minimize button rightwards and the gear icon / action cluster leftwards away from display screen edges.
- Updated unit test `VideoPlayerTopBarSpacingTest.kt` to account for the enlarged minimize touch target.
- Commands run and results: `./gradlew ktlintCheck` passed; `./gradlew :app:testGithubDebugUnitTest` passed (3046 passed); `./gradlew :app:assembleGithubDebug` passed.
- Verified: Kotlin formatting, unit tests, and GitHub debug APK build passed. NOT verified: emulator or real car display (no connected device or active emulator was available).

## 2026-10-03: Added the aaos-log-change recipe (record changes and bug fixes) and README step 4.
- Documented the change-logging recipe `.github/skills/aaos-log-change/SKILL.md` in `README.md`, `AGENTS.md`, `AAOS_FORK.md`, and `AAOS_LOG.md`.
- Commands run and results: none (docs-only change).
- Verified: documentation diff and formatting checked; no compile, unit tests, emulator or real-car checks were run.

## 2026-10-03: Fix AAOS home screen current-media card blank state on playback resumption
- Fixed AAOS home screen current-media card showing blank when returning to previously played media:
  - Updated `Media3MusicService.onCreate()` to restore saved queue state from `QueuePersistence` upon service startup, initializing `EnhancedMusicPlayerManager` and setting the saved tracks and seek position on ExoPlayer (paused).
  - Updated `EnhancedMusicPlayerManager.restoreSavedQueue()` to populate ExoPlayer (`player.setMediaItems`) when `player.mediaItemCount == 0`.
  - Implemented `onPlaybackResumption()` in `LibrarySessionCallback` inside `Media3MusicService.kt` to return `MediaItemsWithStartPosition` for AndroidX Media3 playback resumption requests.
  - Updated `onGetChildren` in `LibrarySessionCallback` to fall back to `player.currentMediaItem` or timeline when manager state is empty, and added `customCacheKey` to `toAutoMediaItem()`.
  - Bumped version code to 33 and version name to 2.2.14 in `app/build.gradle.kts`.
- Commands run and results: `./gradlew ktlintCheck` passed; `./gradlew :app:assembleGithubDebug` passed.
- Verified: Kotlin sources formatted with Spotless/ktlint and GitHub debug APK assembled successfully. No emulator or real-car verification was performed.

## 2026-10-03: Launcher icon aligned with official YouTube play-mark proportions
- Kept the red mark at 46 x 32 dp and resized its white triangle from 22 x 26 dp to 12 x 13.8 dp, matching the official YouTube logo SVG's triangle proportions. Updated the monochrome adaptive icon to use the same silhouette.
- Compared against the official YouTube logo SVG (`https://www.gstatic.com/youtube/img/branding/youtubelogo/svg/youtubelogo.svg`) and the Nuvio Android launcher foreground.
- Commands run and results: `./gradlew :app:assembleGithubDebug` passed; `git diff --check` passed. `graphify update .` could not run because graphify is not installed.
- Verified: Android resources compiled and the GitHub debug APK assembled. No emulator or real-car visual check was performed.

## 2026-10-03: Instructions rewritten for a non-coder owner: communication rules, approval gate in the sync procedure, click-by-click release steps, prompt-based README how-to.

## 2026-10-03: Signed bundles now collected in a standard For upload to Play Console folder.
- Updated the release instructions, release helper, fork environment notes and README with the standard signed-bundle folder and collection steps.
- Commands run and results: `git diff --check` passed; no build commands were run (docs-only change).
- Verified: documentation diff and whitespace only; no compile, unit tests, emulator or real-car checks were run.

## 2026-10-03: Upload confirmed by the owner in Play Console
- The owner confirmed the accepted upload for version code 32/name `2.2.13` in Play Console.
- Updated the release-state table in `AAOS_RELEASE.md` to record the confirmed upload and to note that the repo version must be raised before the next release.
- Commands run and results: `git diff --check` passed; `git status --short` showed only the docs updates for the release record.
- Verified: no compile, unit-test, emulator or real-car check was run for this status update; the only verification here is the owner's Play Console confirmation. No device or vehicle claim is made by an agent.

## 2026-10-03: README gained a 'Keeping this fork up to date' how-to.

- Added parent-sync and release guidance to the README and added it to the README contract.
- Verified the documentation diff and whitespace with `git diff --check`; no builds were run (docs-only change).

## 2026-10-02: Docs anonymised: removed personal identifiers from fork-owned docs.

## 2026-10-02: Docs restructure (no app code changed)

- Replaced the legacy migration guide with `AAOS_FORK.md`, `AAOS_UPSTREAM_SYNC.md`, `AAOS_RELEASE.md` and this log, identical in both fork repos.
- Owner-confirmed from Play Console: last uploaded 31 (2.2.12); the repo is at 32 (2.2.13) for the next upload.
- Owner-confirmed: the app runs on the Polestar 3 from Play Internal testing. Not re-verified by an agent.
- Branch policy: `main` only, with temporary `sync/upstream-<date>` branches for upstream updates.
- Video quality: the owner confirmed a maximum-resolution default is unsuitable for the car display. Upstream defaults (1080p / 480p) apply; the README row claiming 2160p was removed.
- Signing finding: Gradle expects `release.keystore` in the repo, which does not exist, so Gradle bundles are unsigned. Releases are signed with the Android Studio wizard using `Key.jks`.

## Archived history

## Historical migration snapshot

This dated section records one migration checkpoint. It is not the current branch or release
status; check live refs, configuration, and Play Console before acting on these values.

- Working branch: `aaos-upstream-migration`, based on `upstream/main`. At the last check, HEAD and
  `upstream/main` both resolved to `10f588961d3467a2210a302d3698e9262c3ea201`.
- `my-custom-features` no longer exists (retired 2026-10-02; main is the only branch).
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
- The release configuration now uses application ID `com.JF_Flow`, target SDK 36, version code 32,
  and version name `2.2.13`. Code 30 was rejected as already used on 2026-10-01, and code 31 was
  the previous upload candidate. Code 28/name `2.2.9` had also been rejected as used; code
  29/name `2.2.10` preceded the camera compatibility update. The owner manages release bumps;
  change versions only when explicitly requested. Before every upload, confirm the code is still
  unused in Play Console and increment it again if needed.

## Known decisions

- Android Auto: resolved — AAOS only; do not add Android Auto metadata.
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

### 2026-10-02 icon, playback and Play version follow-up

- Compared Nuvio's Android adaptive launcher foreground
  (`NuvioMobile/composeApp/src/androidMain/res/mipmap-mdpi/ic_launcher_foreground.webp`): visible
  artwork spans approximately 42 x 46 px on its 108 x 108 px canvas. Flow's red play mark is
  sized to a 46 x 32 dp footprint in both foreground assets; its white triangle is unchanged.
- Restored the original unset video-quality defaults: 1080p on Wi‑Fi and 480p on cellular. Updated
  the DataStore fallbacks and both regular and TV settings initial states. Existing saved
  preferences remain untouched. The quality default is a user setting, not a fork-specific
  playback override.
- Enlarged the player minimize button hit target from 52 dp to 60 dp and moved it from 4 dp to
  8 dp right to improve reachability on the larger player UI.
- At the owner's explicit request for a Play Console upload, advanced the release version from
  code 31/name `2.2.12` to code 32/name `2.2.13`. Confirm code 32 remains unused in Play Console.
- `ktlintCheck`, `:app:assembleGithubDebug`, `:app:compileFossDebugKotlin`, and
  `:app:bundleGithubRelease` passed. The merged release manifest confirms application ID
  `com.JF_Flow`, code 32, and name `2.2.13`.
- Gradle reported that the release keystore is unavailable, so the bundle is unsigned and is not
  upload-ready until signed with the correct key. No Play Console availability check or device /
  vehicle verification was performed.

### 2026-10-02 launcher icon and player top bar spacing update

- Updated launcher foreground assets (`ic_launcher_foreground.xml` and `ic_launcher_dynamic_foreground.xml`)
  to replace the small flat 46 x 32 dp box with the official YouTube play mark vector path (68 x 45 dp
  on the 108 dp viewport), matching the official YouTube logo's smooth Bezier curves and centered triangle proportions.
- Updated video player top bar positioning in `PlayerControlsOverlay.kt` and `VideoPlayerTopBar.kt`:
  increased top control row horizontal padding to 16 dp (normal) / 20 dp (portrait) and shifted the minimize chevron
  icon to +12 dp horizontal offset, moving the minimize button inward from the left screen edge and the gear/action cluster
  inward from the right screen edge.
- Verified with `./gradlew ktlintCheck`, `:app:testGithubDebugUnitTest` (3046 passed), and `:app:assembleGithubDebug`.

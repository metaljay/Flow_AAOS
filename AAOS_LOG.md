# Flow AAOS log

Newest entry first. One entry per verified change, upstream sync or upload. Format:

```
## YYYY-MM-DD: short title
- What changed and why
- Commands run and results
- Verified: compile / unit tests / emulator / real car (state which; list anything NOT verified)
```

Agents read this file only when they need evidence. Rules live in `AAOS_FORK.md`; facts about the car live in `AAOS_CAR_NOTES.md`. This file keeps the newest 15 entries; older ones are in `AAOS_LOG_ARCHIVE.md`.

## 2026-10-05: Fork docs tidied for future agents (no app change)
- Added `AAOS_CAR_NOTES.md` (identical in both forks: launcher, media card, safe area, testing like the car, Play), `CLAUDE.md` and `.claude/skills` (a link to `.github/skills`) so Claude Code finds the same rules and recipes.
- Recipes: tweak adds a car-like emulator check (open from the app list, check edges, media card and a reboot when relevant); sync and tweak read the car notes; release asks the owner to reply `Uploaded` and then records it.
- `AAOS_FORK.md`: invariants renumbered in order, stale "app listed twice" text removed, rules for writing car facts into both repos, a 15-entry log limit and keeping docs consistent. `AAOS_UPSTREAM_SYNC.md`: last merged parent commit recorded, duplicate line removed, test commands made foolproof. `AAOS_RELEASE.md`: last upload set from the owner's car report. Older log entries moved to `AAOS_LOG_ARCHIVE.md` unchanged.
- Commands run and results: `git diff --check` passed. Documentation only; no build needed.
- Verified: documentation-only change. NOT verified: nothing in the app changed.

## 2026-10-05: Release prepared for version 39 (2.2.20)
- Contains the top bar edge inset logged below. Version raised by the release recipe and pushed on its own.
- Commands run and results: `./gradlew :app:bundleGithubRelease` passed (unsigned, as expected).
- Merged release manifest checked: application ID `com.JF_Flow`, version code 39, name 2.2.20, target SDK 36, automotive and camera features optional.
- NOT verified: signing (done by the owner in Android Studio), Play upload, the real car.

## 2026-10-05: Top bar icons moved in from the right edge; display safe-area rule added; card-after-restart finding
- Top bar trailing actions (search, notifications, settings cog) now end with a 12 dp inset (`FlowTopBarDefaults.ActionsEndInset`, applied once in `FlowTopBar`, so every screen's bar gets it). Why: on the Polestar 3 the cog was only partly tappable because the screen's rounded edge covers the app window's edge. Owner request. Added a "Display safe area" invariant (now number 6) to `AAOS_FORK.md`.
- Car media card after a full restart (owner test on the Polestar 3, reproduced on the emulator): the card works during use (it followed Flow and then Nuvio), but after a full system restart it shows only the Flow icon and stays blank, even after playing in either app. The app's session does hold the right item; the car launcher (car-media-common `MediaSource.isMediaTemplate`, read from the emulator launcher's code) only accepts a media service if it has `androidx.car.app.launchable=true` or the app has no launcher activity, so after a restart it rejects the remembered source ("No opt-in info found ... Skipping MBS"). The opt-in is what broke the app icon on the Polestar, so a card that refills after a restart and an icon that opens the app cannot both be had with a normal app; no code change for this.
- Commands run and results: `./gradlew ktlintCheck`, `:app:compileGithubDebugKotlin`, `:app:compileFossDebugKotlin`, `:app:assembleGithubDebug` and `git diff --check` passed; `:app:testGithubDebugUnitTest` passed with Android Studio's bundled JBR (3098 tests, 0 failures).
- Verified (emulator `Automotive_Large_Portrait`, GitHub debug build): the settings cog sits about 32 px from the right edge (was about 20 px). The blank card after a restart was reproduced there.
- NOT verified: the real Polestar 3 for the new spacing.

## 2026-10-05: Release prepared for version 38 (2.2.19)
- Contains the app icon fix logged below (car media opt-in removed). Version raised by the release recipe and pushed on its own.
- Commands run and results: `./gradlew :app:bundleGithubRelease` passed (unsigned, as expected).
- Merged release manifest checked: application ID `com.JF_Flow`, version code 38, name 2.2.19, target SDK 36, automotive and camera features optional; car media service present without `androidx.car.app.launchable`.
- NOT verified: signing (done by the owner in Android Studio), Play upload, the real car.

## 2026-10-05: App icon opens Flow again (car media opt-in removed)
- Removed `androidx.car.app.launchable` from `FlowCarMediaBrowserService` in the manifest. Why: on the Polestar 3 (one icon per app) the app icon opened the car's media screen ("Continue watching") instead of the app, with no way in; the owner could only open the app from the Play Store's Open button. The car launcher source (AOSP `AppGridRepository`) adds a separate media entry for any opted-in media service; the Polestar keeps one entry per app and picks that one. The rest of the media card code (mirroring video and music, saved last item, artwork provider) is kept.
- Commands run and results: `./gradlew ktlintCheck`, `:app:compileGithubDebugKotlin`, `:app:compileFossDebugKotlin`, `:app:assembleGithubDebug` and `git diff --check` passed. Unit tests not re-run (manifest and comment change only; they passed earlier today).
- Verified (emulator `Automotive_Large_Portrait`, GitHub debug build): the app grid shows one Flow icon and tapping it opens Flow; while a video played, the home card showed its title, channel and thumbnail. After a full reboot the emulator launcher logged "Skipping MBS ... non media template app" for Flow, so on that launcher the card does not refill itself after a restart (the emulator home screen then stayed black, an emulator problem).
- NOT verified: the real Polestar 3 (icon behaviour and whether its card follows the service after a restart).

## 2026-10-05: Release prepared for version 37 (2.2.18)
- Contains the Home logo inset logged below. Version raised by the release recipe and pushed on its own.
- Commands run and results: `./gradlew :app:bundleGithubRelease` passed (unsigned, as expected).
- Merged release manifest checked: application ID `com.JF_Flow`, version code 37, name 2.2.18, min SDK 26, target SDK 36, automotive and camera features optional; car media service and artwork provider present.
- NOT verified: signing (done by the owner in Android Studio), Play upload, the real car.

## 2026-10-05: Home screen logo moved in from the left edge; car media card checked after reboot and after an update
- Home logo: the 44 dp logo sat in the top bar's leading slot with only the bar's 4 dp padding, so it almost touched the left edge while the search/settings icons sit about 16 dp in. Added a 12 dp start inset (`HomeScreen.kt`). Owner request.
- Car media card, emulator investigation (no code change): after a full reboot the card showed the last video (paused) and, when music was playing at shutdown, the last song (the car resumed it, as its play-on-boot setting allows). After Flow was reinstalled (same as a Play update) or force-stopped, the card went blank showing only "Flow" and stayed blank even after Flow was reopened and played music; Flow's session had the right title and state, so the car's home screen simply did not reconnect. A full reboot restored it. This is car launcher behaviour after the app's process is replaced; Flow cannot make the launcher reconnect. The earlier entry's "force-stop" check had restarted the launcher, which hid this.
- Commands run and results: `./gradlew ktlintCheck` passed; `:app:assembleGithubDebug` passed; `:app:compileGithubDebugKotlin` and `:app:compileFossDebugKotlin` passed; `:app:testGithubDebugUnitTest` passed with Android Studio's bundled JBR (3098 tests, 0 failures; the default JDK 27 fails 180 Robolectric tests with "Unsupported class file major version 71", an environment issue).
- Verified (emulator `Automotive_Large_Portrait`, GitHub debug build): the Home logo now sits about the same distance from the left edge as the settings icon from the right.
- NOT verified: the real Polestar 3.

## 2026-10-04: Release prepared for version 36 (2.2.17)
- Contains the launcher mark, minimise arrow and music-service crash fix logged below, plus the car media card change. Version 35 was prepared but not uploaded; the release rule raises the version again anyway.
- Commands run and results: `./gradlew :app:bundleGithubRelease` passed (unsigned, as expected).
- Merged release manifest checked: application ID `com.JF_Flow`, version code 36, name 2.2.17, target SDK 36, automotive and camera features optional; car media service and artwork provider present.
- NOT verified: signing (done by the owner in Android Studio), Play upload, the real car.

## 2026-10-04: Launcher mark sized like Nuvio's, minimise arrow nudged left, music-service crash fixed
- Launcher icon: the official YouTube mark (68 x 45 on the 108 viewport) is now scaled by 0.706 to 48 x 32 in both foreground assets, so it matches the Nuvio launcher artwork (measured at about 41 x 45 on the same grid). Owner request.
- Video player minimise arrow: offset reduced from 12 dp to 6 dp to the right of the upstream position (`VideoPlayerTopBar.kt`). Owner request: still in from the edge, just less than before.
- Crash fix: `Media3MusicService.restoreSavedQueueStateIfNeeded` read `player.mediaItemCount` on a background thread ("Player is accessed on the wrong thread", Flow crash report at `Media3MusicService.kt:288`, version 2.2.15-debug on the emulator). Removed that off-thread read; the same check already runs on the main thread a few lines later.
- Commands run and results: `./gradlew ktlintCheck`, `:app:compileGithubDebugKotlin`, `:app:compileFossDebugKotlin`, `:app:testGithubDebugUnitTest`, `:app:assembleGithubDebug` and `git diff --check` all passed.
- Verified (emulator `Automotive_Large_Portrait`, GitHub debug build): the app grid shows the smaller red mark at a size similar to Nuvio's; the player's minimise arrow sits about 52 px from the left edge (settings gear about 42 px from the right); after installing the fix and rebooting the emulator the music service started with no crash.
- NOT verified: the real Polestar 3.

## 2026-10-04: Release prepared for version 35 (2.2.16)
- Contains the car media card change logged below. Version raised by the release recipe and pushed on its own.
- Commands run and results: `./gradlew :app:bundleGithubRelease` passed (unsigned, as expected).
- Merged release manifest checked: application ID `com.JF_Flow`, version code 35, name 2.2.16, min SDK 26, target SDK 36, automotive and camera features optional; `FlowCarMediaBrowserService` (with `androidx.car.app.launchable`) and the `com.JF_Flow.carmediaart` provider present.
- NOT verified: signing (done by the owner in Android Studio), Play upload, the real car.

## 2026-10-04: Car media card shows the last video or song reliably, including after Flow is closed

- Why the card was often blank: on the Google car launcher an app with a launcher activity must opt its `MediaBrowserService` in with `androidx.car.app.launchable`, otherwise the card ignores the service and only shows text while a live Flow session happens to be active (emulator log "Skipping MBS for ... Media3MusicService belonging to non media template app"). The card also reads only the browse service's session, which was the music session, so videos could never appear through it. Sources: Google "Build media apps for cars", "Configure manifest", "Display media artwork", "Media controls / playback resumption" pages; AOSP `CarMediaService.java` and car-media-common `PlaybackViewModel.java`.
- Added `FlowCarMediaSession` (one session mirroring whichever of the video or music player played last, last item saved to disk and restored as paused, controls forwarded, "Open Flow" prompt or music resume when play is pressed while closed), `FlowCarMediaBrowserService` (opted in, "Continue" browse tab) and `FlowCarMediaArtworkProvider` (content:// artwork, which AAOS requires). One `startObserving` call in `FlowApplication.onCreate`; manifest and fork-only strings file updated.
- Commands run and results: `./gradlew ktlintCheck` passed; `./gradlew :app:compileGithubDebugKotlin` passed; `./gradlew :app:compileFossDebugKotlin` passed; `./gradlew :app:testGithubDebugUnitTest` passed; `./gradlew :app:assembleGithubDebug` passed; `git diff --check` passed.
- Verified (emulator `Automotive_Large_Portrait`, Android 15, GitHub debug build): before the change the home card showed only "Flow" with no text. After it: a real YouTube video played in Flow appeared on the card (title and channel); after force-stopping Flow and restarting the launcher the card still showed that video with its thumbnail; a hand-seeded saved item also showed with Flow closed; pressing play with Flow closed showed the "Open Flow to continue watching" prompt in the car media screen (with a "Continue" tab listing the video) and its button opened Flow (the first scripted tap did nothing, probably sent before the dialog was ready; the second opened Flow).
- NOT verified: music playback mirrored on the card and the music resume from the card's play button; the real Polestar 3 (its launcher may differ from the emulator's Google car launcher).

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

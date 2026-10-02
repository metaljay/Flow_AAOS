# Flow: releasing to the Polestar 3 via Google Play

## Release state (update after every confirmed upload)

| Item | Value |
| --- | --- |
| Release application ID | `com.JF_Flow` |
| **Last uploaded to Play** | **31 (2.2.12)**, confirmed by the owner from Play Console on 2026-10-02 |
| Code in the repo now | 32 (2.2.13), ready for the next upload |
| Version file | `app/build.gradle.kts` (`versionCode` and `versionName` in `defaultConfig`, around line 40) |
| Release key | `/Users/jordanfern/Documents/AAOS/Key.jks` (outside the repo; keep a backup) |
| Who signs and uploads | The owner, with Android Studio and Play Console. Agents prepare and verify. |

## The version-code rule (hard rule)

Play Console rejects any upload whose version code was already used, so every bundle needs a code **strictly higher than every code ever uploaded**. Upstream's version numbers have nothing to do with it.

- At the end of any session that changes app code, make sure the code in the repo is **higher than "Last uploaded to Play"**. If it already is, leave it (one bump per upload, not per commit). Raise the version name's patch number alongside it so builds are identifiable.
- After an upstream merge, check it again; the merge can bring back a lower number.
- Agents cannot see Play Console. If Play says "version code already used", raise the code by one, rebuild, and update the table above.
- Docs-only changes need no bump.

## Steps

1. **Agent: prepare.** On `main`, work verified. Apply the version-code rule above and confirm the values in `app/build.gradle.kts` (`versionCode` and `versionName` in `defaultConfig`, around line 40).
2. **Agent: check the build.** `./gradlew :app:bundleGithubRelease`. Flow's Gradle file looks for a `release.keystore` in the repo folder. That file does not exist (and must not be created or committed), so a Gradle-built bundle is **unsigned. That is expected.** Only the Android Studio wizard signs it, using `/Users/jordanfern/Documents/AAOS/Key.jks`. Do not add passwords to `local.properties` or create keystore files in the repo. Inspect the merged release manifest: application ID `com.JF_Flow`, version code/name, min/target SDK, automotive and camera features optional.
3. **Owner: sign in Android Studio.** Build menu, Generate Signed App Bundle / APK, choose Android App Bundle. Module `app`. Key store `/Users/jordanfern/Documents/AAOS/Key.jks` (enter alias and passwords yourself). Build variant `githubRelease` (a *release* variant, never debug). Finish. The `.aab` appears in the module's `release` build folder.
4. **Owner or agent: sanity check the file.** `jarsigner -verify <file>.aab` should say "jar verified". Confirm the file name and date are the new ones, not an old bundle.
5. **Owner: upload.** Play Console, the Flow app, Test and release, Testing, **Internal testing**, Create new release, upload the `.aab`, review, roll out.
6. **Owner: install on the car** from the Play Store on the Polestar 3 (internal testing invitation). Debug and nightly builds get a suffix (for example `.debug`); they are emulator-only and never go on the car.
7. **Record.** Update "Last uploaded to Play" above and add an `AAOS_LOG.md` entry. Optional: `git tag play-<code>` and push the tag.

## Never

- Never upload or install a debug build on the car.
- Never change the application ID; it must match the existing Play listing.
- Never replace the key, or commit/print/share it or its passwords.
- Never put real-vehicle claims in the log unless the owner actually tested on the car.

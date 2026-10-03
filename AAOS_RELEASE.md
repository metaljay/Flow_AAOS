# Flow: releasing to the Polestar 3 via Google Play

## Release state (update after every confirmed upload)

| Item | Value |
| --- | --- |
| Release application ID | `com.JF_Flow` |
| **Last uploaded to Play** | **32 (2.2.13)**, confirmed by the owner from Play Console on 2026-10-03 |
| Code in the repo now | **34 (2.2.15)** |
| Signed bundles for upload | The folder named `For upload to Play Console` in the AAOS folder (next to both repos, outside git). Files are named `<App>-<code>-<name>.aab`, for example `Flow-34-2.2.15.aab`. |
| Version file | `app/build.gradle.kts` (`versionCode` and `versionName` in `defaultConfig`, around line 40) |
| Release key | `Key.jks` in the AAOS folder that contains both repos (outside the repo; keep a backup) |
| Who signs and uploads | The owner, with Android Studio and Play Console. Agents prepare and verify. |

## The version-code rule (hard rule)

Play Console rejects any upload whose version code was already used, so every bundle needs a code **strictly higher than every code ever uploaded**. Upstream's version numbers have nothing to do with it.

- At the end of any session that changes app code, make sure the code in the repo is **higher than "Last uploaded to Play"**. If it already is, leave it (one bump per upload, not per commit). Raise the version name's patch number alongside it so builds are identifiable.
- After an upstream merge, check it again; the merge can bring back a lower number.
- Agents cannot see Play Console. If Play says "version code already used", raise the code by one, rebuild, and update the table above.
- Docs-only changes need no bump.

## Steps

1. **Agent: prepare.** On `main`, work verified. Apply the version-code rule above and confirm the values in `app/build.gradle.kts` (`versionCode` and `versionName` in `defaultConfig`, around line 40).
2. **Agent: check the build.** `./gradlew :app:bundleGithubRelease`. Flow's Gradle file looks for a `release.keystore` in the repo folder. That file does not exist (and must not be created or committed), so a Gradle-built bundle is **unsigned. That is expected.** Only the Android Studio wizard signs it, using `Key.jks` in the AAOS folder that contains both repos. The signing wizard uses module `app` and build variant `githubRelease`; the `.aab` appears in the module's `release` build folder. Do not add passwords to `local.properties` or create keystore files in the repo. Inspect the merged release manifest: application ID `com.JF_Flow`, version code/name, min/target SDK, automotive and camera features optional.
3. **Owner: sign the bundle in Android Studio.** (a) Open this project in Android Studio. (b) In the top menu choose Build, then Generate Signed App Bundle / APK. (c) Choose Android App Bundle and click Next. (d) Set Module to the module named above. (e) For Key store path click Choose existing and select the file Key.jks in the AAOS folder; enter the key store password, choose the key alias and enter the key password. (f) click Next, set Destination Folder to the folder named For upload to Play Console in the AAOS folder (create it if it is missing; Android Studio usually remembers it next time), tick the build variant named above (a release variant, never debug), and click Create. (g) Tell the agent: `Bundle built`
4. **Agent: collect and check the file.** When the owner says `Bundle built`, find the newest .aab file under the folder `../For upload to Play Console` (if there is none, look in the module's release build folder). Copy it to the top level of `../For upload to Play Console` named `<App>-<code>-<name>.aab` using the real app name, version code and version name. Check it is signed and, if tools are available, check its version code. Then run `open "../For upload to Play Console"` so the folder opens in Finder, and tell the owner the exact file name to upload. Report in plain English.
5. **Owner: upload in Play Console.** (a) Open Google Play Console and choose the app. (b) In the left menu choose Test and release, then Testing, then Internal testing. (c) Click Create new release. (d) Upload the file the agent named (it is in the folder that just opened in Finder). (e) Click Next, review, then Save and roll out. If Play says the version code was already used, tell the agent: it raises the code by one and you repeat from step 3.
6. **Owner: install on the car** from the Play Store on the Polestar 3 (internal testing invitation). Debug and nightly builds get a suffix (for example `.debug`); they are emulator-only and never go on the car.
7. **Record.** Update "Last uploaded to Play" above and add an `AAOS_LOG.md` entry. Optional: `git tag play-<code>` and push the tag.

## Never

- Never upload or install a debug build on the car.
- Never change the application ID; it must match the existing Play listing.
- Never replace the key, or commit/print/share it or its passwords.
- Never put real-vehicle claims in the log unless the owner actually tested on the car.

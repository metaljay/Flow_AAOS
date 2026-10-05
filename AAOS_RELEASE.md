# Flow: releasing to the Polestar 3 via Google Play

> Fork-owned file. The release facts table is specific to this app; every other section is word for word the same in the Flow and Nuvio forks.

## Release facts

| Item | Value |
| --- | --- |
| **Last uploaded to Play** | **39 (2.2.20)**, reported installed and working on the car by the owner on 2026-10-05 |
| App name for bundle files | `Flow` |
| Release application ID | `com.JF_Flow` |
| Version file | `app/build.gradle.kts` (`versionCode`, `versionName`) |
| Build command (check) | `./gradlew :app:bundleGithubRelease` |
| Gradle-built bundle | **Unsigned, as expected.** Flow's Gradle file looks for a `release.keystore` in the repo, which does not exist and must not be created or committed. Only the Android Studio wizard signs. |
| Signing wizard: module | `app` |
| Signing wizard: build variant | `githubRelease` (the `.aab` appears in the module's `release` build folder) |
| Debug builds | `com.JF_Flow.debug` and `com.JF_Flow.nightly`: emulator only |
| Release key | `Key.jks` in the AAOS folder that contains both repos (outside git; keep a backup) |
| Signed bundles for upload | The `For upload to Play Console` folder in the AAOS folder (outside git), named `<App>-<code>-<name>.aab`, for example `Flow-39-2.2.20.aab` |
| Who signs and uploads | The owner, with Android Studio and Play Console. Agents prepare and check. |

## The version rule (hard rule): every release build raises the version

The version numbers do not matter to Play or to the owner. They only have to go **up on every upload**. There is no reliable way to track versions across chats, tools and machines, so **every time a release bundle is prepared, the version is raised automatically, whatever it is now.** The numbers will drift away from the parent project's numbers; that is accepted.

- First run `git pull`. Then set the new version code to **the larger of (the code in the version file) and ("Last uploaded to Play" above), plus 1**, and raise the version name's patch number by 1. Commit and push this change on its own ("chore(release): bump version for upload") before building.
- Do this on every release preparation, even if the repo already looks higher than the last upload and even if the previous bundle was never uploaded. Gaps are fine; going down or repeating a number is not.
- Debug and test builds used only for checking do not need a bump. Docs-only changes never need a bump.
- The parent's version numbers are ignored. After a parent update keep OUR numbers on any conflict.
- If Play says "version code already used", raise the code by one more and rebuild.
- "Last uploaded to Play" is a record and safety net, not something that must be perfect.

## Steps

This is the last stage of both the `aaos-sync` and `aaos-tweak` recipes; it can also be run alone (`.github/skills/aaos-release/SKILL.md`).

1. **Agent: prepare.** On `main`, work verified. Run `git pull`, then apply the version rule above: always raise the version in the version file, commit and push the bump on its own.
2. **Agent: check the build.** Run the build command from the table. Read the "Gradle-built bundle" row: never add passwords to `local.properties` or create keystore files in the repo. Inspect the merged release manifest: application ID, version code and name, min and target SDK, automotive and other hardware features optional.
3. **Owner: sign the bundle in Android Studio.** (a) Open this project in Android Studio. (b) In the top menu choose Build, then Generate Signed App Bundle / APK. (c) Choose Android App Bundle and click Next. (d) Set Module to the module in the table. (e) For Key store path click Choose existing and select the file Key.jks in the AAOS folder; enter the key store password, choose the key alias and enter the key password. (f) Click Next, set Destination Folder to the folder named For upload to Play Console in the AAOS folder (create it if it is missing; Android Studio usually remembers it next time), tick the build variant in the table (a release variant, never debug), and click Create. (g) Tell the agent: `Bundle built`
4. **Agent: collect and check the file.** When the owner says `Bundle built`, find the newest .aab file under `../For upload to Play Console` (if there is none, look in the module's release build folder). Copy it to the top level of `../For upload to Play Console` named `<App>-<code>-<name>.aab` with the real app name, version code and version name. Check it is signed and, if tools are available, its version code. Then run `open "../For upload to Play Console"` so the folder opens in Finder, and tell the owner the exact file name to upload.
5. **Owner: upload in Play Console.** (a) Open Google Play Console and choose the app. (b) In the left menu choose Test and release, then Testing, then Internal testing. (c) Click Create new release. (d) Upload the file the agent named (it is in the folder that just opened in Finder). (e) Click Next, review, then Save and roll out. (f) Tell the agent: `Uploaded`. If Play says the version code was already used, tell the agent instead: it raises the code by one and you repeat from step 3.
6. **Owner: install on the car** from the Play Store on the Polestar 3 (internal testing invitation). The Play Store on the car may show a temporary "(unreviewed)" name and placeholder icon; that is cosmetic (`AAOS_CAR_NOTES.md`).
7. **Agent: record (optional).** When the owner says `Uploaded`, update "Last uploaded to Play" above and add an `AAOS_LOG.md` entry (`.github/skills/aaos-uploaded/SKILL.md`). The version rule does not depend on it.

## Never

- Never upload or install a debug or nightly build on the car.
- Never change the application ID; it must match the existing Play listing.
- Never replace the key, or commit, print or share it or its passwords.
- Never put real-car claims in the log unless the owner actually tested on the car.

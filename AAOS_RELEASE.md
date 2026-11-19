# Flow: releasing to the Polestar 3 via Google Play

> Fork-owned file. The release facts table is specific to this app; every other section is word for word the same in the Flow and Nuvio forks.

## Release facts

| Item | Value |
| --- | --- |
| **Last uploaded to Play** | **48 (2.2.29)**, on Play internal testing (confirmed by `scripts/aaos/release.sh --check` on 2026-11-19; not yet tested on the car) |
| App name for bundle files | `Flow` |
| Release application ID | `com.JF_Flow` |
| Version file | `app/build.gradle.kts` (`versionCode`, `versionName`) |
| Build command (check) | `./gradlew :app:bundleGithubRelease` |
| Gradle-built bundle | **Unsigned by default.** Flow's Gradle file looks for a `release.keystore` in the repo, which does not exist and must not be created or committed. Only `scripts/aaos/release.sh` (which sets `AAOS_KEYCHAIN_SIGNING=1`, C13) or the Android Studio wizard signs. |
| Signing wizard: module | `app` |
| Signing wizard: build variant | `githubRelease` (the `.aab` appears in the module's `release` build folder) |
| Debug builds | `com.JF_Flow.debug` and `com.JF_Flow.nightly`: emulator only |
| Release key | `Key.jks` in the `Play Console` folder next to both repos (outside git; Time Machine keeps a copy off the Mac) |
| Signed bundles for upload | The app's folder in the `Play Console` folder next to both repos (outside git): one file per release, named `<App>-v<name>-build<code>.aab`, for example `Flow-v2.2.20-build39.aab`. Android Studio saves into its `Android Studio output` sub-folder first |
| Who signs and uploads | On the owner's go, the agent runs `scripts/aaos/release.sh`: it signs with the upload key from the Mac's login Keychain and releases to Internal testing only. The owner sets up the Keychain items (`scripts/aaos/setup-release.sh`) and the Play key file once. Fallback: the owner signs with Android Studio and uploads in Play Console. |

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
3. **Agent: release.** On the owner's go, run `scripts/aaos/release.sh`. It stops if there are uncommitted changes or the commit is not on `origin/main`, runs the lint and unit tests, builds the bundle signed from the Keychain, checks the signature, uploads it and releases it to Internal testing only. It then files the bundle and its mapping file in the app's folder in the Play Console folder and makes the local tag `play-<code>`. `--check` only signs in to Play and looks; `--dry-run` does everything except release. If it stops, the message says why and nothing was released. Push the tag afterwards.
4. **Fallback (if the script cannot run): the owner signs in Android Studio.** (a) Open this project in Android Studio. (b) In the top menu choose Build, then Generate Signed App Bundle / APK. (c) Choose Android App Bundle and click Next. (d) Set Module to the module in the table. (e) For Key store path click Choose existing and select the file Key.jks in the Play Console folder next to this project's folder; enter the key store password, choose the key alias and enter the key password. (f) Click Next, set Destination Folder to the folder named Android Studio output inside this app's folder in that Play Console folder (create it if it is missing; Android Studio usually remembers it next time), tick the build variant in the table (a release variant, never debug), and click Create. (g) Tell the agent: `Bundle built`. The agent then finds the newest .aab file under `../Play Console/<App>/Android Studio output`, checks inside it that the application ID, version code and version name are this release's and that it is signed, moves it up to `../Play Console/<App>/` named `<App>-v<name>-build<code>.aab` (the `mapping.txt` beside it becomes `<App>-v<name>-build<code>-mapping.txt` and stays in the output folder), runs `open "../Play Console/<App>"` and tells the owner the exact file name.
5. **Fallback: the owner uploads in Play Console.** (a) Open Google Play Console and choose the app. (b) In the left menu choose Test and release, then Testing, then Internal testing. (c) Click Create new release. (d) Upload the file the agent named. (e) Click Next, review, then Save and roll out. (f) Tell the agent: `Uploaded`. If Play says the version code was already used, tell the agent instead: it raises the code by one and the release is repeated.
6. **Owner: install on the car** from the Play Store on the Polestar 3 (internal testing invitation). The Play Store on the car may show a temporary "(unreviewed)" name and placeholder icon; that is cosmetic (`AAOS_CAR_NOTES.md`).
7. **Agent: record (optional).** When `scripts/aaos/release.sh` ends with "Released version N" (or the owner says `Uploaded` after the fallback), update "Last uploaded to Play" above and record it in the session log (`AAOS_FORK.md` section 10) (`.github/skills/aaos-uploaded/SKILL.md`). The version rule does not depend on it.

## Never

- Never upload or install a debug or nightly build on the car.
- Never change the application ID; it must match the existing Play listing.
- Never replace the key, or commit, print, read, type or share it, its passwords or the Play key file. Only Internal testing is ever written; never change Play Console settings.
- Never put real-car claims in the log unless the owner actually tested on the car.

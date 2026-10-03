---
name: aaos-release
description: Use when the owner asks to prepare a Google Play release bundle for the Polestar 3.
---
The owner is not a coder. Follow AAOS_FORK.md section 4b (talking to the owner) for everything you say.

Read AAOS_FORK.md and AAOS_RELEASE.md, then:

1. Do steps 1 and 2 of AAOS_RELEASE.md yourself: make sure the version code is higher than "Last uploaded to Play" (raise it, commit and push if it is not), build the release bundle, and inspect the merged release manifest (application ID, version code and name, min/target SDK, automotive and camera features). Never sign, upload or touch any key, keystore or password.
2. Add an AAOS_LOG.md entry (exactly what you ran and what you did NOT verify), commit and push.
3. Report in plain English. Then give the owner their part as a numbered click-by-click list with every value filled in: the expected version code and name, the module and build variant from AAOS_RELEASE.md. The finished .aab file will appear in the folder named For upload to Play Console in the AAOS folder. End with the exact message for them to paste back: `Bundle built`
4. When the owner replies `Bundle built`: find the newest .aab file under `../For upload to Play Console` (if there is none, look in the module's release build folder). Copy it to the top level of `../For upload to Play Console` named `<App>-<code>-<name>.aab` using the real app name, version code and version name. Check it is signed (`jarsigner -verify` with the real path) and, if bundletool or aapt2 is available, check its version code; otherwise say that part was not checked. Then run `open "../For upload to Play Console"` so the folder opens in Finder, tell the owner the exact file name to upload, and give them the Play Console click path from AAOS_RELEASE.md. Tell them to paste `/aaos-uploaded <code>` (real code filled in) after Play accepts the upload.

Never put personal names, usernames, emails or absolute paths into any file, commit message or log.

---
name: aaos-release
description: Use to prepare a Google Play release bundle for the Polestar 3. This is the last stage of aaos-sync and aaos-tweak and can also be run alone.
---
The owner is not a coder. Follow AAOS_FORK.md section 4b (talking to the owner) for everything you say.

Read AAOS_FORK.md and AAOS_RELEASE.md, then:

1. Run `git pull`. Apply the version rule in AAOS_RELEASE.md: ALWAYS raise the version. New code = the larger of (the code in the repo) and ("Last uploaded to Play"), plus 1; raise the version name's patch number by 1. Commit and push that bump on its own ("chore(release): bump version for upload") before building.
2. Build the release bundle with the command in AAOS_RELEASE.md and inspect the merged release manifest: application ID, version code and name, min/target SDK, automotive and camera features. Never sign, upload or touch any key, keystore or password.
3. Add an AAOS_LOG.md entry (exactly what you ran and what you did NOT verify), commit and push.
4. Report in plain English. Then give the owner their part as a numbered click-by-click list with every value filled in: the new version code and name the bundle will carry, the module and build variant from AAOS_RELEASE.md, and the destination folder named For upload to Play Console in the AAOS folder. End with the exact message for them to paste back: `Bundle built`
5. When the owner replies `Bundle built`: do the "collect and check the file" step of AAOS_RELEASE.md (find the newest .aab, copy it to the For upload to Play Console folder with the name <App>-<code>-<name>.aab using the real values, check it is signed and, if tools are available, its version code, then open the folder in Finder). Tell the owner the exact file name to upload and give them the Play Console click path from AAOS_RELEASE.md.
6. Finish by telling the owner that when Play accepts the upload there is nothing they have to do; if they want the optional record, they can paste the aaos-uploaded message.

Never put personal names, usernames, emails or absolute paths into any file, commit message or log.

---
name: aaos-release
description: Use to prepare a Google Play release bundle for the Polestar 3. This is the last stage of aaos-sync and aaos-tweak and can also be run alone.
---
The owner has no coding experience. Follow `AAOS_FORK.md` section 6 (talking to the owner) for everything you say.

Read `AAOS_RELEASE.md` (its "Release facts" table holds every app-specific value you need), then:

1. Do step 1 of `AAOS_RELEASE.md`: `git pull`, then ALWAYS raise the version by the version rule, and commit and push that bump on its own before building.
2. Do step 2: run the build command from the table and inspect the merged release manifest. Never sign, upload or touch any key, keystore or password.
3. Add a short `AAOS_LOG.md` entry (at most three lines: version, what it contains, what you did NOT verify), commit and push.
4. Report in plain English. Then give the owner step 3 of `AAOS_RELEASE.md` as a numbered click-by-click list with every value filled in: the new version code and name the bundle will carry, the module and build variant from the table, and the destination folder named For upload to Play Console in the AAOS folder. End with the exact message for them to paste back: `Bundle built`
5. When the owner replies `Bundle built`: do step 4 (collect, rename, check and open the folder). Tell the owner the exact file name to upload and give them step 5 (the Play Console clicks), ending with the message to paste back: `Uploaded`
6. When the owner replies `Uploaded` (now or in a later chat), follow `.github/skills/aaos-uploaded/SKILL.md`. If they never reply, nothing breaks: the version rule does not depend on it.

Follow the safety rails in `AAOS_FORK.md` section 5.

---
name: aaos-release
description: Use to prepare a Google Play release bundle for the Polestar 3. This is the last stage of aaos-sync and aaos-tweak and can also be run alone.
---
The owner has no coding experience. Follow `AAOS_FORK.md` section 6 (talking to the owner) for everything you say.

Read `AAOS_RELEASE.md` (its "Release facts" table holds every app-specific value you need), then:

1. Do step 1 of `AAOS_RELEASE.md`: `git pull`, then ALWAYS raise the version by the version rule, and commit and push that bump on its own before building.
2. Do step 2: run the build command from the table and inspect the merged release manifest. Never touch any key, keystore, password or the Play key file.
3. Record in the session log (`AAOS_FORK.md` section 10) (at most three lines: version, what it contains, what you did NOT verify). Commit and push any repo doc changes.
4. Report in plain English and ask for the owner's go. On their go, do step 3 of `AAOS_RELEASE.md`: run `scripts/aaos/release.sh` and tell them what it printed. Push the `play-<code>` tag afterwards, and record the release as in step 7. Only if the script cannot run, use the fallback: give the owner step 4 of `AAOS_RELEASE.md` as a numbered click-by-click list with every value filled in (new version code and name, the module and build variant from the table, the app's Android Studio output folder in the Play Console folder), ending with the message to paste back: `Bundle built`. Then do the agent half of step 4, and give them step 5 (the Play Console clicks), ending with `Uploaded`.
5. If the owner replies `Uploaded` (fallback, now or in a later chat), follow `.github/skills/aaos-uploaded/SKILL.md`. If they never reply, nothing breaks: the version rule does not depend on it.

Follow the safety rails in `AAOS_FORK.md` section 5.

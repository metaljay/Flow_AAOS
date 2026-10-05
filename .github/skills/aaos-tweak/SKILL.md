---
name: aaos-tweak
description: Use when the owner wants to change, tweak, improve or fix the app for the car. Makes the change, records it, and prepares the release.
---
The owner has no coding experience. Follow `AAOS_FORK.md` section 6 (talking to the owner) for everything you say.

Read `AGENTS.md`, `AAOS_FORK.md`, `AAOS_CAR_NOTES.md`, `AAOS_UPSTREAM_SYNC.md` ("Checks to run" and "Hotspots") and `AAOS_RELEASE.md`. The tweak is described in the owner's message; ask one short question only if it is unclear. If the owner says the change was already made earlier (in this or another chat), do not redo it: work out what changed from `git status`, `git diff` and `git log -5`, compare it with any summary they gave, and report any difference.

1. Make the change, keeping it as small as possible and following the repo's own rules. Prefer small edits to parent-owned files so future parent updates conflict less.
2. Run every command under "Checks to run" in `AAOS_UPSTREAM_SYNC.md` exactly as written. If something fails, fix it; if you cannot, stop, explain in plain English, and do not push broken code.
3. Car-like check, if an emulator is available ("Testing like the car" in `AAOS_CAR_NOTES.md`): install the debug build, open the app **from the emulator's app list** (not with adb), look at the screen you changed, and check nothing tappable sits closer than 16 dp to the left or right edge. If the change touches media, playback, the manifest or launch behaviour, also check the home screen media card, then reboot the emulator and check the app icon and card again. If you cannot run the emulator, say so.
4. Update the docs (`AAOS_FORK.md` section 10). If the change adds, alters or removes a customisation: update section 7 and its Part 2 block in `AAOS_FORK.md` (a new customisation gets the next free ID and all four headings), the Hotspots table in `AAOS_UPSTREAM_SYNC.md`, and, if the owner would notice it, the "What's different in the car" table in `README.md`. When a number changes (a size, an offset), search `README.md`, the recipes and every `AAOS_*.md` file for the old number and update every mention. If you learned something about the car, the launcher, the media card, Play or the display, add it to `AAOS_CAR_NOTES.md` in both repos (Flow and Nuvio sit next to each other in the AAOS folder).
5. Add an `AAOS_LOG.md` entry at the top (below the intro), keeping the log to 15 entries: what changed and why, the commands run and their results, "Verified:" and "NOT verified:" (emulator and real car are not verified unless you or the owner actually tested there).
6. Stage only the files you changed, by name. Commit with a clear message and push to origin main. No force-push.
7. Tell the owner they can look at the change in the emulator first (the steps under "What to test" in the approval report layout in `AAOS_UPSTREAM_SYNC.md`), but it is optional; the real test is the car. Then continue straight on with `.github/skills/aaos-release/SKILL.md` from its first step. Do not raise the version yourself here; the release recipe does it.
8. Finish with the standard report from `AAOS_FORK.md` section 6.

Follow the safety rails in `AAOS_FORK.md` section 5.

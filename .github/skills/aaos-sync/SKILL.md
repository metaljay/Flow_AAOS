---
name: aaos-sync
description: Use when the owner asks to update the app from its parent (upstream) project, or to check whether the parent has anything new. Merges the parent's changes, redoes every car customisation in the new code, reports for approval, then prepares the release.
---
The owner has no coding experience and relies on you to carry every car customisation into the parent's new code. Follow `AAOS_FORK.md` section 6 (talking to the owner) for everything you say.

Read `AAOS_FORK.md` (all of it, including the Part 2 customisation details), `AAOS_CAR_NOTES.md`, `AAOS_UPSTREAM_SYNC.md` and `AAOS_RELEASE.md`. Run every command yourself with real dates and values; never ask the owner to run commands for this.

1. Do steps 0 to 2 of `AAOS_UPSTREAM_SYNC.md` (safety tag, find what is new, assess). If there is nothing new, say so in plain English and stop.
   If the owner only asked to **check** for updates without changing anything, stop here: give them parts 1 to 3 of the approval report (what's new, which customisations the parent touched, risks), say clearly that it is a preview and nothing was merged, and offer the prompt to run the full update.
2. Do steps 3 to 7 (sync branch, merge, redo every customisation, restore fork-owned values, verify). If you can start the emulator, do the car-like check yourself (open the app from the emulator's app list, not with adb; follow "Testing like the car" in `AAOS_CAR_NOTES.md`) and say what you saw.
3. Step 8: build the debug app, write the approval report exactly as laid out in `AAOS_UPSTREAM_SYNC.md`, and STOP. Do not merge into `main`.
4. On `approve sync`: do steps 9 to 11 (land, clean up, record), then continue straight on with `.github/skills/aaos-release/SKILL.md` from its first step. On `cancel sync`: delete the sync branch and leave `main` untouched. On `problem: ...`: investigate and fix on the sync branch, re-verify, and send a fresh report. If the owner returns in a new chat, find the open `sync/` branch, re-run the checks quickly, then continue.
5. Finish with the standard report from `AAOS_FORK.md` section 6.

Follow the safety rails in `AAOS_FORK.md` section 5: never force-push, never push to the parent, never commit keys or passwords.

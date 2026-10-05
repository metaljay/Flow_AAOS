---
name: aaos-sync
description: Use when the owner asks to pull the latest parent (upstream) changes into this fork, test them, and prepare the release.
---
The owner is not a coder. Follow AAOS_FORK.md section 4b (talking to the owner) for everything you say.

Read AAOS_FORK.md, AAOS_CAR_NOTES.md, AAOS_UPSTREAM_SYNC.md and AAOS_RELEASE.md. This recipe takes a parent update all the way to a release bundle that is ready to upload.

1. Run steps 0 to 6 of AAOS_UPSTREAM_SYNC.md yourself (safety tag, fetch, sync branch, merge, resolve conflicts, keep our version numbers and fork-only values, verify). Fill in real dates and values; never ask the owner to run commands for this.
2. If there are no new parent commits, say so in plain English and stop.
3. Build the debug version of the app listed in "Checks to run". Then tell the owner how to test it on the emulator, with every value filled in: (a) open this project in Android Studio; (b) in the device list at the top choose the emulator named Automotive_Large_Portrait (if it is missing: Tools, Device Manager, Create Device, Automotive); (c) click the green Run button; (d) look at: the bottom navigation is at the bottom, text and buttons are large, the app opens, something plays, and each area the parent update touched (name those areas in everyday words). If you can start the emulator and look yourself, do that too (open the app from the emulator's app list, not with adb; check the media card; follow "Testing like the car" in AAOS_CAR_NOTES.md) and say what you saw.
4. STOP before merging into main. Give the owner a plain-English review: roughly how many parent changes came in, which areas of the app they touch, which of our car customisations were affected and how you kept them, and which checks passed, failed or were not run. Ask them to reply with exactly one of: `approve sync`, `cancel sync`, or `problem: <what you saw>`.
5. On `approve sync`: do steps 8 to 10 of AAOS_UPSTREAM_SYNC.md (merge into main, push, delete the sync branch, write the AAOS_LOG.md entry, commit and push). Then continue straight on by following .github/skills/aaos-release/SKILL.md from its first step. On `cancel sync`: delete the sync branch and leave main untouched. On `problem`: investigate and fix on the sync branch, then repeat from step 3. If the owner returns in a new chat, find the open sync/ branch, re-run the checks quickly, then continue.
6. Finish with the standard report from section 4b.

Never force-push, never push to the parent, never commit keys or passwords. Never put personal names, usernames, emails or absolute paths into any file, commit message or log.

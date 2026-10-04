---
name: aaos-tweak
description: Use when the owner wants to change, tweak, improve or fix the app for the car. Makes the change, records it, and prepares the release.
---
The owner is not a coder. Follow AAOS_FORK.md section 4b (talking to the owner) for everything you say.

Read AGENTS.md, AAOS_FORK.md, AAOS_UPSTREAM_SYNC.md and AAOS_RELEASE.md. The tweak is described in the owner's message; ask one short question only if it is unclear. If the owner says the change was already made earlier (in this or another chat), do not redo it: work out what changed from `git status`, `git diff` and `git log -5`, and compare it with any summary they gave; report any difference.

1. Make the change, keeping it as small as possible and following the repo's own rules. Prefer small edits to upstream-owned files so future parent updates conflict less.
2. Run every command listed under "Checks to run" in AAOS_UPSTREAM_SYNC.md. If something fails, fix it; if you cannot, stop, explain in plain English, and do not push broken code.
3. If the change adds, alters or removes a car-specific customisation, update Part 2 of AAOS_FORK.md, the invariants in section 5, and the hotspot table in AAOS_UPSTREAM_SYNC.md. When a number changes (a size, an offset), search README.md and every AAOS_*.md file for the old number and update every mention so the docs never contradict each other.
4. Add an AAOS_LOG.md entry at the top (below the intro): what changed and why, the commands run and their results, "Verified:" and "NOT verified:" (emulator and real car are not verified unless the owner says they tested).
5. Stage only the files you changed, by name. Commit with a clear message and push to origin main. No force-push.
6. Tell the owner they can look at the change in the emulator first (same clicks as step 3 of .github/skills/aaos-sync/SKILL.md) but it is optional; the real test is the car. Then continue straight on by following .github/skills/aaos-release/SKILL.md from its first step. Do not raise the version yourself here; the release recipe does it.
7. Finish with the standard report from section 4b.

Never put personal names, usernames, emails or absolute paths into any file, commit message or log.

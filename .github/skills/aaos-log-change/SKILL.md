---
name: aaos-log-change
description: Use after any change or bug fix to the app, to record it properly (log, customisation list, version code, build check, push).
---
The owner is not a coder. Follow AAOS_FORK.md section 4b (talking to the owner) for everything you say.

Read AAOS_FORK.md, AAOS_RELEASE.md and AAOS_UPSTREAM_SYNC.md, then:

1. Work out what changed. Use this chat if it contains the change; if it does not (for example this is a new chat), use `git status`, `git log -5` and `git diff` instead. Summarise the change in everyday words.
2. If the change adds, alters or removes a car-specific customisation, update Part 2 of AAOS_FORK.md. If it must survive future parent updates, also update the invariants (section 5) and, if it touches a new file area, the hotspot table in AAOS_UPSTREAM_SYNC.md.
3. Check the app still builds, using the "Checks to run" in AAOS_UPSTREAM_SYNC.md. If anything fails, stop, explain in plain English, and do not push broken code.
4. Make sure the version code in the repo is higher than "Last uploaded to Play" in AAOS_RELEASE.md. Raise it (and the name's patch number) only if it is not already higher.
5. Add an AAOS_LOG.md entry at the top (below the intro): what changed and why, the commands you ran and their results, and "Verified:" / "NOT verified:" (emulator and real car are not verified unless the owner says they tested).
6. Stage only the files you changed, by name. Commit with a clear message and push to origin main. No force-push.
7. Finish with the standard report from section 4b. For "What you need to do next": if app code changed, say "When you are ready to ship, paste: Follow the instructions in .github/skills/aaos-release/SKILL.md exactly." Otherwise "nothing".

Never put personal names, usernames, emails or absolute paths into any file, commit message or log.

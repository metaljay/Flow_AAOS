---
name: aaos-sync
description: Use when the owner asks to pull, sync or merge the latest parent (upstream) changes into this fork.
---
The owner is not a coder. Follow AAOS_FORK.md section 4b (talking to the owner) for everything you say.

Read AAOS_FORK.md and AAOS_UPSTREAM_SYNC.md, then:

1. Run steps 0 to 6 of AAOS_UPSTREAM_SYNC.md yourself (safety tag, fetch, sync branch, merge, resolve conflicts, restore fork-only values, verify). Fill in real dates and values; never ask the owner to run anything for this.
2. If there are no new parent commits, say so in plain English and stop.
3. STOP before merging into main. Give the owner a plain-English review: roughly how many parent changes came in, which areas of the app they touch (everyday words), which of our car customisations were affected and how you kept them, and which checks passed, failed or were not run. Then ask them to reply with exactly one of these: `approve sync` or `cancel sync`.
4. On `approve sync`: do steps 7 to 9 of AAOS_UPSTREAM_SYNC.md (merge into main, push, delete the sync branch, write the AAOS_LOG.md entry, commit and push). On `cancel sync`: delete the sync branch and leave main untouched. If the owner returns in a new chat, find the open sync/ branch, re-run the checks quickly, then continue.
5. Finish with the standard report from section 4b. Say whether the Play version code must go up (yes or no), and give the owner the exact next message to paste to start a release: Follow the instructions in .github/skills/aaos-release/SKILL.md exactly.

Never force-push, never push to the parent, never commit keys or passwords. Never put personal names, usernames, emails or absolute paths into any file, commit message or log.

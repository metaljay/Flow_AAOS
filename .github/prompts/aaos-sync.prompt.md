---
description: Pull the latest parent (upstream) changes safely, following AAOS_UPSTREAM_SYNC.md
---
Read AAOS_FORK.md and AAOS_UPSTREAM_SYNC.md, then pull the latest parent (upstream) changes into this fork, following AAOS_UPSTREAM_SYNC.md exactly, step by step.

- Use a sync/ branch. Do not touch main until every check passes.
- Before merging, tell me how many parent commits are new. If there are none, stop.
- Re-apply every customisation in the hotspot table and every invariant in AAOS_FORK.md section 5. Restore the fork-owned values: version code above "Last uploaded to Play", application ID, README banner, and the AAOS block at the top of AGENTS.md.
- Never force-push, never push to upstream, never commit keys or passwords. Never put personal names, usernames, emails or absolute paths into any file, commit message or log.
- Run the checks in the sync file. Say exactly what passed, what failed and what was not run.
- When everything passes: merge to main, push, and delete the sync branch (local and remote).
- Add an AAOS_LOG.md entry: the parent commit reached, the conflicts and how you resolved them, what was verified and what was NOT verified. Commit and push it.
- Finish with a plain-English report: what changed, any risks, and whether the Play version code must go up before the next release.

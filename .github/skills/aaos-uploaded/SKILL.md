---
name: aaos-uploaded
description: Optional housekeeping. Use when the owner confirms a version was uploaded to Google Play, to record it.
---
The owner has no coding experience. Follow `AAOS_FORK.md` section 6 (talking to the owner) for everything you say.

This is optional housekeeping: the version rule does not depend on it. Read `AAOS_RELEASE.md`. If you do not know the version code and version name that were uploaded and accepted (for example from the release you just prepared), ask for them in one short question.

- In `AAOS_RELEASE.md` update the "Last uploaded to Play" row (code and name, confirmed by the owner today; add "and working on the car" only if the owner said so).
- Add an `AAOS_LOG.md` entry: the upload was confirmed by the owner. Do not claim any emulator or real-car verification unless the owner says it was done.
- Optionally create and push a git tag named `play-<code>`, with the real code filled in.
- Do not change app code or the version file. Stage only the docs by name, commit and push to origin main, no force-push.
- Finish with the standard report from `AAOS_FORK.md` section 6. "What you need to do next" is "nothing" unless Play rejected the upload.

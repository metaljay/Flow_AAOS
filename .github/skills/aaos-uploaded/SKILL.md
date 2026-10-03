---
name: aaos-uploaded
description: Use when the owner confirms a version was uploaded to Google Play, to record it.
---
The owner is not a coder. Follow AAOS_FORK.md section 4b (talking to the owner) for everything you say.

Read AAOS_RELEASE.md. If the owner has not told you the version code and version name that were uploaded and accepted, ask for them in one short question.

- In AAOS_RELEASE.md update the "Last uploaded to Play" row (code and name, confirmed by the owner today) and set the "Code in the repo now" row to say it equals the last upload and must be raised before the next release.
- Add an AAOS_LOG.md entry: the upload was confirmed by the owner. Do not claim any emulator or real-car verification unless the owner says it was done.
- Optionally create and push a git tag named play-<code>, with the real code filled in.
- Do not change app code or the version files. Stage only the docs by name, commit and push to origin main, no force-push.
- Finish with the standard report from section 4b. "What you need to do next" is "nothing" unless Play rejected the upload.

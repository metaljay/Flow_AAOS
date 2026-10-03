---
description: Record a confirmed Google Play upload (keeps the version-code rule accurate)
---
Read AAOS_RELEASE.md. If I have not told you, ask me which version code and version name were uploaded to Play and accepted.

- In AAOS_RELEASE.md update the "Last uploaded to Play" row (code and name, confirmed by the owner today) and set the "Code in the repo now" row to say it equals the last upload and must be raised before the next release.
- Add an AAOS_LOG.md entry: the upload was confirmed by the owner. Do not claim any emulator or real-car verification unless I tell you it was done.
- Optionally create and push a git tag named play-<code>.
- Do not change app code or the version files. Stage only the docs by name, commit and push to origin main, no force-push, and report in plain English.

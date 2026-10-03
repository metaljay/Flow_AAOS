---
description: Prepare a Play release (steps 1 and 2 of AAOS_RELEASE.md); the owner signs and uploads
---
Read AAOS_FORK.md and AAOS_RELEASE.md. Do steps 1 and 2 of AAOS_RELEASE.md only.

- Check the version code in the repo is strictly higher than "Last uploaded to Play". If it is not, raise the code (and the name's patch number), commit and push.
- Build the release bundle with the command in AAOS_RELEASE.md and inspect the merged release manifest: application ID, version code and name, min/target SDK, automotive and camera features.
- Do NOT sign, upload or touch any key, keystore or password.
- Add an AAOS_LOG.md entry listing exactly what you ran and what you did NOT verify (emulator, real car). Commit and push it.
- Report in plain English: the exact version code and name to expect in the signed bundle, and anything that looks wrong.

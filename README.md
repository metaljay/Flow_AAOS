<div align="center">

<strong>🚗 Android Automotive OS (AAOS) Fork</strong>

<br><br>

<img src="Assets/logo.png" alt="Flow logo" width="112">

# Flow for AAOS

### A large-screen, in-car adaptation of Flow for Android Automotive OS

Built with the Polestar 3 in mind, with AAOS integration and thoughtful changes for use on a
vehicle display.

<br>

[![Android Automotive OS](https://img.shields.io/badge/Platform-Android_Automotive_OS-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/training/cars)
[![Upstream Flow](https://img.shields.io/badge/Forked_from-Flow-4285F4?style=for-the-badge&logo=github&logoColor=white)](https://github.com/A-EDev/Flow)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-orange?style=for-the-badge&logo=gnu&logoColor=white)](License)

<br>

[AAOS rules](AAOS_FORK.md) · [Release steps](AAOS_RELEASE.md) · [Upstream project](https://github.com/A-EDev/Flow) · [License](License)

</div>

---

## 🚘 About this fork

Flow for AAOS is a community fork of [A-EDev/Flow](https://github.com/A-EDev/Flow), adapted to run
on Android Automotive OS (AAOS). It keeps Flow's core experience while adding vehicle-display
integration and larger-screen refinements, with the Polestar 3 as the primary target.

This project targets **Android Automotive OS**, the operating system built into compatible
vehicles. It is not an Android Auto version.

## ✨ What’s different

| | AAOS-focused changes |
| --- | --- |
| 🧭 | **Navigation stays at the bottom** at every window size, keeping the main destinations in a consistent place. |
| 👀 | **More legible on a large display**, with enlarged Material 3 typography, icons, touch targets and player controls. |
| 📐 | **Larger app bars**, with a 72 dp minimum height for top and bottom bars. |
| 🚗 | **AAOS system integration**, including automotive app metadata, a car-launcher entry and media browsing support. |
| 📷 | **Camera features remain optional**, so a camera is not required for device compatibility. |
| ▶️ | **Traditional red play-mark launcher icon** and the fork’s dedicated Play application identity. |

These changes are specific to the automotive experience; they are not a promise of compatibility
with every vehicle, display configuration or AAOS version.

## 🧭 Internal testing and installation

**There are no public GitHub release downloads for this fork.** The intended distribution path is
Google Play internal testing: maintainers build a Play-compatible Android App Bundle and publish
it to the private internal testing track. This is how this custom AAOS app is delivered to invited
testers through Google Play.

To install or update the app, you need access to the internal test and a compatible AAOS device.
After accepting the test invitation, use the Google Play Store on the vehicle to install the app.
The app is not available as a public Play Store listing.

## 🛠️ Building from source

The repository contains the source code; a local build is not a public release. To create the
GitHub-flavor bundle used for Play testing:

```bash
./gradlew :app:bundleGithubRelease
```

Uploading to Play requires the authorized release-signing configuration and access to the Play
Console internal testing track. Do not distribute an unsigned or locally signed build as an
official test release.

## 🔄 Keeping this fork up to date with its parent

This fork follows [Flow](https://github.com/A-EDev/Flow). `main` holds the parent's code plus the AAOS customisations. Parent updates are never merged straight into `main`: an AI agent first applies them on a temporary `sync/` branch, re-applies the car customisations, runs checks, and waits for the owner's approval. Nothing is ever pushed to the parent. No git knowledge is needed: you paste short messages to an AI agent (GitHub Copilot, Codex or Gemini) opened in this repository, and it does the work and reports back in plain English.

### 1. Check for and review a parent update
Paste this to your agent:
```
Follow the instructions in .github/skills/aaos-sync/SKILL.md exactly.
```
In GitHub Copilot you can type `/aaos-sync` instead. The agent tells you whether anything is new. If it is, it prepares and checks the update, then **stops and explains the changes in plain English**. Nothing reaches `main` until you reply `approve sync`. Reply `cancel sync` to discard the update safely. To see more detail first, ask: "Explain the three biggest changes and whether any touch the car customisations."

### 2. Prepare a release
```
Follow the instructions in .github/skills/aaos-release/SKILL.md exactly.
```
The agent builds and checks the release bundle, then gives you the exact clicks to sign it in Android Studio and upload it to Google Play Internal testing. Every Play upload needs a higher version code than the last one, whatever the parent's version says; the agent handles this.

### 3. Record the upload
After Play accepts the upload, paste this (replace 33 with the version code you uploaded):
```
Follow the instructions in .github/skills/aaos-uploaded/SKILL.md exactly. The version code uploaded was 33.
```

### What protects the customisations
- `AAOS_FORK.md`: rules, safety rails and the customisations that must survive every merge.
- `AAOS_UPSTREAM_SYNC.md`: the step-by-step procedure, the files most likely to conflict, and how to roll back.
- `AAOS_RELEASE.md`: release steps and the version-code rule. `AAOS_LOG.md`: dated history of what changed and what was verified.
- `AGENTS.md` is the entry point for AI agents; the recipes live in `.github/skills/`.

## 📚 AAOS rules and fork inventory

For the fork rules, invariant list, implementation locations, rationale and upstream-migration
requirements, see **[AAOS_FORK.md](AAOS_FORK.md)**. It is the reference for understanding which
AAOS behaviors are intentional and must be preserved.

For release procedure and Play version rules, see **[AAOS_RELEASE.md](AAOS_RELEASE.md)**.

## 📄 License

Flow is distributed under the [GNU General Public License v3.0](License). See the license file for
the terms that apply to this fork and its upstream project.

<div align="center">

<strong>🚗 Android Automotive OS (AAOS) Fork</strong>

<br><br>

<img src="Assets/logo.png" alt="Flow logo" width="112">

# Flow for AAOS

### Flow, the YouTube app, adapted for the Polestar 3's built-in Android Automotive screen

<br>

[![Android Automotive OS](https://img.shields.io/badge/Platform-Android_Automotive_OS-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/training/cars)
[![Forked from Flow](https://img.shields.io/badge/Forked_from-Flow-4285F4?style=for-the-badge&logo=github&logoColor=white)](https://github.com/A-EDev/Flow)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-orange?style=for-the-badge&logo=gnu&logoColor=white)](License)

[Original Flow project](https://github.com/A-EDev/Flow) · [Car customisations](AAOS_FORK.md#part-2-customisation-details) · [License](License)

</div>

---

## 🚘 What this is

Flow is a free YouTube client (watch videos, Shorts and music without the official YouTube app). This is a personal copy of it (a "fork"), changed to work well on the screen of a Polestar 3, which runs **Android Automotive OS** (AAOS): Android built into the car itself. It is not an Android Auto app (Android Auto mirrors a phone onto the car screen).

Changes from the original Flow project (the "parent") are brought into this copy from time to time, and the car changes are redone on top each time, so the app stays up to date without losing them.

## ✨ What's different in the car

| | Change | Details |
| --- | --- | --- |
| 🧭 | **Navigation stays at the bottom**, even on the car's wide screen. | [More](AAOS_FORK.md#c4-bottom-navigation-at-every-window-size) |
| 👀 | **Bigger text, icons, buttons and player controls**, easy to read and tap at a glance. | [More](AAOS_FORK.md#c5-large-display-legibility) |
| 👆 | **Nothing you tap hides under the screen's rounded edges.** | [More](AAOS_FORK.md#c6-display-safe-area) |
| 🚗 | **Works as a car app**: it appears in the car's app list and the car lets its screens run. | [More](AAOS_FORK.md#c2-aaos-manifest) |
| 🎵 | **The car's home screen media card** shows what Flow is playing. | [More](AAOS_FORK.md#c8-car-media-card) |
| 📷 | **Installs without a camera** (the car has none for apps). | [More](AAOS_FORK.md#c3-camera-optional) |
| ▶️ | **Red YouTube-style play icon** in the car's app list. | [More](AAOS_FORK.md#c7-launcher-icon) |
| 📦 | **Updates come through your own Play Store listing**, so they reach the car. | [More](AAOS_FORK.md#c1-play-identity) |

**Known limit:** after a full restart of the car, the home screen media card stays blank. That is the price of the app icon opening the app properly; see the [car notes](AAOS_CAR_NOTES.md).

## 📲 Getting it on the car

There are no public downloads. The app reaches the car only through **Google Play Internal testing** (a private test track on your own Play listing):

1. Accept the internal test invitation for Flow with the Google account used in the car.
2. Open the Play Store in the car and install or update Flow.

The car's Play Store may show a temporary name ending in "(unreviewed)" and a placeholder icon. That is normal for an internal test, and installs and updates still work.

## 🛠️ What do you want to do?

There are only two jobs, and both end with the new version on your car. Each is done by an AI assistant (Claude Code, GitHub Copilot, Codex or Gemini). Open it in the **Flow** folder (this app's folder), paste the prompt, and follow what it tells you. It explains everything in plain English, runs the commands itself, and only asks you to do what only you can do: approve, sign in Android Studio, upload to Play, and test in the car. Each prompt only affects this app; do the other app separately in its own folder.

In Claude Code or GitHub Copilot you can type the short command shown instead of pasting the prompt.

### 1. Update from the original app (`/aaos-sync`)

```
Follow the instructions in .github/skills/aaos-sync/SKILL.md exactly.
```

The assistant brings in the parent's changes on a temporary copy, redoes every car change in the new code, builds a test version, and sends you a report with four parts: **what's new for you**, **how each of your car changes was kept or adapted** (or if any is at risk), **risks with a recommendation**, and **what to test**. Nothing reaches the real app until you reply with one of these:

- `approve sync`: it goes ahead and releases it to the car (below).
- `cancel sync`: everything is thrown away and nothing changes.
- `problem: ` followed by what you saw: it fixes that and reports again.

### 2. Improve or fix the app for the car (`/aaos-tweak`)

```
Follow the instructions in .github/skills/aaos-tweak/SKILL.md exactly. The tweak: [describe what you want changed, or what is wrong on the car, in plain English].
```

The assistant makes the change, checks the app still builds, records what it did, and releases it to the car (below). If you mention something you noticed about the car itself, it adds it to the car notes in both apps.

### Both jobs end with a release to the car

1. The assistant raises the version number (Play needs a higher one for every upload) and checks the build.
2. It gives you click-by-click steps to sign the file in Android Studio. When that's done, reply `Bundle built`.
3. It puts the file in the **For upload to Play Console** folder, opens that folder, and gives you the clicks for Google Play Console. When Play accepts it, reply `Uploaded` and it records the upload.
4. Install or update the app from the Play Store in the car.

### If something goes wrong

- **A chat ended halfway through a job:** open a new chat in the same folder and paste:
  ```
  Read AGENTS.md, then check AAOS_LOG.md, git status and any sync/ branch, and tell me in plain English where the last job got to and what's next. Do not change anything yet.
  ```
- **Play says the version code was already used:** tell the assistant `Play says the version code was already used.` It raises the number and you sign and upload again.
- **The assistant stops with an error:** it gives you one message to paste back to it, or to another AI assistant. You never need to fix code yourself.

## 🗂️ Behind the scenes

You don't need to read these files. They are the instructions that keep any AI assistant on track between chats.

| File | What it holds |
| --- | --- |
| `README.md` | This page |
| [`AAOS_FORK.md`](AAOS_FORK.md) | Rules for AI assistants, and the full list of car customisations with technical detail |
| [`AAOS_UPSTREAM_SYNC.md`](AAOS_UPSTREAM_SYNC.md) | How parent updates are done, and the report you get before approving |
| [`AAOS_RELEASE.md`](AAOS_RELEASE.md) | Release steps, the version number rule, and the last version uploaded |
| [`AAOS_CAR_NOTES.md`](AAOS_CAR_NOTES.md) | Things learned on the real Polestar 3 (the same in both apps) |
| [`AAOS_LOG.md`](AAOS_LOG.md) | A diary of every change and what was checked (older entries in `AAOS_LOG_ARCHIVE.md`) |
| `AGENTS.md`, `CLAUDE.md`, `GEMINI.md` | Where AI assistants start reading |
| `.github/skills/` | The step-by-step recipes that the prompts above run |

## 📄 License

Flow is distributed under the [GNU General Public License v3.0](License). The license applies to this fork and to the original project.

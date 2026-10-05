# Flow: updating from the parent (upstream sync)

> Fork-owned file. "Parent facts", "Restore after every merge", "Checks to run" and "Hotspots" are specific to this app; every other section is word for word the same in the Flow and Nuvio forks.

The owner has no coding experience and relies on the agent to bring every customisation (`AAOS_FORK.md` section 7) across into the parent's new code, and to say plainly what changed. Do this only when the owner asks, or before a release that needs parent fixes. `main` is not touched until the owner approves. Parent changes only flow in; nothing is ever pushed to the parent. The recipe that drives this file is `.github/skills/aaos-sync/SKILL.md`.

## Parent facts

| Item | Value |
| --- | --- |
| Parent repository | https://github.com/A-EDev/Flow (git remote `upstream`) |
| Parent branch synced | `main` |
| **Last merged parent commit** | `10f588961d3467a2210a302d3698e9262c3ea201` (2026-09-30). Step 1 lists everything newer; step 11 updates this row. |

## Steps

In the commands below, `<branch>` is the parent branch in "Parent facts" and `<date>` is today's date; fill in the real values.

0. **Safety.** `git status` must be clean. Then `git switch main && git pull origin main` and mark the last good state with `git tag pre-sync-$(date +%F)`.
1. **Find what is new.** `git fetch upstream`, then `git log --oneline main..upstream/<branch>`. If nothing is listed, stop and tell the owner there is nothing new. Note the full SHA with `git rev-parse upstream/<branch>`.
2. **Assess before merging.** Read the new parent commits and `git diff --stat main...upstream/<branch>`. Using the Hotspots table (each row names its customisation), note which customisations' areas the parent touched and what the parent added or changed that the owner would notice. This feeds the approval report.
3. **Create the sync branch.** `git switch -c sync/upstream-$(date +%F) main`
4. **Merge.** `git merge upstream/<branch>`. Use a merge, not a rebase or reset, so our history stays intact.
5. **Redo the customisations.** Resolve conflicts using the Hotspots table. Rule: take the parent's new code, then re-apply our customisation on top; never choose "ours" or "theirs" wholesale on a hotspot file unless the table says "keep ours". Then go through **every** customisation in `AAOS_FORK.md` section 7 and follow "After a parent update" in its Part 2 block, including those whose files merged without conflict: the parent can change behaviour near our code silently. If a customisation cannot be redone safely, mark it "At risk" in the report; never drop it silently.
6. **Restore fork-owned values** (list below).
7. **Verify.** Run "Checks to run", do each customisation's "Check" from Part 2, and inspect the merged **release** manifest. If the emulator is available, do the car-like check in `AAOS_CAR_NOTES.md` ("Testing like the car").
8. **Approval gate.** Build the debug app, write the approval report (below) and stop. Do not merge into `main`. The owner replies `approve sync`, `cancel sync` or `problem: <what you saw>`. On `cancel sync`, delete the sync branch (see "Abort or roll back"); `main` stays untouched. On `problem`, fix it on the sync branch, re-verify and report again. If the owner returns in a new chat, find the open `sync/` branch, re-run the checks quickly, then continue.
9. **Land it.** `git switch main && git merge --ff-only sync/upstream-<date>`. If that refuses because `main` moved, merge `main` into the sync branch, re-verify, retry. Then `git push origin main`.
10. **Clean up.** `git branch -d sync/upstream-<date>`; if it was pushed, `git push origin --delete sync/upstream-<date>`. Never leave sync branches behind.
11. **Record.** Update "Last merged parent commit" above and add an `AAOS_LOG.md` entry: parent SHA, number of parent commits, conflicts, the status of each customisation, what was and was not verified. If a customisation's code moved, update its Part 2 block and the Hotspots table. Then continue with the release (`AAOS_RELEASE.md`).

## The approval report

The owner cannot read code, so this report is how they decide. Write it in plain English (`AAOS_FORK.md` section 6), using this layout:

> **Parent update for [app]: [number] new parent changes, [first date] to [last date]**
>
> **1. What's new for you.** The parent's new features and fixes, as the owner would notice them in the car (3 to 8 bullets). Say "nothing you would notice" if that is the case.
>
> **2. Your customisations.** A table with **every** ID from `AAOS_FORK.md` section 7, none skipped:
>
> | ID | Customisation | Status | What I did |
> | --- | --- | --- | --- |
>
> Status is one of: **Unaffected** (the parent did not touch that area), **Kept** (the parent touched the area; ours re-applied unchanged), **Adapted** (the parent changed the code, so ours was redone in a new form; say how), **At risk** (could not fully redo or check it; say why and what could go wrong), **Removed** (only if the owner decided so).
>
> **3. Risks and recommendation.** Anything risky: large rewrites, new permissions, sign-in, account or server changes, new network or tracking code, removed features, build or SDK changes. End with "Recommendation: approve" or "Recommendation: wait, because ...".
>
> **4. What to test.** First on the emulator (open Android Studio, choose the emulator `Automotive_Large_Portrait` in the device list at the top (if it is missing: Tools, Device Manager, Create Device, Automotive), click the green Run button, then open the app from the car's app list), then on the car after the release. List the areas this update touched in everyday words, plus the usual checks: the app opens from its icon, text and buttons are large, something plays, and the home screen media card shows it.
>
> **5. Checks I ran.** Each check: passed, failed or not run. Say "emulator" or "real car" explicitly; nothing on the car is verified until the owner tests it.
>
> Reply with exactly one of: `approve sync`, `cancel sync`, or `problem: <what you saw>`.

## Abort or roll back

- Before step 9: `git switch main && git branch -D sync/upstream-<date>`. `main` was never touched.
- After step 9: create a revert commit (`git revert -m 1 <merge commit>`). Do not reset or force-push. The `pre-sync-<date>` tag marks the last good state.

## Restore after every merge

- Keep OUR version code and name in `app/build.gradle.kts` (take ours on any conflict); never copy the parent's. The release stage raises them.
- Release `applicationId` is exactly `com.JF_Flow` (C1).
- The merged release manifest still has the AAOS metadata, every launcher alias with `distractionOptimized`, the camera features optional, the car media service before `Media3MusicService` without `androidx.car.app.launchable`, the artwork provider, and the parent's target SDK (C2, C3, C8).
- `README.md` and the fork block at the top of `AGENTS.md` are ours (C10).

## Checks to run

```bash
./gradlew ktlintCheck
./gradlew :app:compileGithubDebugKotlin
./gradlew :app:compileFossDebugKotlin
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew :app:testGithubDebugUnitTest
./gradlew :app:assembleGithubDebug
git diff --check
```

Run the lines one at a time (one flavour at a time saves memory). Always use flavour-prefixed tasks, never bare `assembleDebug` or `compileDebugKotlin`. The test line needs Android Studio's Java: if about 180 tests fail with "Unsupported class file major version 71", the wrong Java was used; rerun the line exactly as written. If a build fails in `kspGithubDebugKotlin`, run `./gradlew --stop` and retry. Check the merged release manifest for both flavours (`github` and `foss`) where their values differ.

## Hotspots: files where our customisations live

| File / area | Customisation | What we changed | On conflict |
| --- | --- | --- | --- |
| `app/build.gradle.kts` | C1, C9 | release `applicationId`, version code and name, ABI-split workaround | Keep ours for those; take the parent's for the rest |
| `app/src/main/AndroidManifest.xml`, `res/xml/automotive_app_desc.xml` | C2, C3, C8 | AAOS features and metadata, car launcher, `distractionOptimized`, camera optional, car media service and provider | Keep every AAOS entry; give any new parent activity the same metadata |
| `service/FlowCarMediaSession.kt`, `FlowCarMediaBrowserService.kt`, `FlowCarMediaArtworkProvider.kt`, `FlowApplication.kt` (one `startObserving` call), `res/values/aaos_car_media_strings.xml` | C8 | car media card mirroring the video and music players | Keep; if the parent renames `GlobalPlayerState.currentVideo`, `EnhancedPlayerManager.playerState` or `EnhancedMusicPlayerManager.currentTrack`/`playerState`, re-point the observers |
| `ui/components/layout/navigation/FlowNavigationChrome.kt`, `FlowNavigationBar.kt` | C4 | bottom navigation forced, larger icons | The parent's side rail must not return |
| `ui/components/layout/topbar/*` | C5, C6 | 72 dp bars, larger actions, 12 dp end inset | Keep screens on the shared component |
| `ui/theme/Type.kt` | C5 | larger typography | Re-apply our scale on the parent's new scale |
| `ui/screens/home/HomeScreen.kt` | C5 | larger logo and search; logo 12 dp start inset | Re-apply |
| `ui/components/videoplayer/`, `shorts/`, `musicplayer/` | C5 | larger controls; minimise button size and offset | Re-apply sizes, keep the parent's logic |
| `res/drawable/ic_launcher_foreground.xml`, `ic_launcher_dynamic_foreground.xml` | C7 | red play mark | Keep ours |
| `README.md`, `AGENTS.md` | C10 | owner README; fork block at the top of AGENTS | Keep our README and block; take the parent's text below the block |
| `CLAUDE.md`, `GEMINI.md`, `.claude/skills`, `.github/skills/aaos-*`, `AAOS_*.md` | C10 | fork-owned agent entry points, recipes and docs | Keep ours (the parent's `.gitignore` ignores `.claude/` and `claude.md`; these are tracked anyway, never delete them) |

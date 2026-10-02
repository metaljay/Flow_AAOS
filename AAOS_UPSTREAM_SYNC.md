# Upstream sync: how to pull Flow parent updates safely

Do this only when the owner asks, or before a release that needs parent fixes. `main` is not touched until the sync branch is verified. Parent changes only flow in; nothing is ever pushed to upstream.

## Steps

0. **Safety.** `git status` must be clean (apart from known items in `AAOS_FORK.md` section 6). Then `git switch main && git pull origin main` and mark the last good state with `git tag pre-sync-$(date +%F)`.
1. **Find what is new.** `git fetch upstream`. The target is always `upstream/main`. `git log --oneline main..upstream/main`: if nothing is listed, stop. Note the full SHA with `git rev-parse upstream/main`.
2. **Create the sync branch.** `git switch -c sync/upstream-$(date +%F) main`
3. **Merge.** `git merge upstream/main`. Use a merge, not a rebase or reset, so our history stays intact.
4. **Resolve conflicts** using the hotspot table below. Rule: take upstream's new code, then re-apply our customisation on top. Never choose "ours" or "theirs" wholesale on a hotspot file. Also review hotspot files that merged *without* conflict, because upstream can change behaviour near our changes silently.
5. **Restore fork-owned values** (list below).
6. **Verify.** Run the checks below, walk the invariants in `AAOS_FORK.md` section 5, and inspect the merged **release** manifest for application ID, version, min/target SDK, automotive and camera features, every launcher activity/alias, MediaBrowser entry, and the flavor-specific values that differ between `github` and `foss`. Use the emulator if available.
7. **Land it.** `git switch main && git merge --ff-only sync/upstream-<date>`. If that refuses because `main` moved, merge `main` into the sync branch, re-verify, retry. Then `git push origin main`.
8. **Clean up.** `git branch -d sync/upstream-<date>`; if it was pushed, `git push origin --delete sync/upstream-<date>`. Never leave sync branches behind.
9. **Record.** Add an `AAOS_LOG.md` entry (upstream SHA, conflicts, verification, limits). If releasing, continue with `AAOS_RELEASE.md`.

## Abort or roll back

- Before step 7: `git switch main && git branch -D sync/upstream-<date>`. `main` was never touched.
- After step 7: create a revert commit (`git revert -m 1 <merge commit>`). Do not reset or force-push. The `pre-sync-<date>` tag marks the last good state.

## Restore after every merge

- The Play version code/name in `app/build.gradle.kts` (`versionCode` and `versionName` in `defaultConfig`, around line 40) must be **above the last uploaded code** in `AAOS_RELEASE.md`. Upstream's value is irrelevant; never keep a lower number.
- Release `applicationId` is exactly `com.JF_Flow`.
- The release `applicationId` is still `com.JF_Flow`.
- The merged manifest still contains the AAOS metadata, every launcher alias, the camera features as optional, the MediaBrowser entry, the target SDK, and the correct application ID.
- README banner/contract and the AGENTS AAOS block are intact.

## Checks to run

```bash
./gradlew ktlintCheck
./gradlew :app:compileGithubDebugKotlin
./gradlew :app:compileFossDebugKotlin      # run one flavour at a time (memory)
./gradlew :app:testGithubDebugUnitTest
./gradlew :app:assembleGithubDebug
```
Always use flavour-prefixed tasks. Never bare `assembleDebug` or `compileDebugKotlin`.

## Hotspots: files where our changes live

| File / area | What we changed | On conflict |
| --- | --- | --- |
| `app/src/main/AndroidManifest.xml` | AAOS features, car launcher, MediaBrowser, camera optional, `distractionOptimized` | Keep every AAOS entry; give any new upstream activity the same metadata |
| `app/build.gradle.kts` | release `applicationId`, Play version code/name, ABI-split workaround | Keep ours for those lines; take upstream for the rest |
| `ui/components/layout/navigation/FlowNavigationChrome.kt`, `FlowNavigationBar.kt` | bottom nav forced | Upstream's adaptive side rail must not return |
| `ui/components/layout/topbar/*` | 72 dp bars, larger actions | Keep screens on the shared component |
| `ui/theme/Type.kt` | larger typography | Re-apply our scale on upstream's new scale |
| `ui/screens/home/HomeScreen.kt` | larger logo/search | Re-apply |
| `ui/components/videoplayer/`, `shorts/`, `musicplayer/` | larger controls | Re-apply sizes, keep upstream logic |
| `res/drawable/ic_launcher_foreground.xml`, `ic_launcher_dynamic_foreground.xml` | red play mark | Keep ours |
| `README.md`, `AGENTS.md` | fork banner/contract; AGENTS top block | Keep our block/banner, take upstream text elsewhere |

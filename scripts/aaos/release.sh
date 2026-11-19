#!/usr/bin/env bash
# One command from the Mac to Play internal testing: tests -> signed release bundle -> upload and release.
#   scripts/aaos/release.sh             full run (also files the bundle and mapping in Play Console/Flow, tags play-<code>)
#   scripts/aaos/release.sh --check     sign in to Play and look, change nothing (skips the build)
#   scripts/aaos/release.sh --dry-run   build, upload to a throwaway edit, validate, discard (releases nothing)
set -euo pipefail
cd "$(dirname "$0")/../.."
START=$SECONDS
MODE=""
case "${1:-}" in
  "") ;;
  --check|--dry-run) MODE="$1" ;;
  *) echo "Unknown option: $1 (use --check or --dry-run)"; exit 2 ;;
esac
die() { echo "Stopped: $*" >&2; exit 1; }
elapsed() { echo "Elapsed: $((SECONDS - START))s"; }
trap elapsed EXIT

if [[ "$MODE" == "--check" ]]; then
  node scripts/aaos/play-upload.mjs --check
  exit 0
fi

# What goes up must be a commit.
if [[ -n "$(git status --porcelain --untracked-files=no)" ]]; then
  git status --short --untracked-files=no
  die "there are uncommitted changes. Commit them first so the upload matches a commit."
fi
echo "Commit: $(git log -1 --format='%h %s')"

# A real release must be on GitHub first (the version bump is pushed before building).
if [[ -z "$MODE" ]]; then
  git fetch -q origin || die "couldn't reach GitHub to check the commit is pushed."
  [[ "$(git rev-parse HEAD)" == "$(git rev-parse origin/main)" ]] || die "this commit is not the same as origin/main. Push (or pull) first."
fi

for a in store-file key-alias store-password key-password; do
  security find-generic-password -s flow-release -a "$a" >/dev/null 2>&1 || die "signing details not in the Keychain. Run scripts/aaos/setup-release.sh."
done

export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
[[ -x "$JAVA_HOME/bin/java" ]] || die "Android Studio's Java not found at $JAVA_HOME."
export PATH="$JAVA_HOME/bin:$PATH"

VERSION_CODE="$(sed -n 's/^[[:space:]]*versionCode = \([0-9][0-9]*\).*/\1/p' app/build.gradle.kts | head -1)"
VERSION_NAME="$(sed -n 's/^[[:space:]]*versionName = "\([^"]*\)".*/\1/p' app/build.gradle.kts | head -1)"
[[ -n "$VERSION_CODE" && -n "$VERSION_NAME" ]] || die "couldn't read versionCode / versionName from app/build.gradle.kts."

./gradlew ktlintCheck
./gradlew :app:testGithubDebugUnitTest
AAOS_KEYCHAIN_SIGNING=1 ./gradlew :app:bundleGithubRelease

AAB="app/build/outputs/bundle/githubRelease/app-github-release.aab"
[[ -f "$AAB" ]] || die "the bundle wasn't built ($AAB)."
# The Keychain password is only needed for building; verifying a signature needs none.
# jarsigner exits 0 even for an unsigned jar, so read what it says.
VERIFY="$(jarsigner -verify "$AAB" 2>&1 || true)"
if grep -qi "jar is unsigned" <<<"$VERIFY" || ! grep -q "jar verified" <<<"$VERIFY"; then
  die "the bundle is not signed. Check the Keychain items (scripts/aaos/setup-release.sh)."
fi
echo "Bundle is signed."

node scripts/aaos/play-upload.mjs ${MODE:+"$MODE"} --bundle "$AAB" --version-code "$VERSION_CODE"

# Only after a real release: file the bundle and mapping like the manual route, and tag it locally (never pushed here).
if [[ -z "$MODE" ]]; then
  OUT="../../Play Console/Flow"
  if [[ -d "$OUT" ]]; then
    NAME="Flow-v${VERSION_NAME}-build${VERSION_CODE}"
    if [[ -e "$OUT/$NAME.aab" ]]; then
      echo "Not filed: $OUT/$NAME.aab already exists (never overwritten)."
    else
      cp "$AAB" "$OUT/$NAME.aab" && echo "Filed: $OUT/$NAME.aab"
    fi
    MAP="app/build/outputs/mapping/githubRelease/mapping.txt"
    if [[ -f "$MAP" && -d "$OUT/Android Studio output" && ! -e "$OUT/Android Studio output/$NAME-mapping.txt" ]]; then
      cp "$MAP" "$OUT/Android Studio output/$NAME-mapping.txt" && echo "Filed the mapping file beside it."
    else
      echo "Mapping file not filed (missing, no 'Android Studio output' folder, or already there)."
    fi
  else
    echo "Warning: $OUT not found, so the bundle was not filed. It is at $AAB."
  fi
  git tag "play-$VERSION_CODE" 2>/dev/null && echo "Tagged play-$VERSION_CODE (local)." || echo "Tag play-$VERSION_CODE already exists; left as is."
fi

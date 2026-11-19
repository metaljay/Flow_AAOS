#!/usr/bin/env bash
# One-time setup, run by the owner in Terminal: stores the release-signing details in the login Keychain
# (service "flow-release") so scripts/aaos/release.sh can sign and upload. Re-run to change values.
#   scripts/aaos/setup-release.sh            store / update
#   scripts/aaos/setup-release.sh --remove   delete the items
# Passwords are typed hidden and never appear as command arguments (the `security` tool prompts for them itself).
set -euo pipefail

SERVICE=flow-release
ACCOUNTS=(store-file key-alias store-password key-password)

if [[ "${1:-}" == "--remove" ]]; then
  for a in "${ACCOUNTS[@]}"; do
    security delete-generic-password -s "$SERVICE" -a "$a" >/dev/null 2>&1 && echo "Removed $a" || echo "Not present: $a"
  done
  exit 0
fi

read -r -p "Path to your upload keystore file (usually Key.jks in your Play Console folder): " STORE_FILE
STORE_FILE="${STORE_FILE/#\~/$HOME}"
STORE_FILE="${STORE_FILE%\"}"; STORE_FILE="${STORE_FILE#\"}"
STORE_FILE="${STORE_FILE%\'}"; STORE_FILE="${STORE_FILE#\'}"
[[ -f "$STORE_FILE" ]] || { echo "No file at: $STORE_FILE"; exit 1; }
read -r -p "Key alias: " KEY_ALIAS
[[ -n "$KEY_ALIAS" ]] || { echo "The alias can't be empty."; exit 1; }

# Non-secret items go in with -w <value>; the two passwords are prompted by `security` (hidden, asked twice).
security add-generic-password -U -s "$SERVICE" -a store-file -w "$STORE_FILE"
security add-generic-password -U -s "$SERVICE" -a key-alias -w "$KEY_ALIAS"

echo
echo "Store password (hidden; the keystore's password):"
security add-generic-password -U -s "$SERVICE" -a store-password -w
echo "Key password (hidden; type the same again if it is the same as the store password):"
security add-generic-password -U -s "$SERVICE" -a key-password -w

mkdir -p "$HOME/.config/claude-car-play"
chmod 700 "$HOME/.config/claude-car-play"
echo
echo "Saved to your login Keychain."
echo "Next: put the Play service-account key file at"
echo "  $HOME/.config/claude-car-play/key.json"
echo "and make it private:  chmod 600 $HOME/.config/claude-car-play/key.json"

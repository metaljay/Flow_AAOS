# AAOS car notes: hard-won facts about the Polestar 3

> Fork-owned file, identical in the Flow and Nuvio forks. Read it before touching the manifest, the media card, launch behaviour or anything near the screen edges. When a new car fact is learned, add it here **in both repos** in the same change. Evidence for each fact is in `AAOS_LOG.md` (and `AAOS_LOG_ARCHIVE.md`) under the date given.

## The car and its launcher

- The Polestar 3 runs Google's car launcher, but it does **not** look exactly like the emulator. Example: the Polestar app list shows **one icon per app**; the emulator showed two when an app had a media entry (2026-10-05).
- Apps run in a window below the Polestar's own top bar (it shows "Google Maps" and shortcuts), not full screen.
- A forced full restart (hold the play/pause button for about 20 s) is not the same as the car's normal overnight sleep. Test both when it matters.

## Media card (home screen, bottom left)

- The card follows the media app that played last. Playing something in Flow or Nuvio makes that app the card's source.
- While the car is running, the card shows what Flow or Nuvio is playing (title, channel/episode, picture) and switches between them (owner photos 2026-10-05).
- **Never add `androidx.car.app.launchable=true` to the media service.** It makes the Polestar app icon open the car's own media screen ("Continue watching") with no way into the app; the owner could only open the apps from the Play Store's Open button (2026-10-05).
- Without that setting, the car's card logic (`MediaSource.isMediaTemplate` in the car launcher) rejects the app's media service after a **full restart**: the card shows only the Flow or Nuvio icon and stays blank, even after playing again. The card accepts a service only if it has that setting or the app has no launcher icon at all, so a card that refills after a restart and an icon that opens the app cannot both be had (2026-10-05). Accepted trade-off.
- After an app is **updated or force-stopped** while the car is on, the card loses its connection and stays blank until a restart (seen with the setting on, 2026-10-05).
- The card only shows artwork from a local `content://` URI, never a web URL; each app serves its saved artwork through its own `...carmediaart` provider.

## Display safe area

- The screen's rounded corners and bezel cover the edge of the app window. Keep every touch target at least 16 dp from the left and right window edges, and icons about 24-28 dp in (2026-10-05: Flow's settings cog was only partly tappable).

## Testing like the car

- **Open apps from the car's app list (app grid) on the emulator, not with `adb`/`am start`/`monkey`.** Launching directly skips the launcher, which hid the icon bug on 2026-10-05.
- When media code is touched: play something, go to the home screen and check the card; then do a full emulator reboot and check it again.
- Screenshots on the emulator need the display id: `adb exec-out screencap -d <id> -p`, with the id from `adb shell dumpsys SurfaceFlinger --display-id` (plain `screencap` warns about multiple displays and produces a bad file).
- The emulator's system UI sometimes crashes or goes black after reinstalls and reboots; restart the emulator (`adb emu kill`, then start it again) before deciding an app is broken.
- Emulator results never prove car behaviour. Say "emulator" or "real car" explicitly in every report and log entry.

## Google Play (Internal testing)

- Both apps are drafts that were never sent for review, so the Play Store on the car shows the temporary name "com.JF_... (unreviewed)" and a placeholder icon, whatever artwork the listing has. This is cosmetic; installs and updates work. Sending for review is **not** recommended (Flow is a YouTube client using YouTube's mark; a rejection could put a policy strike on the developer account).
- The Play website's device list shows an old duplicate "Polestar Polestar" entry; it seems to share an ID with "Polestar 3" (ticking or unticking one changes both). Leave both ticked.

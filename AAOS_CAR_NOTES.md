# AAOS car notes: hard-won facts about the Polestar 3

> Fork-owned file, identical in the Flow and Nuvio forks. Read it before touching the manifest, the media card, launch behaviour or anything near the screen edges. When a new car fact is learned, add it here **in both repos** in the same change. Evidence for each fact is in the owner's private session log and its archive (`AAOS_FORK.md` section 10) under the date given.

## The car and its launcher

- The Polestar 3 runs Volvo's own car launcher (see the readout below), which does **not** look exactly like the emulator's Google launcher. Example: the Polestar app list shows **one icon per app**; the emulator showed two when an app had a media entry (2026-10-05).
- Apps run in a window below the Polestar's own top bar (it shows "Google Maps" and shortcuts), not full screen.
- A forced full restart (hold the play/pause button for about 20 s) is not the same as the car's normal overnight sleep. Test both when it matters.
- The car's own readout (Settings, About, Device Info in Flow 46, 2026-11-17): Android 15, both `android.software.car.templates_host.media` and `android.software.car.templates_host` Supported, home screen `com.volvocars.launcher` 2.5.126 (Volvo's own launcher, not Google's CarLauncher), media screen `com.android.car.media` 36 (Google's car media app). The emulator reports both features too, but runs Google's CarLauncher.

## Media card (home screen, bottom left)

- The card follows the media app that played last. Playing something in Flow or Nuvio makes that app the card's source.
- While the car is running, the card shows what Flow or Nuvio is playing (title, channel/episode, picture) and switches between them (owner photos 2026-10-05).
- **Never add `androidx.car.app.launchable=true` to an always-on media service.** On its own it makes the Polestar app icon open the car's own media screen ("Continue watching") with no way into the app; the owner could only open the apps from the Play Store's Open button (2026-10-05).
- Without that setting, the car's card logic (`MediaSource.isMediaTemplate` in Google's car launcher) rejects the app's media service after a **full restart**: the card shows only the app icon and stays blank, even after playing again (2026-10-05).
- **In Flow 47 and Nuvio 151 on the car (2026-11-18): the card refills after a restart and the icon opens the app.** How: a second, switched-off media service with `launchable=true` and `androidx.car.app.media.CalMediaActivityComponent` (value `package/class`, written `${applicationId}/full.class.Name`; a bare class name is ignored). At start-up the app switches it on, and the original off, only when the car reports `android.software.car.templates_host.media`; Google's car media app then opens the named activity instead of its own media screen. Emulator (Google's launcher, car media app 37), both apps: both app-grid entries open the app (Google's launcher lists the app **twice**: its icon plus a media entry); after a cold restart, without opening the app, the card shows the last item paused with title and picture; tapping the card opens the app. Worked on the car (Volvo's launcher, car media app 36).
- With that opt-in on, after an app is **updated or force-stopped** while the car is on, the card goes blank and stays blank, even after playing again, until the media source changes (switching to another media app and back, or a restart) (emulator, 2026-11-17; seen on the car with `launchable` on, 2026-10-05). **Why (Google's `CarMediaService`, read 2026-11-18):** the card's link to the app's media service dies with the process, and the car only rebuilds it when the media *source* changes; a source is the app's media service, so the same app with the same service never counts as a change. `matchPrimaryMediaSource` compares the service class name and `getMediaService` only finds *switched-on* services, so a playing session that names a different switched-on service of the same app (extras `android.media.session.BROWSE_SERVICE`) does count. An app without the opt-in cannot take the card from an opted-in app by playing (emulator), so both apps carry it or neither.
- **Fix being tried (2026-11-18, emulator only, not yet on the car):** Flow and Nuvio each carry two identical launchable twins, exactly one switched on. When the first playing state of a process is about to be published and no client other than the app has bound its browse service since the process started (`onGetRoot`), the app switches on the other twin, names it in the session extras and switches the old one off. Emulator results (Flow): force-stop, open, play, home: the card shows the item (it was blank before; `CarMediaService` logs a source change at the first play); full emulator restart: the card refills without opening the app and keeps the twin that was last on; force-stop again: swaps back and the card shows; `adb install -r` then play: the card shows. Start-up never swaps (the car may be binding then). A needless swap while another app owns the card is harmless.
- **Undoing it:** the switched state survives app updates. A release that removes the opt-in must still switch the original service back to its default and all the launchable twins off; deleting the code alone leaves the app with no media service at all (emulator, 2026-11-17).
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

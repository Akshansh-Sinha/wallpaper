# Calendar Wallpaper

Android app that draws a themed monthly calendar (one theme per month) and sets it as the
home + lock screen wallpaper, redrawing just after midnight every day. Past days turn red,
today is ringed, and the month rolls over on the 1st.

How it works: `assets/calendar.html` is rendered in an off-screen WebView at full screen size,
captured to a bitmap and set via `WallpaperManager`. An inexact `AlarmManager` alarm triggers the
redraw at ~00:01; reboots and clock/timezone changes trigger it too. No dependencies beyond the
Android SDK. Font: Fredoka (SIL OFL, see `assets/fonts/OFL.txt`).

Build: push to GitHub, and the "Build APK" workflow uploads `app-debug.apk` as an artifact.
Or open in Android Studio and run.

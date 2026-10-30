package com.example.calendarwallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Midnight alarm, reboot, or clock change: redraw the wallpaper and book the next midnight. */
class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        Scheduler.scheduleNext(context)
        WallpaperUpdater.render(context) { pending.finish() }
    }
}

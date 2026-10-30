package com.example.calendarwallpaper

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (16 * resources.displayMetrics.density).toInt()

        val status = TextView(this).apply {
            text = "Tap the button once. After that the wallpaper redraws itself every night just after midnight."
            setPadding(pad, pad, pad, pad / 2)
        }
        val button = Button(this).apply { text = "Set calendar wallpaper" }
        button.setOnClickListener {
            button.isEnabled = false
            status.text = "Drawing…"
            Scheduler.scheduleNext(this)
            WallpaperUpdater.render(this) { ok ->
                button.isEnabled = true
                status.text = if (ok) "Done. Check your home and lock screen." else "Couldn't draw the wallpaper. Your old one is unchanged."
            }
        }
        val preview = WebView(this).apply {
            settings.javaScriptEnabled = true
            loadUrl(WallpaperUpdater.PAGE)
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(status)
            addView(button, LinearLayout.LayoutParams(-1, -2).apply { setMargins(pad, 0, pad, pad / 2) })
            addView(preview, LinearLayout.LayoutParams(-1, 0, 1f))
        })
    }
}

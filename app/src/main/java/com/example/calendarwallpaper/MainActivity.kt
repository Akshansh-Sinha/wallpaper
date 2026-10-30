package com.example.calendarwallpaper

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (16 * resources.displayMetrics.density).toInt()

        val status = TextView(this).apply {
            text = "Pick a design, then tap the button. After that the wallpaper redraws itself every night just after midnight."
            setPadding(pad, pad, pad, pad / 2)
        }
        val preview = WebView(this).apply {
            settings.javaScriptEnabled = true
            loadUrl(Designs.url(this@MainActivity))
        }

        val choices = RadioGroup(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(pad, 0, pad, 0)
        }
        Designs.all.forEachIndexed { i, (label, _) ->
            choices.addView(RadioButton(this).apply { id = i + 1; text = label })
        }
        choices.check(Designs.index(this) + 1)
        choices.setOnCheckedChangeListener { _, checkedId ->
            Designs.set(this, checkedId - 1)
            preview.loadUrl(Designs.url(this))
            status.text = "Selected. Tap the button to apply it."
        }

        val button = Button(this).apply { text = "Set calendar wallpaper" }
        button.setOnClickListener {
            button.isEnabled = false
            status.text = "Drawing…"
            Scheduler.scheduleNext(this)
            WallpaperUpdater.render(this) { ok ->
                button.isEnabled = true
                status.text = if (ok) "Done. Check your home and lock screen." else "Couldn't draw the wallpaper (${WallpaperUpdater.lastReason}). Your old one is unchanged."
            }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(status)
            addView(choices)
            addView(button, LinearLayout.LayoutParams(-1, -2).apply { setMargins(pad, 0, pad, pad / 2) })
            addView(preview, LinearLayout.LayoutParams(-1, 0, 1f))
        })
    }
}

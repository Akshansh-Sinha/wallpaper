package com.example.calendarwallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Renders assets/calendar.html in an off-screen WebView at full screen size
 * and sets the result as the home + lock screen wallpaper.
 * Must be called on the main thread; onDone is called exactly once, on the main thread.
 */
object WallpaperUpdater {
    const val PAGE = "file:///android_asset/calendar.html"
    private const val SETTLE_MS = 1500L   // give fonts + JS time to paint after load
    private const val TIMEOUT_MS = 10_000L

    fun render(context: Context, onDone: (Boolean) -> Unit) {
        val app = context.applicationContext
        val main = Handler(Looper.getMainLooper())
        val (w, h) = screenSize(app)
        val web = WebView(app)
        var finished = false

        fun finish(ok: Boolean) {
            if (finished) return
            finished = true
            main.removeCallbacksAndMessages(null) // only this Handler's own posts
            web.destroy()
            onDone(ok)
        }

        web.settings.javaScriptEnabled = true
        web.setBackgroundColor(Color.BLACK)
        // Not attached to a window, so size it by hand.
        web.measure(
            View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY),
        )
        web.layout(0, 0, w, h)

        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // Use the main Handler, not view.postDelayed: an unattached view never runs its posts.
                main.postDelayed({ finish(capture(app, web, w, h)) }, SETTLE_MS)
            }
        }
        main.postDelayed({ finish(false) }, TIMEOUT_MS)
        web.loadUrl(PAGE)
    }

    private fun capture(app: Context, web: WebView, w: Int, h: Int): Boolean = runCatching {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        web.draw(Canvas(bmp))
        // Safety check: the screen centre is inside the cream calendar card.
        // If it came out dark, the WebView didn't paint, so keep the old wallpaper.
        val p = bmp.getPixel(w / 2, h / 2)
        if (Color.red(p) + Color.green(p) + Color.blue(p) < 300) return false
        WallpaperManager.getInstance(app).setBitmap(
            bmp, null, true,
            WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK,
        )
        true
    }.getOrDefault(false)

    private fun screenSize(ctx: Context): Pair<Int, Int> {
        val display = ctx.getSystemService(DisplayManager::class.java).getDisplay(Display.DEFAULT_DISPLAY)
        val m = DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getRealMetrics(m) // full panel incl. status/nav bars: 1080x2400 on CMF Phone 1
        return m.widthPixels to m.heightPixels
    }
}

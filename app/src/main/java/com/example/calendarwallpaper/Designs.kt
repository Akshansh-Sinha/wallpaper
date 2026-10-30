package com.example.calendarwallpaper

import android.content.Context

/** The available looks. To add one: drop an HTML page in assets/ and add a line here. */
object Designs {
    val all = listOf(
        "CMF" to "calendar.html",
        "Tiles" to "calendar-tiles.html",
    )
    private const val PREFS = "prefs"
    private const val KEY = "design"

    fun index(ctx: Context): Int =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY, 0).coerceIn(all.indices)

    fun set(ctx: Context, i: Int) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY, i).apply()

    fun url(ctx: Context): String = "file:///android_asset/" + all[index(ctx)].second
}

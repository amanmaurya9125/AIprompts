package com.aman.aiprompts.RateUs

import android.content.Context

object RatePreference {

    private const val PREF = "rate_pref"
    private const val KEY_FAVORITE_COUNT = "favorite_count"
    private const val KEY_REVIEW_SHOWN = "review_shown"

    fun incrementFavorite(context: Context) {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_FAVORITE_COUNT, 0) + 1
        prefs.edit().putInt(KEY_FAVORITE_COUNT, count).apply()
    }

    fun getFavoriteCount(context: Context): Int {
        return context
            .getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getInt(KEY_FAVORITE_COUNT, 0)
    }

    fun isReviewShown(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_REVIEW_SHOWN, false)
    }

    fun markReviewShown(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_REVIEW_SHOWN, true).apply()
    }
}

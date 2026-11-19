package com.example.hakaton.data

import android.content.Context

object StaffCode {
    private const val PREFS = "staff_prefs"
    private const val KEY = "staff_code"
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun prefs() = requireNotNull(appContext) { "StaffCode.init не вызван" }
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(code: String) = prefs().edit().putString(KEY, code).apply()
    fun get(): String = prefs().getString(KEY, "") ?: ""
    fun clear() = prefs().edit().remove(KEY).apply()
}
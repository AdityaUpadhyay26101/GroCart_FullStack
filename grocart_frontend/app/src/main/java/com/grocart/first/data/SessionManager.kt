package com.grocart.first.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    // ✅ SHARED PREFERENCES FOR USER SESSION
    private val prefs: SharedPreferences = context.getSharedPreferences("GroCartPrefs", Context.MODE_PRIVATE)


    // ✅ FUNCTION TO SAVE USER SESSION
    fun saveUserSession(userId: Long, username: String) {
        val editor = prefs.edit()
        editor.putLong("USER_ID", userId)
        editor.putString("USERNAME", username)
        editor.apply()
    }


    // ✅ FUNCTION TO GET USER SESSION
    fun getUserId(): Long = prefs.getLong("USER_ID", -1L)
    // ✅ FUNCTION TO GET USERNAME
    fun getUsername(): String? = prefs.getString("USERNAME", null)


    // ✅ FUNCTION TO LOGOUT
    fun logout() {
        prefs.edit().clear().apply()
    }
}
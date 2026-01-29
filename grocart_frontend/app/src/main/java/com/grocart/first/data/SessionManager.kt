package com.grocart.first.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("GroCartPrefs", Context.MODE_PRIVATE)


    fun saveUserSession(userId: Long, username: String) {
        val editor = prefs.edit()
        editor.putLong("USER_ID", userId)
        editor.putString("USERNAME", username)
        editor.apply()
    }


    fun getUserId(): Long = prefs.getLong("USER_ID", -1L)
    fun getUsername(): String? = prefs.getString("USERNAME", null)


    fun logout() {
        prefs.edit().clear().apply()
    }
}
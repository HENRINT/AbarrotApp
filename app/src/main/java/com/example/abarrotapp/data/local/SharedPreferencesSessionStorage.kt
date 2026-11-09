package com.example.abarrotapp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.abarrotapp.domain.storage.SessionStorage

class SharedPreferencesSessionStorage(context: Context) : SessionStorage {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    override fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    override fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    override fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    companion object {
        private const val PREFS_NAME = "abarrotes_session_prefs"
        private const val KEY_TOKEN = "auth_token"
    }
}

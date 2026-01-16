package com.example.sketchup.domain.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Android implementation using SharedPreferences
 */
actual class PlatformKeyValueStorage(
    private val context: Context
) : KeyValueStorage {
    
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("SketchUpPrefs", Context.MODE_PRIVATE)
    }
    
    override suspend fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
    
    override suspend fun getString(key: String): String? {
        return prefs.getString(key, null)
    }
    
    override suspend fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }
    
    override suspend fun getInt(key: String, default: Int): Int {
        return prefs.getInt(key, default)
    }
    
    override suspend fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
    
    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return prefs.getBoolean(key, default)
    }
    
    override suspend fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
    
    override suspend fun clear() {
        prefs.edit().clear().apply()
    }
}


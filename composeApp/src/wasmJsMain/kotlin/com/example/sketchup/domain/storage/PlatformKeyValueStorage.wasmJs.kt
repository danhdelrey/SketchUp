package com.example.sketchup.domain.storage

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * Web implementation using localStorage
 */
actual class PlatformKeyValueStorage : KeyValueStorage {

    override suspend fun putString(key: String, value: String) {
        localStorage[key] = value
    }

    override suspend fun getString(key: String): String? {
        return localStorage[key]
    }

    override suspend fun putInt(key: String, value: Int) {
        localStorage[key] = value.toString()
    }

    override suspend fun getInt(key: String, default: Int): Int {
        return localStorage[key]?.toIntOrNull() ?: default
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        localStorage[key] = value.toString()
    }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return localStorage[key]?.toBooleanStrictOrNull() ?: default
    }

    override suspend fun remove(key: String) {
        localStorage.removeItem(key)
    }

    override suspend fun clear() {
        localStorage.clear()
    }
}


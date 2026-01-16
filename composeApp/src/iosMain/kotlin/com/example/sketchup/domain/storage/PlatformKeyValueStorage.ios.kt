package com.example.sketchup.domain.storage

import platform.Foundation.NSUserDefaults

/**
 * iOS implementation using UserDefaults
 */
actual class PlatformKeyValueStorage : KeyValueStorage {
    
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    override suspend fun putString(key: String, value: String) {
        userDefaults.setObject(value, key)
    }
    
    override suspend fun getString(key: String): String? {
        return userDefaults.stringForKey(key)
    }
    
    override suspend fun putInt(key: String, value: Int) {
        userDefaults.setInteger(value.toLong(), key)
    }
    
    override suspend fun getInt(key: String, default: Int): Int {
        val value = userDefaults.integerForKey(key)
        return if (value == 0L && !userDefaults.objectForKey(key)?.toString().isNullOrEmpty()) {
            default
        } else {
            value.toInt()
        }
    }
    
    override suspend fun putBoolean(key: String, value: Boolean) {
        userDefaults.setBool(value, key)
    }
    
    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return if (userDefaults.objectForKey(key) == null) {
            default
        } else {
            userDefaults.boolForKey(key)
        }
    }
    
    override suspend fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }
    
    override suspend fun clear() {
        val domain = userDefaults.dictionaryRepresentation().keys
        domain.forEach { key ->
            userDefaults.removeObjectForKey(key.toString())
        }
    }
}


package com.example.sketchup.domain.storage
expect class PlatformKeyValueStorage() : KeyValueStorage
 */
 * - Web: localStorage
 * - iOS: UserDefaults
 * - Android: SharedPreferences or DataStore
 * Platform-specific implementation
/**

}
    suspend fun clear()
    suspend fun remove(key: String)
    suspend fun getBoolean(key: String, default: Boolean = false): Boolean
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getInt(key: String, default: Int = 0): Int
    suspend fun putInt(key: String, value: Int)
    suspend fun getString(key: String): String?
    suspend fun putString(key: String, value: String)
interface KeyValueStorage {
 */
 * Each platform implements using native solutions
 * Key-value storage abstraction
/**



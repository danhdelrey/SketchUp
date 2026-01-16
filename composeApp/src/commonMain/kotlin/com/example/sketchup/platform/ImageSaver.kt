package com.example.sketchup.platform

/**
 * Platform-specific interface for saving images.
 * Each platform (Android, iOS, Web) provides its own implementation.
 */
interface ImageSaver {
    /**
     * Saves an image to the device's storage.
     * @param bytes The PNG image data as a byte array
     * @param fileName The desired file name (without extension)
     * @return true if save was successful, false otherwise
     */
    suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean
}
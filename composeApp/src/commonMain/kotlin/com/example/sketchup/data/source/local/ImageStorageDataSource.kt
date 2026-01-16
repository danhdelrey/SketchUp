package com.example.sketchup.data.source.local

/**
 * Platform-specific data source for image storage.
 * Each platform (Android, iOS, Web) provides its own implementation.
 */
interface ImageStorageDataSource {
    /**
     * Saves an image to the device's storage.
     * @param imageData The PNG image data as a byte array
     * @param fileName The desired file name (without extension)
     * @return true if save was successful, false otherwise
     */
    suspend fun saveImage(imageData: ByteArray, fileName: String): Boolean
}

package com.example.sketchup.domain.repository

/**
 * Repository interface for image storage operations.
 * This abstracts platform-specific image saving logic.
 */
interface ImageStorageRepository {
    /**
     * Saves an image to storage.
     * @param imageData The image data as a byte array
     * @param fileName The desired file name (without extension)
     * @return Result indicating success or failure
     */
    suspend fun saveImage(imageData: ByteArray, fileName: String): Result<String>
}

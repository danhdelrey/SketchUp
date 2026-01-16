package com.example.sketchup.data.repository

import com.example.sketchup.data.source.local.ImageStorageDataSource
import com.example.sketchup.domain.repository.ImageStorageRepository

/**
 * Implementation of ImageStorageRepository.
 * Delegates to platform-specific data source for actual storage operations.
 */
class ImageStorageRepositoryImpl(
    private val imageStorageDataSource: ImageStorageDataSource
) : ImageStorageRepository {

    override suspend fun saveImage(imageData: ByteArray, fileName: String): Result<String> {
        return try {
            val success = imageStorageDataSource.saveImage(imageData, fileName)
            if (success) {
                Result.success("Image saved successfully: $fileName.png")
            } else {
                Result.failure(Exception("Failed to save image"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

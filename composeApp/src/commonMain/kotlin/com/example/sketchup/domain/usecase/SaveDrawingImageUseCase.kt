package com.example.sketchup.domain.usecase

import com.example.sketchup.domain.repository.ImageStorageRepository
import kotlinx.datetime.Clock

/**
 * Use case for saving a drawing as an image.
 * Handles image generation and storage coordination.
 */
class SaveDrawingImageUseCase(
    private val imageStorageRepository: ImageStorageRepository
) {
    /**
     * Saves the drawing image.
     * @param imageData The image data as a byte array
     * @param customFileName Optional custom file name, will generate timestamp-based name if null
     * @return Result indicating success with file path or failure with error message
     */
    suspend operator fun invoke(
        imageData: ByteArray,
        customFileName: String? = null
    ): Result<String> {
        val fileName = customFileName ?: generateFileName()
        return imageStorageRepository.saveImage(imageData, fileName)
    }

    private fun generateFileName(): String {
        return "sketch_${Clock.System.now().toEpochMilliseconds()}"
    }
}

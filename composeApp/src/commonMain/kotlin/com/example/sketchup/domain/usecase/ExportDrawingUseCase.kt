package com.example.sketchup.domain.usecase

import com.example.sketchup.domain.repository.DrawingRepository
import com.example.sketchup.domain.repository.ImageExportRepository
import kotlinx.coroutines.flow.first

/**
 * Use case for exporting drawing as image
 * Combines drawing data with platform-specific export
 */
class ExportDrawingUseCase(
    private val drawingRepository: DrawingRepository,
    private val imageExportRepository: ImageExportRepository
) {
    suspend operator fun invoke(imageData: ByteArray, fileName: String): Result<String> {
        // Business logic: validate image data
        if (imageData.isEmpty()) {
            return Result.failure(IllegalArgumentException("Image data is empty"))
        }

        // Check if export is supported on this platform
        if (!imageExportRepository.isExportSupported()) {
            return Result.failure(UnsupportedOperationException("Export not supported on this platform"))
        }

        return imageExportRepository.exportImage(imageData, fileName)
    }

    suspend fun share(imageData: ByteArray, fileName: String): Result<Unit> {
        if (imageData.isEmpty()) {
            return Result.failure(IllegalArgumentException("Image data is empty"))
        }

        return imageExportRepository.shareImage(imageData, fileName)
    }
}


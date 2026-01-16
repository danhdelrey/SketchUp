package com.example.sketchup.data.repository

import com.example.sketchup.domain.repository.ImageExportRepository

/**
 * Base implementation providing common logic
 * Platform-specific implementations extend this
 */
abstract class BaseImageExportRepository : ImageExportRepository {

    protected fun generateFileName(baseName: String): String {
        val timestamp = System.currentTimeMillis()
        return "${baseName}_$timestamp.png"
    }

    protected fun validateImageData(data: ByteArray): Result<Unit> {
        return when {
            data.isEmpty() -> Result.failure(IllegalArgumentException("Image data is empty"))
            data.size > 50_000_000 -> Result.failure(IllegalArgumentException("Image too large (>50MB)"))
            else -> Result.success(Unit)
        }
    }
}

/**
 * Expect/Actual for platform-specific implementation
 */
expect class PlatformImageExportRepository() : ImageExportRepository


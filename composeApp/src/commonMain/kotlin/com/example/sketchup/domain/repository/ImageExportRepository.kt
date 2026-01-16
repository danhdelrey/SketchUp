package com.example.sketchup.domain.repository

/**
 * Domain interface for image export
 * Platform-specific implementations will handle actual export
 */
interface ImageExportRepository {
    /**
     * Export image to platform-specific storage
     * @param imageData The image data in PNG format
     * @param fileName Desired filename
     * @return Result with file path or error
     */
    suspend fun exportImage(imageData: ByteArray, fileName: String): Result<String>
    
    /**
     * Share image using platform sharing mechanism
     */
    suspend fun shareImage(imageData: ByteArray, fileName: String): Result<Unit>
    
    /**
     * Check if platform supports image export
     */
    fun isExportSupported(): Boolean
}


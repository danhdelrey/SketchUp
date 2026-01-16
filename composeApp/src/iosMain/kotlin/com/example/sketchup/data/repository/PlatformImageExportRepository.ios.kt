package com.example.sketchup.data.repository

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum

/**
 * iOS-specific image export using Photos framework
 * Utilizes iOS Photo Library
 */
actual class PlatformImageExportRepository : BaseImageExportRepository() {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun exportImage(imageData: ByteArray, fileName: String): Result<String> {
        validateImageData(imageData).getOrElse { return Result.failure(it) }

        return try {
            // Convert ByteArray to NSData
            val nsData = imageData.usePinned { pinned ->
                NSData.create(
                    bytes = pinned.addressOf(0),
                    length = imageData.size.toULong()
                )
            }

            // Create UIImage from NSData
            val image = UIImage.imageWithData(nsData)
                ?: return Result.failure(Exception("Failed to create image from data"))

            // Save to photo library
            UIImageWriteToSavedPhotosAlbum(image, null, null, null)

            Result.success("Saved to Photos")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun shareImage(imageData: ByteArray, fileName: String): Result<Unit> {
        // iOS sharing via UIActivityViewController
        // TODO: Implement share functionality
        return Result.failure(NotImplementedError("Share not implemented yet"))
    }

    override fun isExportSupported(): Boolean = true
}


package com.example.sketchup.data.repository

import com.example.sketchup.domain.repository.ImageExportRepository
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

/**
 * Web-specific image export using Blob and download
 * Downloads image file to user's computer
 */
actual class PlatformImageExportRepository : BaseImageExportRepository() {

    override suspend fun exportImage(imageData: ByteArray, fileName: String): Result<String> {
        validateImageData(imageData).getOrElse { return Result.failure(it) }

        return try {
            val finalFileName = if (fileName.endsWith(".png")) fileName else "$fileName.png"

            // Convert ByteArray to Uint8Array
            val uint8Array = Uint8Array(imageData.size)
            imageData.forEachIndexed { index, byte ->
                uint8Array[index] = byte
            }

            // Create Blob from Uint8Array
            val blob = Blob(arrayOf(uint8Array), BlobPropertyBag(type = "image/png"))

            // Create download link
            val url = URL.createObjectURL(blob)
            val anchor = document.createElement("a") as HTMLAnchorElement
            anchor.href = url
            anchor.download = finalFileName
            anchor.click()

            // Clean up
            URL.revokeObjectURL(url)

            Result.success(finalFileName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun shareImage(imageData: ByteArray, fileName: String): Result<Unit> {
        // Web sharing via Web Share API
        // TODO: Implement using navigator.share if available
        return Result.failure(NotImplementedError("Share not implemented yet for web"))
    }

    override fun isExportSupported(): Boolean = true
}


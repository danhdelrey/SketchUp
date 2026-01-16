package com.example.sketchup.data.repository

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import com.example.sketchup.domain.repository.ImageExportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android-specific image export using MediaStore API
 * Utilizes Android's scoped storage
 */
actual class PlatformImageExportRepository(
    private val context: Context
) : BaseImageExportRepository() {

    override suspend fun exportImage(imageData: ByteArray, fileName: String): Result<String> {
        validateImageData(imageData).getOrElse { return Result.failure(it) }

        return withContext(Dispatchers.IO) {
            try {
                val finalFileName = if (fileName.endsWith(".png")) fileName else "$fileName.png"

                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, finalFileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SketchUp")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let { imageUri ->
                    resolver.openOutputStream(imageUri)?.use { outputStream ->
                        outputStream.write(imageData)
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        resolver.update(imageUri, contentValues, null, null)
                    }

                    Result.success(imageUri.toString())
                } ?: Result.failure(Exception("Failed to create image file"))

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun shareImage(imageData: ByteArray, fileName: String): Result<Unit> {
        // Android sharing via Intent
        // TODO: Implement share functionality
        return Result.failure(NotImplementedError("Share not implemented yet"))
    }

    override fun isExportSupported(): Boolean = true
}


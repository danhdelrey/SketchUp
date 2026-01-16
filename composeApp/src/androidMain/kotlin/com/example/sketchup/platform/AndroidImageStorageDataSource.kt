package com.example.sketchup.platform

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import com.example.sketchup.data.source.local.ImageStorageDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

/**
 * Android implementation of ImageStorageDataSource.
 * Uses MediaStore API to save images to the Pictures folder.
 */
class AndroidImageStorageDataSource(
    private val context: Context
) : ImageStorageDataSource {

    override suspend fun saveImage(imageData: ByteArray, fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SketchUp")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    val stream: OutputStream? = resolver.openOutputStream(it)
                    stream?.use { outputStream ->
                        outputStream.write(imageData)
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        resolver.update(it, contentValues, null, null)
                    }
                    return@withContext true
                }
                return@withContext false

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }
}

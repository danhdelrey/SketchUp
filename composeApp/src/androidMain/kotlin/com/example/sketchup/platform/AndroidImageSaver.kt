package com.example.sketchup.platform

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

/**
 * Android implementation of ImageSaver.
 * Uses MediaStore API to save images to the Pictures folder.
 * Requires Context to access the Android file/media system.
 */
class AndroidImageSaver(private val context: Context) : ImageSaver {

    override suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Configure image file metadata
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    // For Android 10 (Q) and above, specify relative path
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SketchUp")
                        put(MediaStore.Images.Media.IS_PENDING, 1) // Mark as writing in progress
                    }
                }

                // 2. Insert metadata into MediaStore to get URI (no data yet)
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    // 3. Open output stream to write data to URI
                    val stream: OutputStream? = resolver.openOutputStream(it)

                    stream?.use { outputStream ->
                        // Write bytes directly (faster, preserves original quality)
                        outputStream.write(bytes)
                    }

                    // 4. Complete the write operation (for Android 10+)
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

package com.example.sketchup.core.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

/**
 * Converts an ImageBitmap to PNG byte array for WASM/Web.
 * Uses Skia for image encoding.
 */
actual fun ImageBitmap.toPngByteArray(): ByteArray {
    // Convert to Skia Bitmap
    val skiaBitmap = this.asSkiaBitmap()
    // Create Image from Bitmap and encode to PNG
    val image = Image.makeFromBitmap(skiaBitmap)
    return image.encodeToData(EncodedImageFormat.PNG)?.bytes ?: ByteArray(0)
}

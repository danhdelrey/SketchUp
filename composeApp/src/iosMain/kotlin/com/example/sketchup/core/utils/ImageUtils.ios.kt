package com.example.sketchup.core.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Color
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface

/**
 * Converts an ImageBitmap to PNG byte array for iOS.
 * Uses Skia for image processing with white background.
 */
actual fun ImageBitmap.toPngByteArray(): ByteArray {
    // 1. Convert Compose ImageBitmap to Skia Bitmap
    val originalBitmap = this.asSkiaBitmap()

    // 2. Create a new Surface (drawing surface)
    // makeRasterN32Premul: Creates a standard pixel processing surface
    val surface = Surface.makeRasterN32Premul(originalBitmap.width, originalBitmap.height)
    val canvas = surface.canvas

    // 3. Fill entire surface with white color
    canvas.clear(Color.WHITE)

    // 4. Draw original image (with transparent areas) on top
    val originalImage = Image.makeFromBitmap(originalBitmap)
    canvas.drawImage(originalImage, 0f, 0f)

    // 5. Capture the result from Surface as a new Image
    val finalImage = surface.makeImageSnapshot()

    // 6. Export as PNG bytes
    return finalImage.encodeToData(EncodedImageFormat.PNG)?.bytes ?: ByteArray(0)
}

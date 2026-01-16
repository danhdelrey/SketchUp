package com.example.sketchup.platform

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIImage
import kotlin.coroutines.resume

/**
 * iOS implementation of ImageSaver.
 * Uses PHPhotoLibrary to save images to the Photos app.
 */
class IosImageSaver : ImageSaver {

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            // 1. Convert ByteArray (Kotlin) -> NSData (iOS)
            val nsData = bytes.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
            }

            // 2. Create UIImage from NSData
            val image = UIImage(data = nsData)

            // 3. Use PHPhotoLibrary to save the image
            PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                changeBlock = {
                    // Create request to add image to photo library
                    PHAssetChangeRequest.creationRequestForAssetFromImage(image)
                },
                completionHandler = { success, _ ->
                    if (success) {
                        continuation.resume(true)
                    } else {
                        continuation.resume(false)
                    }
                }
            )
        }
    }
}

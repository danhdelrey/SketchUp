package com.example.sketchup.platform

import com.example.sketchup.data.source.local.ImageStorageDataSource
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
 * iOS implementation of ImageStorageDataSource.
 * Uses PHPhotoLibrary to save images to the Photos app.
 */
class IosImageStorageDataSource : ImageStorageDataSource {

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun saveImage(imageData: ByteArray, fileName: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val nsData = imageData.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = imageData.size.toULong())
            }

            val image = UIImage(data = nsData)

            PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                changeBlock = {
                    PHAssetChangeRequest.creationRequestForAssetFromImage(image)
                },
                completionHandler = { success, _ ->
                    continuation.resume(success)
                }
            )
        }
    }
}

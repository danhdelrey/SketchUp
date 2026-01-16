
import com.example.sketchup.platform.ImageSaver
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

class IosImageSaver : ImageSaver {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            // 1. Chuyển đổi ByteArray (Kotlin) -> NSData (iOS)
            val nsData = bytes.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
            }

            // 2. Tạo UIImage từ NSData
            val image = UIImage(data = nsData)

            // 3. Sử dụng PHPhotoLibrary để lưu ảnh
            PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                changeBlock = {
                    // Tạo yêu cầu thêm ảnh vào thư viện
                    PHAssetChangeRequest.creationRequestForAssetFromImage(image)
                },
                completionHandler = { success, error ->
                    if (success) {
                        continuation.resume(true)
                    } else {
                        // Có thể log error?.localizedDescription() nếu cần debug
                        continuation.resume(false)
                    }
                }
            )
        }
    }
}
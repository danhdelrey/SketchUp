
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import com.example.sketchup.platform.ImageSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

// Class này cần Context để truy cập vào hệ thống file/media của Android
class AndroidImageSaver(private val context: Context) : ImageSaver {

    override suspend fun saveImage(bytes: ByteArray, fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Cấu hình thông tin file ảnh
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    // Từ Android 10 (Q) trở lên, cần chỉ định đường dẫn tương đối
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyDrawingApp")
                        put(MediaStore.Images.Media.IS_PENDING, 1) // Đánh dấu đang ghi file
                    }
                }

                // 2. Chèn thông tin vào MediaStore để lấy đường dẫn URI (chưa có dữ liệu)
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    // 3. Mở luồng ghi dữ liệu vào URI đó
                    val stream: OutputStream? = resolver.openOutputStream(it)

                    stream?.use { outputStream ->
                        // Chuyển ByteArray thành Bitmap rồi nén lại (hoặc ghi trực tiếp byte nếu muốn giữ nguyên gốc)
                        // Cách 1: Ghi trực tiếp byte (nhanh hơn, giữ nguyên chất lượng gốc)
                        outputStream.write(bytes)

                        // Cách 2: Nếu muốn decode ra Bitmap để xử lý thêm thì dùng BitmapFactory (không bắt buộc ở đây)
                    }

                    // 4. Kết thúc quá trình ghi (cho Android 10+)
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
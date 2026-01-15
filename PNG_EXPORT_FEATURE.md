# Tính năng Xuất Ảnh PNG - SketchUp App

## Tổng quan
Đã thêm tính năng xuất ảnh đã vẽ thành file PNG cho cả Android và Web platform.

## Các thay đổi đã thực hiện

### 1. Platform-Specific Image Savers

#### Android (`androidMain`)
- **File**: `ImageSaver.android.kt`
- **Chức năng**: 
  - Lưu ảnh vào thư mục `Pictures/SketchUp` 
  - Hỗ trợ Android 10+ (MediaStore API)
  - Hỗ trợ Android 9 và thấp hơn (legacy storage)
  - Tự động đặt tên file theo timestamp: `SketchUp_yyyyMMdd_HHmmss.png`

#### Web (`webMain` và `jsMain`)
- **File**: `ImageSaver.web.kt`, `ImageSaver.js.kt`
- **Chức năng**:
  - Chuyển đổi ImageBitmap sang PNG bằng Skia
  - Tạo data URL base64
  - Tự động download file qua browser
  - Tên file: `SketchUp_<timestamp>.png`

#### iOS (`iosMain`)
- **File**: `ImageSaver.ios.kt`
- **Trạng thái**: Stub implementation (chưa hoàn thiện)
- **Note**: Cần implement sau khi có yêu cầu

### 2. Dependency Injection Updates

#### Common Module (`commonMain`)
- **File**: `ImageSaver.kt`
  - Định nghĩa interface `ImageSaver`
  - Thêm expect function `provideImageSaver()`

- **File**: `KoinModule.kt`
  - Thêm `ImageSaver` vào Koin module
  - Thêm expect function `getPlatformContext()` để lấy context theo platform

#### Platform-Specific Koin Modules
- **Android**: `KoinModule.android.kt` - Cung cấp AndroidApp.appContext
- **Web/JS**: `KoinModule.web.kt`, `KoinModule.js.kt` - Không cần context
- **iOS**: `KoinModule.ios.kt` - Không cần context

### 3. UI Updates

#### DrawingScreen
- **Thêm nút Save**: Icon floppy disk ở góc trên bên phải
- **Chức năng**: 
  - Capture graphics layer thành ImageBitmap
  - Gọi `DrawingEvent.SavePng` với bitmap
  - Sử dụng coroutineScope để xử lý async

#### DrawingScreenModel
- **Cập nhật constructor**: Nhận `ImageSaver` từ Koin
- **Thực hiện saveImage()**: Gọi `imageSaver.saveImage()` trong coroutine

### 4. Android Permissions

#### AndroidManifest.xml
```xml
<!-- Quyền ghi file cho Android 9 trở xuống -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />

<!-- Quyền đọc file ảnh cho Android 13+ -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

**Note**: Android 10+ không cần WRITE_EXTERNAL_STORAGE vì sử dụng Scoped Storage

### 5. AndroidApp Update
- Thêm `android:name=".AndroidApp"` vào manifest để khởi tạo appContext

## Cách sử dụng

### Android
1. Vẽ hình trên canvas
2. Nhấn nút Save (icon 💾) ở góc trên bên phải
3. Ảnh sẽ được lưu vào `Pictures/SketchUp/SketchUp_<timestamp>.png`
4. Có thể xem trong Gallery hoặc Files app

### Web
1. Vẽ hình trên canvas
2. Nhấn nút Save (icon 💾) ở góc trên bên phải
3. Browser sẽ tự động download file `SketchUp_<timestamp>.png`
4. File sẽ ở trong thư mục Downloads của browser

## Kiến trúc

```
┌─────────────────────────────────────┐
│      DrawingScreen (UI)             │
│  ┌──────────────────────────────┐   │
│  │ Save Button → Capture Layer  │   │
│  └──────────────────────────────┘   │
└──────────────┬──────────────────────┘
               │ DrawingEvent.SavePng(bitmap)
               ▼
┌─────────────────────────────────────┐
│    DrawingScreenModel               │
│  - onEvent(SavePng)                 │
│  - saveImage(bitmap)                │
└──────────────┬──────────────────────┘
               │ imageSaver.saveImage()
               ▼
┌─────────────────────────────────────┐
│       ImageSaver (Interface)        │
└──────────────┬──────────────────────┘
               │
        ┌──────┴──────┬──────────┐
        ▼             ▼          ▼
┌──────────────┐ ┌─────────┐ ┌────────┐
│Android Saver │ │Web Saver│ │iOS Stub│
│MediaStore API│ │Skia+JS  │ │  TODO  │
└──────────────┘ └─────────┘ └────────┘
```

## Testing

### Android
- Kiểm tra trên Android 10+ (API 29+)
- Kiểm tra trên Android 9 và thấp hơn (API 28-)
- Xác nhận file được lưu trong Pictures/SketchUp
- Kiểm tra permissions được grant

### Web
- Test trên Chrome, Firefox, Safari
- Kiểm tra file download thành công
- Xác nhận định dạng PNG hợp lệ
- Kiểm tra chất lượng ảnh (800x600px)

## Các vấn đề tiềm ẩn & Giải pháp

### Android
**Vấn đề**: Runtime permissions cho WRITE_EXTERNAL_STORAGE trên Android 6+
**Giải pháp**: Cần thêm runtime permission request nếu target Android < 10

**Vấn đề**: File không hiển thị ngay trong Gallery
**Giải pháp**: Code đã xử lý bằng MediaStore, sẽ tự động scan

### Web
**Vấn đề**: Browser block popup/download
**Giải pháp**: User phải tương tác (click button) trước khi download

**Vấn đề**: Memory issues với ảnh lớn
**Giải pháp**: Canvas size cố định 800x600, phù hợp với web

## TODO - Cải tiến tương lai

1. **Thêm tùy chọn định dạng**: JPEG, WebP
2. **Chọn thư mục lưu**: Cho phép user chọn thư mục (Android)
3. **Compression options**: Điều chỉnh chất lượng ảnh
4. **Thông báo UI**: Toast/Snackbar khi lưu thành công/thất bại
5. **iOS Implementation**: Hoàn thiện ImageSaver cho iOS
6. **Share functionality**: Thêm nút Share thay vì chỉ Save
7. **Preview trước khi lưu**: Hiển thị preview dialog

## Kết luận

Tính năng xuất PNG đã được implement thành công cho Android và Web, sử dụng:
- ✅ Multiplatform architecture với expect/actual
- ✅ Dependency Injection với Koin
- ✅ Compose Graphics Layer API
- ✅ Platform-specific storage APIs
- ✅ Clean Architecture principles

Code đã sẵn sàng để build và test!


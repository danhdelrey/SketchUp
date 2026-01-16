package com.example.sketchup

import androidx.compose.ui.window.ComposeUIViewController
import com.example.sketchup.core.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    // Gọi initKoin cho iOS (nếu chưa gọi ở đâu khác như iOSApp.swift)
    // Lưu ý: Tốt nhất nên gọi initKoin trong hàm main của Kotlin hoặc @main của Swift.
    // Nhưng để nhanh gọn, bạn có thể kiểm tra xem Koin đã start chưa.
    // Cách an toàn nhất cho iOS là gọi initKoin() từ phía Swift (iosApp/iosApp/iOSApp.swift).
    // Nếu bạn muốn làm nhanh từ Kotlin:
    try {
        initKoin()
    } catch (e: Exception) {
        // Koin đã start rồi thì bỏ qua
    }

    return ComposeUIViewController { App() }
}
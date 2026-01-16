package com.example.sketchup

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.example.sketchup.di.initKoin
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // 1. Khởi tạo Koin (giữ nguyên logic cũ)
    initKoin()

    // 2. Sử dụng ComposeViewport thay cho CanvasBasedWindow
    // document.body!! nghĩa là render toàn bộ app vào thẻ <body> của HTML
    ComposeViewport(document.body!!) {
        App()
    }
}
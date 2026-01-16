package com.example.sketchup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sketchup.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Khởi tạo Koin và truyền Context vào
        initKoin {
            androidContext(applicationContext)
        }

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Dọn dẹp Koin khi thoát app để tránh lỗi "A Koin Application has already been started" khi mở lại
        stopKoin()
    }
}


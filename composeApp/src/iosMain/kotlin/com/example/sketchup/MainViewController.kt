package com.example.sketchup

import androidx.compose.ui.window.ComposeUIViewController
import com.example.sketchup.di.initKoin
import platform.UIKit.UIViewController

/**
 * Creates the main UIViewController for iOS.
 * Initializes Koin DI and sets up Compose UI.
 */
fun MainViewController(): UIViewController {
    // Initialize Koin for iOS
    try {
        initKoin()
    } catch (e: Exception) {
        // Koin already started, ignore
    }

    return ComposeUIViewController { App() }
}
package com.example.sketchup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sketchup.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin

/**
 * Main entry point for the Android application.
 * Initializes Koin DI and sets up Compose UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Koin with Android Context
        initKoin {
            androidContext(applicationContext)
        }

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up Koin when app exits to avoid "A Koin Application has already been started" error on reopen
        stopKoin()
    }
}


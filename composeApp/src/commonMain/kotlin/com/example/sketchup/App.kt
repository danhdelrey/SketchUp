package com.example.sketchup

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.example.sketchup.core.di.appModule
import com.example.sketchup.core.theme.AppTheme
import com.example.sketchup.view.features.drawing.screen.DrawingScreen
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        AppTheme {
            Navigator(DrawingScreen())
        }
    }
}
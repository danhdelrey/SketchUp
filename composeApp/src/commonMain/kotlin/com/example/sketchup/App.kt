package com.example.sketchup

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.example.sketchup.core.theme.AppTheme
import com.example.sketchup.presentation.drawing.screen.DrawingScreen

@Composable
fun App() {
    AppTheme {
        Navigator(DrawingScreen())
    }
}
package com.example.sketchup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.example.sketchup.core.di.appModule
import com.example.sketchup.core.theme.AppTheme
import com.example.sketchup.view.features.drawing.screen.DrawingScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

import sketchup.composeapp.generated.resources.Res
import sketchup.composeapp.generated.resources.compose_multiplatform

@Composable
fun App() {
    AppTheme {
        KoinApplication(application = {
            modules(appModule)
        }) {
            Navigator(DrawingScreen())
        }
    }
}
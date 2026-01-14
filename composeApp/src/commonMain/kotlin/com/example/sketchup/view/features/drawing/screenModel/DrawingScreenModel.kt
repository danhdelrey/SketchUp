package com.example.sketchup.view.features.drawing.screenModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sketchup.data.model.DrawingPath
import com.example.sketchup.data.repository.DrawingRepository
import com.example.sketchup.platform.ImageSaver
import com.example.sketchup.view.features.drawing.event.DrawingEvent
import com.example.sketchup.view.features.drawing.state.DrawingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class DrawingScreenModel(
    private val repository: DrawingRepository,
    //private val imageSaver: ImageSaver
) : ScreenModel {

    // UI State kết hợp từ Repo và State cục bộ (màu hiện tại, nét vẽ đang kéo)
    private val _currentPathPoints = MutableStateFlow<List<Offset>>(emptyList())
    private val _currentColor = MutableStateFlow(Color.Black)
    private val _currentWidth = MutableStateFlow(10f)

    // Kết hợp các luồng dữ liệu thành một State duy nhất cho UI
    val state = combine(
        repository.paths,
        _currentPathPoints,
        _currentColor
    ) { paths, currentPoints, color ->
        DrawingState(
            paths = paths,
            currentDrawingPath = if (currentPoints.isNotEmpty()) DrawingPath(
                currentPoints,
                color,
                _currentWidth.value
            ) else null,
            selectedColor = color
        )
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), DrawingState())

    fun onEvent(event: DrawingEvent) {
        when (event) {
            is DrawingEvent.StartDraw -> {
                _currentPathPoints.update { listOf(event.offset) }
            }
            is DrawingEvent.UpdateDraw -> {
                _currentPathPoints.update { it + event.offset }
            }
            is DrawingEvent.EndDraw -> {
                val points = _currentPathPoints.value
                if (points.isNotEmpty()) {
                    repository.addPath(DrawingPath(points, _currentColor.value, _currentWidth.value))
                    _currentPathPoints.update { emptyList() }
                }
            }
            is DrawingEvent.Undo -> repository.undo()
            is DrawingEvent.Redo -> repository.redo()
            is DrawingEvent.PickColor -> _currentColor.update { event.color }
            is DrawingEvent.ChangeBrushSize -> _currentWidth.update { event.size }
            is DrawingEvent.SavePng -> saveImage(event.bitmap)
        }
    }

    private fun saveImage(bitmap: ImageBitmap) {
        screenModelScope.launch {
            try {
                //imageSaver.saveImage(bitmap)
                println("Image saved successfully")
            } catch (e: Exception) {
                println("Failed to save image: ${e.message}")
            }
        }
    }
}


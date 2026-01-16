package com.example.sketchup.presentation.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sketchup.domain.model.Brush
import com.example.sketchup.domain.model.BrushType
import com.example.sketchup.domain.model.DrawingStroke
import com.example.sketchup.domain.usecase.AddStrokeUseCase
import com.example.sketchup.domain.usecase.ExportDrawingUseCase
import com.example.sketchup.domain.usecase.UndoRedoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for drawing screen using Clean Architecture
 * Depends on Use Cases, not on repositories directly
 */
class DrawingViewModel(
    private val addStrokeUseCase: AddStrokeUseCase,
    private val undoRedoUseCase: UndoRedoUseCase,
    private val exportDrawingUseCase: ExportDrawingUseCase
) : ScreenModel {

    private val _currentBrush = MutableStateFlow(Brush.DEFAULT)
    private val _currentStrokePoints = MutableStateFlow<List<Offset>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)
    private val _error = MutableStateFlow<String?>(null)

    // Combine all flows into single UI state
    val uiState = combine(
        undoRedoUseCase.canUndo(),
        undoRedoUseCase.canRedo(),
        _currentBrush,
        _currentStrokePoints,
        _isLoading,
        _message,
        _error
    ) { canUndo, canRedo, brush, points, loading, message, error ->
        DrawingUiState(
            currentBrush = brush,
            currentStrokePoints = points,
            canUndo = canUndo,
            canRedo = canRedo,
            isLoading = loading,
            message = message,
            error = error
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DrawingUiState()
    )

    fun onEvent(event: DrawingUiEvent) {
        when (event) {
            is DrawingUiEvent.OnTouchStart -> handleTouchStart(event.offset)
            is DrawingUiEvent.OnTouchMove -> handleTouchMove(event.offset)
            is DrawingUiEvent.OnTouchEnd -> handleTouchEnd()

            is DrawingUiEvent.OnUndo -> handleUndo()
            is DrawingUiEvent.OnRedo -> handleRedo()
            is DrawingUiEvent.OnClear -> handleClear()

            is DrawingUiEvent.OnBrushColorChange -> handleBrushColorChange(event.color)
            is DrawingUiEvent.OnBrushSizeChange -> handleBrushSizeChange(event.size)
            is DrawingUiEvent.OnBrushTypeChange -> handleBrushTypeChange(event.type)

            is DrawingUiEvent.OnExportImage -> handleExportImage(event.imageData)
            is DrawingUiEvent.OnToggleEraser -> handleToggleEraser()
        }
    }

    private fun handleTouchStart(offset: Offset) {
        _currentStrokePoints.value = listOf(offset)
    }

    private fun handleTouchMove(offset: Offset) {
        _currentStrokePoints.update { it + offset }
    }

    private fun handleTouchEnd() {
        val points = _currentStrokePoints.value
        if (points.size < 2) {
            _currentStrokePoints.value = emptyList()
            return
        }

        screenModelScope.launch {
            val stroke = DrawingStroke.fromOffsets(
                id = generateStrokeId(),
                offsets = points,
                brush = _currentBrush.value
            )

            addStrokeUseCase(stroke)
            _currentStrokePoints.value = emptyList()
        }
    }

    private fun handleUndo() {
        screenModelScope.launch {
            undoRedoUseCase.undo()
        }
    }

    private fun handleRedo() {
        screenModelScope.launch {
            undoRedoUseCase.redo()
        }
    }

    private fun handleClear() {
        // TODO: Implement clear functionality
    }

    private fun handleBrushColorChange(color: Color) {
        _currentBrush.update { it.copy(color = color) }
    }

    private fun handleBrushSizeChange(size: Float) {
        _currentBrush.update { it.copy(size = size) }
    }

    private fun handleBrushTypeChange(type: BrushType) {
        _currentBrush.update { it.copy(type = type) }
    }

    private fun handleToggleEraser() {
        _currentBrush.update {
            it.copy(
                type = if (it.type == BrushType.ERASER) BrushType.NORMAL else BrushType.ERASER
            )
        }
    }

    private fun handleExportImage(imageData: ByteArray) {
        screenModelScope.launch {
            _isLoading.value = true
            _message.value = "Đang xuất ảnh..."

            val fileName = "sketch_${System.currentTimeMillis()}"
            val result = exportDrawingUseCase(imageData, fileName)

            result.fold(
                onSuccess = { path ->
                    _message.value = "Lưu ảnh thành công! ✅"
                    _error.value = null
                },
                onFailure = { error ->
                    _error.value = "Lỗi: ${error.message}"
                    _message.value = null
                }
            )

            _isLoading.value = false

            // Clear message after 3 seconds
            kotlinx.coroutines.delay(3000)
            _message.value = null
            _error.value = null
        }
    }

    private fun generateStrokeId(): String {
        return "stroke_${System.currentTimeMillis()}_${(0..9999).random()}"
    }

    fun clearMessage() {
        _message.value = null
        _error.value = null
    }
}


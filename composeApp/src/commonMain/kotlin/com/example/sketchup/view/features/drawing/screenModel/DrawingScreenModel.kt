package com.example.sketchup.view.features.drawing.screenModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sketchup.data.model.DrawingPath
import com.example.sketchup.data.repository.DrawingRepository
import com.example.sketchup.platform.ImageSaver
import com.example.sketchup.view.features.drawing.event.DrawingEvent
import com.example.sketchup.view.features.drawing.state.DrawingState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock


class DrawingScreenModel(
    private val repository: DrawingRepository,
    private val imageSaver: ImageSaver
) : ScreenModel {

    // UI State kết hợp từ Repo và State cục bộ (màu hiện tại, nét vẽ đang kéo)
    private val _currentPathPoints = MutableStateFlow<List<Offset>>(emptyList())
    private val _currentColor = MutableStateFlow(Color.Black)
    private val _currentWidth = MutableStateFlow(10f)
    private val _isEraseMode = MutableStateFlow(false)
    private val _currentTouchPosition = MutableStateFlow<Offset?>(null)
    val currentBrushSize = _currentWidth.asStateFlow()

    // 1. Tạo kênh giao tiếp để gửi thông báo ra UI (Side Effect)
    // Channel.BUFFERED giúp giữ lại tin nhắn nếu UI chưa kịp nhận
    private val _messageChannel = Channel<String>(Channel.BUFFERED)
    val messageFlow = _messageChannel.receiveAsFlow()

    // Kết hợp các luồng dữ liệu thành một State duy nhất cho UI
    val state = combine(
        repository.paths,
        _currentPathPoints,
        _currentColor,
        _isEraseMode,
        _currentTouchPosition,
    ) { paths, currentPoints, color, isEraseMode, touchPosition ->
        DrawingState(
            paths = paths,
            currentDrawingPath = if (currentPoints.isNotEmpty()) DrawingPath(
                currentPoints,
                color,
                _currentWidth.value,
                isEraser = isEraseMode
            ) else null,
            selectedColor = color,
            brushSize = _currentWidth.value,
            isEraseMode = isEraseMode,
            currentTouchPosition = touchPosition
        )
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), DrawingState())

    fun onEvent(event: DrawingEvent) {
        when (event) {
            is DrawingEvent.StartDraw -> {
                _currentPathPoints.update { listOf(event.offset) }
                _currentTouchPosition.update { event.offset }
            }
            is DrawingEvent.UpdateDraw -> {
                _currentPathPoints.update { it + event.offset }
                _currentTouchPosition.update { event.offset }
            }
            is DrawingEvent.EndDraw -> {
                val points = _currentPathPoints.value
                if (points.isNotEmpty()) {
                    repository.addPath(DrawingPath(
                        points,
                        _currentColor.value,
                        _currentWidth.value,
                        isEraser = _isEraseMode.value
                    ))
                    _currentPathPoints.update { emptyList() }
                }
                _currentTouchPosition.update { null }
            }
            is DrawingEvent.Undo -> repository.undo()
            is DrawingEvent.Redo -> repository.redo()
            is DrawingEvent.PickColor -> _currentColor.update { event.color }
            is DrawingEvent.ChangeBrushSize -> _currentWidth.update { event.size }
            is DrawingEvent.SavePng -> saveImage(event.bytes)
            is DrawingEvent.ToggleEraseMode -> _isEraseMode.update { !it }
        }
    }

    private fun saveImage(bytes: ByteArray) {
        screenModelScope.launch {
            // 2. Thông báo bắt đầu lưu
            _messageChannel.send("Đang lưu ảnh...")

            try {
                // Giả lập độ trễ nhỏ để người dùng kịp đọc chữ "Đang lưu" (tuỳ chọn)
                // kotlinx.coroutines.delay(500)

                val success = imageSaver.saveImage(bytes, "sketch_${Clock.System.now().nanosecondsOfSecond}")
                if (success) {
                    _messageChannel.send("Lưu ảnh thành công! ✅")
                } else {
                    _messageChannel.send("Lưu ảnh thất bại ❌")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _messageChannel.send("Lỗi: ${e.message}")
            }
        }
    }
}


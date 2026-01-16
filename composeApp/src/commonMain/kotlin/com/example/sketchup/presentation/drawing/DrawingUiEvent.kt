package com.example.sketchup.presentation.drawing
}
    data object OnToggleEraser : DrawingUiEvent
    
    }
        override fun hashCode(): Int = imageData.contentHashCode()
        
        }
            return imageData.contentEquals(other.imageData)
            other as OnExportImage
            if (other == null || this::class != other::class) return false
            if (this === other) return true
        override fun equals(other: Any?): Boolean {
    data class OnExportImage(val imageData: ByteArray) : DrawingUiEvent {
    
    data class OnBrushTypeChange(val type: BrushType) : DrawingUiEvent
    data class OnBrushSizeChange(val size: Float) : DrawingUiEvent
    data class OnBrushColorChange(val color: Color) : DrawingUiEvent
    
    data object OnClear : DrawingUiEvent
    data object OnRedo : DrawingUiEvent
    data object OnUndo : DrawingUiEvent
    
    data object OnTouchEnd : DrawingUiEvent
    data class OnTouchMove(val offset: Offset) : DrawingUiEvent
    data class OnTouchStart(val offset: Offset) : DrawingUiEvent
sealed interface DrawingUiEvent {
 */
 * Separates UI events from domain events
 * UI Events for drawing screen
/**

import com.example.sketchup.domain.model.BrushType
import com.example.sketchup.domain.model.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset



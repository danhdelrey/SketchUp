package com.example.sketchup.presentation.drawing.model

/**
 * Side effects for the drawing screen.
 * Represents one-time events that should be shown to the user.
 */
sealed interface DrawingEffect {
    data class ShowMessage(val message: String) : DrawingEffect
    data class ShowError(val error: String) : DrawingEffect
}

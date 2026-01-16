package com.example.sketchup.domain.usecase

import com.example.sketchup.domain.model.DrawingStroke
import com.example.sketchup.domain.repository.DrawingRepository

/**
 * Use case for adding a stroke to drawing
 * Encapsulates business logic
 */
class AddStrokeUseCase(
    private val repository: DrawingRepository
) {
    suspend operator fun invoke(stroke: DrawingStroke) {
        // Business rules can be added here
        // e.g., validate stroke has at least 2 points
        if (stroke.points.size < 2) {
            return // Don't add invalid strokes
        }
        
        repository.addStroke(stroke)
    }
}


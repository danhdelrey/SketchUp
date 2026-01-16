package com.example.sketchup.domain.usecase

import com.example.sketchup.domain.model.DrawingPath
import com.example.sketchup.domain.repository.DrawingRepository

/**
 * Use case for adding a new drawing path.
 * Encapsulates the business logic for adding paths.
 */
class AddDrawingPathUseCase(
    private val repository: DrawingRepository
) {
    operator fun invoke(path: DrawingPath) {
        repository.addPath(path)
    }
}

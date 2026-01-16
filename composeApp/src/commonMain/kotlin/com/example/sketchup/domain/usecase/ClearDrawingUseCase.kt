package com.example.sketchup.domain.usecase

import com.example.sketchup.domain.repository.DrawingRepository

/**
 * Use case for clearing all drawing paths.
 */
class ClearDrawingUseCase(
    private val repository: DrawingRepository
) {
    operator fun invoke() {
        repository.clear()
    }
}

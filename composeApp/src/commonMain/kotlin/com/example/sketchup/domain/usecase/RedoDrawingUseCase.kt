package com.example.sketchup.domain.usecase

import com.example.sketchup.domain.repository.DrawingRepository

/**
 * Use case for redoing the last undone action.
 */
class RedoDrawingUseCase(
    private val repository: DrawingRepository
) {
    operator fun invoke() {
        if (repository.canRedo()) {
            repository.redo()
        }
    }
}

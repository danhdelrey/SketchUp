package com.example.sketchup.domain.usecase

import com.example.sketchup.domain.model.DrawingPath
import com.example.sketchup.domain.repository.DrawingRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * Use case for observing drawing paths.
 * Provides reactive updates to the UI layer.
 */
class ObserveDrawingPathsUseCase(
    private val repository: DrawingRepository
) {
    operator fun invoke(): StateFlow<List<DrawingPath>> {
        return repository.paths
    }
}

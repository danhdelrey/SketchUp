package com.example.sketchup.examples

/**
 * EXAMPLE: How to add a new feature using Clean Architecture
 * 
 * Feature: Auto-save drawing periodically
 */

// ============================================
// STEP 1: Create Domain Model (if needed)
// ============================================
// domain/model/AutoSaveConfig.kt
data class AutoSaveConfig(
    val enabled: Boolean = true,
    val intervalSeconds: Long = 30,
    val maxAutoSaves: Int = 5
)

// ============================================
// STEP 2: Add Repository Interface Method
// ============================================
// domain/repository/DrawingRepository.kt
interface DrawingRepository {
    // ...existing methods...
    
    // NEW: Auto-save related methods
    suspend fun autoSaveDrawing(): Result<String>
    suspend fun getAutoSaveConfig(): AutoSaveConfig
    suspend fun setAutoSaveConfig(config: AutoSaveConfig)
}

// ============================================
// STEP 3: Create Use Case
// ============================================
// domain/usecase/AutoSaveDrawingUseCase.kt
class AutoSaveDrawingUseCase(
    private val repository: DrawingRepository,
    private val storage: KeyValueStorage
) {
    companion object {
        private const val KEY_LAST_AUTO_SAVE = "last_auto_save_time"
    }
    
    suspend operator fun invoke(): Result<String> {
        // Business logic: Check if enough time has passed
        val config = repository.getAutoSaveConfig()
        if (!config.enabled) {
            return Result.failure(Exception("Auto-save is disabled"))
        }
        
        val lastSaveTime = storage.getLong(KEY_LAST_AUTO_SAVE, default = 0)
        val currentTime = System.currentTimeMillis()
        val elapsedSeconds = (currentTime - lastSaveTime) / 1000
        
        if (elapsedSeconds < config.intervalSeconds) {
            return Result.failure(Exception("Too soon to auto-save"))
        }
        
        // Perform auto-save
        val result = repository.autoSaveDrawing()
        
        // Update last save time
        if (result.isSuccess) {
            storage.putLong(KEY_LAST_AUTO_SAVE, currentTime)
        }
        
        return result
    }
}

// ============================================
// STEP 4: Update ViewModel
// ============================================
// presentation/drawing/DrawingViewModel.kt
class DrawingViewModel(
    // ...existing dependencies...
    private val autoSaveUseCase: AutoSaveDrawingUseCase
) : ScreenModel {
    
    init {
        // Start auto-save job
        startAutoSaveJob()
    }
    
    private fun startAutoSaveJob() {
        screenModelScope.launch {
            while (true) {
                delay(30_000) // Check every 30 seconds
                autoSaveUseCase().fold(
                    onSuccess = { id ->
                        println("Auto-saved: $id")
                    },
                    onFailure = { error ->
                        // Silently fail for auto-save
                        println("Auto-save skipped: ${error.message}")
                    }
                )
            }
        }
    }
}

// ============================================
// STEP 5: Update DI
// ============================================
// di/Koin.kt
val commonModule = module {
    // ...existing code...
    
    // NEW: Auto-save use case
    singleOf(::AutoSaveDrawingUseCase)
}

// ============================================
// STEP 6: Platform Implementation (Example: Android)
// ============================================
// androidMain/data/repository/InMemoryDrawingRepository.kt
class InMemoryDrawingRepository(
    private val context: Context
) : DrawingRepository {
    // ...existing code...
    
    override suspend fun autoSaveDrawing(): Result<String> {
        val drawing = _currentDrawing.value
        val fileName = "autosave_${System.currentTimeMillis()}.json"
        
        return try {
            // Save to internal storage (Android-specific)
            val file = File(context.filesDir, fileName)
            file.writeText(serializeDrawing(drawing))
            Result.success(fileName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAutoSaveConfig(): AutoSaveConfig {
        // Load from SharedPreferences
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return AutoSaveConfig(
            enabled = prefs.getBoolean("auto_save_enabled", true),
            intervalSeconds = prefs.getLong("auto_save_interval", 30),
            maxAutoSaves = prefs.getInt("max_auto_saves", 5)
        )
    }
    
    private fun serializeDrawing(drawing: Drawing): String {
        // JSON serialization logic
        return "..." 
    }
}

// ============================================
// COMPLETE! New feature added following Clean Architecture
// ============================================

/**
 * Benefits of this approach:
 * 
 * 1. Business Logic (Use Case) is testable:
 *    - Mock repository and storage
 *    - Test time interval logic
 *    - Test configuration handling
 * 
 * 2. Platform-specific:
 *    - Android: Use internal storage
 *    - iOS: Use Documents directory
 *    - Web: Use IndexedDB
 * 
 * 3. Separation of Concerns:
 *    - ViewModel just calls use case
 *    - Use case contains business rules
 *    - Repository handles persistence
 * 
 * 4. Easily extensible:
 *    - Add cloud sync by injecting cloud repository
 *    - Add encryption by wrapping use case
 *    - Change storage strategy without touching ViewModel
 */


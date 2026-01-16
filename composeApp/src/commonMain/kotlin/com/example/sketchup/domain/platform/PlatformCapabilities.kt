package com.example.sketchup.domain.platform
expect class PlatformInfo() : PlatformCapabilities
 */
 * Expect/Actual pattern for platform capabilities
/**

}
    fun getPlatformVersion(): String
     */
     * Get platform version
    /**
    
    fun getPlatformName(): String
     */
     * Get platform name for analytics/debugging
    /**
    
    fun supportsHaptics(): Boolean
     */
     * Check if platform supports haptic feedback
    /**
    
    fun supportsPressureSensitivity(): Boolean
     */
     * Check if platform supports pressure sensitivity
    /**
interface PlatformCapabilities {
 */
 * Each platform can provide different capabilities
 * Platform-specific capabilities interface
/**



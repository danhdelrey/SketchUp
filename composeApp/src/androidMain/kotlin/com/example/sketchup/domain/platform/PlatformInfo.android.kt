package com.example.sketchup.domain.platform

/**
 * Android platform capabilities
 */
actual class PlatformInfo : PlatformCapabilities {
    override fun supportsPressureSensitivity(): Boolean {
        // Android devices with stylus support pressure
        return true
    }
    
    override fun supportsHaptics(): Boolean {
        return true
    }
    
    override fun getPlatformName(): String = "Android"
    
    override fun getPlatformVersion(): String {
        return "Android ${android.os.Build.VERSION.RELEASE}"
    }
}


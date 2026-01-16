package com.example.sketchup.domain.platform

import platform.UIKit.UIDevice

/**
 * iOS platform capabilities
 */
actual class PlatformInfo : PlatformCapabilities {
    override fun supportsPressureSensitivity(): Boolean {
        // iOS devices with Apple Pencil support pressure
        return true
    }
    
    override fun supportsHaptics(): Boolean {
        return true
    }
    
    override fun getPlatformName(): String = "iOS"
    
    override fun getPlatformVersion(): String {
        return "iOS ${UIDevice.currentDevice.systemVersion}"
    }
}


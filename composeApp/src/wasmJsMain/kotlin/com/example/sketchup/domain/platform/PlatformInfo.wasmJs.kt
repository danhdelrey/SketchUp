package com.example.sketchup.domain.platform

import kotlinx.browser.window

/**
 * Web platform capabilities
 */
actual class PlatformInfo : PlatformCapabilities {
    override fun supportsPressureSensitivity(): Boolean {
        // Web supports pressure via PointerEvent API
        return js("'PointerEvent' in window && 'pressure' in PointerEvent.prototype") as Boolean
    }

    override fun supportsHaptics(): Boolean {
        // Web Vibration API
        return js("'vibrate' in navigator") as Boolean
    }

    override fun getPlatformName(): String = "Web"

    override fun getPlatformVersion(): String {
        return window.navigator.userAgent
    }
}


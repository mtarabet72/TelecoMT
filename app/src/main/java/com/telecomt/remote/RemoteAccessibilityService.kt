package com.telecomt.remote

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class RemoteAccessibilityService : AccessibilityService() {

    companion object {
        var instance: RemoteAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Pas besoin d'écouter les événements, on utilise juste
        // performGlobalAction() depuis le serveur HTTP.
    }

    override fun onInterrupt() {}

    fun sendGlobalAction(action: Int): Boolean {
        return performGlobalAction(action)
    }
}

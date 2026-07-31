package com.telecomt.remote

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
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

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun sendGlobalAction(action: Int): Boolean = performGlobalAction(action)

    fun swipe(direction: String): Boolean {
        val metrics = resources.displayMetrics
        val cx = metrics.widthPixels / 2f
        val cy = metrics.heightPixels / 2f
        val distance = minOf(metrics.widthPixels, metrics.heightPixels) * 0.25f

        var x1 = cx; var y1 = cy; var x2 = cx; var y2 = cy
        when (direction) {
            "up" -> { y1 = cy + distance; y2 = cy - distance }
            "down" -> { y1 = cy - distance; y2 = cy + distance }
            "left" -> { x1 = cx + distance; x2 = cx - distance }
            "right" -> { x1 = cx - distance; x2 = cx + distance }
            else -> return false
        }

        val path = Path().apply { moveTo(x1, y1); lineTo(x2, y2) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 200))
            .build()
        return dispatchGesture(gesture, null, null)
    }

    fun tapCenter(): Boolean {
        val metrics = resources.displayMetrics
        val cx = metrics.widthPixels / 2f
        val cy = metrics.heightPixels / 2f
        val path = Path().apply { moveTo(cx, cy); lineTo(cx, cy) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        return dispatchGesture(gesture, null, null)
    }
}

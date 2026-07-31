package com.telecomt.remote

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import fi.iki.elonen.NanoHTTPD

class CommandHttpServer(private val context: Context, port: Int) : NanoHTTPD(port) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun serve(session: IHTTPSession): Response {
        val params = session.parameters
        val code = params["code"]?.firstOrNull()?.lowercase() ?: ""

        val result = when (code) {
            "home" -> globalAction(AccessibilityService.GLOBAL_ACTION_HOME)
            "back" -> globalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            "recents" -> globalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
            "notifications" -> globalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
            "power" -> globalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
            "dpad_up" -> RemoteAccessibilityService.instance?.swipe("up") ?: false
            "dpad_down" -> RemoteAccessibilityService.instance?.swipe("down") ?: false
            "dpad_left" -> RemoteAccessibilityService.instance?.swipe("left") ?: false
            "dpad_right" -> RemoteAccessibilityService.instance?.swipe("right") ?: false
            "dpad_center" -> RemoteAccessibilityService.instance?.tapCenter() ?: false
            "volume_up" -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                true
            }
            "volume_down" -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
                true
            }
            "mute" -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_TOGGLE_MUTE, AudioManager.FLAG_SHOW_UI)
                true
            }
            "ping" -> true
            else -> false
        }

        val response = newFixedLengthResponse(
            if (result) Response.Status.OK else Response.Status.BAD_REQUEST,
            "application/json",
            """{"success":$result,"code":"$code"}"""
        )
        response.addHeader("Access-Control-Allow-Origin", "*")
        response.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS")
        return response
    }

    private fun globalAction(action: Int): Boolean {
        val service = RemoteAccessibilityService.instance
        return service?.sendGlobalAction(action) ?: false
    }
}

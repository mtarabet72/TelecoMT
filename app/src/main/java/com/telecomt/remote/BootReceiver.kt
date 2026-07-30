package com.telecomt.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            try {
                val server = CommandHttpServer(context.applicationContext, 8080)
                server.start()
            } catch (e: Exception) {
                // déjà démarré ou port occupé
            }
        }
    }
}

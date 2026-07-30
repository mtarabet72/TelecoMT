package com.telecomt.remote

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.text.format.Formatter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils

class MainActivity : AppCompatActivity() {

    private var server: CommandHttpServer? = null
    private val port = 8080

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusText = findViewById<TextView>(R.id.statusText)
        val ipText = findViewById<TextView>(R.id.ipText)

        startServerIfNeeded()

        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val ip = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)

        statusText.text = if (isAccessibilityServiceEnabled()) {
            "Serveur actif — service d'accessibilité OK"
        } else {
            "⚠ Active le service d'accessibilité dans Paramètres"
        }
        ipText.text = "http://$ip:$port"
    }

    private fun startServerIfNeeded() {
        if (server == null) {
            server = CommandHttpServer(applicationContext, port)
            try {
                server?.start()
            } catch (e: Exception) {
                // Le port est peut-être déjà utilisé par une instance précédente (BootReceiver)
            }
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val serviceName = "$packageName/${RemoteAccessibilityService::class.java.canonicalName}"
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        while (colonSplitter.hasNext()) {
            if (colonSplitter.next().equals(serviceName, ignoreCase = true)) return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        // On ne stoppe pas le serveur ici : on veut qu'il continue en fond
        // même si l'activité est fermée.
    }
}

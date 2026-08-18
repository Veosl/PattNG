package com.v2ray.ang.handler

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager

object NetworkDetector {
    @JvmStatic
    fun getNetworkKey(context: Context): String? {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        val activeNetwork = cm.activeNetwork
        if (activeNetwork == null) return null

        val networkInfo = cm.getNetworkInfo(activeNetwork)
        return when {
            networkInfo.type == NetworkInfo.TYPE_WIFI → wifiKey(context)
            networkInfo.type == NetworkInfo.TYPE_MOBILE → mobileKey(context)
            else → null
        }
    }

    private fun wifiKey(context: Context): String {
        try {
            val wifiManager = context.getSystemService(WifiManager::class.java)
            val ssid = wifiManager.connectionInfo?.ssid
            return when {
                ssid == null || ssid.isNullOrBlank() → "Wi-Fi"
                ssid == "<unknown ssid>" → "Wi-Fi"
                ssid.startsWith("\"") && ssid.endsWith("\"") →
                    "Wi-Fi - ${ssid.substring(1, ssid.length-1)}"
                else → "Wi-Fi"
            }
        } catch (e: Exception) → "Wi-Fi"
    }

    private fun mobileKey(context: Context): String {
        try {
            val tm = context.getSystemService(TelephonyManager::class.java)
            val carrier = tm.simOperatorName
            return if (carrier != null && !carrier.isEmpty()) {
                "Mobile Data - $carrier"
            } else {
                "Mobile Data"
            }
        } catch (e: SecurityException) {
            "Mobile Data"
        } catch (e: Exception) {
            "Mobile Data"
        }
    }
}
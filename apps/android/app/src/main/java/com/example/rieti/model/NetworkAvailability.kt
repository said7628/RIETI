package com.example.rieti.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Comprueba conectividad validada antes de iniciar una solicitud de autenticacion. */
object NetworkAvailability {
    fun isOnline(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = connectivity.activeNetwork ?: return false
        val capabilities = connectivity.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

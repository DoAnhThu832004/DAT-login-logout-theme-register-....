package com.example.app.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Kiểm tra trạng thái mạng tại thời điểm hiện tại (synchronous).
 */
fun isNetworkAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

/**
 * Flow phát ra trạng thái mạng real-time (true = có mạng, false = mất mạng).
 */
fun networkStateFlow(context: Context) = callbackFlow {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Phát giá trị ban đầu
    trySend(isNetworkAvailable(context))

    val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            trySend(true)
        }
        override fun onLost(network: Network) {
            trySend(isNetworkAvailable(context)) // kiểm tra lại vì có thể còn mạng khác
        }
        override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
            val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            trySend(hasInternet)
        }
    }

    val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    cm.registerNetworkCallback(request, callback)

    awaitClose {
        cm.unregisterNetworkCallback(callback)
    }
}

/**
 * Composable trả về State<Boolean> theo dõi trạng thái mạng real-time.
 * true = có kết nối, false = không có kết nối.
 */
@Composable
fun rememberNetworkState(): State<Boolean> {
    val context = LocalContext.current
    return produceState(initialValue = isNetworkAvailable(context)) {
        networkStateFlow(context).collect { value = it }
    }
}

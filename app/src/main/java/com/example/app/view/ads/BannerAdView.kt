package com.example.app.view.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.app.admob.AdMobManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.TEST_BANNER_ID,
    screenName: String = "general_screen"
) {
    // Kiểm tra cờ tính năng từ Remote Config
    if (!com.example.app.analytics.RemoteConfigManager.isShowAds()) return

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                adListener = object : com.google.android.gms.ads.AdListener() {
                    override fun onAdImpression() {
                        com.example.app.analytics.AnalyticsHelper.logAdImpression(
                            adFormat = "banner",
                            adUnitId = adUnitId,
                            screenName = screenName
                        )
                    }

                    override fun onAdClicked() {
                        com.example.app.analytics.AnalyticsHelper.logAdClick(
                            adFormat = "banner",
                            adUnitId = adUnitId
                        )
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

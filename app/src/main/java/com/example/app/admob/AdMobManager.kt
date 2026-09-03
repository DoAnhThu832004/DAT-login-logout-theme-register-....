package com.example.app.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Official Google Test Ad Unit IDs
    const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading: Boolean = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading: Boolean = false

    /**
     * Khởi tạo Mobile Ads SDK và preload sẵn Interstitial Ad
     */
    fun init(context: Context) {
        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "AdMob SDK Initialized: $initializationStatus")
            loadInterstitialAd(context)
        }
    }

    /**
     * Preload Interstitial Ad vào bộ nhớ
     */
    fun loadInterstitialAd(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial Ad Loaded successfully")
                    interstitialAd = ad
                    isInterstitialLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Failed to load Interstitial Ad: ${loadAdError.message}")
                    interstitialAd = null
                    isInterstitialLoading = false
                }
            }
        )
    }

    /**
     * Hiển thị Interstitial Ad:
     * - Khi tắt Ad hoặc Ad lỗi thì gọi onAdDismissed()
     * - Tự động preload ad tiếp theo
     */
    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        val currentAd = interstitialAd
        if (currentAd != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial Ad Dismissed")
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Interstitial Ad failed to show: ${adError.message}")
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial Ad showed full screen content.")
                    // Telemetry: ad_impression (interstitial)
                    com.example.app.analytics.AnalyticsHelper.logAdImpression(
                        adFormat = "interstitial",
                        adUnitId = TEST_INTERSTITIAL_ID,
                        screenName = "interstitial_dialog"
                    )
                }

                override fun onAdClicked() {
                    // Telemetry: ad_click (interstitial)
                    com.example.app.analytics.AnalyticsHelper.logAdClick(
                        adFormat = "interstitial",
                        adUnitId = TEST_INTERSTITIAL_ID
                    )
                }
            }
            currentAd.show(activity)
        } else {
            Log.d(TAG, "Interstitial Ad not ready yet, invoking callback immediately.")
            loadInterstitialAd(activity)
            onAdDismissed()
        }
    }

    /**
     * Load Rewarded Ad
     */
    fun loadRewardedAd(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            TEST_REWARDED_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Rewarded Ad Loaded successfully")
                    rewardedAd = ad
                    isRewardedLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Failed to load Rewarded Ad: ${loadAdError.message}")
                    rewardedAd = null
                    isRewardedLoading = false
                }
            }
        )
    }

    /**
     * Hiển thị Rewarded Ad
     */
    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdDismissed: () -> Unit
    ) {
        val currentAd = rewardedAd
        if (currentAd != null) {
            var rewardEarned = false

            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd(activity)
                    if (rewardEarned) {
                        onUserEarnedReward()
                    }
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    // Telemetry: ad_impression (rewarded)
                    com.example.app.analytics.AnalyticsHelper.logAdImpression(
                        adFormat = "rewarded",
                        adUnitId = TEST_REWARDED_ID,
                        screenName = "rewarded_dialog"
                    )
                }

                override fun onAdClicked() {
                    // Telemetry: ad_click (rewarded)
                    com.example.app.analytics.AnalyticsHelper.logAdClick(
                        adFormat = "rewarded",
                        adUnitId = TEST_REWARDED_ID
                    )
                }
            }

            currentAd.show(activity) { rewardItem ->
                Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                // Telemetry: ad_reward_completed
                com.example.app.analytics.AnalyticsHelper.logAdRewardCompleted(
                    rewardType = rewardItem.type,
                    rewardAmount = rewardItem.amount.toLong()
                )
                rewardEarned = true
            }
        } else {
            loadRewardedAd(activity)
            onAdDismissed()
        }
    }
}

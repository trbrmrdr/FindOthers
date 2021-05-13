package com.trbr.s5differences.Ads

import com.applovin.mediation.AppLovinExtras
import com.applovin.mediation.ApplovinAdapter
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.google.ads.mediation.facebook.FacebookAdapter
import com.google.ads.mediation.facebook.FacebookExtras
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.trbr.s5differences.App
import com.trbr.s5differences.Helper.LogUtils


val APPLOVIN_SDK_KEY = "sdkKey"
val FACEBOOK_VIDEO_PLACEMENT_KEY = "YOUR_PLACEMENT_ID"
val FACEBOOK_INTERSTITIAL_PLACEMENT_KEY = "YOUR_PLACEMENT_ID"

val ADMOB_INTERSTITIAL_KEY = "ca-app-pub-3940256099942544/1033173712"
val ADMOB_REWARDED_KEY = "ca-app-pub-3940256099942544/5224354917"

object AdsManager {

    object FacebookAds {

        lateinit var rewardedVideoAd: RewardedVideoAd
        lateinit var interstitialAd: InterstitialAd
        val rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onError(ad: Ad?, error: AdError) {
                LogUtils.i(
                    "Rewarded video ad failed to load: " + error.getErrorMessage()
                )
            }

            override fun onAdLoaded(ad: Ad) {
                LogUtils.i(
                    "Rewarded video ad is loaded and ready to be displayed!"
                )
            }

            override fun onAdClicked(ad: Ad) {
                LogUtils.i("Rewarded video ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                LogUtils.i("Rewarded video ad impression logged!")
            }

            override fun onRewardedVideoCompleted() {
                LogUtils.i("Rewarded video completed!")
                // Call method to give reward
                // giveReward();
            }

            override fun onRewardedVideoClosed() {
                LogUtils.i("Rewarded video ad closed!")
            }
        }

        var interstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
                LogUtils.i("Interstitial ad displayed.")
            }

            override fun onInterstitialDismissed(ad: Ad) {
                // Interstitial dismissed callback
                LogUtils.i("Interstitial ad dismissed.")
            }

            override fun onError(ad: Ad?, adError: AdError) {
                // Ad error callback
                LogUtils.i("Interstitial ad failed to load: " + adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                // Interstitial ad is loaded and ready to be displayed
                LogUtils.i("Interstitial ad is loaded and ready to be displayed!")
                // Show the ad
//                interstitialAd.show()
            }

            override fun onAdClicked(ad: Ad) {
                // Ad clicked callback
                LogUtils.i("Interstitial ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                // Ad impression logged callback
                LogUtils.i("Interstitial ad impression logged!")
            }
        }

        init {
            var extras = FacebookExtras()
                .setNativeBanner(true)
                .build()

            var adrequest = AdRequest.Builder()
                .addNetworkExtrasBundle(FacebookAdapter::class.java, extras)
                .build()


            AudienceNetworkAds.initialize(App.application)
        }

        fun load() {
            rewardedVideoAd = RewardedVideoAd(
                App.application,
                FACEBOOK_VIDEO_PLACEMENT_KEY
            )
            rewardedVideoAd.loadAd(
                rewardedVideoAd.buildLoadAdConfig()
                    .withAdListener(rewardedVideoAdListener)
                    .build()
            )

            interstitialAd = InterstitialAd(
                App.application,
                FACEBOOK_INTERSTITIAL_PLACEMENT_KEY
            )
            interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build()
            )
        }

        fun isAvail(video: Boolean): Boolean {
            if (video)
                return rewardedVideoAd.isAdLoaded && !rewardedVideoAd.isAdInvalidated
            return interstitialAd.isAdLoaded && !interstitialAd.isAdInvalidated
        }

        fun show(video: Boolean): Boolean {
            if (video)
                return rewardedVideoAd.show()

            return interstitialAd.show()
        }


    }

    object Applovin {
        init {

            val extras = AppLovinExtras.Builder()
                .setMuteAudio(true)
                .build()
            val request = AdRequest.Builder()
                .addNetworkExtrasBundle(ApplovinAdapter::class.java, extras)
                .build()

            AppLovinSdk.getInstance(
                APPLOVIN_SDK_KEY, null,
                App.application
            ).initializeSdk()
            AppLovinPrivacySettings.setHasUserConsent(
                true,
                App.application
            )
        }
    }

    object Admob {
        private var mInterstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd? = null

        val interstitial_callback = object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                LogUtils.i(adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                LogUtils.i("Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        }

        var mRewardedAd: RewardedAd? = null
        val rewarded_ad_kallback = object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                LogUtils.i(adError?.message)
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                LogUtils.i("Ad was loaded.")
                mRewardedAd = rewardedAd
            }
        }


        init {
            MobileAds.initialize(App.application) { initializationStatus ->
                val statusMap = initializationStatus.adapterStatusMap
                for (adapterClass in statusMap.keys) {
                    val status = statusMap[adapterClass]
                    LogUtils.i(
                        String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status!!.description, status.latency
                        )
                    )
                }
            }

            /*MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf("ABCDEF012345"))
                    .build()
            )*/
        }

        fun load() {
            var adRequest = AdRequest.Builder().build()

            com.google.android.gms.ads.interstitial.InterstitialAd.load(
                App.application, ADMOB_INTERSTITIAL_KEY, adRequest, interstitial_callback
            )

            RewardedAd.load(App.application, ADMOB_REWARDED_KEY, adRequest, rewarded_ad_kallback)
        }

        fun show(video: Boolean) {
            if (video) {
                mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        LogUtils.i("Ad was dismissed.")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError?) {
                        LogUtils.i("Ad failed to show.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        LogUtils.i("Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                        // Don't set the ad reference to null to avoid showing the ad a second time.
                        mRewardedAd = null
                    }
                }
                mRewardedAd?.show(App.activity, { rewardItem ->
                    var rewardAmount = rewardItem.amount
                    var rewardType = rewardItem.type
                    LogUtils.i("User earned the reward.")
                })
                return
            }


            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    LogUtils.i("Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    load()
                }

                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    LogUtils.i("Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    LogUtils.i("Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(App.activity)

        }
    }

    init {
        FacebookAds.apply { load() }
//        Applovin
//        Admob.apply { load() }
    }


}
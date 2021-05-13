package com.trbr.s5differences

import android.os.Bundle
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import java.util.*


enum class EventsName(val event: String) {
    MigrationComplete("migration_complete"),
    LevelComplete("level_complete"),
    ImageComplete("image_complete"),
    ImageReplay("image_replay"),
    LevelSpotsTime("spots_time"),
    AntiCheatTimer("cheat_timer"),
    HintUse("hint_use"),
    HintBuy("hint_buy"),
    AdClick("ad_click"),
    AdImpression("ad_impression"),
    QuestStarsReward("stars_reward"),
    SelectCategory("select_category"),
    DailyReward("daily_reward"),
    ABTestDailyReward("ab_reward07-"),
    ABTestCategories("ab_categories10-"),
    VideoAdImpression("video_ad_impression"),
    AdVideoLoaded("ad_video_loaded"),
    AdVideoNoFill("ad_video_nofill"),
    ShowInterstitial("show_interstial"), // в название эвента опечатка. для iOS версии НЕ исправлять...
    InterstitialLoaded("ad_interstial_loaded"),
    InterstitialNoFill("ad_interstitial_nofill"),
    AdInterstitialMilestone("ad_interstitial"),
    AdVideoMilestone("ad_video"),
    ABTestLevels("ab_levels0803-"),
    BankOpen("bank_open"),
    AttStatus("att_status"),
    ImageLike("like"),
    LiveopsStart("liveops_start"),
    LiveopsComplete("liveops_complete"),
    PushStatus("push_status"),
}


object Analytics {

    val firebaseAnalytics: FirebaseAnalytics
    val fb_logger: AppEventsLogger

    init {
        //Yandex
        val config =
            YandexMetricaConfig.newConfigBuilder(App.application.getString(R.string.appmetrica_pub_key))
                .build()
        YandexMetrica.activate(App.application, config)
        YandexMetrica.enableActivityAutoTracking(App.application)


        //Facebook
//        AppEventsLogger.activateApp(App.application)
        fb_logger = AppEventsLogger.newLogger(App.application)
       /* if (BuildConfig.DEBUG) {
//            FacebookSdk.setAutoLogAppEventsEnabled(false)
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
        }*/

        firebaseAnalytics = FirebaseAnalytics.getInstance(App.application)

    }


    fun log(event_name: String, params: Map<String, Any> = mapOf()) {
        // без необходимости не забиваем аналитику
//        if (BuildConfig.DEBUG) return

        YandexMetrica.reportEvent(event_name, params)

        val bandle_params = Bundle()
        for (param in params) {
            bandle_params.putString(param.key, param.value.toString())
        }

        firebaseAnalytics.logEvent(event_name, bandle_params)

        fb_logger.logEvent(event_name, bandle_params)
    }


    fun onShowInterstitial(network_id: String) {
        log(EventsName.ShowInterstitial.event, mapOf("network" to network_id))
    }

    fun onLoadInterstitial(network_id: String, unitName: String = "") {
        log(
            EventsName.InterstitialLoaded.event, mapOf(
                "network" to network_id,
                "ad_unit" to unitName
            )
        )
    }

    fun onDidFailInterstitial() {
        log(EventsName.InterstitialNoFill.event)
    }

    fun onAtt(status: String) {
        log(EventsName.AttStatus.event, mapOf("status" to status))
    }

    fun onAdInterstitial(milestone_id: Int) {
        log(EventsName.AdInterstitialMilestone.event + milestone_id)
        log(EventsName.AdVideoMilestone.event + milestone_id)
    }

    fun onAdVideo(milestone_id: Int) {
        log(EventsName.AdVideoMilestone.event + milestone_id)
    }

    fun onDidFailAdVideo() {
        log(EventsName.AdVideoNoFill.event)
    }

    fun onLoadAdVideo(network_id: String, unitName: String = "") {

        log(EventsName.AdVideoLoaded.event, mapOf("network" to network_id, "ad_unit" to unitName))
    }

    fun onABTestLevelsStart(ab: String) {
        log(EventsName.ABTestLevels.event)
    }

    fun onGetReward(level: Int) {
        log(EventsName.QuestStarsReward.event, mapOf("level" to level))
    }

    fun onABDailyRewardStartTest(ab: Boolean) {
        log(
            EventsName.ABTestDailyReward.event + if (ab) {
                "b"
            } else {
                "a"
            }
        )
    }

    fun onABCategoryTest(ab: Boolean) {
        log(
            EventsName.ABTestCategories.event + if (ab) {
                "b"
            } else {
                "a"
            }
        )
    }

    fun onSelect(category: Int) {
        log(EventsName.SelectCategory.event, mapOf("id" to category))
    }

    fun onGetDailyReward(level: Int, day: Int) {
        log(EventsName.DailyReward.event, mapOf("level" to level, "day" to day))
    }

    fun onComplete(image_id: Int, level: Int) {
        // событие на получение уровня шлем во все системы
        log(EventsName.LevelComplete.event, mapOf("id" to level))

        // трекать отдельно достижениее: 1, 5, 10, 20, level % 50
        val milestoneLevels = arrayOf(5, 10, 20)
        if (milestoneLevels.contains(level) || level % 50 == 0) {

            log(EventsName.LevelComplete.event + level)
        }

        // Специальный эвент для FB
        if (level == 0) {
            fb_logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_TUTORIAL)
        }
    }

    fun onReplay(image_id: Int, level: Int) {
        log(EventsName.ImageReplay.event, mapOf("id" to image_id, "level" to level))
    }

    fun onAntiCheat(image_id: Int, level: Int) {
        log(EventsName.AntiCheatTimer.event, mapOf("image" to image_id, "level" to level))
    }

    fun onHintUse(
        image_id: Int,
        onLevel_level: Int,
        isReplay: Boolean = false,
        withCoins: Boolean = false
    ) {

        val params = mutableMapOf(
            "image" to image_id,
            "level" to onLevel_level,
            "coin" to if (withCoins) 1 else 0
        )
        if (isReplay) {
            params.put("replay", 1)
        }

        log(EventsName.HintUse.event, params)
    }

    fun onHintBuy(level: Int, count: Int) {
        log(EventsName.HintBuy.event, mapOf("level" to level, "count" to count))
    }

    fun onLike(image_id: Int, status: Boolean) {

        log(
            EventsName.ImageLike.event,
            mapOf("image" to image_id, "status" to if (status) 1 else 0)
        )
    }

    fun onPushAuth(status_granted: Boolean) {
        log(EventsName.PushStatus.event, mapOf("granted" to status_granted))
    }

    fun onAdClick(type: String, level_id: Int) {
        log(EventsName.AdClick.event, mapOf("type" to type, "level" to level_id))
    }

    fun onBankOpen(level_id: Int) {
        log(EventsName.BankOpen.event, mapOf("level" to level_id))
    }

    fun onVideoAdImpression(image_id: Int = 0, level: Int = 0) {
        log(EventsName.VideoAdImpression.event, mapOf("image" to image_id, "level" to level))
    }

    fun logSocial(params: Map<String, Objects>) {
        YandexMetrica.reportEvent("social_apps", params)
    }


}
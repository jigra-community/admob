package com.getjigra.community.admob.interstitial

import com.getjigra.JSObject
import com.getjigra.PluginCall
import com.getjigra.community.admob.helpers.FullscreenPluginCallback
import com.getjigra.community.admob.models.AdMobPluginError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.common.util.BiConsumer

object InterstitialAdCallbackAndListeners {

    fun getInterstitialAdLoadCallback(call: PluginCall,
                                      notifyListenersFunction: BiConsumer<String, JSObject>,
    ): InterstitialAdLoadCallback {
        return object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                ad.fullScreenContentCallback = FullscreenPluginCallback(InterstitialAdPluginPluginEvent, notifyListenersFunction)

                AdInterstitialExecutor.interstitialAd = ad

                val adInfo = JSObject()
                adInfo.put("adUnitId", ad.adUnitId)
                call.resolve(adInfo)

                notifyListenersFunction.accept(InterstitialAdPluginPluginEvent.Loaded, adInfo)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                val adMobError = AdMobPluginError(adError)

                notifyListenersFunction.accept(InterstitialAdPluginPluginEvent.FailedToLoad, adMobError)
                call.reject(adError.message)
            }
        }
    }
}

package com.getjigra.community.admob.helpers

import com.getjigra.JSObject
import com.getjigra.community.admob.models.AdMobPluginError
import com.getjigra.community.admob.models.LoadPluginEventNames
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.common.util.BiConsumer

class FullscreenPluginCallback(private val loadPluginObject: LoadPluginEventNames,
                               private val notifyListenersFunction: BiConsumer<String, JSObject>): FullScreenContentCallback() {

    override fun onAdShowedFullScreenContent() {
        notifyListenersFunction.accept(loadPluginObject.Showed, JSObject())
    }

    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
        val adMobError = AdMobPluginError(adError)
        notifyListenersFunction.accept(
                loadPluginObject.FailedToShow, adMobError
        )
    }

    override fun onAdDismissedFullScreenContent() {
        notifyListenersFunction.accept(loadPluginObject.Dismissed, JSObject())
    }
}

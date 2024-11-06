#import <Foundation/Foundation.h>
#import <Jigra/Jigra.h>

// Define the plugin using the JIG_PLUGIN Macro, and
// each method the plugin supports using the JIG_PLUGIN_METHOD macro.
JIG_PLUGIN(AdMob, "AdMob",
           JIG_PLUGIN_METHOD(initialize, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(trackingAuthorizationStatus, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(requestConsentInfo, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(requestTrackingAuthorization, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(showConsentForm, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(resetConsentInfo, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(setApplicationMuted, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(setApplicationVolume, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(showBanner, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(resumeBanner, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(hideBanner, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(removeBanner, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(prepareInterstitial, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(showInterstitial, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(prepareRewardVideoAd, JIGPluginReturnPromise);
           JIG_PLUGIN_METHOD(showRewardVideoAd, JIGPluginReturnPromise);
)

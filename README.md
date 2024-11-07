<h3 align="center">AdMob</h3>
<p align="center"><strong><code>@jigra-community/admob</code></strong></p>
<p align="center">
  Jigra community plugin for native AdMob.
</p>

## Installation
If you use jigra 5:

```
% npm install --save @jigra-community/admob@5
% npx jig update
```

### Android configuration

In file `android/app/src/main/AndroidManifest.xml`, add the following XML elements under `<manifest><application>` :

```xml
<meta-data
 android:name="com.google.android.gms.ads.APPLICATION_ID"
 android:value="@string/admob_app_id"/>
```

In file `android/app/src/main/res/values/strings.xml` add the following lines :

```xml
<string name="admob_app_id">[APP_ID]</string>
```

Don't forget to replace `[APP_ID]` by your AdMob application Id.

#### Variables

This plugin will use the following project variables (defined in your app's `variables.gradle` file):

- `playServicesAdsVersion` version of `com.google.android.gms:play-services-ads` (default: `22.0.0`)
- `androidxCoreKTXVersion`: version of `androidx.core:core-ktx` (default: `1.10.0`)

### iOS configuration

Add the following in the `ios/App/App/info.plist` file inside of the outermost `<dict>`:

```xml
<key>GADIsAdManagerApp</key>
<true/>
<key>GADApplicationIdentifier</key>
<string>[APP_ID]</string>
<key>SKAdNetworkItems</key>
<array>
  <dict>
    <key>SKAdNetworkIdentifier</key>
    <string>cstr6suwn9.skadnetwork</string>
  </dict>
</array>
<key>NSUserTrackingUsageDescription</key>
<string>[Why you use NSUserTracking. ex: This identifier will be used to deliver personalized ads to you.]</string>
```

Don't forget to replace `[APP_ID]` by your AdMob application Id.

## Example

### Initialize AdMob

```ts
import { AdMob } from '@jigra-community/admob';

export async function initialize(): Promise<void> {
  await AdMob.initialize();

  const [trackingInfo, consentInfo] = await Promise.all([
    AdMob.trackingAuthorizationStatus(),
    AdMob.requestConsentInfo(),
  ]);

  if (trackingInfo.status === 'notDetermined') {
    /**
     * If you want to explain TrackingAuthorization before showing the iOS dialog,
     * you can show the modal here.
     * ex)
     * const modal = await this.modalCtrl.create({
     *   component: RequestTrackingPage,
     * });
     * await modal.present();
     * await modal.onDidDismiss();  // Wait for close modal
     **/

    await AdMob.requestTrackingAuthorization();
  }

  const authorizationStatus = await AdMob.trackingAuthorizationStatus();
  if (
    authorizationStatus.status === 'authorized' &&
    consentInfo.isConsentFormAvailable &&
    consentInfo.status === AdmobConsentStatus.REQUIRED
  ) {
    await AdMob.showConsentForm();
  }
}
```

Send and array of device Ids in `testingDevices? to use production like ads on your specified devices -> https://developers.google.com/admob/android/test-ads#enable_test_devices

### User Message Platform (UMP)

Later this year, Google will require all publishers serving ads to EEA and UK users to use a Google-certified Consent Management Platform (CMP)

Currently we just support Google's consent management solution.

To use UMP, you must [create your GDPR messages](https://support.google.com/admob/answer/10113207?hl=en&ref_topic=10105230&sjid=6731900490614517032-AP)

You may need to [setup IDFA messages](https://support.google.com/admob/answer/10115027?hl=en), it will work along with GDPR messages and will show when users are not in EEA and UK.

Example of how to use UMP

```ts
import { AdMob, AdmobConsentStatus, AdmobConsentDebugGeography } from '@jigra-community/admob';

async showConsent() {
  const consentInfo = await AdMob.requestConsentInfo();

  if (consentInfo.isConsentFormAvailable && consentInfo.status === AdmobConsentStatus.REQUIRED) {
    const {status} = await AdMob.showConsentForm();
  }
}
```

If you testing on real device, you have to set `debugGeography` and add your device ID to `testDeviceIdentifiers`. You can find your device ID with logcat (Android) or XCode (iOS).

```ts
  const consentInfo = await AdMob.requestConsentInfo({
    debugGeography: AdmobConsentDebugGeography.EEA,
    testDeviceIdentifiers: ['YOUR_DEVICE_ID']
  });
```

**Note**: When testing, if you choose not consent (Manage -> Confirm Choices). The ads may not load/show. Even on testing enviroment. This is normal. It will work on Production so don't worry.

**Note**: The order in which they are combined with other methods is as follows.

1. AdMob.initialize
2. AdMob.requestConsentInfo
3. AdMob.showConsentForm (If consent form required )
3/ AdMob.showBanner

### Show Banner

```ts
import { AdMob, BannerAdOptions, BannerAdSize, BannerAdPosition, BannerAdPluginEvents, AdMobBannerSize } from '@jigra-community/admob';

export async function banner(): Promise<void> {
    AdMob.addListener(BannerAdPluginEvents.Loaded, () => {
      // Subscribe Banner Event Listener
    });

    AdMob.addListener(BannerAdPluginEvents.SizeChanged, (size: AdMobBannerSize) => {
      // Subscribe Change Banner Size
    });

    const options: BannerAdOptions = {
      adId: 'YOUR ADID',
      adSize: BannerAdSize.BANNER,
      position: BannerAdPosition.BOTTOM_CENTER,
      margin: 0,
      // isTesting: true
      // npa: true
    };
    AdMob.showBanner(options);
}
```

### Show Interstitial

```ts
import { AdMob, AdOptions, AdLoadInfo, InterstitialAdPluginEvents } from '@jigra-community/admob';

export async function interstitial(): Promise<void> {
  AdMob.addListener(InterstitialAdPluginEvents.Loaded, (info: AdLoadInfo) => {
    // Subscribe prepared interstitial
  });

  const options: AdOptions = {
    adId: 'YOUR ADID',
    // isTesting: true
    // npa: true
  };
  await AdMob.prepareInterstitial(options);
  await AdMob.showInterstitial();
}
```

### Show RewardVideo

```ts
import { AdMob, RewardAdOptions, AdLoadInfo, RewardAdPluginEvents, AdMobRewardItem } from '@jigra-community/admob';

export async function rewardVideo(): Promise<void> {
  AdMob.addListener(RewardAdPluginEvents.Loaded, (info: AdLoadInfo) => {
    // Subscribe prepared rewardVideo
  });

  AdMob.addListener(RewardAdPluginEvents.Rewarded, (rewardItem: AdMobRewardItem) => {
    // Subscribe user rewarded
    console.log(rewardItem);
  });

  const options: RewardAdOptions = {
    adId: 'YOUR ADID',
    // isTesting: true
    // npa: true
    // ssv: {
    //   userId: "A user ID to send to your SSV"
    //   customData: JSON.stringify({ ...MyCustomData })
    //}
  };
  await AdMob.prepareRewardVideoAd(options);
  const rewardItem = await AdMob.showRewardVideoAd();
}
```

## Server-side Verification Notice
SSV callbacks are only fired on Production Adverts, therefore test Ads will not fire off your SSV callback.

For E2E tests or just for validating the data in your `RewardAdOptions` work as expected, you can add a custom GET
request to your mock endpoint after the `RewardAdPluginEvents.Rewarded` similar to this:
```ts
AdMob.addListener(RewardAdPluginEvents.Rewarded, async () => {
  // ...
  if (ENVIRONMENT_IS_DEVELOPMENT) {
    try {
      const url = `https://your-staging-ssv-endpoint` + new URLSearchParams({
        'ad_network': 'TEST',
        'ad_unit': 'TEST',
        'custom_data': customData, // <-- passed CustomData
        'reward_amount': 'TEST',
        'reward_item': 'TEST',
        'timestamp': 'TEST',
        'transaction_id': 'TEST',
        'user_id': userId, // <-- Passed UserID
        'signature': 'TEST',
        'key_id': 'TEST'
      });
      await fetch(url);
    } catch (err) {
      console.error(err);
    }
  }
  // ...
});
```


## Index
<docgen-index>

* [`initialize(...)`](#initialize)
* [`trackingAuthorizationStatus()`](#trackingauthorizationstatus)
* [`requestTrackingAuthorization()`](#requesttrackingauthorization)
* [`setApplicationMuted(...)`](#setapplicationmuted)
* [`setApplicationVolume(...)`](#setapplicationvolume)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

## API
<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize(...)

```typescript
initialize(options?: AdMobInitializationOptions | undefined) => Promise<void>
```

Initialize AdMob with <a href="#admobinitializationoptions">AdMobInitializationOptions</a>

| Param         | Type                                                                              | Description                                                          |
| ------------- | --------------------------------------------------------------------------------- | -------------------------------------------------------------------- |
| **`options`** | <code><a href="#admobinitializationoptions">AdMobInitializationOptions</a></code> | <a href="#admobinitializationoptions">AdMobInitializationOptions</a> |

**Since:** 1.1.2

--------------------


### trackingAuthorizationStatus()

```typescript
trackingAuthorizationStatus() => Promise<TrackingAuthorizationStatusInterface>
```

Confirm requestTrackingAuthorization status (iOS &gt;14)

**Returns:** <code>Promise&lt;<a href="#trackingauthorizationstatusinterface">TrackingAuthorizationStatusInterface</a>&gt;</code>

**Since:** 3.1.0

--------------------


### requestTrackingAuthorization()

```typescript
requestTrackingAuthorization() => Promise<void>
```

request requestTrackingAuthorization (iOS &gt;14).
This is deprecated method. We recommend UMP Consent.

**Since:** 5.2.0

--------------------


### setApplicationMuted(...)

```typescript
setApplicationMuted(options: ApplicationMutedOptions) => Promise<void>
```

Report application mute state to AdMob SDK

| Param         | Type                                                                        |
| ------------- | --------------------------------------------------------------------------- |
| **`options`** | <code><a href="#applicationmutedoptions">ApplicationMutedOptions</a></code> |

**Since:** 4.1.1

--------------------


### setApplicationVolume(...)

```typescript
setApplicationVolume(options: ApplicationVolumeOptions) => Promise<void>
```

Report application volume to AdMob SDK

| Param         | Type                                                                          |
| ------------- | ----------------------------------------------------------------------------- |
| **`options`** | <code><a href="#applicationvolumeoptions">ApplicationVolumeOptions</a></code> |

**Since:** 4.1.1

--------------------


### Interfaces


#### AdMobInitializationOptions

| Prop                               | Type                                                              | Description                                                                                                                                                                                                                                                 | Default            | Since |
| ---------------------------------- | ----------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------ | ----- |
| **`testingDevices`**               | <code>string[]</code>                                             | An Array of devices IDs that will be marked as tested devices if {@link <a href="#admobinitializationoptions">AdMobInitializationOptions.initializeForTesting</a>} is true (Real Ads will be served to Testing devices, but they will not count as 'real'). |                    | 1.2.0 |
| **`initializeForTesting`**         | <code>boolean</code>                                              | If set to true, the devices on {@link <a href="#admobinitializationoptions">AdMobInitializationOptions.testingDevices</a>} will be registered to receive test production ads.                                                                               | <code>false</code> | 1.2.0 |
| **`tagForChildDirectedTreatment`** | <code>boolean</code>                                              | For purposes of the Children's Online Privacy Protection Act (COPPA), there is a setting called tagForChildDirectedTreatment.                                                                                                                               |                    | 3.1.0 |
| **`tagForUnderAgeOfConsent`**      | <code>boolean</code>                                              | When using this feature, a Tag For Users under the Age of Consent in Europe (TFUA) parameter will be included in all future ad requests.                                                                                                                    |                    | 3.1.0 |
| **`maxAdContentRating`**           | <code><a href="#maxadcontentrating">MaxAdContentRating</a></code> | As an app developer, you can indicate whether you want Google to treat your content as child-directed when you make an ad request.                                                                                                                          |                    | 3.1.0 |


#### TrackingAuthorizationStatusInterface

| Prop         | Type                                                                     |
| ------------ | ------------------------------------------------------------------------ |
| **`status`** | <code>'authorized' \| 'denied' \| 'notDetermined' \| 'restricted'</code> |


#### ApplicationMutedOptions

| Prop        | Type                 | Description                                                                                                                                                                                                                                                                                           | Since |
| ----------- | -------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`muted`** | <code>boolean</code> | To inform the SDK that the app volume has been muted. Note: Video ads that are ineligible to be shown with muted audio are not returned for ad requests made, when the app volume is reported as muted or set to a value of 0. This may restrict a subset of the broader video ads pool from serving. | 4.1.1 |


#### ApplicationVolumeOptions

| Prop         | Type                                                                               | Description                                                                                                                                                                                                                          | Since |
| ------------ | ---------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----- |
| **`volume`** | <code>0 \| 1 \| 0.1 \| 0.2 \| 0.3 \| 0.4 \| 0.5 \| 0.6 \| 0.7 \| 0.8 \| 0.9</code> | If your app has its own volume controls (such as custom music or sound effect volumes), disclosing app volume to the Google Mobile Ads SDK allows video ads to respect app volume settings. enable set 0.0 - 1.0, any float allowed. | 4.1.1 |


### Enums


#### MaxAdContentRating

| Members                | Value                           | Description                                                 |
| ---------------------- | ------------------------------- | ----------------------------------------------------------- |
| **`General`**          | <code>'General'</code>          | Content suitable for general audiences, including families. |
| **`ParentalGuidance`** | <code>'ParentalGuidance'</code> | Content suitable for most audiences with parental guidance. |
| **`Teen`**             | <code>'Teen'</code>             | Content suitable for teen and older audiences.              |
| **`MatureAudience`**   | <code>'MatureAudience'</code>   | Content suitable only for mature audiences.                 |

</docgen-api>

## TROUBLE SHOOTING

### If you have error:

> [error] Error running update: Analyzing dependencies
> [!] CocoaPods could not find compatible versions for pod "Google-Mobile-Ads-SDK":

You should run `pod repo update` ;

## License

Jigra AdMob is [MIT licensed](./LICENSE).

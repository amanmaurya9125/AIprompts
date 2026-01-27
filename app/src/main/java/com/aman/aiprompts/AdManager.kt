package com.aman.aiprompts

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private val adUnitId : String by lazy {
        val isdebug = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

        if (isdebug) {
            "ca-app-pub-3940256099942544/1033173712" // TEST
        } else {
            "ca-app-pub-xxxxxxxxxxxxxxxxxxxxxxxxxxx" // REAL
        }
    }

    fun loadAd() {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(ad: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun show(activity: Activity,onDismiss : ()-> Unit={}) {

        interstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadAd()
                    onDismiss()
                }
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadAd()
                    onDismiss()
                }
            }
        if (interstitialAd != null) {
            interstitialAd?.show(activity)
        }
        else{
            loadAd()
            onDismiss()
        }
    }
}

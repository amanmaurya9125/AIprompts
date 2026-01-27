package com.aman.aiprompts

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aman.aiprompts.ConsentManager.ConsentManager
import com.aman.aiprompts.ui.theme.AIPromptsTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

class MainActivity : ComponentActivity() {

    lateinit var interstitialAdManager: InterstitialAdManager
        private set
    private lateinit var consentManager: ConsentManager

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        consentManager = ConsentManager(this)
        interstitialAdManager = InterstitialAdManager(this)
            setContent {
                AIPromptsTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF0A0A0A) // SAME as splash background
                    ) {
                        MyApp(consentManager,interstitialAdManager)
                    }
                }
            }
        consentManager.requestConsent(this) {

            // ✅ Initialize AdMob ONLY after consent
            if (consentManager.canRequestAds()) {
                val requestConfiguration = RequestConfiguration.Builder()
                    .setTestDeviceIds(
                        listOf("B8BFC56509A971BC86DE6DE8CE09547E")
                    )
                    .build()
                MobileAds.setRequestConfiguration(requestConfiguration)
                MobileAds.initialize(this)
                interstitialAdManager.loadAd()
            }
        }
        }
    }
package com.aman.aiprompts.ConsentManager

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class ConsentManager(context: Context) {
    private val consentInformation = UserMessagingPlatform.getConsentInformation(context)

    fun requestConsent(activity: Activity, onComplete: () -> Unit) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                if (consentInformation.isConsentFormAvailable) {
                    loadForm(activity, onComplete)
                } else {
                    onComplete()
                }
            },
            {
                onComplete()
            }
        )
    }

    private fun loadForm(activity: Activity, onComplete: () -> Unit) {
        UserMessagingPlatform.loadConsentForm(
            activity,
            { form ->
                if (consentInformation.consentStatus ==
                    ConsentInformation.ConsentStatus.REQUIRED
                ) {
                    form.show(activity) {
                        onComplete()
                    }
                } else {
                    onComplete()
                }
            },
            {
                onComplete()
            }
        )
    }

    fun canRequestAds(): Boolean {
        return consentInformation.canRequestAds()
    }

    fun showManageConsent(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {}
    }
}
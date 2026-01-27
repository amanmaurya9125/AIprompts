package com.aman.aiprompts.RateUs

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

object RateUsManager {
    fun showReview(activity: Activity) {
        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
}

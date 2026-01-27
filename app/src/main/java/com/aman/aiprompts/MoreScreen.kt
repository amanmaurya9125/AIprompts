package com.aman.aiprompts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aman.aiprompts.ConsentManager.ConsentManager
import com.aman.aiprompts.RateUs.RatePreference
import com.aman.aiprompts.RateUs.RateUsManager

@Composable
fun MoreScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0F1F),
                        Color(0xFF071A2B),
                        Color(0xFF050814)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(30.dp))

            // 🔥 Header
            MoreHeader()

            Spacer(Modifier.height(25.dp))

            // ⚙️ Settings Card
            MoreSettingsCard()
        }
    }
}

@Composable
fun MoreHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF4C1D95),
                        Color(0xFFEC4899),
                        Color(0xFF0EA5E9)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = "AI Photo Prompt",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "More & Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

fun privacyPolicy(context: Context) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://sites.google.com/view/aiprompts-privacypolicy")
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@Composable
fun MoreSettingsCard() {
    val context = LocalContext.current
    val consentManager = ConsentManager(context)
    val activity = context as Activity
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF111827),
                        Color(0xFF0B1220)
                    )
                )
            )
            .padding(vertical = 8.dp)
    ) {

        MoreItem(
            icon = Icons.Default.Star,
            title = "Rate Us",
            onClick = {
                RateUsManager.showReview(activity)
                RatePreference.markReviewShown(context)
            }
        )

        DividerLine()

        MoreItem(
            icon = Icons.Default.Share,
            title = "Share Our App",
            onClick = { shareApp(context) }
        )

        DividerLine()

        MoreItem(
            icon = Icons.Default.Security,
            title = "Privacy Policy",
            onClick = { privacyPolicy(context) }
        )

        DividerLine()

        MoreItem(
            icon = Icons.Default.Info,
            title = "Manage Ad Consent",
            onClick = { consentManager.showManageConsent(activity) }
        )
    }
}

@Composable
fun MoreItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(Modifier.width(15.dp))

        Text(
            text = title,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun DividerLine() {
    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Color.White.copy(alpha = 0.08f),
        thickness = 1.dp
    )
}

fun shareApp(context: Context) {
    val appPackageName = context.packageName
    val shareText = """
        ✨ AI Photo Prompts ✨
        
        Create stunning AI images with powerful prompts 🚀
        
        Download now 👇
        https://play.google.com/store/apps/details?id=$appPackageName
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    context.startActivity(
        Intent.createChooser(intent, "Share AI Photo Prompts via")
    )
}

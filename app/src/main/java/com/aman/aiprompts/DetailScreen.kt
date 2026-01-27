package com.aman.aiprompts

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aman.aiprompts.ConsentManager.ConsentManager
import com.aman.aiprompts.ViewModel.PromptViewModel
import kotlinx.coroutines.delay


@Composable
fun PromptDetailScreen(
    id: String,
    viewModel: PromptViewModel,
    consentManager: ConsentManager,
    adManager: InterstitialAdManager,
) {
    val prompt by viewModel
        .getPrompt(id)
        .collectAsState(initial = null)
//    val prompt = promptState.value

    if (prompt == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0F1F),
                        Color(0xFF050814)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PromptImageCard(prompt!!.imageUrl)

            Spacer(Modifier.height(16.dp))

            PromptContentCard(
                promptText = prompt!!.prompt,
                viewModel,
                consentManager,
                adManager
            )
        }
    }
}

@Composable
fun PromptImageCard(imageUrl: String) {
    Box(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(0.84f)
            .aspectRatio(3.2f / 4f)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFCA10DB), Color(0xFF8F00FD), Color(0xFF4A01DF))
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun PromptContentCard(
    promptText: String,
    viewModel: PromptViewModel,
    consentManager: ConsentManager,
    adManager: InterstitialAdManager,
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(
                    width = 4.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFC10DE6), Color(0xFDF3F3F5), Color(0xFF4A01DF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

//                ActionButtons()

                ActionIcon(promptText, viewModel, consentManager,adManager)

                Spacer(Modifier.height(1.dp))

                Text(
                    "Prompt:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(5.dp))

                LiveTypingText(
                    text = promptText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(9.dp)
                )
            }
        }
    }
}


@Composable
fun ActionIcon(text: String, viewModel: PromptViewModel, consentManager: ConsentManager,adManager : InterstitialAdManager) {
    val context = LocalContext.current
    val activity = context as Activity
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var showIcon by remember { mutableStateOf(false) }
    val showAd by viewModel.showAd.collectAsState()

    LaunchedEffect(Unit) {
        delay(4500)
        showIcon = true
    }

    LaunchedEffect(showAd) {
        if (showAd && consentManager.canRequestAds()) {
            adManager.show(activity)
            viewModel.onAdShown()
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {

                IconButton(
                    onClick = {
                        val clip = ClipData.newPlainText("Prompt", text)
                        clipboard.setPrimaryClip(clip)
                        viewModel.onCopyClicked()
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        tint = Color.White,
                        contentDescription = "Copy"
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {

                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(
                            Intent.createChooser(intent, "Share via")
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        tint = Color.White,
                        contentDescription = "Share"
                    )
                }
            }
        }
    }
}

@Composable
fun LiveTypingText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    var visibleText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        visibleText = ""
        text.forEach { word ->
            visibleText += word
            delay(1)

            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Text(
        text = visibleText,
        color = Color.LightGray,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        lineHeight = 22.sp,
        modifier = modifier.verticalScroll(scrollState)
    )
}


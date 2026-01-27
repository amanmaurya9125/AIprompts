package com.aman.aiprompts

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aman.aiprompts.RateUs.RatePreference
import com.aman.aiprompts.RateUs.RateUsManager
import com.aman.aiprompts.RoomDatabase.PromptEntity
import com.aman.aiprompts.ViewModel.PromptViewModel

@Composable
fun FavoriteScreen(
    navController: NavController,
    viewModel: PromptViewModel,
) {
    val context = LocalContext.current
    val activity = context as Activity
    val favoritePrompts by viewModel.favoritePrompts.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    LaunchedEffect(Unit) {
        val count = RatePreference.getFavoriteCount(context)
        val reviewShown = RatePreference.isReviewShown(context)

        if (count >= 6 && !reviewShown) {
            RateUsManager.showReview(activity)
            RatePreference.markReviewShown(context)
        }
    }

    val columns = when {
        screenWidthDp < 600 -> 2      // phone
        screenWidthDp < 840 -> 3      // small tablet
        else -> 4                     // large tablet
    }

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
            FavoriteHeader()

            Spacer(Modifier.height(25.dp))

            if (favoritePrompts.isEmpty()) {

                // ❤️ Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyFavoriteState()
                }

            } else {

                // ✅ Grid must take remaining space
                LazyVerticalGrid(
                    modifier = Modifier
                        .weight(1f),
                    columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        bottom = 80.dp // space for bottom nav
                    )
                ) {
                    items(
                        items = favoritePrompts,
                        key = { it.id }
                    ) { prompt ->
                        FavoritePromptCard(
                            prompt = prompt,
                            onLike = {
                                viewModel.toggleFavorite(prompt.id)
                            },
                            onClick = {
                                navController.navigate("detail/${prompt.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteHeader() {
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
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = "AI Photo Prompts",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Your Favorite Prompts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun FavoritePromptCard(prompt: PromptEntity, onLike: (String) -> Unit, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF460F9C),
                        Color(0xFFEC4899),
                        Color(0xFF07A6EE)
//                    listOf(Color(0xFFC10DE6), Color(0xFDF3F3F5), Color(0xFF4A01DF))
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(prompt.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = if (prompt.isFavorite) Color.Red else Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(24.dp)
                .clickable { onLike(prompt.id) }
        )
    }
}

@Composable
fun EmptyFavoriteState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(72.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "No favorites yet!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Tap the heart on any prompt to save it here.",
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

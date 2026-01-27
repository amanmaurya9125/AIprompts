package com.aman.aiprompts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aman.aiprompts.RateUs.RatePreference
import com.aman.aiprompts.RoomDatabase.PromptEntity
import com.aman.aiprompts.ViewModel.PromptViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: PromptViewModel,
    paddingValues: PaddingValues,
) {

    val prompts by viewModel.categoryPrompts.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    val columns = when {
        screenWidthDp < 600 -> 2      // phone
        screenWidthDp < 840 -> 3      // small tablet
        else -> 4                     // large tablet
    }
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp)
    ) {

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(Modifier.width(10.dp))
                Text(
                    "AI Photo Prompts",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            PromptCategoryTab(viewModel)

            Spacer(Modifier.height(20.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(prompts) { prompt ->
                    PromptCard(
                        prompt = prompt,
                        onLike = { id -> viewModel.toggleFavorite(id) },
                        onClick = { navController.navigate("detail/${prompt.id}") })
                }
            }
        }


    }
}

@Composable
fun PromptCard(prompt: PromptEntity, onLike: (String) -> Unit, onClick: () -> Unit) {
    val context = LocalContext.current
    val rotation by animateFloatAsState(
        targetValue = if (prompt.isFavorite) 5f else 0f,
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .graphicsLayer { rotationZ = rotation }
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
                .clickable {
                    onLike(prompt.id)
                    if (!prompt.isFavorite) {
                        RatePreference.incrementFavorite(context)
                    }
                }
        )
    }
}


@Composable
fun PromptCategoryTab(viewModel: PromptViewModel) {
    val category =
        listOf(
            "New",
            "Trending",
            "Couple",
            "Girl",
            "Men",
            "Birthday",
            "Hyper Realistic",
            "Places",
            "Car",
            "Halloween",
            "Stranger Things",
        )
    var selectedCategory by remember { mutableStateOf(viewModel.selectedCategory.value) }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(category) { tab ->
            PromptTab(
                text = tab,
                isSelected = selectedCategory == tab,
                onClick = {
                    selectedCategory = tab
                    viewModel.selectCategory(tab)
                })
        }
    }
}

@Composable
fun PromptTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(durationMillis = 250),
        label = ""
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = if (isSelected)
                    Brush.horizontalGradient(
                        colors = listOf(

                            Color(0xFF5D27D8),
                            Color(0xFFDE12D4)
                        )
                    )
                else
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
            )

            .border(
                width = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = Color.White)
    }
}





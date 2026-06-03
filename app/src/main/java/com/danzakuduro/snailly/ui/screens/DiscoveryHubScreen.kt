package com.danzakuduro.snailly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danzakuduro.snailly.ui.theme.DarkGreen
import com.danzakuduro.snailly.ui.theme.White

data class DiscoveryTile(
    val name: String,
    val url: String,
    val icon: ImageVector,
    val color: Color,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryHubScreen(
    onBack: () -> Unit,
    onNavigateToUrl: (String) -> Unit
) {
    val tiles = listOf(
        DiscoveryTile("YouTube Kids", "https://www.youtubekids.com", Icons.Default.PlayCircle, Color(0xFFFF0000), "Video aman untuk anak"),
        DiscoveryTile("Wikipedia Kids", "https://simple.wikipedia.org", Icons.Default.School, Color(0xFF4A90E2), "Belajar ensiklopedia"),
        DiscoveryTile("Nat Geo Kids", "https://kids.nationalgeographic.com", Icons.Default.Explore, Color(0xFFFFD700), "Jelajahi alam liar"),
        DiscoveryTile("Google Kids", "https://www.google.com/search?q=kids&safe=active", Icons.Default.Language, Color(0xFF34A853), "Cari info sekolah"),
        DiscoveryTile("ABCmouse", "https://www.abcmouse.com", Icons.Default.AutoAwesome, Color(0xFF9B59B6), "Game belajar seru"),
        DiscoveryTile("PBS Kids", "https://pbskids.org", Icons.Default.PlayCircle, Color(0xFFF39C12), "Kartun edukasi")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discovery Hub", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Text(
                text = "Pilih petualanganmu hari ini! ✨",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tiles) { tile ->
                    DiscoveryCard(tile = tile, onClick = { onNavigateToUrl(tile.url) })
                }
            }
        }
    }
}

@Composable
fun DiscoveryCard(tile: DiscoveryTile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(tile.color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tile.icon,
                    contentDescription = null,
                    tint = tile.color,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = tile.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = tile.description,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

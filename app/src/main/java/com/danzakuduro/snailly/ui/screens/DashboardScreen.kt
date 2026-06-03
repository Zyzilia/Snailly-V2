package com.danzakuduro.snailly.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danzakuduro.snailly.data.HistoryItem
import com.danzakuduro.snailly.supabase
import com.danzakuduro.snailly.ui.theme.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onViewTodos: () -> Unit,
    onOpenBrowser: () -> Unit
) {
    var totalCount by remember { mutableIntStateOf(0) }
    var positiveCount by remember { mutableIntStateOf(0) }
    var negativeCount by remember { mutableIntStateOf(0) }
    var userName by remember { mutableStateOf("User Snailly") }
    var isLoading by remember { mutableStateOf(true) }

    // Refresh data when the screen is visible
    LaunchedEffect(Unit) {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                // Fetch User Name
                val user = supabase.auth.currentUserOrNull()
                user?.userMetadata?.get("full_name")?.let {
                    userName = it.toString().removeSurrounding("\"")
                }

                // Fetch Stats from History table
                // We fetch the latest data from the database
                val history = supabase.from("history").select().decodeList<HistoryItem>()
                totalCount = history.size
                positiveCount = history.count { it.isSafe }
                negativeCount = history.count { !it.isSafe }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkGreen, Color(0xFF1B3D2F))
                )
            )
    ) {
        // Decorative Circles for Background
        BackgroundDecoration()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Snailly", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = White)
                        Text(text = "Safe Browsing", fontSize = 10.sp, color = White.copy(alpha = 0.6f))
                    }
                }
                
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.background(White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Welcome Section
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Halo, $userName! ✨",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = "Ayo jelajahi internet dengan aman bersama Snailly.",
                    fontSize = 14.sp,
                    color = White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Cards Section
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = White)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total\nAkses",
                        value = totalCount.toString(),
                        valueColor = DarkGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Konten\nPositif",
                        value = positiveCount.toString(),
                        valueColor = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Konten\nNegatif",
                        value = negativeCount.toString(),
                        valueColor = Color(0xFFC62828),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.1f)),
                border = BoxDefaults.transparentBorder() // Simulated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DashboardButton(
                        text = "Mulai Menjelajah Aman",
                        containerColor = BrightBlue,
                        contentColor = White,
                        onClick = onOpenBrowser
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DashboardButton(
                        text = "Pusat Jelajah (Discovery Hub)",
                        containerColor = White,
                        contentColor = DarkGreen,
                        onClick = onViewTodos
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Snailly v2.0 - Keamanan Anak adalah Prioritas",
                fontSize = 10.sp,
                color = White.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BackgroundDecoration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = White.copy(alpha = 0.05f),
            radius = 400f,
            center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.1f)
        )
        drawCircle(
            color = White.copy(alpha = 0.03f),
            radius = 600f,
            center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.9f)
        )
    }
}

@Composable
fun StatCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(130.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor
            )
        }
    }
}

@Composable
fun DashboardButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = contentColor)
    }
}

object BoxDefaults {
    @Composable
    fun transparentBorder() = androidx.compose.foundation.BorderStroke(1.dp, White.copy(alpha = 0.2f))
}

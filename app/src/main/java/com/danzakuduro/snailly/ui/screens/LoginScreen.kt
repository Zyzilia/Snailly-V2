package com.danzakuduro.snailly.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danzakuduro.snailly.supabase
import com.danzakuduro.snailly.ui.components.BackgroundDecoration
import com.danzakuduro.snailly.ui.components.CustomInputField
import com.danzakuduro.snailly.ui.components.SnaillyButton
import com.danzakuduro.snailly.ui.theme.DarkGreen
import com.danzakuduro.snailly.ui.theme.LinkOrange
import com.danzakuduro.snailly.ui.theme.White
import com.danzakuduro.snailly.ui.theme.BrightBlue
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkGreen, Color(0xFF1B3D2F))
                )
            )
    ) {
        BackgroundDecoration()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Header Section (Same as Dashboard)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Face, contentDescription = null, tint = White, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Snailly", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = White)
                    Text(text = "Safe Browsing", fontSize = 12.sp, color = White.copy(alpha = 0.6f))
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Masuk ke Akun Anda",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Text(
                text = "Silakan login untuk mulai menjelajah dengan aman.",
                fontSize = 14.sp,
                color = White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp).align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            CustomInputField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "Contoh: budi@email.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomInputField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "Ketik password Anda",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            SnaillyButton(
                text = "Login",
                containerColor = BrightBlue,
                contentColor = White,
                isLoading = isLoading,
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Harap isi semua bidang", Toast.LENGTH_SHORT).show()
                        return@SnaillyButton
                    }
                    scope.launch {
                        isLoading = true
                        try {
                            supabase.auth.signInWith(Email) {
                                this.email = email
                                this.password = password
                            }
                            onLoginSuccess()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Login gagal: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = White)) {
                    append("Belum punya akun? ")
                }
                withStyle(style = SpanStyle(color = LinkOrange, fontWeight = FontWeight.Bold)) {
                    append("Daftar Sekarang")
                }
            }
            Text(
                text = annotatedString,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Snailly v2.0 - Keamanan Anak adalah Prioritas",
                fontSize = 10.sp,
                color = White.copy(alpha = 0.4f)
            )
        }
    }
}

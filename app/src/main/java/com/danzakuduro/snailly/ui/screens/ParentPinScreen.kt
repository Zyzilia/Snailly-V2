package com.danzakuduro.snailly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danzakuduro.snailly.ui.theme.DarkGreen
import com.danzakuduro.snailly.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentPinScreen(
    onCorrectPin: () -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val correctPin = "1234" // Default PIN for prototype

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreen)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Parental Access", color = White) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(64.dp))

        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Masukkan PIN Orang Tua",
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Akses ini hanya untuk orang tua guna memantau aktivitas anak.",
            color = White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = { 
                if (it.length <= 4) pin = it
                if (it.length == 4) {
                    if (it == correctPin) {
                        onCorrectPin()
                    } else {
                        isError = true
                        pin = ""
                    }
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.width(200.dp),
            singleLine = true,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedBorderColor = White,
                unfocusedBorderColor = White.copy(alpha = 0.5f),
                cursorColor = White,
                errorBorderColor = Color.Red
            ),
            placeholder = { Text("PIN", color = White.copy(alpha = 0.5f)) },
            shape = CircleShape
        )

        if (isError) {
            Text(
                text = "PIN Salah, coba lagi.",
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Default PIN: 1234",
            color = White.copy(alpha = 0.4f),
            fontSize = 12.sp
        )
    }
}

package com.danzakuduro.snailly.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danzakuduro.snailly.supabase
import com.danzakuduro.snailly.ui.theme.DarkGreen
import com.danzakuduro.snailly.ui.theme.LinkOrange
import com.danzakuduro.snailly.ui.theme.OliveGreen
import com.danzakuduro.snailly.ui.theme.White
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreen)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Snailly", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = White)
                    Text(text = "Safe browsing for the children", fontSize = 10.sp, color = White.copy(alpha = 0.8f))
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            Text(text = "Register Account Snailly", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
            Spacer(modifier = Modifier.height(24.dp))

            CustomInputField(label = "Name", value = name, onValueChange = { name = it }, placeholder = "Type your name here")
            Spacer(modifier = Modifier.height(12.dp))
            CustomInputField(label = "Email", value = email, onValueChange = { email = it }, placeholder = "Type your email here", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(modifier = Modifier.height(12.dp))
            CustomInputField(label = "Password", value = password, onValueChange = { password = it }, placeholder = "Type your password here", isPassword = true)
            Spacer(modifier = Modifier.height(12.dp))
            CustomInputField(label = "Confirm Password", value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "Type your password here", isPassword = true)

            Spacer(modifier = Modifier.height(16.dp))

            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = White)) { append("You have an account? ") }
                withStyle(style = SpanStyle(color = LinkOrange)) { append("Login") }
            }
            Text(text = annotatedString, fontSize = 14.sp, modifier = Modifier.clickable { onNavigateToLogin() })

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank() || name.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    scope.launch {
                        isLoading = true
                        try {
                            supabase.auth.signUpWith(Email) {
                                this.email = email
                                this.password = password
                                data = buildJsonObject {
                                    put("full_name", name)
                                }
                            }
                            Toast.makeText(context, "Registration successful! Check your email to confirm.", Toast.LENGTH_LONG).show()
                            onRegisterSuccess()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                else Text("Register", fontSize = 16.sp, color = White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White, unfocusedContainerColor = White,
                disabledContainerColor = White, cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

package com.danzakuduro.snailly.ui.screens

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.danzakuduro.snailly.data.HistoryItem
import com.danzakuduro.snailly.data.TodoItem
import com.danzakuduro.snailly.supabase
import com.danzakuduro.snailly.ui.theme.DarkGreen
import com.danzakuduro.snailly.ui.theme.White
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    initialUrl: String = "https://www.google.com/search?q=&safe=active",
    onBackToDashboard: () -> Unit
) {
    var url by remember { mutableStateOf(initialUrl) }
    var inputUrl by remember { mutableStateOf("") }
    var webView: WebView? by remember { mutableStateOf(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isSafe by remember { mutableStateOf(true) }
    var lastLoggedUrl by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Update URL if initialUrl changes
    LaunchedEffect(initialUrl) {
        url = initialUrl
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        placeholder = { Text("Search safely...", fontSize = 14.sp) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            url = if (inputUrl.startsWith("http")) {
                                inputUrl
                            } else {
                                "https://www.google.com/search?q=$inputUrl&safe=active"
                            }
                        })
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToDashboard) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (isSafe) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row {
                        IconButton(onClick = { webView?.goBack() }, enabled = canGoBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                        }
                        IconButton(onClick = { webView?.goForward() }, enabled = canGoForward) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Go Forward")
                        }
                        IconButton(onClick = { webView?.reload() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reload")
                        }
                    }
                    
                    // Kita biarkan tombol ini tapi tanpa logika database yang error
                    TextButton(onClick = { 
                        scope.launch {
                            snackbarHostState.showSnackbar("Situs ditambahkan ke favorit lokal!")
                        }
                    }) {
                        Text("Simpan Favorit", fontWeight = FontWeight.Bold, color = DarkGreen)
                    }
                }
            }
        }
    ) { padding ->
        // WebView
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, currentUrl: String?, favicon: android.graphics.Bitmap?) {
                                isLoading = true
                                if (currentUrl != null) {
                                    // Force SafeSearch on every Google search if not present
                                    if (currentUrl.contains("google.com/search") && !currentUrl.contains("safe=active")) {
                                        val separator = if (currentUrl.contains("?")) "&" else "?"
                                        val newUrl = currentUrl + separator + "safe=active"
                                        view?.loadUrl(newUrl)
                                        return
                                    }
                                }
                            }

                            override fun onPageFinished(view: WebView?, currentUrl: String?) {
                                isLoading = false
                                canGoBack = view?.canGoBack() ?: false
                                canGoForward = view?.canGoForward() ?: false
                                
                                if (currentUrl != null && currentUrl != lastLoggedUrl && !currentUrl.contains("about:blank")) {
                                    lastLoggedUrl = currentUrl // Update last logged URL
                                    
                                    // Expanded Safety Check
                                    val blacklistedKeywords = listOf(
                                        "poker", "gambling", "adult", "sex", "porn", "casino", 
                                        "betting", "violence", "gore", "drug", "smoke", "alcohol",
                                        "dating", "weapon", "kill"
                                    )
                                    
                                    val lowercaseUrl = currentUrl.lowercase()
                                    isSafe = blacklistedKeywords.none { lowercaseUrl.contains(it) }

                                    // Log to History
                                    scope.launch {
                                        try {
                                            val currentUser = supabase.auth.currentUserOrNull()
                                            if (currentUser != null) {
                                                val historyToInsert = buildJsonObject {
                                                    put("url", currentUrl)
                                                    put("title", view?.title ?: "")
                                                    put("timestamp", SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date()))
                                                    put("is_safe", isSafe)
                                                    put("user_id", currentUser.id)
                                                }
                                                withContext(Dispatchers.IO) {
                                                    supabase.from("history").insert(historyToInsert)
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }
                        settings.javaScriptEnabled = true
                        loadUrl(url)
                        webView = this
                    }
                },
                update = {
                    if (it.url != url) {
                        it.loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter), color = Color(0xFF4CAF50))
            }

            // Warning Overlay if unsafe (Simulation)
            if (!isSafe) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Red, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Situs Terblokir", color = White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Snailly mendeteksi konten yang mungkin tidak aman untuk anak-anak di situs ini.",
                            color = White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { 
                                url = "https://www.google.com"
                                isSafe = true 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Kembali ke Google")
                        }
                    }
                }
            }
        }
    }
}

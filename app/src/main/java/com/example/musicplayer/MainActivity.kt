package com.example.musicplayer

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.viewmodel.VoiceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<VoiceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var keepSplashScreenOn = true

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                keepSplashScreenOn
            }
        }

        lifecycle.coroutineScope.launch {
            // just place holder for little long operation to finish before the splash screen continue
             keepSplashScreenOn = false
        }

        setContent {
            MusicPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VoiceRecognitionScreen(modifier = Modifier.padding(innerPadding), viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceRecognitionScreen(modifier: Modifier = Modifier, viewModel: VoiceViewModel = viewModel()) {
    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    when {
        audioPermissionState.status.isGranted -> {
            // Permission is granted, show the main UI
            VoiceRecognitionContent(modifier, viewModel)
        }
        audioPermissionState.status.shouldShowRationale -> {
            // Show rationale and request permission
            RationaleDialog(onRequestPermission = { audioPermissionState.launchPermissionRequest() })
        }
        else -> {
            // Permission hasn't been requested yet, request it
            LaunchedEffect(Unit) {
                audioPermissionState.launchPermissionRequest()
            }
            Text("Requesting audio permission...")
        }
    }
}

@Composable
fun VoiceRecognitionContent(modifier: Modifier = Modifier, viewModel: VoiceViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = uiState.recognizedText,
                onValueChange = { /* Read-only */ },
                readOnly = true,
                label = { Text("Recognized Text") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.toggleRecording() },
                modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
            ) {
                Text(if (uiState.isRecording) "Stop" else "Start")
            }

            Button(
                onClick = { /* viewModel.toggleAuthentication() */ },
                enabled = !uiState.isAuthenticated,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (uiState.isAuthenticated) "Authenticated" else "Authenticate")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            item {
                Text(
                    text = "Action: ${uiState.actionItem}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun RationaleDialog(onRequestPermission: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Handle dismiss */ },
        title = { Text("Permission Required") },
        text = { Text("Audio recording permission is required for this feature to work.") },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Request Permission")
            }
        }
    )
}
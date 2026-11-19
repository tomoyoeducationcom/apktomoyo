package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.attemptAutoSignIn
import com.example.auth.authStateFlow
import com.example.auth.signOutUser
import com.example.data.ParentPreferences
import com.example.data.ParentRepository
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.TomoyoBrandEmblem
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ParentDashboardShell
import com.example.ui.screens.ParentVerificationScreen
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.TomoyoParentTheme
import com.example.viewmodel.ParentDashboardViewModel
import com.example.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TomoyoParentTheme {
                TomoyoParentAppRoot()
            }
        }
    }
}

@Composable
private fun TomoyoParentAppRoot() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val authUser by Firebase.auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

    var isCheckingAutoSignIn by remember {
        mutableStateOf(Firebase.auth.currentUser == null)
    }

    LaunchedEffect(Unit) {
        if (Firebase.auth.currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = { isCheckingAutoSignIn = false },
                onUnauthenticated = { isCheckingAutoSignIn = false },
                scope = scope
            )
        } else {
            isCheckingAutoSignIn = false
        }
    }

    val verifiedUser = authUser?.takeIf { it.isEmailVerified }

    when {
        isCheckingAutoSignIn && verifiedUser == null -> {
            BrandedLoadingScreen(statusText = "Memeriksa sesi akun orang tua...")
        }

        verifiedUser == null -> {
            LoginScreen(
                onAuthSuccess = { isCheckingAutoSignIn = false }
            )
        }

        else -> {
            key(verifiedUser.uid) {
                val appContext = context.applicationContext
                val vm: ParentDashboardViewModel = viewModel(
                    key = verifiedUser.uid,
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ParentDashboardViewModel(
                                repository = ParentRepository(appContext),
                                preferences = ParentPreferences(appContext),
                                currentUserId = verifiedUser.uid,
                                userEmail = verifiedUser.email.orEmpty(),
                                userDisplayName = verifiedUser.displayName.orEmpty(),
                                userPhotoUrl = verifiedUser.photoUrl?.toString()
                            ) as T
                        }
                    }
                )

                val profileState by vm.parentProfileState.collectAsStateWithLifecycle()
                val profileEditState by vm.profileEditState.collectAsStateWithLifecycle()

                val handleSignOut: () -> Unit = {
                    signOutUser(
                        credentialManager = credentialManager,
                        scope = scope
                    )
                }

                when (val state = profileState) {
                    is UiState.Loading -> {
                        BrandedLoadingScreen(statusText = "Memuat profil orang tua...")
                    }

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.widthIn(max = 480.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                TomoyoBrandEmblem(size = 64.dp)
                                ErrorStateCard(message = state.message)
                                OutlinedButton(onClick = handleSignOut) {
                                    Text("Keluar / Ganti Akun Google")
                                }
                            }
                        }
                    }

                    is UiState.Success -> {
                        val profile = state.data
                        if (profile == null) {
                            ParentVerificationScreen(
                                userEmail = verifiedUser.email.orEmpty(),
                                initialName = verifiedUser.displayName.orEmpty(),
                                editState = profileEditState,
                                onActivateProfile = { name, phone ->
                                    vm.saveParentProfile(name, phone)
                                },
                                onSignOut = handleSignOut
                            )
                        } else {
                            ParentDashboardShell(
                                viewModel = vm,
                                profile = profile,
                                onSignOutConfirmed = handleSignOut
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandedLoadingScreen(statusText: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TomoyoBrandEmblem(size = 72.dp)
            Spacer(modifier = Modifier.height(18.dp))
            CircularProgressIndicator(color = BrandCrimson)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

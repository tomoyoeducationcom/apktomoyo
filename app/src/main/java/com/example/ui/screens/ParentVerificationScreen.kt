package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.components.BatikPaomanDivider
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.TomoyoBrandEmblem
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.CoastalTeal
import com.example.viewmodel.ProfileEditState

@Composable
fun ParentVerificationScreen(
    userEmail: String,
    initialName: String,
    editState: ProfileEditState,
    onActivateProfile: (name: String, phone: String) -> Unit,
    onSignOut: () -> Unit
) {
    var displayName by remember(initialName) { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        TomoyoBrandEmblem(size = 52.dp)
                        Column {
                            Text(
                                text = "Verifikasi Akun Orang Tua",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "TOMOYO PARENT · Aktivasi Profil Keluarga",
                                style = MaterialTheme.typography.labelMedium,
                                color = BrandCrimson
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    BatikPaomanDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Akun Google Anda belum terdaftar dalam profil orang tua TOMOYO EDUCATION. Lengkapi nama lengkap dan nomor WhatsApp aktif Anda untuk mengaktifkan akses dashboard orang tua.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, CoastalTeal.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Verified,
                                contentDescription = "Email terverifikasi",
                                tint = CoastalTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Email Google Terverifikasi",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = CoastalTeal
                                )
                                Text(
                                    text = userEmail.ifBlank { "-" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = {
                            displayName = it
                            localError = null
                        },
                        label = { Text("Nama Lengkap Orang Tua / Wali *") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Badge, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verify_parent_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            localError = null
                        },
                        label = { Text("Nomor WhatsApp Aktif * (cth. 081234567890)") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Phone, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verify_parent_phone_input")
                    )

                    val shownError = localError ?: if (editState.isError) editState.message else null
                    if (shownError != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        ErrorStateCard(message = shownError)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val cleanName = displayName.trim()
                            val cleanPhone = phone.trim()
                            val digits = cleanPhone.filter { it.isDigit() }
                            if (cleanName.length < 2) {
                                localError = "Nama lengkap orang tua minimal 2 karakter."
                                return@Button
                            }
                            if (digits.length < 8) {
                                localError = "Nomor WhatsApp aktif wajib valid (minimal 8 digit)."
                                return@Button
                            }
                            onActivateProfile(cleanName, cleanPhone)
                        },
                        enabled = !editState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("activate_parent_profile_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandCrimson,
                            contentColor = Color.White
                        )
                    ) {
                        if (editState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mengaktifkan Profil...")
                        } else {
                            Text("Aktifkan Profil & Masuk Dashboard")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onSignOut,
                        enabled = !editState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_switch_account_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Keluar / Ganti Akun Google")
                    }
                }
            }
        }
    }
}

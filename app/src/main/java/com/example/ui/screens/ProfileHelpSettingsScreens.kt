package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.BranchLocation
import com.example.model.ParentProfile
import com.example.model.SiteContentSection
import com.example.model.TomoyoDefaults
import com.example.ui.components.EditorialCard
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.StatusTag
import com.example.ui.components.StatusTone
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.CoastalTeal
import com.example.ui.theme.CoastalTealSoft
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg

private fun openExternalUri(context: Context, uriString: String) {
    if (uriString.isBlank()) return
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, "Tidak dapat membuka tautan di perangkat ini.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ParentProfileScreen(
    profile: ParentProfile?,
    fallbackName: String,
    userEmail: String,
    userPhotoUrl: String?,
    editState: com.example.viewmodel.ProfileEditState,
    onSaveProfile: (name: String, phone: String) -> Unit,
    onClearMessage: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    var name by remember(profile?.displayName, fallbackName) {
        mutableStateOf(profile?.displayName?.ifBlank { fallbackName } ?: fallbackName)
    }
    var phone by remember(profile?.phone) {
        mutableStateOf(profile?.phone.orEmpty())
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Profil Orang Tua",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            EditorialCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (!userPhotoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = userPhotoUrl,
                            contentDescription = "Foto akun Google",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.PersonOutline,
                                    contentDescription = null,
                                    tint = BrandCrimson,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile?.displayName?.ifBlank { fallbackName } ?: fallbackName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        StatusTag(label = "Akun Google Terverifikasi", tone = StatusTone.TEAL)
                    }
                }
            }
        }

        if (!editState.message.isNullOrBlank()) {
            item {
                if (editState.isError) {
                    ErrorStateCard(message = editState.message)
                } else {
                    EditorialCard(
                        containerColor = StatusSuccessBg,
                        borderColor = StatusSuccess.copy(alpha = 0.3f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = StatusSuccess)
                            Text(editState.message, style = MaterialTheme.typography.bodyMedium, color = StatusSuccess)
                        }
                    }
                }
            }
        }

        item {
            EditorialCard {
                Text(
                    text = "Informasi Kontak Keluarga",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Data ini otomatis terisi saat Anda mendaftarkan anak di TOMOYO EDUCATION.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        onClearMessage()
                    },
                    label = { Text("Nama Lengkap Orang Tua / Wali *") },
                    leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        onClearMessage()
                    },
                    label = { Text("Nomor WhatsApp Aktif") },
                    leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_phone_input")
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onSaveProfile(name, phone) },
                    enabled = !editState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("profile_save_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandCrimson,
                        contentColor = Color.White
                    )
                ) {
                    if (editState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Menyimpan...")
                    } else {
                        Text("Simpan Profil Orang Tua")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
fun HelpAndContactScreen(
    contact: SiteContentSection,
    branches: List<BranchLocation>,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Bantuan dan Kontak Resmi",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Hubungi admin resmi ${TomoyoDefaults.OFFICIAL_NAME} atau kunjungi lokasi pusat & cabang kami di Indramayu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            EditorialCard {
                Text(
                    text = "Layanan Komunikasi Orang Tua",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                val waDisplay = contact.whatsappDisplay?.ifBlank { "0821-1977-0700" } ?: "0821-1977-0700"
                val waUrl = contact.whatsappUrl?.ifBlank { "https://wa.me/6282119770700" }
                    ?: "https://wa.me/6282119770700"
                val emailAddress = contact.email?.ifBlank { "tomoyoeducation.com@gmail.com" }
                    ?: "tomoyoeducation.com@gmail.com"

                Button(
                    onClick = { openExternalUri(context, waUrl) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("contact_whatsapp_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CoastalTeal,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Outlined.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("WhatsApp Admin ($waDisplay)")
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { openExternalUri(context, "mailto:$emailAddress") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("contact_email_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Outlined.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(emailAddress)
                }

                val igUrl = contact.instagramUrl.orEmpty()
                val fbUrl = contact.facebookUrl.orEmpty()
                if (igUrl.isNotBlank() || fbUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (igUrl.isNotBlank()) {
                            OutlinedButton(
                                onClick = { openExternalUri(context, igUrl) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Instagram")
                            }
                        }
                        if (fbUrl.isNotBlank()) {
                            OutlinedButton(
                                onClick = { openExternalUri(context, fbUrl) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Facebook")
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Lokasi Belajar Indramayu",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(branches, key = { it.slug }) { branch ->
            EditorialCard {
                StatusTag(label = branch.roleLabel, tone = StatusTone.TEAL)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = branch.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (branch.latitude != null && branch.longitude != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Koordinat: ${branch.latitude}, ${branch.longitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (branch.mapsUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { openExternalUri(context, branch.mapsUrl) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("branch_maps_${branch.slug}"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka Peta Lokasi")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Outlined.OpenInNew, contentDescription = null, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
fun AccountSettingsScreen(
    userEmail: String,
    currentUserId: String,
    backendApiUrl: String,
    onSaveBackendUrl: (String) -> Unit,
    onRequestSignOut: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    val context = LocalContext.current
    val dbId = remember(context) { context.getString(R.string.firestore_database_id) }
    var serverUrlInput by remember(backendApiUrl) { mutableStateOf(backendApiUrl) }
    var savedUrlBanner by remember { mutableStateOf(false) }

    LaunchedEffect(savedUrlBanner) {
        if (savedUrlBanner) {
            kotlinx.coroutines.delay(2500L)
            savedUrlBanner = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Pengaturan Akun & Keamanan",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            EditorialCard(
                containerColor = CoastalTealSoft,
                borderColor = CoastalTeal.copy(alpha = 0.25f)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VerifiedUser,
                        contentDescription = "Keamanan Akun",
                        tint = CoastalTeal
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Isolasi Data Orang Tua Aktif",
                            style = MaterialTheme.typography.titleSmall,
                            color = CoastalTeal
                        )
                        Text(
                            text = "Aplikasi TOMOYO PARENT hanya memproses data milik akun Anda (UID terverifikasi) dan tidak memiliki akses ke fitur admin ataupun data keluarga lain.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item {
            EditorialCard {
                Text("Informasi Sesi Firebase", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(10.dp))
                SettingMetaRow("Email Google", userEmail)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)
                SettingMetaRow("Parent UID", currentUserId)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)
                SettingMetaRow("Database ID", dbId)
            }
        }

        item {
            EditorialCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsActive,
                        contentDescription = null,
                        tint = BrandCrimson
                    )
                    Text(
                        text = "Server Notifikasi WhatsApp (Fonnte)",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Opsional: Masukkan URL HTTPS server backend TOMOYO EDUCATION Anda (endpoint /api/notify/registration) agar pengiriman pendaftaran anak otomatis memicu notifikasi WhatsApp admin via Fonnte.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = serverUrlInput,
                    onValueChange = {
                        serverUrlInput = it
                        savedUrlBanner = false
                    },
                    label = { Text("URL Backend (cth. https://tomoyo.example.com)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_backend_url_input")
                )
                if (savedUrlBanner) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "URL server backend berhasil diperbarui.",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusSuccess
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        onSaveBackendUrl(serverUrlInput.trim())
                        savedUrlBanner = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("settings_save_backend_url_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan URL Backend")
                }
            }
        }

        item {
            EditorialCard {
                Text("Keluar Sesi", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Akhiri sesi Google Anda di perangkat ini. Anda dapat masuk kembali kapan saja menggunakan akun Google yang sama.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRequestSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("settings_logout_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusDanger,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout Akun Orang Tua")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun SettingMetaRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.ifBlank { "-" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

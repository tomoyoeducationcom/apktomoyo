package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.BranchLocation
import com.example.model.ChildRegistrationDraft
import com.example.model.LearningPackage
import com.example.model.TomoyoFormatters
import com.example.ui.components.EditorialCard
import com.example.ui.components.ErrorStateCard
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.CoastalTeal
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.viewmodel.RegistrationFormState

@Composable
fun ChildRegistrationScreen(
    formState: RegistrationFormState,
    packages: List<LearningPackage>,
    branches: List<BranchLocation>,
    onUpdateParentInfo: (name: String?, phone: String?) -> Unit,
    onUpdateChildDraft: (localId: String, (ChildRegistrationDraft) -> ChildRegistrationDraft) -> Unit,
    onAddChildRow: () -> Unit,
    onRemoveChildRow: (localId: String) -> Unit,
    onUploadPhoto: (localId: String, uri: Uri) -> Unit,
    onSubmit: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    var activeUploadDraftId by remember { mutableStateOf<String?>(null) }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val targetId = activeUploadDraftId
        if (uri != null && targetId != null) {
            onUploadPhoto(targetId, uri)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Pendaftaran Anak Baru",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Anda dapat mendaftarkan 1 atau beberapa anak sekaligus. Foto diunggah aman ke Cloudinary dan setiap pengajuan masuk ke status Menunggu Persetujuan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!formState.submitSuccessMessage.isNullOrBlank()) {
            item {
                EditorialCard(
                    containerColor = StatusSuccessBg,
                    borderColor = StatusSuccess.copy(alpha = 0.35f)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Berhasil",
                            tint = StatusSuccess
                        )
                        Text(
                            text = formState.submitSuccessMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = StatusSuccess
                        )
                    }
                }
            }
        }

        if (!formState.submitWarningMessage.isNullOrBlank()) {
            item {
                EditorialCard(
                    containerColor = StatusWarningBg,
                    borderColor = StatusWarning.copy(alpha = 0.35f)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Informasi",
                            tint = StatusWarning
                        )
                        Text(
                            text = formState.submitWarningMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (formState.formErrors.isNotEmpty()) {
            item {
                ErrorStateCard(message = formState.formErrors.joinToString("\n• ", prefix = "• "))
            }
        }

        // Parent Identity Section
        item {
            EditorialCard {
                Text(
                    text = "Data Orang Tua / Wali",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = formState.parentName,
                    onValueChange = { onUpdateParentInfo(it, null) },
                    label = { Text("Nama Lengkap Orang Tua / Wali *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_parent_name_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = formState.parentPhone,
                    onValueChange = { onUpdateParentInfo(null, it) },
                    label = { Text("Nomor WhatsApp Aktif *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_parent_phone_input")
                )
            }
        }

        // Child Draft Cards
        itemsIndexed(formState.children, key = { _, item -> item.localId }) { idx, child ->
            val selectedPkg = packages.find { it.slug == child.packageId } ?: packages.firstOrNull()
            val selectedBranch = branches.find { it.slug == child.branchId } ?: branches.firstOrNull()
            val livePrice = TomoyoFormatters.getPackagePriceForBranch(selectedPkg, child.branchId)
            val liveNote = TomoyoFormatters.getPackagePriceNoteForBranch(selectedPkg, child.branchId)
            val isUploading = child.localId in formState.uploadingDraftIds
            val uploadErr = formState.uploadErrors[child.localId]

            EditorialCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calon Siswa #${idx + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandCrimson
                    )
                    if (formState.children.size > 1) {
                        IconButton(
                            onClick = { onRemoveChildRow(child.localId) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Hapus baris anak ke-${idx + 1}",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = child.childName,
                    onValueChange = { name ->
                        onUpdateChildDraft(child.localId) { it.copy(childName = name) }
                    },
                    label = { Text("Nama Lengkap Anak *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_child_name_${idx}")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = child.gradeLevel,
                    onValueChange = { grade ->
                        onUpdateChildDraft(child.localId) { it.copy(gradeLevel = grade) }
                    },
                    label = { Text("Tingkat Kelas * (cth. TK B, Kelas 3 SD)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_child_grade_${idx}")
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Jenis Kelamin *", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Laki-laki", "Perempuan").forEach { gender ->
                        FilterChip(
                            selected = child.childGender == gender,
                            onClick = {
                                onUpdateChildDraft(child.localId) { it.copy(childGender = gender) }
                            },
                            label = { Text(gender) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Foto Anak (Opsional — Cloudinary)", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(6.dp))
                if (child.childPhotoUrl.isNotBlank()) {
                    AsyncImage(
                        model = child.childPhotoUrl,
                        contentDescription = "Pratinjau foto ${child.childName}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedButton(
                    onClick = {
                        activeUploadDraftId = child.localId
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    enabled = !isUploading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reg_upload_photo_${idx}")
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mengunggah ke Cloudinary...")
                    } else {
                        Icon(Icons.Outlined.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (child.childPhotoUrl.isBlank()) "Pilih & Unggah Foto Anak" else "Ganti Foto Anak")
                    }
                }
                if (!uploadErr.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uploadErr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Pilih Cabang TOMOYO *", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    branches.forEach { branch ->
                        val active = child.branchId == branch.slug
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (active) BrandCrimson else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onUpdateChildDraft(child.localId) { it.copy(branchId = branch.slug) }
                            }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = branch.roleLabel.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandCrimson
                                )
                                Text(
                                    text = branch.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Pilih Paket Belajar Resmi *", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    packages.forEach { pkg ->
                        val active = child.packageId == pkg.slug
                        val branchPrice = TomoyoFormatters.getPackagePriceForBranch(pkg, child.branchId)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (active) BrandCrimson else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onUpdateChildDraft(child.localId) { it.copy(packageId = pkg.slug) }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pkg.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = pkg.programType,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = if (branchPrice != null) {
                                        TomoyoFormatters.formatRupiah(branchPrice)
                                    } else {
                                        "Belum diatur"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = CoastalTeal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tarif Paket (${selectedBranch?.roleLabel ?: child.branchId})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${selectedPkg?.title.orEmpty()} — ${selectedBranch?.name.orEmpty()}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (!liveNote.isNullOrBlank()) {
                                Text(
                                    text = liveNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = if (livePrice != null) TomoyoFormatters.formatRupiah(livePrice) else "Belum diatur",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (livePrice != null) StatusSuccess else StatusWarning
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = child.notes,
                    onValueChange = { note ->
                        onUpdateChildDraft(child.localId) { it.copy(notes = note) }
                    },
                    label = { Text("Catatan Tambahan (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onAddChildRow,
                    enabled = formState.children.size < 10 && !formState.isSubmitting,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("reg_add_child_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tambah Anak")
                }

                Button(
                    onClick = onSubmit,
                    enabled = !formState.isSubmitting && formState.uploadingDraftIds.isEmpty(),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(50.dp)
                        .testTag("reg_submit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandCrimson,
                        contentColor = Color.White
                    )
                ) {
                    if (formState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mengirim...")
                    } else {
                        Text("Kirim Pendaftaran")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

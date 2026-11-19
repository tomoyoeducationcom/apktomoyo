package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.BranchLocation
import com.example.model.LearningPackage
import com.example.model.StudentRecord
import com.example.model.TomoyoFormatters
import com.example.ui.components.EditorialCard
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.LoadingSkeletonCard
import com.example.ui.components.StatusTag
import com.example.ui.components.StatusTone
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.CoastalTeal
import com.example.ui.theme.StatusSuccess
import com.example.viewmodel.PendingChildEditState
import com.example.viewmodel.UiState

@Composable
fun ChildrenAndEducationScreen(
    studentsState: UiState<List<StudentRecord>>,
    packages: List<LearningPackage>,
    branches: List<BranchLocation>,
    pendingEditState: PendingChildEditState,
    focusEducationStatus: Boolean,
    onStartEditPending: (StudentRecord) -> Unit,
    onUpdateEditDraft: ((StudentRecord) -> StudentRecord) -> Unit,
    onUploadEditPhoto: (android.net.Uri) -> Unit,
    onSaveEdit: () -> Unit,
    onDismissEdit: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    var filterMode by remember(focusEducationStatus) {
        mutableStateOf(if (focusEducationStatus) "status" else "all")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (focusEducationStatus) "Status Pendidikan Anak" else "Data Anak Terhubung",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Menampilkan data anak yang terhubung langsung dengan akun orang tua Anda. Status pendaftaran dan status akademik dikelola oleh admin resmi TOMOYO EDUCATION.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = filterMode == "all",
                        onClick = { filterMode = "all" },
                        label = { Text("Semua Anak") }
                    )
                    FilterChip(
                        selected = filterMode == "status",
                        onClick = { filterMode = "status" },
                        label = { Text("Fokus Status Belajar") }
                    )
                }
            }
        }

        when (studentsState) {
            is UiState.Loading -> {
                items(2) { LoadingSkeletonCard() }
            }
            is UiState.Error -> {
                item { ErrorStateCard(message = studentsState.message) }
            }
            is UiState.Success -> {
                val list = studentsState.data
                if (list.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "Belum Ada Anak Terdaftar",
                            description = "Daftarkan anak Anda melalui formulir pendaftaran agar data pendidikan dan pembayaran tersinkronisasi.",
                            action = {
                                Button(
                                    onClick = onNavigateToRegister,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrandCrimson,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("empty_children_register_button")
                                ) {
                                    Icon(Icons.Outlined.PersonAddAlt1, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Daftarkan Anak Sekarang")
                                }
                            }
                        )
                    }
                } else {
                    items(list, key = { it.id }) { student ->
                        StudentDetailCard(
                            student = student,
                            focusEducationStatus = filterMode == "status",
                            onEditPending = { onStartEditPending(student) }
                        )
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(12.dp)) }
    }

    if (pendingEditState.student != null) {
        EditPendingChildDialog(
            state = pendingEditState,
            packages = packages,
            branches = branches,
            onUpdate = onUpdateEditDraft,
            onUploadPhoto = onUploadEditPhoto,
            onSave = onSaveEdit,
            onDismiss = onDismissEdit
        )
    }
}

@Composable
private fun StudentDetailCard(
    student: StudentRecord,
    focusEducationStatus: Boolean,
    onEditPending: () -> Unit
) {
    val regTone = when (student.status) {
        "approved" -> StatusTone.SUCCESS
        "rejected" -> StatusTone.DANGER
        else -> StatusTone.WARNING
    }
    val acadTone = when (student.academicStatus) {
        "aktif" -> StatusTone.TEAL
        "lulus" -> StatusTone.SUCCESS
        "nonaktif" -> StatusTone.DANGER
        else -> StatusTone.NEUTRAL
    }

    EditorialCard(modifier = Modifier.testTag("student_card_${student.id}")) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!student.childPhotoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = student.childPhotoUrl,
                    contentDescription = "Foto ${student.childName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Face,
                            contentDescription = "Belum ada foto anak",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.childName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = buildString {
                        append(student.gradeLevel)
                        if (!student.childGender.isNullOrBlank()) {
                            append(" · ${student.childGender}")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusTag(
                        label = TomoyoFormatters.getStatusLabel(student.status),
                        tone = regTone
                    )
                    StatusTag(
                        label = "Belajar: ${TomoyoFormatters.getAcademicStatusLabel(student.academicStatus)}",
                        tone = acadTone
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            InfoRow("Program", student.packageName)
            InfoRow("Cabang", student.branchName)
            if (!focusEducationStatus) {
                if (!student.schoolOrigin.isNullOrBlank()) {
                    InfoRow("Asal Sekolah", student.schoolOrigin)
                }
                if (!student.birthDate.isNullOrBlank()) {
                    InfoRow("Tanggal Lahir", student.birthDate)
                }
                if (!student.studentAddress.isNullOrBlank()) {
                    InfoRow("Alamat", student.studentAddress)
                }
            }
            if (student.billingAmount != null) {
                InfoRow(
                    "Tarif Paket",
                    "${TomoyoFormatters.formatRupiah(student.billingAmount)}" +
                        (student.initialPaymentStatus?.let { " (${TomoyoFormatters.getPaymentStatusLabel(it)})" } ?: ""),
                    valueColor = StatusSuccess
                )
            }
            if (!student.registeredAtIso.isNullOrBlank()) {
                InfoRow("Tanggal Daftar", TomoyoFormatters.formatJakartaDateTime(student.registeredAtIso))
            }
            if (!student.nextDueAtIso.isNullOrBlank()) {
                InfoRow("Jatuh Tempo", TomoyoFormatters.formatJakartaDateTime(student.nextDueAtIso))
            }
            if (!student.notes.isNullOrBlank()) {
                InfoRow("Catatan Orang Tua", student.notes)
            }
        }

        if (!student.adminNotes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Catatan Admin TOMOYO",
                        style = MaterialTheme.typography.labelMedium,
                        color = CoastalTeal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = student.adminNotes,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        if (student.status == "pending") {
            OutlinedButton(
                onClick = onEditPending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("edit_pending_child_${student.id}"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ubah Data (Selama Menunggu)")
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Data terverifikasi admin — hubungi admin untuk perubahan administrasi.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.38f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            modifier = Modifier.weight(0.62f)
        )
    }
}

@Composable
private fun EditPendingChildDialog(
    state: PendingChildEditState,
    packages: List<LearningPackage>,
    branches: List<BranchLocation>,
    onUpdate: ((StudentRecord) -> StudentRecord) -> Unit,
    onUploadPhoto: (android.net.Uri) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val student = state.student ?: return
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onUploadPhoto(uri)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Data Pendaftaran") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = student.childName,
                    onValueChange = { name -> onUpdate { it.copy(childName = name) } },
                    label = { Text("Nama Lengkap Anak *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = student.gradeLevel,
                    onValueChange = { grade -> onUpdate { it.copy(gradeLevel = grade) } },
                    label = { Text("Tingkat Kelas *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = student.parentPhone,
                    onValueChange = { phone -> onUpdate { it.copy(parentPhone = phone) } },
                    label = { Text("Nomor WhatsApp *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Jenis Kelamin", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Laki-laki", "Perempuan").forEach { gender ->
                        FilterChip(
                            selected = (student.childGender ?: "Laki-laki") == gender,
                            onClick = { onUpdate { it.copy(childGender = gender) } },
                            label = { Text(gender) }
                        )
                    }
                }
                Text("Cabang", style = MaterialTheme.typography.labelMedium)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    branches.forEach { branch ->
                        val active = student.branchId == branch.slug
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (active) BrandCrimson else MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onUpdate { it.copy(branchId = branch.slug) } }
                        ) {
                            Text(
                                text = "${branch.roleLabel}: ${branch.name}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
                Text("Paket Belajar", style = MaterialTheme.typography.labelMedium)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    packages.forEach { pkg ->
                        val active = student.packageId == pkg.slug
                        val price = TomoyoFormatters.getPackagePriceForBranch(pkg, student.branchId)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (active) BrandCrimson else MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onUpdate { it.copy(packageId = pkg.slug) } }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = pkg.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = TomoyoFormatters.formatRupiah(price),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CoastalTeal
                                )
                            }
                        }
                    }
                }
                OutlinedButton(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    enabled = !state.isUploadingPhoto,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isUploadingPhoto) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mengunggah foto...")
                    } else {
                        Icon(Icons.Outlined.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (student.childPhotoUrl.isNullOrBlank()) "Unggah Foto Anak" else "Ganti Foto Anak")
                    }
                }
                OutlinedTextField(
                    value = student.notes.orEmpty(),
                    onValueChange = { note -> onUpdate { it.copy(notes = note) } },
                    label = { Text("Catatan Tambahan (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (!state.errorMessage.isNullOrBlank()) {
                    ErrorStateCard(message = state.errorMessage)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !state.isSaving && !state.isUploadingPhoto,
                colors = ButtonDefaults.buttonColors(containerColor = BrandCrimson, contentColor = Color.White)
            ) {
                Text(if (state.isSaving) "Menyimpan..." else "Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

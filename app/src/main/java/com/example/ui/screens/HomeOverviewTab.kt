package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.AnnouncementItem
import com.example.model.ParentProfile
import com.example.model.PaymentRecord
import com.example.model.StudentRecord
import com.example.model.TomoyoFormatters
import com.example.ui.components.BatikPaomanDivider
import com.example.ui.components.EditorialCard
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.LoadingSkeletonCard
import com.example.ui.components.StatusTag
import com.example.ui.components.StatusTone
import com.example.ui.components.TuguManggaSilhouette
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.CoastalTeal
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.MangoGold
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.viewmodel.UiState

enum class ParentModuleDestination {
    HOME,
    CHILDREN,
    EDUCATION_STATUS,
    REGISTER_CHILD,
    PAYMENT_INFO,
    PAYMENT_HISTORY,
    ANNOUNCEMENTS,
    SCHEDULES,
    PROFILE,
    HELP_CONTACT,
    ACCOUNT_SETTINGS
}

@Composable
fun HomeOverviewTab(
    profile: ParentProfile?,
    fallbackName: String,
    userEmail: String,
    userPhotoUrl: String?,
    studentsState: UiState<List<StudentRecord>>,
    paymentsState: UiState<List<PaymentRecord>>,
    announcementsState: UiState<List<AnnouncementItem>>,
    onNavigate: (ParentModuleDestination) -> Unit
) {
    val displayName = profile?.displayName?.takeIf { it.isNotBlank() } ?: fallbackName.ifBlank { "Orang Tua Murid" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Indramayu Editorial Welcome Banner
        item {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = InkPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_indramayu_coast),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0.28f)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        InkPrimary.copy(alpha = 0.92f),
                                        InkPrimary.copy(alpha = 0.72f)
                                    )
                                )
                            )
                    )
                    TuguManggaSilhouette(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 10.dp, bottom = 6.dp)
                            .size(72.dp),
                        strokeColor = MangoGold.copy(alpha = 0.65f),
                        fillColor = MangoGold.copy(alpha = 0.12f)
                    )
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (!userPhotoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = userPhotoUrl,
                                    contentDescription = "Foto profil $displayName",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.15f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Outlined.PersonOutline,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TOMOYO PARENT · INDRAMAYU",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MangoGold
                                )
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = userEmail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.78f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        BatikPaomanDivider(darkBackground = true)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Pantau perkembangan belajar, pendaftaran anak, jadwal bimbel, dan administrasi pembayaran resmi keluarga Anda.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                    }
                }
            }
        }

        // 2. Real-Time Family Summary Metrics
        item {
            when (studentsState) {
                is UiState.Loading -> LoadingSkeletonCard()
                is UiState.Error -> ErrorStateCard(message = studentsState.message)
                is UiState.Success -> {
                    val students = studentsState.data
                    val approvedCount = students.count { it.status == "approved" }
                    val pendingCount = students.count { it.status == "pending" }
                    val activeAcademicCount = students.count { it.academicStatus == "aktif" }
                    val dueStudents = students.filter {
                        TomoyoFormatters.isPaymentDueNow(it.nextDueAtIso, it.dueConfirmedIso)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SummaryMetricCard(
                                title = "Anak Terdaftar",
                                value = "${students.size}",
                                subtitle = "$approvedCount disetujui",
                                accent = CoastalTeal,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_metric_children")
                                    .clickable { onNavigate(ParentModuleDestination.CHILDREN) }
                            )
                            SummaryMetricCard(
                                title = "Status Aktif",
                                value = "$activeAcademicCount",
                                subtitle = "$pendingCount menunggu",
                                accent = BrandCrimson,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_metric_academic")
                                    .clickable { onNavigate(ParentModuleDestination.EDUCATION_STATUS) }
                            )
                        }

                        if (dueStudents.isNotEmpty()) {
                            EditorialCard(
                                containerColor = StatusWarningBg,
                                borderColor = StatusWarning.copy(alpha = 0.35f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.WarningAmber,
                                        contentDescription = "Jatuh tempo pembayaran",
                                        tint = StatusWarning
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Pengingat Jatuh Tempo (${dueStudents.size} Anak)",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = StatusWarning
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dueStudents.joinToString(", ") { it.childName } +
                                                " telah memasuki jadwal jatuh tempo pembayaran.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Access Modules Grid (All 12 modules easily reachable)
        item {
            Text(
                text = "Layanan Orang Tua",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickModuleTile(
                        title = "Data Anak",
                        subtitle = "Profil & paket anak",
                        icon = Icons.Outlined.ChildCare,
                        tag = "home_tile_children",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentModuleDestination.CHILDREN) }
                    )
                    QuickModuleTile(
                        title = "Pendaftaran Anak",
                        subtitle = "Tambah / daftar baru",
                        icon = Icons.Outlined.PersonAddAlt1,
                        tag = "home_tile_register",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentModuleDestination.REGISTER_CHILD) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickModuleTile(
                        title = "Status Pendidikan",
                        subtitle = "Pendaftaran & akademik",
                        icon = Icons.Outlined.School,
                        tag = "home_tile_education",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentModuleDestination.EDUCATION_STATUS) }
                    )
                    QuickModuleTile(
                        title = "Pembayaran",
                        subtitle = "Tagihan & riwayat",
                        icon = Icons.Outlined.ReceiptLong,
                        tag = "home_tile_payments",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentModuleDestination.PAYMENT_INFO) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickModuleTile(
                        title = "Pengumuman",
                        subtitle = "Info resmi lembaga",
                        icon = Icons.Outlined.Campaign,
                        tag = "home_tile_announcements",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentModuleDestination.ANNOUNCEMENTS) }
                    )
                    QuickModuleTile(
                        title = "Informasi Jadwal",
                        subtitle = "Jadwal belajar aktif",
                        icon = Icons.Outlined.CalendarMonth,
                        tag = "home_tile_schedules",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentModuleDestination.SCHEDULES) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickModuleTile(
                        title = "Profil Orang Tua",
                        subtitle = "Nama & WhatsApp",
                        icon = Icons.Outlined.PersonOutline,
                        tag = "home_tile_profile",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentModuleDestination.PROFILE) }
                    )
                    QuickModuleTile(
                        title = "Bantuan & Kontak",
                        subtitle = "WhatsApp & lokasi cabang",
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        tag = "home_tile_help",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(ParentModuleDestination.HELP_CONTACT) }
                    )
                }
                QuickModuleTile(
                    title = "Pengaturan Akun & Sesi",
                    subtitle = "Keamanan akun Google, koneksi server notifikasi, dan keluar",
                    icon = Icons.Outlined.Settings,
                    tag = "home_tile_settings",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigate(ParentModuleDestination.ACCOUNT_SETTINGS) }
                )
            }
        }

        // 4. Latest Payment Snapshot (Real Data Only)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ringkasan Pembayaran Terbaru",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Lihat Semua",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrandCrimson,
                    modifier = Modifier
                        .clickable { onNavigate(ParentModuleDestination.PAYMENT_HISTORY) }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                )
            }
        }

        item {
            when (paymentsState) {
                is UiState.Loading -> LoadingSkeletonCard()
                is UiState.Error -> ErrorStateCard(message = paymentsState.message)
                is UiState.Success -> {
                    val latest = paymentsState.data.firstOrNull()
                    if (latest == null) {
                        EmptyStateCard(
                            title = "Belum Ada Transaksi Tercatat",
                            description = "Riwayat pembayaran akan muncul otomatis setelah admin mencatat pembayaran resmi Anda."
                        )
                    } else {
                        EditorialCard(
                            modifier = Modifier.clickable { onNavigate(ParentModuleDestination.PAYMENT_HISTORY) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = latest.childName,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "${latest.periodLabel} · ${latest.method}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = TomoyoFormatters.formatRupiah(latest.amount),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    StatusTag(
                                        label = TomoyoFormatters.getPaymentStatusLabel(latest.status),
                                        tone = when (latest.status) {
                                            "lunas" -> StatusTone.SUCCESS
                                            "cicilan" -> StatusTone.WARNING
                                            else -> StatusTone.DANGER
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Latest Published Announcement Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pengumuman Terbaru",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Semua Info",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrandCrimson,
                    modifier = Modifier
                        .clickable { onNavigate(ParentModuleDestination.ANNOUNCEMENTS) }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                )
            }
        }

        item {
            when (announcementsState) {
                is UiState.Loading -> LoadingSkeletonCard()
                is UiState.Error -> ErrorStateCard(message = announcementsState.message)
                is UiState.Success -> {
                    val latest = announcementsState.data.firstOrNull()
                    if (latest == null) {
                        EmptyStateCard(
                            title = "Belum Ada Pengumuman",
                            description = "Pengumuman resmi dari TOMOYO EDUCATION akan ditampilkan di sini."
                        )
                    } else {
                        EditorialCard(
                            modifier = Modifier.clickable { onNavigate(ParentModuleDestination.ANNOUNCEMENTS) }
                        ) {
                            if (!latest.dateLabel.isNullOrBlank()) {
                                Text(
                                    text = latest.dateLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandCrimson
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = latest.title,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = latest.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    EditorialCard(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            color = accent
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickModuleTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .testTag(tag)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BrandCrimson,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

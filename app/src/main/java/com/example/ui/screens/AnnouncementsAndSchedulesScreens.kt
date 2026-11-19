package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.AnnouncementItem
import com.example.model.BranchLocation
import com.example.model.LearningPackage
import com.example.model.ScheduleItem
import com.example.model.TomoyoFormatters
import com.example.ui.components.EditorialCard
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.LoadingSkeletonCard
import com.example.ui.components.StatusTag
import com.example.ui.components.StatusTone
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.CoastalTeal
import com.example.viewmodel.UiState

@Composable
fun AnnouncementsScreen(
    announcementsState: UiState<List<AnnouncementItem>>,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Pengumuman Resmi",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Informasi kegiatan belajar, libur akademik, dan pembaruan resmi dari TOMOYO EDUCATION Indramayu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        when (announcementsState) {
            is UiState.Loading -> items(2) { LoadingSkeletonCard() }
            is UiState.Error -> item { ErrorStateCard(message = announcementsState.message) }
            is UiState.Success -> {
                val itemsList = announcementsState.data
                if (itemsList.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "Belum Ada Pengumuman",
                            description = "Pengumuman resmi yang dipublikasikan admin akan tampil langsung di halaman ini."
                        )
                    }
                } else {
                    items(itemsList, key = { it.id }) { item ->
                        EditorialCard(modifier = Modifier.testTag("announcement_card_${item.id}")) {
                            if (!item.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Campaign,
                                    contentDescription = null,
                                    tint = BrandCrimson,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = item.dateLabel?.takeIf { it.isNotBlank() }
                                        ?: TomoyoFormatters.formatTimestampJakarta(item.createdAt),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = BrandCrimson
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun SchedulesScreen(
    schedulesState: UiState<List<ScheduleItem>>,
    packages: List<LearningPackage>,
    branches: List<BranchLocation>,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    var selectedBranchFilter by remember { mutableStateOf("all") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Informasi Jadwal Belajar",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Jadwal pertemuan bimbingan belajar aktif sesuai cabang dan program TOMOYO EDUCATION.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedBranchFilter == "all",
                            onClick = { selectedBranchFilter = "all" },
                            label = { Text("Semua Cabang") }
                        )
                    }
                    items(branches, key = { it.slug }) { branch ->
                        FilterChip(
                            selected = selectedBranchFilter == branch.slug,
                            onClick = { selectedBranchFilter = branch.slug },
                            label = { Text(branch.roleLabel) }
                        )
                    }
                }
            }
        }

        when (schedulesState) {
            is UiState.Loading -> items(2) { LoadingSkeletonCard() }
            is UiState.Error -> item { ErrorStateCard(message = schedulesState.message) }
            is UiState.Success -> {
                val filtered = schedulesState.data.filter { item ->
                    selectedBranchFilter == "all" ||
                        item.branchId.isNullOrBlank() ||
                        item.branchId == selectedBranchFilter
                }
                if (filtered.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "Jadwal Belum Tersedia",
                            description = "Jadwal belajar resmi untuk filter cabang ini belum dipublikasikan oleh admin TOMOYO EDUCATION."
                        )
                    }
                } else {
                    items(filtered, key = { it.id }) { schedule ->
                        val branchObj = branches.find { it.slug == schedule.branchId }
                        val pkgObj = packages.find { it.slug == schedule.packageId }

                        EditorialCard(modifier = Modifier.testTag("schedule_card_${schedule.id}")) {
                            Text(
                                text = schedule.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = "Waktu jadwal",
                                    tint = CoastalTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = schedule.dayTime,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CoastalTeal
                                )
                            }

                            if (branchObj != null || pkgObj != null || !schedule.branchId.isNullOrBlank() || !schedule.packageId.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (pkgObj != null || !schedule.packageId.isNullOrBlank()) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.School,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = pkgObj?.title ?: schedule.packageId.orEmpty(),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    if (branchObj != null || !schedule.branchId.isNullOrBlank()) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.LocationOn,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            StatusTag(
                                                label = branchObj?.name ?: schedule.branchId.orEmpty(),
                                                tone = StatusTone.NEUTRAL
                                            )
                                        }
                                    }
                                }
                            }

                            if (!schedule.notes.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = schedule.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

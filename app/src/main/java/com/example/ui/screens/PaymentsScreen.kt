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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.WarningAmber
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.PaymentRecord
import com.example.model.StudentRecord
import com.example.model.TomoyoFormatters
import com.example.ui.components.EditorialCard
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.LoadingSkeletonCard
import com.example.ui.components.StatusTag
import com.example.ui.components.StatusTone
import com.example.ui.theme.CoastalTeal
import com.example.ui.theme.CoastalTealSoft
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningBg
import com.example.viewmodel.UiState

@Composable
fun PaymentsScreen(
    studentsState: UiState<List<StudentRecord>>,
    paymentsState: UiState<List<PaymentRecord>>,
    focusHistory: Boolean,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler(onBack = onBack)
    }

    var selectedTab by remember(focusHistory) {
        mutableStateOf(if (focusHistory) "history" else "billing")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (selectedTab == "history") "Riwayat Pembayaran" else "Informasi Pembayaran",
                    style = MaterialTheme.typography.headlineSmall
                )
                EditorialCard(
                    containerColor = CoastalTealSoft,
                    borderColor = CoastalTeal.copy(alpha = 0.25f)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Data hanya baca",
                            tint = CoastalTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Seluruh status tagihan dan transaksi pembayaran dikelola secara resmi oleh admin TOMOYO EDUCATION (hanya-baca untuk keamanan data keluarga).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedTab == "billing",
                        onClick = { selectedTab = "billing" },
                        label = { Text("Info Tagihan Anak") },
                        modifier = Modifier.testTag("payments_tab_billing")
                    )
                    FilterChip(
                        selected = selectedTab == "history",
                        onClick = { selectedTab = "history" },
                        label = { Text("Riwayat Transaksi") },
                        modifier = Modifier.testTag("payments_tab_history")
                    )
                }
            }
        }

        if (selectedTab == "billing") {
            when (studentsState) {
                is UiState.Loading -> items(2) { LoadingSkeletonCard() }
                is UiState.Error -> item { ErrorStateCard(message = studentsState.message) }
                is UiState.Success -> {
                    val students = studentsState.data
                    if (students.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = "Belum Ada Tagihan Siswa",
                                description = "Informasi nominal paket dan jadwal jatuh tempo bulanan akan muncul setelah anak terdaftar."
                            )
                        }
                    } else {
                        items(students, key = { it.id }) { student ->
                            val isDue = TomoyoFormatters.isPaymentDueNow(
                                student.nextDueAtIso,
                                student.dueConfirmedIso
                            )
                            EditorialCard(
                                containerColor = if (isDue) StatusWarningBg else MaterialTheme.colorScheme.surface,
                                borderColor = if (isDue) StatusWarning.copy(alpha = 0.38f) else MaterialTheme.colorScheme.outline
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = student.childName,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "${student.packageName} · ${student.branchName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    StatusTag(
                                        label = TomoyoFormatters.getPaymentStatusLabel(student.initialPaymentStatus),
                                        tone = when (student.initialPaymentStatus) {
                                            "lunas" -> StatusTone.SUCCESS
                                            "cicilan" -> StatusTone.WARNING
                                            else -> StatusTone.DANGER
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(10.dp))

                                PaymentDetailRow(
                                    label = "Nominal Tagihan",
                                    value = if (student.billingAmount != null) {
                                        TomoyoFormatters.formatRupiah(student.billingAmount)
                                    } else {
                                        "Belum ditentukan admin"
                                    },
                                    highlight = true
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                PaymentDetailRow(
                                    label = "Status Pendaftaran",
                                    value = TomoyoFormatters.getStatusLabel(student.status)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                PaymentDetailRow(
                                    label = "Jatuh Tempo",
                                    value = if (!student.nextDueAtIso.isNullOrBlank()) {
                                        TomoyoFormatters.formatJakartaDateTime(student.nextDueAtIso)
                                    } else {
                                        "Belum tersedia"
                                    }
                                )
                                if (!student.dueConfirmedIso.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    PaymentDetailRow(
                                        label = "Konfirmasi Terakhir",
                                        value = TomoyoFormatters.formatJakartaDateTime(student.dueConfirmedIso)
                                    )
                                }

                                if (isDue) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.WarningAmber,
                                            contentDescription = "Jatuh tempo",
                                            tint = StatusWarning,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Tagihan periode berjalan sudah jatuh tempo. Silakan lakukan pembayaran melalui admin cabang Anda.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = StatusWarning
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            when (paymentsState) {
                is UiState.Loading -> items(2) { LoadingSkeletonCard() }
                is UiState.Error -> item { ErrorStateCard(message = paymentsState.message) }
                is UiState.Success -> {
                    val records = paymentsState.data
                    val totalPaid = records
                        .filter { it.status == "lunas" || it.status == "cicilan" }
                        .sumOf { it.amount }

                    item {
                        EditorialCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Total Pembayaran Tercatat",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = TomoyoFormatters.formatRupiah(totalPaid),
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = StatusSuccess,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                StatusTag(
                                    label = "${records.size} Transaksi",
                                    tone = StatusTone.TEAL
                                )
                            }
                        }
                    }

                    if (records.isEmpty()) {
                        item {
                            EmptyStateCard(
                                title = "Belum Ada Riwayat Transaksi",
                                description = "Transaksi pembayaran yang diverifikasi admin TOMOYO EDUCATION akan tampil secara otomatis di sini."
                            )
                        }
                    } else {
                        items(records, key = { it.id }) { payment ->
                            EditorialCard(modifier = Modifier.testTag("payment_row_${payment.id}")) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = payment.childName,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = payment.periodLabel,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    StatusTag(
                                        label = TomoyoFormatters.getPaymentStatusLabel(payment.status),
                                        tone = when (payment.status) {
                                            "lunas" -> StatusTone.SUCCESS
                                            "cicilan" -> StatusTone.WARNING
                                            else -> StatusTone.DANGER
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(10.dp))

                                PaymentDetailRow(
                                    label = "Nominal",
                                    value = TomoyoFormatters.formatRupiah(payment.amount),
                                    highlight = true
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                PaymentDetailRow(
                                    label = "Metode Pembayaran",
                                    value = payment.method.ifBlank { "-" }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                PaymentDetailRow(
                                    label = "Tanggal Tercatat",
                                    value = TomoyoFormatters.formatTimestampJakarta(payment.createdAt)
                                )
                                if (!payment.dueDateIso.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    PaymentDetailRow(
                                        label = "Jatuh Tempo Periode",
                                        value = TomoyoFormatters.formatJakartaDateTime(payment.dueDateIso)
                                    )
                                }
                                if (!payment.notes.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    PaymentDetailRow(
                                        label = "Catatan",
                                        value = payment.notes
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun PaymentDetailRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (highlight) StatusSuccess else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.58f)
        )
    }
}

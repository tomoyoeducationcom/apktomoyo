package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ParentProfile
import com.example.ui.components.BatikPaomanDivider
import com.example.ui.components.TomoyoBrandEmblem
import com.example.ui.theme.BrandCrimson
import com.example.ui.theme.StatusDanger
import com.example.viewmodel.ParentDashboardViewModel

private data class DrawerMenuEntry(
    val destination: ParentModuleDestination,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardShell(
    viewModel: ParentDashboardViewModel,
    profile: ParentProfile,
    onSignOutConfirmed: () -> Unit
) {
    val context = LocalContext.current
    var currentModule by rememberSaveable { mutableStateOf(ParentModuleDestination.HOME) }
    var showMenuSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val studentsState by viewModel.studentsState.collectAsStateWithLifecycle()
    val paymentsState by viewModel.paymentsState.collectAsStateWithLifecycle()
    val packages by viewModel.packagesState.collectAsStateWithLifecycle()
    val branches by viewModel.branchesState.collectAsStateWithLifecycle()
    val announcementsState by viewModel.announcementsState.collectAsStateWithLifecycle()
    val schedulesState by viewModel.schedulesState.collectAsStateWithLifecycle()
    val contact by viewModel.contactState.collectAsStateWithLifecycle()
    val backendApiUrl by viewModel.backendApiUrl.collectAsStateWithLifecycle()
    val profileEditState by viewModel.profileEditState.collectAsStateWithLifecycle()
    val registrationState by viewModel.registrationState.collectAsStateWithLifecycle()
    val pendingEditState by viewModel.pendingEditState.collectAsStateWithLifecycle()

    if (currentModule != ParentModuleDestination.HOME) {
        BackHandler { currentModule = ParentModuleDestination.HOME }
    }

    val moduleTitle = when (currentModule) {
        ParentModuleDestination.HOME -> "TOMOYO PARENT"
        ParentModuleDestination.CHILDREN -> "Data Anak"
        ParentModuleDestination.EDUCATION_STATUS -> "Status Pendidikan"
        ParentModuleDestination.REGISTER_CHILD -> "Pendaftaran Anak"
        ParentModuleDestination.PAYMENT_INFO -> "Informasi Pembayaran"
        ParentModuleDestination.PAYMENT_HISTORY -> "Riwayat Pembayaran"
        ParentModuleDestination.ANNOUNCEMENTS -> "Pengumuman"
        ParentModuleDestination.SCHEDULES -> "Informasi Jadwal"
        ParentModuleDestination.PROFILE -> "Profil Orang Tua"
        ParentModuleDestination.HELP_CONTACT -> "Bantuan & Kontak"
        ParentModuleDestination.ACCOUNT_SETTINGS -> "Pengaturan Akun"
    }

    val primaryTabs = listOf(
        Triple(ParentModuleDestination.HOME, "Beranda", Icons.Outlined.Home),
        Triple(ParentModuleDestination.CHILDREN, "Anak", Icons.Outlined.ChildCare),
        Triple(ParentModuleDestination.REGISTER_CHILD, "Daftar", Icons.Outlined.PersonAddAlt1),
        Triple(ParentModuleDestination.SCHEDULES, "Jadwal", Icons.Outlined.CalendarMonth)
    )

    val allMenuEntries = listOf(
        DrawerMenuEntry(
            ParentModuleDestination.HOME,
            "Beranda",
            "Ringkasan keluarga & akses cepat",
            Icons.Outlined.Home,
            "menu_item_home"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.PROFILE,
            "Profil Orang Tua",
            "Kelola nama & nomor WhatsApp Anda",
            Icons.Outlined.PersonOutline,
            "menu_item_profile"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.CHILDREN,
            "Data Anak",
            "Daftar anak terhubung & ubah data menunggu",
            Icons.Outlined.ChildCare,
            "menu_item_children"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.REGISTER_CHILD,
            "Pendaftaran Anak",
            "Tambah pendaftaran 1 atau lebih anak baru",
            Icons.Outlined.PersonAddAlt1,
            "menu_item_register"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.EDUCATION_STATUS,
            "Status Pendidikan",
            "Pantau status pendaftaran & belajar anak",
            Icons.Outlined.School,
            "menu_item_education"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.PAYMENT_INFO,
            "Informasi Pembayaran",
            "Nominal tagihan & status jatuh tempo",
            Icons.Outlined.ReceiptLong,
            "menu_item_payment_info"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.PAYMENT_HISTORY,
            "Riwayat Pembayaran",
            "Catatan transaksi pembayaran resmi",
            Icons.Outlined.ReceiptLong,
            "menu_item_payment_history"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.ANNOUNCEMENTS,
            "Pengumuman",
            "Info resmi kegiatan & akademik TOMOYO",
            Icons.Outlined.Campaign,
            "menu_item_announcements"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.SCHEDULES,
            "Informasi Jadwal",
            "Jadwal bimbingan belajar Pusat & Cabang",
            Icons.Outlined.CalendarMonth,
            "menu_item_schedules"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.HELP_CONTACT,
            "Bantuan dan Kontak",
            "WhatsApp admin, email, & peta lokasi cabang",
            Icons.AutoMirrored.Outlined.HelpOutline,
            "menu_item_help"
        ),
        DrawerMenuEntry(
            ParentModuleDestination.ACCOUNT_SETTINGS,
            "Pengaturan Akun",
            "Sesi Google, keamanan UID & server Fonnte",
            Icons.Outlined.Settings,
            "menu_item_settings"
        )
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.statusBars,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    navigationIcon = {
                        if (currentModule != ParentModuleDestination.HOME) {
                            IconButton(
                                onClick = { currentModule = ParentModuleDestination.HOME },
                                modifier = Modifier.testTag("topbar_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali ke Beranda"
                                )
                            }
                        } else {
                            Box(modifier = Modifier.padding(start = 12.dp)) {
                                TomoyoBrandEmblem(size = 36.dp)
                            }
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = moduleTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (currentModule == ParentModuleDestination.HOME) {
                                Text(
                                    text = "TOMOYO EDUCATION Indramayu",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandCrimson
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier.testTag("topbar_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = "Keluar akun",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        primaryTabs.forEach { (dest, label, icon) ->
                            val selected = currentModule == dest
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentModule = dest },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("nav_${dest.name.lowercase()}")
                            )
                        }
                        val isSecondary = primaryTabs.none { it.first == currentModule }
                        NavigationBarItem(
                            selected = isSecondary,
                            onClick = { showMenuSheet = true },
                            icon = { Icon(Icons.Outlined.GridView, contentDescription = "Menu lainnya") },
                            label = { Text("Menu") },
                            modifier = Modifier.testTag("nav_menu_sheet")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        primaryTabs.forEach { (dest, label, icon) ->
                            NavigationRailItem(
                                selected = currentModule == dest,
                                onClick = { currentModule = dest },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                        val isSecondary = primaryTabs.none { it.first == currentModule }
                        NavigationRailItem(
                            selected = isSecondary,
                            onClick = { showMenuSheet = true },
                            icon = { Icon(Icons.Outlined.GridView, contentDescription = "Menu lainnya") },
                            label = { Text("Menu") }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 820.dp)
                            .fillMaxSize()
                    ) {
                        when (currentModule) {
                            ParentModuleDestination.HOME -> HomeOverviewTab(
                                profile = profile,
                                fallbackName = viewModel.userDisplayName,
                                userEmail = viewModel.userEmail,
                                userPhotoUrl = viewModel.userPhotoUrl,
                                studentsState = studentsState,
                                paymentsState = paymentsState,
                                announcementsState = announcementsState,
                                onNavigate = { currentModule = it }
                            )

                            ParentModuleDestination.CHILDREN,
                            ParentModuleDestination.EDUCATION_STATUS -> ChildrenAndEducationScreen(
                                studentsState = studentsState,
                                packages = packages,
                                branches = branches,
                                pendingEditState = pendingEditState,
                                focusEducationStatus = currentModule == ParentModuleDestination.EDUCATION_STATUS,
                                onStartEditPending = viewModel::startEditingPendingChild,
                                onUpdateEditDraft = viewModel::updateEditingChildDraft,
                                onUploadEditPhoto = { uri -> viewModel.uploadEditingChildPhoto(context, uri) },
                                onSaveEdit = viewModel::savePendingChildEdit,
                                onDismissEdit = viewModel::dismissEditingPendingChild,
                                onNavigateToRegister = { currentModule = ParentModuleDestination.REGISTER_CHILD },
                                onBack = { currentModule = ParentModuleDestination.HOME }
                            )

                            ParentModuleDestination.REGISTER_CHILD -> ChildRegistrationScreen(
                                formState = registrationState,
                                packages = packages,
                                branches = branches,
                                onUpdateParentInfo = viewModel::updateRegistrationParentInfo,
                                onUpdateChildDraft = viewModel::updateChildDraft,
                                onAddChildRow = viewModel::addChildDraftRow,
                                onRemoveChildRow = viewModel::removeChildDraftRow,
                                onUploadPhoto = { localId, uri -> viewModel.uploadChildPhoto(context, localId, uri) },
                                onSubmit = viewModel::submitMultiChildRegistration,
                                onBack = { currentModule = ParentModuleDestination.HOME }
                            )

                            ParentModuleDestination.PAYMENT_INFO,
                            ParentModuleDestination.PAYMENT_HISTORY -> PaymentsScreen(
                                studentsState = studentsState,
                                paymentsState = paymentsState,
                                focusHistory = currentModule == ParentModuleDestination.PAYMENT_HISTORY,
                                onBack = { currentModule = ParentModuleDestination.HOME }
                            )

                            ParentModuleDestination.ANNOUNCEMENTS -> AnnouncementsScreen(
                                announcementsState = announcementsState,
                                onBack = { currentModule = ParentModuleDestination.HOME }
                            )

                            ParentModuleDestination.SCHEDULES -> SchedulesScreen(
                                schedulesState = schedulesState,
                                packages = packages,
                                branches = branches,
                                onBack = { currentModule = ParentModuleDestination.HOME }
                            )

                            ParentModuleDestination.PROFILE -> ParentProfileScreen(
                                profile = profile,
                                fallbackName = viewModel.userDisplayName,
                                userEmail = viewModel.userEmail,
                                userPhotoUrl = viewModel.userPhotoUrl,
                                editState = profileEditState,
                                onSaveProfile = viewModel::saveParentProfile,
                                onClearMessage = viewModel::clearProfileMessage,
                                onBack = { currentModule = ParentModuleDestination.HOME }
                            )

                            ParentModuleDestination.HELP_CONTACT -> HelpAndContactScreen(
                                contact = contact,
                                branches = branches,
                                onBack = { currentModule = ParentModuleDestination.HOME }
                            )

                            ParentModuleDestination.ACCOUNT_SETTINGS -> AccountSettingsScreen(
                                userEmail = viewModel.userEmail,
                                currentUserId = viewModel.currentUserId,
                                backendApiUrl = backendApiUrl,
                                onSaveBackendUrl = viewModel::saveBackendApiUrl,
                                onRequestSignOut = { showLogoutDialog = true },
                                onBack = { currentModule = ParentModuleDestination.HOME }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMenuSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Semua Layanan Orang Tua",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                BatikPaomanDivider()
                Spacer(modifier = Modifier.height(10.dp))

                allMenuEntries.forEach { entry ->
                    val selected = currentModule == entry.destination
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(entry.tag)
                            .clickable {
                                currentModule = entry.destination
                                showMenuSheet = false
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = entry.icon,
                                contentDescription = null,
                                tint = if (selected) BrandCrimson else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selected) BrandCrimson else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = entry.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_item_logout")
                        .clickable {
                            showMenuSheet = false
                            showLogoutDialog = true
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            tint = StatusDanger,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Logout / Keluar Akun",
                            style = MaterialTheme.typography.titleSmall,
                            color = StatusDanger
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar dari TOMOYO PARENT?") },
            text = {
                Text(
                    "Anda akan keluar dari sesi akun Google (${viewModel.userEmail}). Data keluarga tetap tersimpan aman di server."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onSignOutConfirmed()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusDanger,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("confirm_logout_button")
                ) {
                    Text("Ya, Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

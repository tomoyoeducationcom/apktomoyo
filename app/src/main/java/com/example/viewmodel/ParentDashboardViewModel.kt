package com.example.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CloudinaryUploader
import com.example.data.ParentPreferences
import com.example.data.ParentRepository
import com.example.data.friendlyFirestoreMessage
import com.example.model.AnnouncementItem
import com.example.model.BranchLocation
import com.example.model.ChildRegistrationDraft
import com.example.model.LearningPackage
import com.example.model.ParentProfile
import com.example.model.PaymentRecord
import com.example.model.ScheduleItem
import com.example.model.SiteContentSection
import com.example.model.StudentRecord
import com.example.model.TomoyoDefaults
import com.example.model.TomoyoFormatters
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class RegistrationFormState(
    val parentName: String = "",
    val parentPhone: String = "",
    val children: List<ChildRegistrationDraft> = listOf(ChildRegistrationDraft()),
    val uploadingDraftIds: Set<String> = emptySet(),
    val uploadErrors: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val formErrors: List<String> = emptyList(),
    val submitSuccessMessage: String? = null,
    val submitWarningMessage: String? = null
)

data class ProfileEditState(
    val isSaving: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)

data class PendingChildEditState(
    val student: StudentRecord? = null,
    val isUploadingPhoto: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class ParentDashboardViewModel(
    private val repository: ParentRepository,
    private val preferences: ParentPreferences,
    val currentUserId: String,
    val userEmail: String,
    val userDisplayName: String,
    val userPhotoUrl: String?
) : ViewModel() {

    private fun <T> Flow<T>.toUiStateFlow(
        fallbackError: String,
        tag: String = "ParentVM"
    ): StateFlow<UiState<T>> = this
        .map<T, UiState<T>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            Log.w(tag, fallbackError, rootError)
            emit(UiState.Error(friendlyFirestoreMessage(rootError, fallbackError)))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    val parentProfileState: StateFlow<UiState<ParentProfile?>> =
        repository.observeParentProfile(currentUserId)
            .toUiStateFlow("Gagal memuat profil orang tua.")

    val studentsState: StateFlow<UiState<List<StudentRecord>>> =
        repository.observeParentStudents(currentUserId)
            .toUiStateFlow("Gagal memuat data anak.")

    val paymentsState: StateFlow<UiState<List<PaymentRecord>>> =
        repository.observeParentPayments(currentUserId)
            .toUiStateFlow("Gagal memuat riwayat pembayaran.")

    val packagesState: StateFlow<List<LearningPackage>> =
        repository.observePublishedPackages()
            .catch { emit(TomoyoDefaults.INITIAL_PACKAGES) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = TomoyoDefaults.INITIAL_PACKAGES
            )

    val branchesState: StateFlow<List<BranchLocation>> =
        repository.observePublishedBranches()
            .catch { emit(TomoyoDefaults.INITIAL_BRANCHES) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = TomoyoDefaults.INITIAL_BRANCHES
            )

    val announcementsState: StateFlow<UiState<List<AnnouncementItem>>> =
        repository.observePublishedAnnouncements()
            .toUiStateFlow("Gagal memuat pengumuman.")

    val schedulesState: StateFlow<UiState<List<ScheduleItem>>> =
        repository.observePublishedSchedules()
            .toUiStateFlow("Gagal memuat informasi jadwal.")

    val contactState: StateFlow<SiteContentSection> =
        repository.observeContactSection()
            .catch { emit(TomoyoDefaults.INITIAL_CONTACT) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = TomoyoDefaults.INITIAL_CONTACT
            )

    val backendApiUrl: StateFlow<String> =
        preferences.backendApiUrlFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ""
            )

    private val _profileEditState = MutableStateFlow(ProfileEditState())
    val profileEditState: StateFlow<ProfileEditState> = _profileEditState.asStateFlow()

    private val _registrationState = MutableStateFlow(
        RegistrationFormState(parentName = userDisplayName)
    )
    val registrationState: StateFlow<RegistrationFormState> = _registrationState.asStateFlow()

    private val _pendingEditState = MutableStateFlow(PendingChildEditState())
    val pendingEditState: StateFlow<PendingChildEditState> = _pendingEditState.asStateFlow()

    init {
        viewModelScope.launch {
            parentProfileState.collect { state ->
                if (state is UiState.Success && state.data != null) {
                    val profile = state.data
                    _registrationState.update { current ->
                        current.copy(
                            parentName = current.parentName.ifBlank { profile.displayName },
                            parentPhone = current.parentPhone.ifBlank { profile.phone.orEmpty() }
                        )
                    }
                }
            }
        }
    }

    fun clearProfileMessage() {
        _profileEditState.update { it.copy(message = null, isError = false) }
    }

    fun saveParentProfile(displayName: String, phone: String) {
        val cleanName = displayName.trim()
        val cleanPhone = phone.trim()
        if (cleanName.length < 2) {
            _profileEditState.value = ProfileEditState(
                isSaving = false,
                message = "Nama lengkap orang tua wajib diisi minimal 2 karakter.",
                isError = true
            )
            return
        }
        val digits = cleanPhone.filter { it.isDigit() }
        if (cleanPhone.isNotEmpty() && digits.length < 8) {
            _profileEditState.value = ProfileEditState(
                isSaving = false,
                message = "Nomor WhatsApp minimal 8 digit angka.",
                isError = true
            )
            return
        }

        viewModelScope.launch {
            _profileEditState.value = ProfileEditState(isSaving = true)
            val result = repository.upsertParentProfile(
                displayName = cleanName,
                phone = cleanPhone.ifBlank { null },
                photoUrl = userPhotoUrl
            )
            result.fold(
                onSuccess = {
                    _registrationState.update {
                        it.copy(parentName = cleanName, parentPhone = cleanPhone)
                    }
                    _profileEditState.value = ProfileEditState(
                        isSaving = false,
                        message = "Profil orang tua berhasil disimpan.",
                        isError = false
                    )
                },
                onFailure = { err ->
                    _profileEditState.value = ProfileEditState(
                        isSaving = false,
                        message = friendlyFirestoreMessage(err, err.localizedMessage ?: "Gagal menyimpan profil."),
                        isError = true
                    )
                }
            )
        }
    }

    fun updateRegistrationParentInfo(name: String? = null, phone: String? = null) {
        _registrationState.update { current ->
            current.copy(
                parentName = name ?: current.parentName,
                parentPhone = phone ?: current.parentPhone,
                formErrors = emptyList()
            )
        }
    }

    fun updateChildDraft(localId: String, transform: (ChildRegistrationDraft) -> ChildRegistrationDraft) {
        _registrationState.update { current ->
            current.copy(
                children = current.children.map { if (it.localId == localId) transform(it) else it },
                formErrors = emptyList()
            )
        }
    }

    fun addChildDraftRow() {
        _registrationState.update { current ->
            if (current.children.size >= 10) return@update current
            current.copy(children = current.children + ChildRegistrationDraft())
        }
    }

    fun removeChildDraftRow(localId: String) {
        _registrationState.update { current ->
            if (current.children.size <= 1) return@update current
            current.copy(
                children = current.children.filterNot { it.localId == localId },
                uploadErrors = current.uploadErrors - localId
            )
        }
    }

    fun uploadChildPhoto(context: Context, localId: String, uri: Uri) {
        viewModelScope.launch {
            _registrationState.update {
                it.copy(
                    uploadingDraftIds = it.uploadingDraftIds + localId,
                    uploadErrors = it.uploadErrors - localId
                )
            }
            val result = CloudinaryUploader.uploadImage(context, uri)
            result.fold(
                onSuccess = { uploaded ->
                    _registrationState.update { state ->
                        state.copy(
                            uploadingDraftIds = state.uploadingDraftIds - localId,
                            children = state.children.map {
                                if (it.localId == localId) it.copy(childPhotoUrl = uploaded.secureUrl) else it
                            }
                        )
                    }
                },
                onFailure = { err ->
                    _registrationState.update { state ->
                        state.copy(
                            uploadingDraftIds = state.uploadingDraftIds - localId,
                            uploadErrors = state.uploadErrors + (localId to (err.localizedMessage ?: "Gagal mengunggah foto."))
                        )
                    }
                }
            )
        }
    }

    fun clearRegistrationBanners() {
        _registrationState.update {
            it.copy(submitSuccessMessage = null, submitWarningMessage = null, formErrors = emptyList())
        }
    }

    fun submitMultiChildRegistration() {
        val current = _registrationState.value
        val errors = TomoyoFormatters.validateRegistrationSubmission(
            parentName = current.parentName,
            parentPhone = current.parentPhone,
            children = current.children
        )
        if (errors.isNotEmpty()) {
            _registrationState.update { it.copy(formErrors = errors) }
            return
        }

        viewModelScope.launch {
            _registrationState.update {
                it.copy(
                    isSubmitting = true,
                    formErrors = emptyList(),
                    submitSuccessMessage = null,
                    submitWarningMessage = null
                )
            }
            val apiUrl = preferences.getBackendApiUrl()
            val result = repository.submitMultiChildRegistration(
                parentName = current.parentName,
                parentPhone = current.parentPhone,
                photoUrl = userPhotoUrl,
                children = current.children,
                packages = packagesState.value,
                branches = branchesState.value,
                backendApiUrl = apiUrl
            )
            result.fold(
                onSuccess = { res ->
                    val anySent = res.notificationResults.any { it.status == "sent" }
                    val anyUnconfigured = res.notificationResults.any { it.status == "unconfigured" }
                    val count = res.createdIds.size
                    val msg = when {
                        anySent -> "Pendaftaran untuk $count anak berhasil disimpan (Menunggu Persetujuan) dan notifikasi WhatsApp telah dikirim ke admin."
                        anyUnconfigured -> "Pendaftaran untuk $count anak berhasil disimpan dengan status Menunggu Persetujuan. Notifikasi WhatsApp otomatis aktif setelah URL server backend diatur."
                        else -> "Pendaftaran untuk $count anak berhasil disimpan dengan status Menunggu Persetujuan."
                    }
                    _registrationState.update {
                        it.copy(
                            isSubmitting = false,
                            children = listOf(ChildRegistrationDraft()),
                            submitSuccessMessage = if (anySent) msg else null,
                            submitWarningMessage = if (!anySent) msg else null
                        )
                    }
                },
                onFailure = { err ->
                    _registrationState.update {
                        it.copy(
                            isSubmitting = false,
                            formErrors = listOf(
                                friendlyFirestoreMessage(err, err.localizedMessage ?: "Gagal mengirim pendaftaran.")
                            )
                        )
                    }
                }
            )
        }
    }

    fun startEditingPendingChild(student: StudentRecord) {
        if (student.status != "pending") return
        _pendingEditState.value = PendingChildEditState(student = student)
    }

    fun dismissEditingPendingChild() {
        _pendingEditState.value = PendingChildEditState()
    }

    fun updateEditingChildDraft(transform: (StudentRecord) -> StudentRecord) {
        _pendingEditState.update { current ->
            val target = current.student ?: return@update current
            current.copy(student = transform(target), errorMessage = null)
        }
    }

    fun uploadEditingChildPhoto(context: Context, uri: Uri) {
        viewModelScope.launch {
            _pendingEditState.update { it.copy(isUploadingPhoto = true, errorMessage = null) }
            val result = CloudinaryUploader.uploadImage(context, uri)
            result.fold(
                onSuccess = { uploaded ->
                    _pendingEditState.update { state ->
                        state.copy(
                            isUploadingPhoto = false,
                            student = state.student?.copy(childPhotoUrl = uploaded.secureUrl)
                        )
                    }
                },
                onFailure = { err ->
                    _pendingEditState.update { state ->
                        state.copy(
                            isUploadingPhoto = false,
                            errorMessage = err.localizedMessage ?: "Gagal mengunggah foto."
                        )
                    }
                }
            )
        }
    }

    fun savePendingChildEdit() {
        val editing = _pendingEditState.value.student ?: return
        val pkg = packagesState.value.find { it.slug == editing.packageId }
            ?: TomoyoDefaults.INITIAL_PACKAGES.first()
        val branch = branchesState.value.find { it.slug == editing.branchId }
            ?: TomoyoDefaults.INITIAL_BRANCHES.first()
        val configuredPrice = TomoyoFormatters.getPackagePriceForBranch(pkg, editing.branchId)

        viewModelScope.launch {
            _pendingEditState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = repository.updatePendingChild(
                studentId = editing.id,
                childName = editing.childName,
                childPhotoUrl = editing.childPhotoUrl,
                childGender = editing.childGender ?: "Laki-laki",
                gradeLevel = editing.gradeLevel,
                packageId = editing.packageId,
                packageName = "${pkg.title} (${pkg.programType})",
                branchId = editing.branchId,
                branchName = branch.name,
                billingAmount = configuredPrice,
                parentPhone = editing.parentPhone,
                notes = editing.notes
            )
            result.fold(
                onSuccess = { _pendingEditState.value = PendingChildEditState() },
                onFailure = { err ->
                    _pendingEditState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = friendlyFirestoreMessage(err, err.localizedMessage ?: "Gagal menyimpan perubahan.")
                        )
                    }
                }
            )
        }
    }

    fun saveBackendApiUrl(url: String) {
        viewModelScope.launch {
            preferences.setBackendApiUrl(url)
        }
    }
}

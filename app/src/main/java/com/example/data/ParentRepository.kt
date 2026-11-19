package com.example.data

import android.content.Context
import com.example.R
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

data class RegistrationSubmissionResult(
    val createdIds: List<String>,
    val notificationResults: List<NotificationDeliveryResult>
)

class ParentRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireVerifiedUser(): Pair<String, String> {
        val user = auth.currentUser
            ?: throw IllegalStateException("Sesi Google belum aktif. Silakan masuk kembali.")
        if (!user.isEmailVerified) {
            throw IllegalStateException("Email akun Google belum terverifikasi.")
        }
        return user.uid to (user.email ?: "")
    }

    private fun <T> wrapCollectionErrors(
        flow: Flow<T>,
        uid: String?,
        operationType: OperationType,
        path: String
    ): Flow<T> = flow.catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exToLog = firestoreError ?: (error as? Exception)
        if (exToLog != null && (uid == null || auth.currentUser?.uid == uid)) {
            handleFirestoreError(exToLog, operationType, path)
        }
        throw error
    }

    fun observeParentProfile(uid: String): Flow<ParentProfile?> = flow {
        require(uid.isNotBlank()) { "Parent UID tidak boleh kosong." }
        val path = "parents/$uid"
        emitAll(
            wrapCollectionErrors(
                db.collection("parents").document(uid)
                    .snapshots()
                    .map { snapshot ->
                        if (snapshot.exists()) {
                            snapshot.toObject(
                                ParentProfile::class.java,
                                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                            )
                        } else {
                            null
                        }
                    },
                uid = uid,
                operationType = OperationType.GET,
                path = path
            )
        )
    }

    fun observeParentStudents(uid: String): Flow<List<StudentRecord>> = flow {
        require(uid.isNotBlank()) { "Parent UID tidak boleh kosong." }
        val path = "students"
        emitAll(
            wrapCollectionErrors(
                db.collection("students")
                    .whereEqualTo("parentId", uid)
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(
                            StudentRecord::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                    },
                uid = uid,
                operationType = OperationType.LIST,
                path = path
            )
        )
    }

    fun observeParentPayments(uid: String): Flow<List<PaymentRecord>> = flow {
        require(uid.isNotBlank()) { "Parent UID tidak boleh kosong." }
        val path = "payments"
        emitAll(
            wrapCollectionErrors(
                db.collection("payments")
                    .whereEqualTo("parentId", uid)
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(
                            PaymentRecord::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                    },
                uid = uid,
                operationType = OperationType.LIST,
                path = path
            )
        )
    }

    fun observePublishedPackages(): Flow<List<LearningPackage>> = flow {
        val path = "packages"
        emitAll(
            wrapCollectionErrors(
                db.collection("packages")
                    .whereEqualTo("isPublished", true)
                    .snapshots()
                    .map { snapshot ->
                        val items = snapshot.toObjects(
                            LearningPackage::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).sortedBy { it.order }
                        items.ifEmpty { TomoyoDefaults.INITIAL_PACKAGES }
                    },
                uid = null,
                operationType = OperationType.LIST,
                path = path
            )
        )
    }

    fun observePublishedBranches(): Flow<List<BranchLocation>> = flow {
        val path = "branches"
        emitAll(
            wrapCollectionErrors(
                db.collection("branches")
                    .whereEqualTo("isPublished", true)
                    .snapshots()
                    .map { snapshot ->
                        val items = snapshot.toObjects(
                            BranchLocation::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).map { branch ->
                            val fallback = TomoyoDefaults.INITIAL_BRANCHES.find {
                                it.id == branch.id || it.slug == branch.slug
                            }
                            branch.copy(
                                latitude = branch.latitude ?: fallback?.latitude,
                                longitude = branch.longitude ?: fallback?.longitude
                            )
                        }.sortedBy { it.order }
                        items.ifEmpty { TomoyoDefaults.INITIAL_BRANCHES }
                    },
                uid = null,
                operationType = OperationType.LIST,
                path = path
            )
        )
    }

    fun observePublishedAnnouncements(): Flow<List<AnnouncementItem>> = flow {
        val path = "announcements"
        emitAll(
            wrapCollectionErrors(
                db.collection("announcements")
                    .whereEqualTo("isPublished", true)
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(
                            AnnouncementItem::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                    },
                uid = null,
                operationType = OperationType.LIST,
                path = path
            )
        )
    }

    fun observePublishedSchedules(): Flow<List<ScheduleItem>> = flow {
        val path = "schedules"
        emitAll(
            wrapCollectionErrors(
                db.collection("schedules")
                    .whereEqualTo("isPublished", true)
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(
                            ScheduleItem::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                    },
                uid = null,
                operationType = OperationType.LIST,
                path = path
            )
        )
    }

    fun observeContactSection(): Flow<SiteContentSection> = flow {
        val path = "siteContent"
        emitAll(
            wrapCollectionErrors(
                db.collection("siteContent")
                    .whereEqualTo("isPublished", true)
                    .snapshots()
                    .map { snapshot ->
                        val items = snapshot.toObjects(
                            SiteContentSection::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                        items.firstOrNull { it.id == "contact" || it.sectionKey == "contact" }
                            ?: TomoyoDefaults.INITIAL_CONTACT
                    },
                uid = null,
                operationType = OperationType.LIST,
                path = path
            )
        )
    }

    suspend fun upsertParentProfile(
        displayName: String,
        phone: String?,
        photoUrl: String?
    ): Result<Unit> {
        var docPath = "parents/unknown"
        return try {
            val (uid, email) = requireVerifiedUser()
            val cleanName = displayName.trim().take(120)
            require(cleanName.isNotEmpty()) { "Nama orang tua wajib diisi." }
            require(email.length >= 3) { "Email Google tidak valid." }

            val cleanPhone = phone?.trim()?.take(30)
            val cleanPhoto = photoUrl?.trim()?.take(500)

            val ref = db.collection("parents").document(uid)
            docPath = ref.path
            val existing = ref.get().await()

            if (!existing.exists()) {
                val payload = buildMap<String, Any> {
                    put("uid", uid)
                    put("displayName", cleanName)
                    put("email", email.take(160))
                    if (!cleanPhone.isNullOrEmpty()) put("phone", cleanPhone)
                    if (!cleanPhoto.isNullOrEmpty()) put("photoURL", cleanPhoto)
                    put("createdAt", FieldValue.serverTimestamp())
                    put("updatedAt", FieldValue.serverTimestamp())
                }
                ref.set(payload).await()
            } else {
                val updates = buildMap<String, Any> {
                    put("displayName", cleanName)
                    if (cleanPhone != null) put("phone", cleanPhone)
                    if (!cleanPhoto.isNullOrEmpty()) put("photoURL", cleanPhoto)
                    put("updatedAt", FieldValue.serverTimestamp())
                }
                ref.update(updates).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitMultiChildRegistration(
        parentName: String,
        parentPhone: String,
        photoUrl: String?,
        children: List<ChildRegistrationDraft>,
        packages: List<LearningPackage>,
        branches: List<BranchLocation>,
        backendApiUrl: String
    ): Result<RegistrationSubmissionResult> {
        return try {
            val (uid, email) = requireVerifiedUser()
            val validationErrors = TomoyoFormatters.validateRegistrationSubmission(
                parentName = parentName,
                parentPhone = parentPhone,
                children = children
            )
            require(validationErrors.isEmpty()) { validationErrors.first() }

            upsertParentProfile(
                displayName = parentName,
                phone = parentPhone,
                photoUrl = photoUrl
            ).getOrThrow()

            val registeredAtIso = TomoyoFormatters.nowIsoUtc()
            val nextDueAtIso = TomoyoFormatters.computeNextMonthlyDueIsoFromNow()

            val batch = db.batch()
            val createdEntries = mutableListOf<CreatedRegistrationPayload>()

            for (child in children) {
                val pkg = packages.find { it.slug == child.packageId }
                    ?: TomoyoDefaults.INITIAL_PACKAGES.find { it.slug == child.packageId }
                    ?: TomoyoDefaults.INITIAL_PACKAGES.first()
                val branch = branches.find { it.slug == child.branchId }
                    ?: TomoyoDefaults.INITIAL_BRANCHES.find { it.slug == child.branchId }
                    ?: TomoyoDefaults.INITIAL_BRANCHES.first()

                val configuredPrice = TomoyoFormatters.getPackagePriceForBranch(pkg, branch.slug)
                val packageLabel = "${pkg.title} (${pkg.programType})".take(120)
                val studentRef = db.collection("students").document()

                val payload = buildMap<String, Any> {
                    put("parentId", uid)
                    put("parentName", parentName.trim().take(120))
                    put("parentEmail", email.trim().take(160))
                    put("parentPhone", parentPhone.trim().take(30))
                    put("childName", child.childName.trim().take(120))
                    if (child.childPhotoUrl.trim().isNotEmpty()) {
                        put("childPhotoUrl", child.childPhotoUrl.trim().take(500))
                    }
                    put("childGender", child.childGender)
                    put("gradeLevel", child.gradeLevel.trim().take(60))
                    put("packageId", pkg.slug)
                    put("packageName", packageLabel)
                    put("branchId", branch.slug)
                    put("branchName", branch.name.take(120))
                    put("registrationChannel", "online")
                    if (configuredPrice != null && configuredPrice >= 0.0) {
                        put("billingAmount", configuredPrice)
                    }
                    put("registeredAtIso", registeredAtIso)
                    put("nextDueAtIso", nextDueAtIso)
                    put("status", "pending")
                    put("academicStatus", "menunggu")
                    if (child.notes.trim().isNotEmpty()) {
                        put("notes", child.notes.trim().take(500))
                    }
                    put("createdAt", FieldValue.serverTimestamp())
                    put("updatedAt", FieldValue.serverTimestamp())
                }

                batch.set(studentRef, payload)
                createdEntries.add(
                    CreatedRegistrationPayload(
                        registrationId = studentRef.id,
                        childName = child.childName.trim().take(120),
                        childGender = child.childGender,
                        gradeLevel = child.gradeLevel.trim().take(60),
                        packageName = packageLabel,
                        branchName = branch.name.take(120),
                        billingAmount = configuredPrice,
                        registeredAtIso = registeredAtIso,
                        nextDueAtIso = nextDueAtIso
                    )
                )
            }

            batch.commit().await()

            val idToken = auth.currentUser?.getIdToken(false)?.await()?.token.orEmpty()
            val notificationResults = NotificationBackendClient.notifyRegistration(
                backendBaseUrl = backendApiUrl,
                idToken = idToken,
                parentName = parentName,
                parentPhone = parentPhone,
                entries = createdEntries
            )

            for (res in notificationResults) {
                try {
                    val logRef = db.collection("notificationLogs").document()
                    val logPayload = mapOf(
                        "registrationId" to res.registrationId.take(128),
                        "parentId" to uid,
                        "type" to "registration",
                        "target" to res.target.ifBlank { TomoyoDefaults.REGISTRATION_WA_TARGET }.take(30),
                        "status" to res.status,
                        "messagePreview" to res.messagePreview.ifBlank { "Notifikasi pendaftaran siswa baru" }.take(500),
                        "providerDetail" to res.detail.take(400),
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    logRef.set(logPayload).await()
                } catch (_: Exception) {
                    // Non-blocking audit log write
                }
            }

            Result.success(
                RegistrationSubmissionResult(
                    createdIds = createdEntries.map { it.registrationId },
                    notificationResults = notificationResults
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "students")
            Result.failure(e)
        }
    }

    suspend fun updatePendingChild(
        studentId: String,
        childName: String,
        childPhotoUrl: String?,
        childGender: String,
        gradeLevel: String,
        packageId: String,
        packageName: String,
        branchId: String,
        branchName: String,
        billingAmount: Double?,
        parentPhone: String,
        notes: String?
    ): Result<Unit> {
        val docPath = "students/$studentId"
        return try {
            requireVerifiedUser()
            require(studentId.isNotBlank()) { "ID siswa tidak valid." }
            require(childName.trim().length in 2..120) { "Nama anak harus 2–120 karakter." }
            require(gradeLevel.trim().length in 1..60) { "Tingkat kelas wajib diisi." }
            require(parentPhone.trim().length in 8..30) { "Nomor WhatsApp minimal 8 karakter." }
            require(childGender in listOf("Laki-laki", "Perempuan")) { "Jenis kelamin tidak valid." }
            require(packageId in listOf("paket-1", "paket-2", "paket-3")) { "Paket tidak valid." }
            require(branchId in listOf("indramayu-kota", "terisi")) { "Cabang tidak valid." }

            val updates = mutableMapOf<String, Any>(
                "childName" to childName.trim().take(120),
                "childGender" to childGender,
                "gradeLevel" to gradeLevel.trim().take(60),
                "packageId" to packageId,
                "packageName" to packageName.trim().take(120),
                "branchId" to branchId,
                "branchName" to branchName.trim().take(120),
                "parentPhone" to parentPhone.trim().take(30),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!childPhotoUrl.isNullOrBlank()) {
                updates["childPhotoUrl"] = childPhotoUrl.trim().take(500)
            }
            if (billingAmount != null && billingAmount >= 0.0) {
                updates["billingAmount"] = billingAmount
            } else {
                updates["billingAmount"] = FieldValue.delete()
            }
            if (notes != null) {
                updates["notes"] = notes.trim().take(500)
            }

            db.collection("students").document(studentId).update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }
}

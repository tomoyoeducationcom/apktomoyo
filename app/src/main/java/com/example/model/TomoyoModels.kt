package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Keep
@IgnoreExtraProperties
data class ParentProfile(
    @DocumentId val id: String = "",
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val phone: String? = null,
    val photoURL: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class StudentRecord(
    @DocumentId val id: String = "",
    val parentId: String = "",
    val parentName: String = "",
    val parentEmail: String = "",
    val parentPhone: String = "",
    val childName: String = "",
    val birthDate: String? = null,
    val schoolOrigin: String? = null,
    val studentAddress: String? = null,
    val childPhotoUrl: String? = null,
    val childGender: String? = null,
    val gradeLevel: String = "",
    val packageId: String = "paket-1",
    val packageName: String = "",
    val branchId: String = "indramayu-kota",
    val branchName: String = "",
    val registrationChannel: String? = null,
    val billingAmount: Double? = null,
    val initialPaymentStatus: String? = null,
    val registeredAtIso: String? = null,
    val nextDueAtIso: String? = null,
    val lastDueNotifiedIso: String? = null,
    val dueConfirmedIso: String? = null,
    val status: String = "pending",
    val academicStatus: String = "menunggu",
    val notes: String? = null,
    val adminNotes: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class PaymentRecord(
    @DocumentId val id: String = "",
    val studentId: String = "",
    val parentId: String = "",
    val childName: String = "",
    val amount: Double = 0.0,
    val method: String = "",
    val periodLabel: String = "",
    val dueDateIso: String? = null,
    val status: String = "belum_bayar",
    val notes: String? = null,
    val recordedBy: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LearningPackage(
    @DocumentId val id: String = "",
    val slug: String = "paket-1",
    val order: Long = 1L,
    val title: String = "",
    val levelRange: String = "",
    val programType: String = "",
    val subjects: List<String> = emptyList(),
    val priceLabel: String? = null,
    val priceNote: String? = null,
    val pricePusat: Double? = null,
    val priceCabang: Double? = null,
    val pricePusatNote: String? = null,
    val priceCabangNote: String? = null,
    val description: String? = null,
    @get:PropertyName("isPublished") @PropertyName("isPublished")
    val isPublished: Boolean = true,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class BranchLocation(
    @DocumentId val id: String = "",
    val slug: String = "indramayu-kota",
    val order: Long = 1L,
    val name: String = "",
    val roleLabel: String = "",
    val mapsUrl: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    @get:PropertyName("isPublished") @PropertyName("isPublished")
    val isPublished: Boolean = true,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class AnnouncementItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val dateLabel: String? = null,
    @get:PropertyName("isPublished") @PropertyName("isPublished")
    val isPublished: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ScheduleItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val packageId: String? = null,
    val branchId: String? = null,
    val dayTime: String = "",
    val notes: String? = null,
    @get:PropertyName("isPublished") @PropertyName("isPublished")
    val isPublished: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SiteContentSection(
    @DocumentId val id: String = "",
    val sectionKey: String = "",
    val title: String? = null,
    val subtitle: String? = null,
    val body: String? = null,
    val vision: String? = null,
    val mission: String? = null,
    val founderName: String? = null,
    val founderRole: String? = null,
    val founderPhotoUrl: String? = null,
    val founderBio: String? = null,
    val email: String? = null,
    val whatsappDisplay: String? = null,
    val whatsappUrl: String? = null,
    val instagramUrl: String? = null,
    val facebookUrl: String? = null,
    @get:PropertyName("isPublished") @PropertyName("isPublished")
    val isPublished: Boolean = true,
    val updatedAt: Timestamp? = null
)

data class ChildRegistrationDraft(
    val localId: String = java.util.UUID.randomUUID().toString(),
    val childName: String = "",
    val childPhotoUrl: String = "",
    val childGender: String = "Laki-laki",
    val gradeLevel: String = "",
    val packageId: String = "paket-1",
    val branchId: String = "indramayu-kota",
    val notes: String = ""
)

object TomoyoDefaults {
    const val APP_NAME_PARENT = "TOMOYO PARENT"
    const val OFFICIAL_NAME = "TOMOYO PRIVAT & BIMBEL EDUCATION"
    const val SHORT_NAME = "TOMOYO EDUCATION"
    const val TAGLINE = "LEARN • GROW • SUCCEED"
    const val MEANING =
        "Tempat tumbuhnya generasi cerdas, mandiri, berprestasi, dan siap meraih masa depan di Indramayu."
    const val REGISTRATION_WA_TARGET = "6283121902760"

    val INITIAL_PACKAGES: List<LearningPackage> = listOf(
        LearningPackage(
            id = "paket-1",
            slug = "paket-1",
            order = 1L,
            title = "PAKET 1: TK – Kelas 1",
            levelRange = "TK – Kelas 1",
            programType = "Program: CALISTUNG",
            subjects = listOf("CALISTUNG (Membaca, Menulis, Berhitung)")
        ),
        LearningPackage(
            id = "paket-2",
            slug = "paket-2",
            order = 2L,
            title = "PAKET 2: Kelas 2 – Kelas 5",
            levelRange = "Kelas 2 – Kelas 5",
            programType = "Mata pelajaran",
            subjects = listOf("Bahasa Indonesia", "Bahasa Inggris", "MTK", "IPAS")
        ),
        LearningPackage(
            id = "paket-3",
            slug = "paket-3",
            order = 3L,
            title = "PAKET 3: Kelas 6",
            levelRange = "Program: Persiapan TKA",
            programType = "Mata pelajaran",
            subjects = listOf("MTK", "Bahasa Indonesia", "IPAS")
        )
    )

    val INITIAL_BRANCHES: List<BranchLocation> = listOf(
        BranchLocation(
            id = "indramayu-kota",
            slug = "indramayu-kota",
            order = 1L,
            name = "TOMOYO EDUCATION INDRAMAYU KOTA (PUSAT)",
            roleLabel = "Pusat",
            mapsUrl = "https://maps.app.goo.gl/nJjYFVJnFPuzzz6B8?g_st=ac",
            latitude = -6.333158,
            longitude = 108.341824
        ),
        BranchLocation(
            id = "terisi",
            slug = "terisi",
            order = 2L,
            name = "TOMOYO EDUCATION KECAMATAN TERISI (CABANG)",
            roleLabel = "Cabang",
            mapsUrl = "https://maps.app.goo.gl/958GhajzPbbdWxoc6?g_st=ac",
            latitude = -6.455393,
            longitude = 108.140484
        )
    )

    val INITIAL_CONTACT = SiteContentSection(
        id = "contact",
        sectionKey = "contact",
        email = "tomoyoeducation.com@gmail.com",
        whatsappDisplay = "0821-1977-0700",
        whatsappUrl = "https://wa.me/6282119770700",
        instagramUrl = "https://www.instagram.com/tomoyoeducation?bqrl=cHJsb3RnY3g1c3g2",
        facebookUrl = "https://www.facebook.com/share/1CSomr9gXK/"
    )
}

object TomoyoFormatters {
    private val rupiahFormat: NumberFormat by lazy {
        NumberFormat.getNumberInstance(Locale("id", "ID"))
    }

    fun formatRupiah(amount: Double?): String {
        if (amount == null) return "-"
        return "Rp ${rupiahFormat.format(amount.toLong())}"
    }

    fun getPackagePriceForBranch(pkg: LearningPackage?, branchId: String): Double? {
        if (pkg == null) return null
        val pusat = pkg.pricePusat?.takeIf { it >= 0.0 }
        val cabang = pkg.priceCabang?.takeIf { it >= 0.0 }
        if (pusat != null || cabang != null) {
            return if (branchId == "indramayu-kota") pusat else cabang
        }
        return parseRupiahAmount(pkg.priceLabel)
    }

    fun getPackagePriceNoteForBranch(pkg: LearningPackage?, branchId: String): String? {
        if (pkg == null) return null
        val branchNote = if (branchId == "indramayu-kota") pkg.pricePusatNote else pkg.priceCabangNote
        return branchNote?.takeIf { it.isNotBlank() } ?: pkg.priceNote?.takeIf { it.isNotBlank() }
    }

    fun parseRupiahAmount(raw: String?): Double? {
        val trimmed = raw?.trim().orEmpty()
        if (trimmed.isEmpty()) return null
        val normalized = trimmed
            .replace(Regex("^rp\\.?\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex(",00\\s*$"), "")
            .replace(Regex(",-\\s*$"), "")
            .replace(".", "")
            .replace(Regex("\\s+"), "")
        return normalized.toLongOrNull()?.takeIf { it >= 0L }?.toDouble()
    }

    fun nowIsoUtc(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(java.util.Date())
    }

    fun computeNextMonthlyDueIsoFromNow(): String {
        val utc = TimeZone.getTimeZone("UTC")
        val cal = Calendar.getInstance(utc)
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.MONTH, 1)
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, minOf(dayOfMonth, maxDay))
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = utc
        return sdf.format(cal.time)
    }

    fun formatJakartaDateTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "-"
        val parsed = parseIsoDate(isoString) ?: return isoString
        val jakarta = TimeZone.getTimeZone("Asia/Jakarta")
        val out = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
        out.timeZone = jakarta
        return "${out.format(parsed)} WIB"
    }

    fun formatTimestampJakarta(ts: Timestamp?): String {
        if (ts == null) return "-"
        val jakarta = TimeZone.getTimeZone("Asia/Jakarta")
        val out = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        out.timeZone = jakarta
        return "${out.format(ts.toDate())} WIB"
    }

    fun isPaymentDueNow(nextDueAtIso: String?, dueConfirmedIso: String?): Boolean {
        if (nextDueAtIso.isNullOrBlank()) return false
        val dueDate = parseIsoDate(nextDueAtIso) ?: return false
        if (!dueConfirmedIso.isNullOrBlank()) {
            val confirmed = parseIsoDate(dueConfirmedIso)
            if (confirmed != null && confirmed.time >= dueDate.time) {
                return false
            }
        }
        return System.currentTimeMillis() >= dueDate.time
    }

    private fun parseIsoDate(iso: String): java.util.Date? {
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd"
        )
        for (p in patterns) {
            try {
                val sdf = SimpleDateFormat(p, Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                return sdf.parse(iso)
            } catch (_: Exception) {
            }
        }
        return null
    }

    fun getStatusLabel(status: String): String = when (status) {
        "approved" -> "Disetujui"
        "rejected" -> "Ditolak"
        else -> "Menunggu Persetujuan"
    }

    fun getAcademicStatusLabel(status: String): String = when (status) {
        "aktif" -> "Aktif"
        "lulus" -> "Lulus"
        "nonaktif" -> "Nonaktif"
        else -> "Menunggu"
    }

    fun getPaymentStatusLabel(status: String?): String = when (status) {
        "lunas" -> "Lunas"
        "cicilan" -> "Cicilan"
        else -> "Belum Bayar"
    }

    fun validateRegistrationSubmission(
        parentName: String,
        parentPhone: String,
        children: List<ChildRegistrationDraft>
    ): List<String> {
        val errors = mutableListOf<String>()
        val trimmedParent = parentName.trim()
        val trimmedPhone = parentPhone.trim()
        if (trimmedParent.length < 2 || trimmedParent.length > 120) {
            errors.add("Nama orang tua / wali wajib diisi (2–120 karakter).")
        }
        val digits = trimmedPhone.filter { it.isDigit() }
        if (digits.length < 8 || trimmedPhone.length > 30) {
            errors.add("Nomor WhatsApp orang tua wajib valid (minimal 8 digit).")
        }
        if (children.isEmpty() || children.size > 10) {
            errors.add("Minimal 1 anak dan maksimal 10 anak per pengiriman.")
        }
        children.forEachIndexed { index, child ->
            val label = "Anak ke-${index + 1}"
            if (child.childName.trim().length < 2 || child.childName.trim().length > 120) {
                errors.add("$label: Nama lengkap anak wajib diisi (2–120 karakter).")
            }
            if (child.gradeLevel.trim().isEmpty() || child.gradeLevel.trim().length > 60) {
                errors.add("$label: Tingkat kelas wajib diisi.")
            }
            if (child.childGender !in listOf("Laki-laki", "Perempuan")) {
                errors.add("$label: Jenis kelamin wajib dipilih.")
            }
            if (child.packageId !in listOf("paket-1", "paket-2", "paket-3")) {
                errors.add("$label: Paket belajar resmi wajib dipilih.")
            }
            if (child.branchId !in listOf("indramayu-kota", "terisi")) {
                errors.add("$label: Cabang wajib dipilih.")
            }
            if (child.notes.trim().length > 500) {
                errors.add("$label: Catatan maksimal 500 karakter.")
            }
        }
        return errors
    }
}

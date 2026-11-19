package com.example.data

import com.example.model.TomoyoDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CreatedRegistrationPayload(
    val registrationId: String,
    val childName: String,
    val childGender: String,
    val gradeLevel: String,
    val packageName: String,
    val branchName: String,
    val billingAmount: Double?,
    val registeredAtIso: String,
    val nextDueAtIso: String
)

data class NotificationDeliveryResult(
    val registrationId: String,
    val status: String,
    val target: String,
    val detail: String,
    val messagePreview: String
)

object NotificationBackendClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun notifyRegistration(
        backendBaseUrl: String,
        idToken: String,
        parentName: String,
        parentPhone: String,
        entries: List<CreatedRegistrationPayload>
    ): List<NotificationDeliveryResult> = withContext(Dispatchers.IO) {
        val cleanBase = backendBaseUrl.trim().trimEnd('/')
        if (cleanBase.isBlank() || !cleanBase.startsWith("http")) {
            return@withContext entries.map { entry ->
                NotificationDeliveryResult(
                    registrationId = entry.registrationId,
                    status = "unconfigured",
                    target = TomoyoDefaults.REGISTRATION_WA_TARGET,
                    detail = "Server API WhatsApp Fonnte belum diatur pada Pengaturan Akun.",
                    messagePreview = "Pendaftaran ${entry.childName} (${entry.packageName})"
                )
            }
        }

        val payloadJson = JSONObject().apply {
            put("parentName", parentName.trim())
            put("parentPhone", parentPhone.trim())
            put(
                "registrations",
                JSONArray().apply {
                    entries.forEach { entry ->
                        put(
                            JSONObject().apply {
                                put("registrationId", entry.registrationId)
                                put("childName", entry.childName)
                                put("childGender", entry.childGender)
                                put("gradeLevel", entry.gradeLevel)
                                put("packageName", entry.packageName)
                                put("branchName", entry.branchName)
                                if (entry.billingAmount != null) {
                                    put("billingAmount", entry.billingAmount)
                                }
                                put("registeredAtIso", entry.registeredAtIso)
                                put("nextDueAtIso", entry.nextDueAtIso)
                            }
                        )
                    }
                }
            )
        }

        try {
            val request = Request.Builder()
                .url("$cleanBase/api/notify/registration")
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Firebase-Token", idToken)
                .post(payloadJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext entries.map { entry ->
                        NotificationDeliveryResult(
                            registrationId = entry.registrationId,
                            status = "failed",
                            target = TomoyoDefaults.REGISTRATION_WA_TARGET,
                            detail = "Server notifikasi merespons HTTP ${response.code}.",
                            messagePreview = "Pendaftaran ${entry.childName}"
                        )
                    }
                }
                val json = JSONObject(raw)
                val arr = json.optJSONArray("results") ?: JSONArray()
                val list = mutableListOf<NotificationDeliveryResult>()
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    list.add(
                        NotificationDeliveryResult(
                            registrationId = obj.optString("registrationId", ""),
                            status = obj.optString("status", "failed"),
                            target = obj.optString("target", TomoyoDefaults.REGISTRATION_WA_TARGET),
                            detail = obj.optString("detail", ""),
                            messagePreview = obj.optString("messagePreview", "Notifikasi pendaftaran siswa baru")
                        )
                    )
                }
                list
            }
        } catch (e: Exception) {
            entries.map { entry ->
                NotificationDeliveryResult(
                    registrationId = entry.registrationId,
                    status = "failed",
                    target = TomoyoDefaults.REGISTRATION_WA_TARGET,
                    detail = e.localizedMessage ?: "Gagal menghubungi server notifikasi.",
                    messagePreview = "Pendaftaran ${entry.childName}"
                )
            }
        }
    }
}

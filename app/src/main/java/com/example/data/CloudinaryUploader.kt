package com.example.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CloudinaryUploadResult(
    val secureUrl: String,
    val publicId: String
)

object CloudinaryUploader {
    private const val CLOUD_NAME = "fgxdx2hq"
    private const val UPLOAD_PRESET = "tomoyo"
    private const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024 // 5 MB
    private val ALLOWED_MIME_TYPES = setOf("image/jpeg", "image/jpg", "image/png", "image/webp")

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun uploadImage(context: Context, uri: Uri): Result<CloudinaryUploadResult> =
        withContext(Dispatchers.IO) {
            try {
                val resolver = context.applicationContext.contentResolver
                val mimeType = resolver.getType(uri)?.lowercase() ?: "image/jpeg"
                if (mimeType !in ALLOWED_MIME_TYPES) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Format file tidak didukung. Gunakan JPG, PNG, atau WebP.")
                    )
                }

                var fileName = "anak_tomoyo.jpg"
                resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
                    ?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (nameIdx >= 0) {
                                cursor.getString(nameIdx)?.takeIf { it.isNotBlank() }?.let { fileName = it }
                            }
                        }
                    }

                val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: return@withContext Result.failure(IllegalStateException("Gagal membaca file gambar."))

                if (bytes.isEmpty()) {
                    return@withContext Result.failure(IllegalArgumentException("File gambar kosong."))
                }
                if (bytes.size > MAX_FILE_SIZE_BYTES) {
                    return@withContext Result.failure(IllegalArgumentException("Ukuran gambar maksimal 5 MB."))
                }

                val mediaType = mimeType.toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull()
                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .addFormDataPart("file", fileName, bytes.toRequestBody(mediaType))
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val rawBody = response.body?.string().orEmpty()
                    val json = runCatching { JSONObject(rawBody) }.getOrNull()
                    if (!response.isSuccessful || json == null || !json.has("secure_url")) {
                        val errMsg = json?.optJSONObject("error")?.optString("message")
                            ?.takeIf { it.isNotBlank() }
                            ?: "Upload ke Cloudinary gagal (HTTP ${response.statusCodeCompat(response.code)})."
                        return@withContext Result.failure(IllegalStateException(errMsg))
                    }
                    val secureUrl = json.getString("secure_url")
                    val publicId = json.optString("public_id", "")
                    Result.success(CloudinaryUploadResult(secureUrl = secureUrl, publicId = publicId))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun okhttp3.Response.statusCodeCompat(code: Int): Int = code
}

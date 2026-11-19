package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.parentPrefsDataStore by preferencesDataStore(name = "tomoyo_parent_prefs")

class ParentPreferences(private val context: Context) {
    private val appContext = context.applicationContext

    private object Keys {
        val BACKEND_API_URL = stringPreferencesKey("backend_api_url")
    }

    val backendApiUrlFlow: Flow<String> = appContext.parentPrefsDataStore.data.map { prefs ->
        prefs[Keys.BACKEND_API_URL].orEmpty()
    }

    suspend fun getBackendApiUrl(): String = backendApiUrlFlow.first()

    suspend fun setBackendApiUrl(url: String) {
        val cleaned = url.trim().trimEnd('/')
        appContext.parentPrefsDataStore.edit { prefs ->
            if (cleaned.isBlank()) {
                prefs.remove(Keys.BACKEND_API_URL)
            } else {
                prefs[Keys.BACKEND_API_URL] = cleaned
            }
        }
    }
}

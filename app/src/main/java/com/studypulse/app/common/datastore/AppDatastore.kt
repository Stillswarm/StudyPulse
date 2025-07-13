package com.studypulse.app.common.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.preferencesDataStore by preferencesDataStore(name = "metadata")
class AppDatastore(
    private val context: Context
) {
    object DatastoreKeys {
        val SEMESTER_ID = stringPreferencesKey("semester_id")
    }

    val semesterIdFlow = context.preferencesDataStore.data.map { prefs ->
        prefs[DatastoreKeys.SEMESTER_ID] ?: ""
    }

    suspend fun saveSemesterId(semesterId: String) {
        context.preferencesDataStore.edit { prefs ->
            prefs[DatastoreKeys.SEMESTER_ID] = semesterId
        }
    }
}
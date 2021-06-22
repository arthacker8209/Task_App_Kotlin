package com.example.my_task_app.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class SortOrder{ BY_DATE, BY_NAME}

private const val TAG = "PreferenceManager"

data class FilterPreferences (val sortOrder: SortOrder, val hideCompleted:Boolean)

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context){
    private val dataStore= context.createDataStore("user_preferences")
    val preferencesFlow = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.e(TAG, "Error occured while reading", exception)
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map { preferences ->

        val sortOrder = SortOrder.valueOf(
            preferences[preferencesKey.SORT_ORDER] ?: SortOrder.BY_DATE.name)

        val hideCompleted = preferences[preferencesKey.HIDE_COMPLETED]?:false

            FilterPreferences(sortOrder, hideCompleted)
    }


    suspend fun updateOrder(sortOrder: SortOrder){
        dataStore.edit { preferences ->
            preferences[preferencesKey.SORT_ORDER]= sortOrder.name
        }
    }
    suspend fun updateHideCompleted(isHidden: Boolean){
        dataStore.edit { preferences ->
            preferences[preferencesKey.HIDE_COMPLETED]= isHidden
        }
    }

    private object preferencesKey{
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide")
    }
}
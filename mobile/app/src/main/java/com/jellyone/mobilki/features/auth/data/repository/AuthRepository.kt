package com.jellyone.mobilki.features.auth.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jellyone.mobilki.features.auth.data.models.ApiResult
import com.jellyone.mobilki.features.auth.data.models.AuthResponse
import com.jellyone.mobilki.features.auth.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore setup
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepository(
    private val context: Context, 
    private val apiService: AuthApiService = AuthApiService()
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }
    
    // Get saved token
    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }
    
    // Get saved user
    val currentUser: Flow<User?> = context.dataStore.data
        .map { preferences ->
            val id = preferences[USER_ID_KEY] ?: return@map null
            val email = preferences[USER_EMAIL_KEY] ?: return@map null
            val name = preferences[USER_NAME_KEY] ?: return@map null
            
            User(id, email, name)
        }
    
    // Sign in user
    suspend fun signIn(email: String, password: String): ApiResult<AuthResponse> {
        val result = apiService.signIn(email, password)
        
        if (result is ApiResult.Success) {
            saveAuthData(result.data)
        }
        
        return result
    }
    
    // Sign up user
    suspend fun signUp(name: String, email: String, password: String): ApiResult<AuthResponse> {
        val result = apiService.signUp(name, email, password)
        
        if (result is ApiResult.Success) {
            saveAuthData(result.data)
        }
        
        return result
    }
    
    // Sign out user
    suspend fun signOut() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_NAME_KEY)
        }
    }
    
    // Save auth data to preferences
    private suspend fun saveAuthData(authResponse: AuthResponse) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = authResponse.token
            preferences[USER_ID_KEY] = authResponse.user.id
            preferences[USER_EMAIL_KEY] = authResponse.user.email
            preferences[USER_NAME_KEY] = authResponse.user.name
        }
    }
    
    // Check if user is authenticated
    fun isAuthenticated(): Flow<Boolean> = authToken.map { it != null }
} 
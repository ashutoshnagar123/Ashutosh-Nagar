package com.example.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

enum class AuthStatus {
    AUTHENTICATED,
    GUEST,
    UNAUTHENTICATED,
    LOADING
}

class AuthRepository(private val context: Context) {
    private val AUTH_STATE = stringPreferencesKey("auth_state")
    private val USER_EMAIL = stringPreferencesKey("user_email")

    val authState: Flow<AuthStatus> = context.dataStore.data.map { prefs ->
        when (prefs[AUTH_STATE]) {
            AuthStatus.AUTHENTICATED.name -> AuthStatus.AUTHENTICATED
            AuthStatus.GUEST.name -> AuthStatus.GUEST
            else -> AuthStatus.UNAUTHENTICATED
        }
    }

    suspend fun login(email: String, pass: String): Result<Unit> {
        delay(1000) // fake network
        if (email.isBlank() || pass.isBlank()) return Result.failure(Exception("Fields cannot be empty"))
        if (pass.length < 6) return Result.failure(Exception("Password too short"))
        context.dataStore.edit { prefs ->
            prefs[AUTH_STATE] = AuthStatus.AUTHENTICATED.name
            prefs[USER_EMAIL] = email
        }
        return Result.success(Unit)
    }

    suspend fun signUp(email: String, pass: String): Result<Unit> {
        delay(1000)
        if (email.isBlank() || pass.isBlank()) return Result.failure(Exception("Fields cannot be empty"))
        if (pass.length < 6) return Result.failure(Exception("Password must be at least 6 characters"))
        context.dataStore.edit { prefs ->
            prefs[AUTH_STATE] = AuthStatus.AUTHENTICATED.name
            prefs[USER_EMAIL] = email
        }
        return Result.success(Unit)
    }

    suspend fun loginAsGuest() {
        context.dataStore.edit { prefs ->
            prefs[AUTH_STATE] = AuthStatus.GUEST.name
            prefs.remove(USER_EMAIL)
        }
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[AUTH_STATE] = AuthStatus.UNAUTHENTICATED.name
            prefs.remove(USER_EMAIL)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        delay(1000)
        if (email.isBlank()) return Result.failure(Exception("Enter an email"))
        return Result.success(Unit)
    }
}

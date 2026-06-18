package com.example.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    
    val authState: StateFlow<AuthStatus> = repo.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthStatus.LOADING
    )
    
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun login(email: String, pass: String) = viewModelScope.launch {
        _loading.value = true
        val res = repo.login(email, pass)
        res.onFailure { _error.emit(it.message ?: "Error") }
        _loading.value = false
    }

    fun signUp(email: String, pass: String) = viewModelScope.launch {
        _loading.value = true
        val res = repo.signUp(email, pass)
        res.onFailure { _error.emit(it.message ?: "Error") }
        _loading.value = false
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        _loading.value = true
        val res = repo.resetPassword(email)
        res.onSuccess { _error.emit("Password reset link sent to $email") }
        res.onFailure { _error.emit(it.message ?: "Error") }
        _loading.value = false
    }

    fun loginAsGuest() = viewModelScope.launch {
        repo.loginAsGuest()
    }

    fun logout() = viewModelScope.launch {
        repo.logout()
    }
}

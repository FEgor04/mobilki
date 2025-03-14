package com.jellyone.mobilki.features.auth.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jellyone.mobilki.features.auth.data.models.ApiResult
import com.jellyone.mobilki.features.auth.data.models.AuthResponse
import com.jellyone.mobilki.features.auth.data.models.User
import com.jellyone.mobilki.features.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

open class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    // Function to create repository - this can be overridden in tests
    protected open fun createRepository(): AuthRepository = AuthRepository(getApplication<Application>().applicationContext)
    
    private val repository by lazy { createRepository() }
    
    // Authentication state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    // User state
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()
    
    // Form validation states
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()
    
    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()
    
    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError.asStateFlow()
    
    init {
        // Check if user is already authenticated
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _userState.value = user
            }
        }
    }
    
    // Sign in function
    fun signIn(email: String, password: String) {
        if (!validateSignInForm(email, password)) {
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            when (val result = repository.signIn(email, password)) {
                is ApiResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is ApiResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // Sign up function
    fun signUp(name: String, email: String, password: String) {
        if (!validateSignUpForm(name, email, password)) {
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            when (val result = repository.signUp(name, email, password)) {
                is ApiResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is ApiResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
                else -> {}
            }
        }
    }
    
    // Sign out function
    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _authState.value = AuthState.SignedOut
        }
    }
    
    // Reset auth state (useful after errors or navigating between screens)
    fun resetAuthState() {
        _authState.value = AuthState.Idle
        _emailError.value = null
        _passwordError.value = null
        _nameError.value = null
    }
    
    // Validation functions
    private fun validateSignInForm(email: String, password: String): Boolean {
        var isValid = true
        
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Please enter a valid email address"
            isValid = false
        } else {
            _emailError.value = null
        }
        
        if (password.length < 6) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        } else {
            _passwordError.value = null
        }
        
        return isValid
    }
    
    private fun validateSignUpForm(name: String, email: String, password: String): Boolean {
        var isValid = validateSignInForm(email, password)
        
        if (name.isBlank()) {
            _nameError.value = "Name cannot be empty"
            isValid = false
        } else {
            _nameError.value = null
        }
        
        return isValid
    }
}

// Auth state sealed class
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val data: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
    data object SignedOut : AuthState()
} 
package com.example.project.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState
    init {
        val user = repository.currentUser()
        if (user != null && user.isEmailVerified) {
            _uiState.update { it.copy(isLoggedIn = true) }
        }
    }

    fun onEmailChange(value: String){
        _uiState.update { it.copy(email = value) }
    }
    fun onPasswordChange(value: String){
        _uiState.update { it.copy(password = value) }
    }
    fun Login(){
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        if (email.isBlank() || password.isBlank()) {
            postError("Please enter email and password")
            return
        }
        viewModelScope.launch {
            setLoading(true)
            when(val result = repository.signIn(email, password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true, message = "Login successful") }
                }
                is AuthResult.EmailNotVerified -> {
                    _uiState.update { it.copy(isLoading = false, error = "Please verify your email address") }
                }
                is AuthResult.Error -> postError(result.errorMessage)
                else -> setLoading(false)
            }
        }
    }
    fun signUp(){
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        if (email.isBlank() || password.isBlank()) {
            postError("Please enter email and password")
            return
        }
        viewModelScope.launch {
            setLoading(true)
            when(val result = repository.signUp(email, password)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = "Sign up successful. Verification email sent. Please verify before logging in."
                        )
                    }
                }
                is AuthResult.Error -> postError(result.errorMessage)
                else -> setLoading(false)
            }
        }
    }

    fun resetPassword(){
        val email = _uiState.value.email.trim()
        if (email.isBlank()) {
            postError("Please enter your email")
            return
        }
        viewModelScope.launch {
            setLoading(true)
            // Pre check for email existence
            val exists = repository.emailExists(email)
            if (!exists){
                _uiState.update { it.copy(isLoading = false, error = "No account found for that email") }
                return@launch
            }
            when (val result = repository.sendPasswordReset(email)){
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = "Password reset email sent"
                        )
                    }
                }
                is AuthResult.Error -> postError(result.errorMessage)
                else -> setLoading(false)
            }
        }
    }

    fun logout(){
        repository.signOut()
        _uiState.update { AuthUiState() }
    }

    private fun setLoading(loading: Boolean){
        _uiState.update { it.copy(isLoading = loading) }
    }
    private fun postError(message: String){
        _uiState.update { it.copy(isLoading = false, error = message) }
    }
    fun consumeMessage(){
        _uiState.update { it.copy(message = null) }
    }
    fun consumeError(){
        _uiState.update { it.copy(error = null) }
    }
}
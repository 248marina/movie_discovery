package com.example.moviesapp.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val message: String? = null
){
    val canSubmitLogin: Boolean get() = email.isNotBlank() && password.isNotBlank() && !isLoading
    val canSubmitSignUp: Boolean get() = email.isNotBlank() && password.isNotBlank() && !isLoading
}
package com.example.project.auth

import com.google.firebase.auth.FirebaseUser

sealed class AuthResult{
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class EmailNotVerified(val user: FirebaseUser?) : AuthResult()
    data class Error(val errorMessage: String) : AuthResult()
    object LoggedOut : AuthResult()
}
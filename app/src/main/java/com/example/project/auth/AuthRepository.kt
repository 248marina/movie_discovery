package com.example.project.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null && user.isEmailVerified) {
                AuthResult.Success(user)
            } else {
                firebaseAuth.signOut()
                AuthResult.EmailNotVerified(user)
            }
            } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login Failed")
        }
    }
    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            user?.sendEmailVerification()?.await()
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign Up Failed")
        }
    }
    suspend fun sendPasswordReset(email: String): AuthResult {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            AuthResult.Success(firebaseAuth.currentUser)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Password Reset Failed")
        }
    }

    suspend fun emailExists(email: String): Boolean {
        return try {
            val methods = firebaseAuth.fetchSignInMethodsForEmail(email).await().signInMethods
            !methods.isNullOrEmpty()
        } catch (e: Exception) {
            false
        }
    }
    fun signOut(): AuthResult {
        firebaseAuth.signOut()
        return AuthResult.LoggedOut
    }
    fun currentUser(): FirebaseUser? = firebaseAuth.currentUser
}


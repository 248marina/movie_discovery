package com.example.moviesapp.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.moviesapp.R
import com.example.moviesapp.auth.AuthViewModel

@Composable
fun SignupScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.value.message) {
        uiState.value.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeMessage()
            if (!uiState.value.isLoggedIn){
                navController.navigate("login"){
                    popUpTo("signup"){ inclusive = true }
                }
            }
        }
    }
    LaunchedEffect(uiState.value.error) {
        uiState.value.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeError()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center){
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50)
                    )
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if(isDarkTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(stringResource(R.string.signup), color = if (isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary , style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = uiState.value.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text(stringResource(R.string.email_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = uiState.value.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text(stringResource(R.string.password_label)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { viewModel.signUp() }, enabled = uiState.value.canSubmitSignUp, modifier = Modifier.fillMaxWidth()) {
                    if (uiState.value.isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.signup))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {
                    navController.navigate("login"){
                        popUpTo("signup"){ inclusive = true }
                    }}) {
                    Text(stringResource(R.string.login_prompt))
                }
            }
        }
    }
    }


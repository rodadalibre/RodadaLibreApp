package com.rodrigocarreon.rodadalibre.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rodrigocarreon.rodadalibre.R
import com.rodrigocarreon.rodadalibre.ui.viewmodel.LoginState
import com.rodrigocarreon.rodadalibre.ui.viewmodel.LoginViewModel

@Composable
fun AuthScreen(
    onNavigateBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
){
    var isLoginMode by remember {mutableStateOf(true)}

    var name by remember {mutableStateOf("")}
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password_confirmation by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success){
            viewModel.resetState()
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_account),
            contentDescription = "Perfil vacío",
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if(isLoginMode) "Inicia Sesión" else "Registrate",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (!isLoginMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        if (!isLoginMode) {
            OutlinedTextField(
                value = password_confirmation,
                onValueChange = { password_confirmation = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
        }

        if (loginState is LoginState.Error) {
            Text(
                text = (loginState as LoginState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (loginState is LoginState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        } else {
            Button(
                onClick = {
                    if(isLoginMode){
                        viewModel.login(email, password)
                    }else{
                        viewModel.register(name, email, password, password_confirmation)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    text = if (isLoginMode) "Iniciar Sesión" else "Crear Cuenta",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = {
                isLoginMode = !isLoginMode
            }
        ) {
            Text(
                text = if (isLoginMode) "¿No tienes una cuenta aún? Regístrate"
                else "¿Ya tienes una cuenta? Inicia Sesión",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
package com.rodrigocarreon.rodadalibre.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rodrigocarreon.rodadalibre.R
import com.rodrigocarreon.rodadalibre.data.model.User
import com.rodrigocarreon.rodadalibre.ui.viewmodel.AuthState
import com.rodrigocarreon.rodadalibre.ui.viewmodel.ChangePasswordState
import com.rodrigocarreon.rodadalibre.ui.viewmodel.ProfileUpdateState
import com.rodrigocarreon.rodadalibre.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val passwordState by viewModel.passwordState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkSession()
    }

    when (val state = authState) {

        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AuthState.Unauthenticated -> {
            LaunchedEffect(Unit) {
                onNavigateBack()
            }
        }

        is AuthState.Authenticated -> {
            ProfileSettingsContent(
                user = state.user,
                updateState = updateState,
                onSaveProfile = viewModel::updateProfile,
                onEditProfile = viewModel::clearUpdateState,
                passwordState = passwordState,
                onChangePassword = viewModel::changePassword,
                onClearPasswordState = viewModel::clearPasswordState,
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@Composable
private fun ProfileSettingsContent(
    user: User,
    updateState: ProfileUpdateState,
    onSaveProfile: (String, String) -> Unit,
    onEditProfile: () -> Unit,
    passwordState: ChangePasswordState,
    onChangePassword: (String, String, String) -> Unit,
    onClearPasswordState: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showPasswordDialog by remember {
        mutableStateOf(false)
    }

    // Close the dialog once the password is saved; the success message stays on the screen
    LaunchedEffect(passwordState) {
        if (passwordState is ChangePasswordState.Saved) showPasswordDialog = false
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            state = passwordState,
            onConfirm = onChangePassword,
            onEdit = onClearPasswordState,
            onDismiss = {
                showPasswordDialog = false
                onClearPasswordState()
            }
        )
    }

    var firstName by remember {
        mutableStateOf(user.first_name)
    }

    var lastName by remember {
        mutableStateOf(user.last_name ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Regresar"
                )
            }
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            ProfileTextField(
                label = "First Name",
                value = firstName,
                onValueChange = {
                    firstName = it
                    onEditProfile()
                }
            )
            Spacer(modifier = Modifier.height(22.dp))
            ProfileTextField(
                label = "Last Name",
                value = lastName,
                onValueChange = {
                    lastName = it
                    onEditProfile()
                }
            )
            Spacer(modifier = Modifier.height(22.dp))
            val isSaving = updateState is ProfileUpdateState.Saving
            val hasChanges = firstName.trim() != user.first_name || lastName.trim() != user.last_name
            Button(
                onClick = {
                    onSaveProfile(firstName, lastName)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                // The backend requires both names
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && hasChanges && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Guardar cambios",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            when (updateState) {
                is ProfileUpdateState.Saved -> ProfileUpdateMessage(
                    text = "Cambios guardados",
                    color = MaterialTheme.colorScheme.primary
                )
                is ProfileUpdateState.Error -> ProfileUpdateMessage(
                    text = "No se pudieron guardar los cambios. Inténtalo de nuevo.",
                    color = MaterialTheme.colorScheme.error
                )
                else -> Unit
            }
            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider()
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Account Information",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))
            AccountOption(
                title = "Cambiar contraseña",
                onClick = {
                    onClearPasswordState()
                    showPasswordDialog = true
                }
            )
            if (passwordState is ChangePasswordState.Saved) {
                ProfileUpdateMessage(
                    text = "Contraseña actualizada",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            AccountOption(
                title = "Eliminar mi cuenta",
                onClick = {
                    // TODO
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    state: ChangePasswordState,
    onConfirm: (String, String, String) -> Unit,
    onEdit: () -> Unit,
    onDismiss: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }

    val isSaving = state is ChangePasswordState.Saving
    // Same rules as the backend: min 6, confirmed, different from the current one
    val validationError = when {
        password.isNotEmpty() && password.length < 6 -> "Debe tener al menos 6 caracteres"
        passwordConfirmation.isNotEmpty() && password != passwordConfirmation -> "Las contraseñas no coinciden"
        password.isNotEmpty() && password == currentPassword -> "Debe ser distinta a la actual"
        else -> null
    }
    val canSave = currentPassword.isNotEmpty() && password.isNotEmpty() &&
        passwordConfirmation.isNotEmpty() && validationError == null && !isSaving

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(text = "Cambiar contraseña") },
        text = {
            Column {
                PasswordField(
                    label = "Contraseña actual",
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        onEdit()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordField(
                    label = "Nueva contraseña",
                    value = password,
                    onValueChange = {
                        password = it
                        onEdit()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordField(
                    label = "Confirmar nueva contraseña",
                    value = passwordConfirmation,
                    onValueChange = {
                        passwordConfirmation = it
                        onEdit()
                    }
                )
                val error = validationError ?: when (state) {
                    is ChangePasswordState.WrongCurrentPassword -> "La contraseña actual es incorrecta"
                    is ChangePasswordState.Error -> "No se pudo cambiar la contraseña. Inténtalo de nuevo."
                    else -> null
                }
                if (error != null) {
                    ProfileUpdateMessage(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(currentPassword, password, passwordConfirmation) },
                enabled = canSave
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text(text = "Cancelar")
            }
        }
    )
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    painter = painterResource(
                        id = if (isVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                    ),
                    contentDescription = if (isVisible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ProfileUpdateMessage(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .padding(top = 10.dp)
            // Announce the result to TalkBack
            .semantics { liveRegion = LiveRegionMode.Polite }
    )
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        label = {
            Text(text = label)
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
private fun AccountOption(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null
        )
    }
    HorizontalDivider()
}


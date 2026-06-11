package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GeoActiveIndicator
import com.example.ui.theme.GeoAlertError
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoItemBg
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOnSurface
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSubduedText
import com.example.ui.theme.GeoSurface

@Composable
fun LoginScreen(
    onLoginSuccess: (username: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GeoBackground)
            .drawBehind {
                // Style guideline: ATMOSPHERE drawing custom gradients for modern layout depth
                val brush = Brush.radialGradient(
                    colors = listOf(
                        GeoPrimaryContainer.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.1f, size.height * 0.15f),
                    radius = size.width * 0.7f
                )
                drawCircle(brush = brush, radius = size.width * 0.7f)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Asymmetric double overlapping header icons to give elegant visual styling
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Secondary offset background circle
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .offset(x = 12.dp, y = (-12).dp)
                        .clip(CircleShape)
                        .background(GeoActiveIndicator.copy(alpha = 0.6f))
                )
                // Primary background circle
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(GeoPrimaryContainer)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map tracking icon",
                        tint = GeoPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 28.sp,
                color = GeoPrimary,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Sales B2B Lead Intelligence",
                fontSize = 12.sp,
                color = GeoSubduedText,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // High Elevational Modern Login card container bounded by Geo styling rules
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, GeoBorder.copy(alpha = 0.5f)), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.login_title),
                        fontSize = 20.sp,
                        color = GeoOnSurface,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.25).sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = stringResource(id = R.string.login_subtitle),
                        fontSize = 12.sp,
                        color = GeoSubduedText,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 20.dp)
                    )

                    AnimatedVisibility(
                        visible = errorText != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        errorText?.let {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GeoAlertError.copy(alpha = 0.1f))
                                    .border(1.dp, GeoAlertError.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = it,
                                    color = GeoAlertError,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Username Input Field
                    Text(
                        text = stringResource(id = R.string.login_username_label),
                        fontSize = 11.sp,
                        color = GeoOnSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { 
                            username = it
                            errorText = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                            .height(56.dp),
                        placeholder = { Text(stringResource(id = R.string.login_username_placeholder), color = GeoSubduedText, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email icon",
                                tint = GeoPrimary.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GeoPrimary,
                            unfocusedBorderColor = GeoBorder,
                            focusedContainerColor = GeoItemBg.copy(alpha = 0.5f),
                            unfocusedContainerColor = GeoItemBg.copy(alpha = 0.5f),
                            focusedTextColor = GeoOnSurface,
                            unfocusedTextColor = GeoOnSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input Field
                    Text(
                        text = stringResource(id = R.string.login_password_label),
                        fontSize = 11.sp,
                        color = GeoOnSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            errorText = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                            .height(56.dp),
                        placeholder = { Text(stringResource(id = R.string.login_password_placeholder), color = GeoSubduedText, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock icon",
                                tint = GeoPrimary.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.testTag("password_visibility_toggle")
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = stringResource(id = R.string.login_pass_visibility_desc),
                                    tint = GeoSubduedText
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GeoPrimary,
                            unfocusedBorderColor = GeoBorder,
                            focusedContainerColor = GeoItemBg.copy(alpha = 0.5f),
                            unfocusedContainerColor = GeoItemBg.copy(alpha = 0.5f),
                            focusedTextColor = GeoOnSurface,
                            unfocusedTextColor = GeoOnSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Remember state row and forgot credentials option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { rememberMe = !rememberMe }
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("remember_me_checkbox"),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = GeoPrimary,
                                    uncheckedColor = GeoBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.login_remember_me),
                                fontSize = 12.sp,
                                color = GeoSubduedText,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text(
                            text = stringResource(id = R.string.login_forgot_password),
                            fontSize = 12.sp,
                            color = GeoPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, context.getString(R.string.login_forgot_toast), Toast.LENGTH_LONG).show()
                                }
                                .padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Core validation button
                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                errorText = context.getString(R.string.login_error_empty)
                            } else if (username == "admin" && password == "admin123") {
                                Toast.makeText(context, context.getString(R.string.login_success_toast), Toast.LENGTH_SHORT).show()
                                onLoginSuccess(username)
                            } else {
                                errorText = context.getString(R.string.login_error_invalid)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(text = stringResource(id = R.string.login_button_text), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Easy onboarding Demo Profile button for convenient applet access
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Loaded secure admin demo session!", Toast.LENGTH_SHORT).show()
                            onLoginSuccess("SalesForceDemo")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("demo_login_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GeoPrimary),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, GeoBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.login_demo_button_text), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // IT Admin registration callout
            Text(
                text = stringResource(id = R.string.login_register_link),
                fontSize = 13.sp,
                color = GeoPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        Toast.makeText(context, context.getString(R.string.login_register_toast), Toast.LENGTH_LONG).show()
                    }
                    .padding(8.dp)
            )
        }
    }
}

package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.model.AuthState
import com.example.myapplication.ui.theme.BazingaSurface
import com.example.myapplication.ui.theme.BazingaSurfaceAlt
import com.example.myapplication.ui.theme.BazingaTextMuted

@Composable
fun ProfileScreen(
    authState: AuthState,
    onSignOut: () -> Unit,
    onNavigateToLibrary: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            if (authState.token.isBlank()) {
                Text(
                    text = "Sign in to view and manage your Bazinga profile.",
                    color = BazingaTextMuted
                )
                Button(onClick = onNavigateToLibrary, modifier = Modifier.fillMaxWidth()) {
                    Text("Go to Sign In")
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = BazingaSurface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = authState.username, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = authState.email, color = BazingaTextMuted, fontSize = 12.sp)
                        Text(text = "Role: ${authState.role.ifBlank { "USER" }}", color = BazingaTextMuted, fontSize = 12.sp)
                        Text(
                            text = "Subscription: ${authState.subscriptionType ?: "Free"}",
                            color = BazingaTextMuted,
                            fontSize = 12.sp
                        )
                        authState.subscriptionExpiration?.let { expiration ->
                            Text(
                                text = "Renews on $expiration",
                                color = BazingaTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BazingaSurfaceAlt)
                ) {
                    Text("Sign out")
                }
            }
        }
    }
}

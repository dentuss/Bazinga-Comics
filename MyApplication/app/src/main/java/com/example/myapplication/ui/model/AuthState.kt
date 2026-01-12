package com.example.myapplication.ui.model

data class AuthState(
    val token: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "",
    val subscriptionType: String? = null,
    val subscriptionExpiration: String? = null
)

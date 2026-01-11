package com.example.myapplication.data

import com.example.myapplication.ui.UiState

class BazingaRepository(private val api: BazingaApi) {
    suspend fun fetchComics(): UiState<List<ComicDto>> =
        runCatching { api.getComics() }
            .fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unable to load comics") }
            )

    suspend fun login(email: String, password: String): UiState<AuthResponse> =
        runCatching { api.login(AuthRequest(email = email, password = password)) }
            .fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unable to sign in") }
            )

    suspend fun register(username: String, email: String, password: String): UiState<AuthResponse> =
        runCatching { api.register(AuthRequest(email = email, password = password, username = username)) }
            .fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unable to register") }
            )

    suspend fun fetchLibrary(token: String): UiState<List<LibraryItemDto>> =
        runCatching { api.getLibrary("Bearer $token") }
            .fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unable to load library") }
            )

    suspend fun addToLibrary(token: String, comicId: Long): UiState<List<LibraryItemDto>> =
        runCatching { api.addToLibrary("Bearer $token", LibraryItemRequest(comicId)) }
            .fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unable to add to library") }
            )
}

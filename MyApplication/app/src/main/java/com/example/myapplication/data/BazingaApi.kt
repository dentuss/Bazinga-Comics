package com.example.myapplication.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

private const val BASE_URL = "http://13.60.79.86:8080"

interface BazingaApi {
    @GET("/api/comics")
    suspend fun getComics(): List<ComicDto>

    @POST("/api/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @GET("/api/library")
    suspend fun getLibrary(@Header("Authorization") token: String): List<LibraryItemDto>

    @POST("/api/library")
    suspend fun addToLibrary(
        @Header("Authorization") token: String,
        @Body request: LibraryItemRequest
    ): List<LibraryItemDto>
}

object BazingaApiClient {
    val api: BazingaApi by lazy {
        val client = OkHttpClient.Builder().build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(BazingaApi::class.java)
    }
}

fun resolveImageUrl(image: String?): String? {
    if (image.isNullOrBlank()) {
        return null
    }

    val trimmed = image.trim()

    val isAbsolute = trimmed.startsWith("http://") || trimmed.startsWith("https://")
            || trimmed.startsWith("data:") || trimmed.startsWith("blob:")

    if (isAbsolute) {
        return trimmed
    }

    val normalized = if (trimmed.startsWith("/")) trimmed.drop(1) else trimmed
    return "$BASE_URL/$normalized"
}

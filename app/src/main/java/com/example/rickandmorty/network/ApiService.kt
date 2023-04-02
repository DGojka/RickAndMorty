package com.example.rickandmorty.network

import com.example.rickandmorty.repository.Person
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("character")
    suspend fun getPersons(@Query("page") page: Int? = null): ApiResponse<Person>
}


data class ApiResponse<T>(
    val info: Info,
    val results: List<T>
)
data class Info(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)
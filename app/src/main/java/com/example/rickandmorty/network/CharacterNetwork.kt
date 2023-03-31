package com.example.rickandmorty.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CharacterNetwork {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(CharacterApiService::class.java)

    suspend fun getAllCharacters(): List<Person>{
        val allCharacters = mutableListOf<Person>()
        for (page in 1..20) {

            allCharacters.addAll(apiService.getCharacters(page).results)
        }

        return allCharacters
    }
}
package com.example.rickandmorty.repository

import com.example.rickandmorty.network.ApiService
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: ApiService) : Repository {
    override suspend fun getPersonsByPage(page: Int): List<Person> {
        return try {
            apiService.getPersons(page).results
        } catch (e: Exception) {
            emptyList()
        }
    }
}
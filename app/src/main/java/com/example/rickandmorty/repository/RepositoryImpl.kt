package com.example.rickandmorty.repository

import com.example.rickandmorty.network.ApiService

class RepositoryImpl(private val apiService: ApiService) : Repository {
    override suspend fun getAllPersons(): List<Person> {
        val allPersons = mutableListOf<Person>()
        for (page in 1..20) {
            allPersons.addAll(apiService.getPersons(page).results)
        }
        return allPersons
    }

    override suspend fun getPersonsByPage(page: Int): List<Person> {
        return try {
            apiService.getPersons(page).results
        } catch (e: Exception) {
            emptyList() // Return an empty list if an exception occurs
        }
    }
}
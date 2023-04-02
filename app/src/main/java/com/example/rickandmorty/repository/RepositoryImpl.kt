package com.example.rickandmorty.repository

import com.example.rickandmorty.network.ApiService

class RepositoryImpl(private val apiService: ApiService) : Repository {
    override suspend fun getAllPersons(): List<Person> {
        val allPages = apiService.getPersons(1).info.pages
        val allPersons = mutableListOf<Person>()
        for (page in 1..allPages) {
            allPersons.addAll(apiService.getPersons(page).results)
        }
        return allPersons
    }

    override suspend fun getPersonsByPage(page: Int): List<Person> {
        return try {
            apiService.getPersons(page).results
        } catch (e: Exception) {
            emptyList()
        }
    }
}
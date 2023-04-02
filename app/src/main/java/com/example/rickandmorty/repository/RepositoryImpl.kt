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
}
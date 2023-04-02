package com.example.rickandmorty.repository

interface Repository {
    suspend fun getAllPersons() : List<Person>

    suspend fun getPersonsByPage(page: Int): List<Person>
}
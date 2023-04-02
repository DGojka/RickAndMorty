package com.example.rickandmorty.repository

interface Repository {
    suspend fun getPersonsByPage(page: Int): List<Person>
}
package com.example.rickandmorty.repository

interface Repository {
    suspend fun getAllPersons() : List<Person>
}
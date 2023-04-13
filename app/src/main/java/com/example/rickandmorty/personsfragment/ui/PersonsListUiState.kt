package com.example.rickandmorty.personsfragment.ui

import com.example.rickandmorty.repository.Person

data class PersonsListUiState(
    val isLoading: Boolean,
    val allFetchedPersons: List<Person> = mutableListOf(),
    val filteredPersons: List<Person> = mutableListOf(),
    val clickedPerson: Person? = null,
    val currentPersonsPage: Int = 1
)
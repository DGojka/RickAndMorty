package com.example.rickandmorty.personsfragment.ui

import com.example.rickandmorty.repository.Person

data class PersonsListUiState(
    val isLoading: Boolean,
    val allFetchedPersons: List<Person>,
    val clickedPerson: Person? = null,
    val currentPersonsPage: Int = 1
)
package com.example.rickandmorty.characterfragment.ui

import com.example.rickandmorty.repository.Person

data class PersonsListUiState(
    val isLoading: Boolean,
    val allPersons: List<Person>,
    val clickedPerson: Person? = null
)
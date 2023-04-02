package com.example.rickandmorty.characterfragment.ui

import com.example.rickandmorty.network.Person

data class PersonsListUiState(
    val isLoading: Boolean,
    val allPersons: List<Person>,
    val clickedPerson: Person? = null
)
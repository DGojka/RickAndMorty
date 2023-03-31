package com.example.rickandmorty.characterfragment

import com.example.rickandmorty.network.Person

sealed class PersonsListUiState {
    object Init : PersonsListUiState()
    class Loaded(var persons: List<Person>) : PersonsListUiState()
    class MoreDetails(var person : Person): PersonsListUiState()

}
package com.example.rickandmorty.personsfragment.helpers

import com.example.rickandmorty.repository.Person

interface PersonsListCallback {
    fun onPersonsListUpdated(personsList: List<Person>)
}
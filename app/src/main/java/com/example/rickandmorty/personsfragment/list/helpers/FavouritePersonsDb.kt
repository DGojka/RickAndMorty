package com.example.rickandmorty.personsfragment.list.helpers

import android.content.Context
import com.example.rickandmorty.repository.Person

class FavouritePersonsDb(val context: Context) {
    private val prefs = context.getSharedPreferences(FAVOURITE_PERSONS, Context.MODE_PRIVATE)

    fun getFavouritePersons(personsList: List<Person>): MutableList<Person> =
        prefs.getStringSet(FAVOURITE_PERSONS, mutableSetOf())?.mapToPersonList(personsList)
            ?: mutableListOf()


    private fun MutableSet<String>.mapToPersonList(personsList: List<Person>): MutableList<Person> {
        return this.mapNotNull { name ->
            personsList.find { it.name == name }
        }.toMutableList()
    }

    companion object {
        const val FAVOURITE_PERSONS = "FavouritePersons"
    }
}
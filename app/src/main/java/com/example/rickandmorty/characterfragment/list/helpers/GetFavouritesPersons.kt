package com.example.rickandmorty.characterfragment.list.helpers

import android.content.Context
import com.example.rickandmorty.repository.Person

const val FAVOURITE_PERSONS = "FavouritePersons"

fun getFavouritePersons(context: Context, personsList: List<Person>): MutableList<Person> {
    val prefs = context.getSharedPreferences(FAVOURITE_PERSONS, Context.MODE_PRIVATE)
    return prefs.getStringSet(FAVOURITE_PERSONS, mutableSetOf())?.mapToPersonList(personsList)
        ?: mutableListOf()
}

private fun MutableSet<String>.mapToPersonList(personsList: List<Person>): MutableList<Person> {
    return this.mapNotNull { name ->
        personsList.find { it.name == name }
    }.toMutableList()
}

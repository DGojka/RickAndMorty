package com.example.rickandmorty.characterfragment.list.helpers

import android.content.Context
import com.example.rickandmorty.repository.Person

fun getFavouritePersons(context: Context, personsList: List<Person>): MutableList<Person> {
    val prefs = context.getSharedPreferences("asd", Context.MODE_PRIVATE)
    return prefs.getStringSet("FavouritePersons", mutableSetOf())?.mapToPersonList(personsList)
        ?: mutableListOf()
}

private fun MutableSet<String>.mapToPersonList(personsList: List<Person>): MutableList<Person> {
    return this.mapNotNull { name ->
        personsList.find { it.name == name }
    }.toMutableList()
}

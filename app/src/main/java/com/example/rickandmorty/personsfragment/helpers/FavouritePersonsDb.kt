package com.example.rickandmorty.personsfragment.helpers

import android.content.Context
import com.example.rickandmorty.repository.Person
import javax.inject.Inject

class FavouritePersonsDb @Inject constructor(val context: Context) : PersonsListCallback {
    private val prefs = context.getSharedPreferences(FAVOURITE_PERSONS, Context.MODE_PRIVATE)
    private var allPersonsList: List<Person> = mutableListOf()

    fun addPersonToFavourite(person: Person) {
        val favPersons = getAllFavouritePersons().toMutableList()
        favPersons.add(person)
        saveCurrentFavPersonsList(favPersons = favPersons)
    }

    fun removePersonFromFavourite(person: Person) {
        val favPersons = getAllFavouritePersons().toMutableList()
        favPersons.remove(person)
        saveCurrentFavPersonsList(favPersons = favPersons)
    }

    fun getAllFavouritePersons(): List<Person> =
        prefs.getStringSet(FAVOURITE_PERSONS, mutableSetOf())
            ?.mapToPersonList(allPersonsList)
            ?: mutableListOf()

    override fun onPersonsListUpdated(personsList: List<Person>) {
        allPersonsList = personsList
    }

    private fun saveCurrentFavPersonsList(favPersons: List<Person>) {
        val editor = prefs.edit()
        editor.putStringSet(FAVOURITE_PERSONS, favPersons.map { it.id.toString() }.toSet())
        editor.apply()
    }

    private fun MutableSet<String>.mapToPersonList(personsList: List<Person>): MutableList<Person> {
        return this.mapNotNull { id ->
            personsList.find { it.id == id.toInt() }
        }.toMutableList()
    }

    companion object {
        const val FAVOURITE_PERSONS = "FavouritePersonsId"
    }

}
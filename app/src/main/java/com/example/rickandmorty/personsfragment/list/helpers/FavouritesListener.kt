package com.example.rickandmorty.personsfragment.list.helpers

import com.example.rickandmorty.repository.Person

interface FavouritesListener {
    fun isPersonFavourite(person: Person): Boolean

    fun addPersonToFavourite(person :Person)

    fun removePersonFromFavourite(person: Person)
}
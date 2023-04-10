package com.example.rickandmorty.personsfragment.list.helpers.listfilter

import android.content.Context
import com.example.rickandmorty.personsfragment.helpers.FavouritePersonsDb
import com.example.rickandmorty.personsfragment.list.helpers.FiltersManager.Companion.FILTERS
import com.example.rickandmorty.personsfragment.list.helpers.FiltersManager.Companion.FILTERS_KEY
import com.example.rickandmorty.repository.Person

class PersonsFilter(
    private var personList: List<Person>,
    private val context: Context,
    private val favouritePersonsDb: FavouritePersonsDb
) {

    fun filter(): List<Person> {
        val filters = getFilters()
        return when {
            shouldFilterByFavouriteAndUnknownStatus(filters) -> filterFavouriteByUnknownStatus()
            shouldFilterByFavouriteAndDead(filters) -> filterFavouriteByDead()
            shouldFilterByFavouriteAndAlive(filters) -> filterFavouriteByAlive()
            shouldFilterByUnknownStatus(filters) -> filterByUnknownStatus()
            filters.contains(Filters.FAVOURITE) -> filterFavourite()
            filters.contains(Filters.DEAD) -> filterByDead()
            filters.contains(Filters.ALIVE) -> filterByAlive()
            else -> personList
        }
    }

    fun updatePersonsList(persons: List<Person>) {
        personList = persons
    }

    private fun shouldFilterByFavouriteAndUnknownStatus(filters: List<Filters>) =
        filters.contains(Filters.FAVOURITE) && filters.contains(Filters.ALIVE) && filters.contains(
            Filters.DEAD
        )

    private fun shouldFilterByFavouriteAndDead(filters: List<Filters>) =
        filters.contains(Filters.FAVOURITE) && filters.contains(Filters.DEAD)

    private fun shouldFilterByFavouriteAndAlive(filters: List<Filters>) =
        filters.contains(Filters.FAVOURITE) && filters.contains(Filters.ALIVE)


    private fun shouldFilterByUnknownStatus(filters: List<Filters>) =
        filters.contains(Filters.ALIVE) && filters.contains(Filters.DEAD)

    private fun filterFavouriteByUnknownStatus() =
        favouritePersonsDb.getFavouritePersons(personList).filter {
            it.status == UNKNOWN
        }

    private fun filterFavouriteByDead() =
        favouritePersonsDb.getFavouritePersons(personList).filter {
            it.status == DEAD
        }

    private fun filterFavouriteByAlive() =
        favouritePersonsDb.getFavouritePersons(personList).filter {
            it.status == ALIVE
        }

    private fun filterFavourite() =
        favouritePersonsDb.getFavouritePersons(personList)

    private fun filterByUnknownStatus() =
        personList.filter {
            it.status == UNKNOWN
        }

    private fun filterByDead() =
        personList.filter {
            it.status == DEAD
        }

    private fun filterByAlive() =
        personList.filter {
            it.status == ALIVE
        }

    private fun getFilters(): MutableList<Filters> {
        val filters = context.getSharedPreferences(FILTERS, Context.MODE_PRIVATE)
            .getStringSet(FILTERS_KEY, null)?.toList() ?: mutableListOf()
        return filters.convertToFilterEnum().toMutableList()
    }

    private fun List<String>.convertToFilterEnum(): List<Filters> {
        return this.map { intValue ->
            when (intValue) {
                "0", FAVOURITES -> Filters.FAVOURITE
                "1", DEAD -> Filters.DEAD
                "2", UNKNOWN -> Filters.ALIVE
                else -> Filters.UNKNOWN_FILTER
            }
        }
    }

    companion object {
        const val FAVOURITES = "Favourites"
        const val ALIVE = "Alive"
        const val DEAD = "Dead"
        const val UNKNOWN = "unknown"
    }
}
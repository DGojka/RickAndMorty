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

    fun filter(constraint: CharSequence?): List<Person> {
        val filters = getFilters()
        return when {
            shouldFilterByFavouriteAndUnknownStatus(filters) -> filterFavouriteByUnknownStatus(
                constraint
            )
            shouldFilterByFavouriteAndDead(filters) -> filterFavouriteByDead(constraint)
            shouldFilterByFavouriteAndAlive(filters) -> filterFavouriteByAlive(constraint)
            shouldFilterByUnknownStatus(filters) -> filterByUnknownStatus(constraint)
            filters.contains(Filters.FAVOURITE) -> filterFavourite(constraint)
            filters.contains(Filters.DEAD) -> filterByDead(constraint)
            filters.contains(Filters.ALIVE) -> filterByAlive(constraint)
            else -> filterByNameOrStatus(constraint)

        }
    }

    fun updatePersonsList(persons: List<Person>){
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

    private fun filterFavouriteByUnknownStatus(constraint: CharSequence?) =
        favouritePersonsDb.getFavouritePersons(personList).filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == UNKNOWN
        }

    private fun filterFavouriteByDead(constraint: CharSequence?) =
        favouritePersonsDb.getFavouritePersons(personList).filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == DEAD
        }

    private fun filterFavouriteByAlive(constraint: CharSequence?) =
        favouritePersonsDb.getFavouritePersons(personList).filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == ALIVE
        }

    private fun filterFavourite(constraint: CharSequence?) =
        favouritePersonsDb.getFavouritePersons(personList).filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) || it.status.contains(constraint.toString(), ignoreCase = true)
        }

    private fun filterByUnknownStatus(constraint: CharSequence?) =
        personList.filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == UNKNOWN

        }

    private fun filterByDead(constraint: CharSequence?) =
        personList.filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == DEAD
        }

    private fun filterByAlive(constraint: CharSequence?) =
        personList.filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == ALIVE
        }

    private fun filterByNameOrStatus(constraint: CharSequence?) =
        personList.filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) || it.status.contains(constraint.toString(), ignoreCase = true)
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
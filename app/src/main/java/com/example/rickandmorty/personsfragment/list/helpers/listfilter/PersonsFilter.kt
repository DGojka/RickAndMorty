package com.example.rickandmorty.personsfragment.list.helpers.listfilter

import com.example.rickandmorty.personsfragment.helpers.FavouritePersonsDb
import com.example.rickandmorty.personsfragment.helpers.PersonsListCallback
import com.example.rickandmorty.personsfragment.list.helpers.FiltersManager
import com.example.rickandmorty.repository.Person

class PersonsFilter(
    private var personsList: List<Person>,
    private val filtersManager: FiltersManager,
    private val favouritePersonsDb: FavouritePersonsDb
) : PersonsListCallback{

    fun filter(): List<Person> {
        val filters = filtersManager.getSavedFilters().toList().convertToFilterEnum()

        return when {
            shouldFilterByFavouriteAndUnknownStatus(filters) -> filterFavouriteByUnknownStatus()
            shouldFilterByFavouriteAndDead(filters) -> filterFavouriteByDead()
            shouldFilterByFavouriteAndAlive(filters) -> filterFavouriteByAlive()
            shouldFilterByUnknownStatus(filters) -> filterByUnknownStatus()
            filters.contains(Filters.FAVOURITE) -> filterFavourite()
            filters.contains(Filters.DEAD) -> filterByDead()
            filters.contains(Filters.ALIVE) -> filterByAlive()
            else -> personsList
        }
    }

    override fun onPersonsListUpdated(personsList: List<Person>) {
        this.personsList = personsList
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
        favouritePersonsDb.getAllFavouritePersons().filter {
            it.status == UNKNOWN
        }

    private fun filterFavouriteByDead() =
        favouritePersonsDb.getAllFavouritePersons().filter {
            it.status == DEAD
        }

    private fun filterFavouriteByAlive() =
        favouritePersonsDb.getAllFavouritePersons().filter {
            it.status == ALIVE
        }

    private fun filterFavourite() =
        favouritePersonsDb.getAllFavouritePersons()

    private fun filterByUnknownStatus() =
        personsList.filter {
            it.status == UNKNOWN
        }

    private fun filterByDead() =
        personsList.filter {
            it.status == DEAD
        }

    private fun filterByAlive() =
        personsList.filter {
            it.status == ALIVE
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
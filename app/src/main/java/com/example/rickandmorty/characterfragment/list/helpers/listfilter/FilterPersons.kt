package com.example.rickandmorty.characterfragment.list.helpers.listfilter

import android.content.Context
import com.example.rickandmorty.characterfragment.list.CharacterListAdapter
import com.example.rickandmorty.characterfragment.list.helpers.Filters
import com.example.rickandmorty.characterfragment.list.helpers.getFavouritePersons
import com.example.rickandmorty.network.Person

class FilterPersons(private val personList: List<Person>, private val context: Context) {
    fun filter(constraint: CharSequence?): List<Person> {
        val filters = getFilters()
        if (filters != null) {
            return when {
                shouldFilterByFavouriteAndStatus(filters) -> filterFavouriteByStatus(constraint)
                shouldFilterByFavouriteAndDead(filters) -> filterFavouriteByDead(constraint)
                shouldFilterByFavouriteAndAlive(filters) -> filterFavouriteByAlive(constraint)
                filters.contains(Filters.FAVOURITE) -> filterFavourite(constraint)
                filters.contains(Filters.DEAD) -> filterByDead(constraint)
                filters.contains(Filters.ALIVE) -> filterByAlive(constraint)
                else -> filterByNameOrStatus(constraint)
            }
        }
        return personList
    }


    private fun shouldFilterByFavouriteAndStatus(filters: List<Filters>) =
        filters.contains(Filters.FAVOURITE) && filters.contains(Filters.ALIVE) && filters.contains(
            Filters.DEAD
        )

    private fun shouldFilterByFavouriteAndDead(filters: List<Filters>) =
        filters.contains(Filters.FAVOURITE) && filters.contains(Filters.DEAD)

    private fun shouldFilterByFavouriteAndAlive(filters: List<Filters>) =
        filters.contains(Filters.FAVOURITE) && filters.contains(Filters.ALIVE)

    private fun filterFavouriteByStatus(constraint: CharSequence?) =
        getFavouritePersons(context, personList).filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == CharacterListAdapter.UNKNOWN
        }

    private fun filterFavouriteByDead(constraint: CharSequence?) =
        getFavouritePersons(context, personList).filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == CharacterListAdapter.DEAD
        }

    private fun filterFavouriteByAlive(constraint: CharSequence?) =
        getFavouritePersons(context, personList).filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == CharacterListAdapter.ALIVE
        }

    private fun filterFavourite(constraint: CharSequence?) =
        getFavouritePersons(context, personList).filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) || it.status.contains(constraint.toString(), ignoreCase = true)
        }

    private fun filterByDead(constraint: CharSequence?) =
        personList.filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == CharacterListAdapter.DEAD
        }

    private fun filterByAlive(constraint: CharSequence?) =
        personList.filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) && it.status == CharacterListAdapter.ALIVE
        }

    private fun filterByNameOrStatus(constraint: CharSequence?) =
        personList.filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) || it.status.contains(constraint.toString(), ignoreCase = true)
        }

    private fun getFilters(): MutableList<Filters>? {
        return context.getSharedPreferences("Filters", Context.MODE_PRIVATE)
            .getStringSet("SelectedItems", null)?.toList()?.convertToFilterEnum()?.toMutableList()
    }

    private fun List<String>.convertToFilterEnum(): List<Filters> {
        return this.map { intValue ->
            when (intValue) {
                "0", CharacterListAdapter.FAVOURITES -> Filters.FAVOURITE
                "1", CharacterListAdapter.DEAD -> Filters.DEAD
                "2", CharacterListAdapter.UNKNOWN -> Filters.ALIVE
                else -> Filters.UNKNOWN_FILTER
            }
        }
    }
}
package com.example.rickandmorty.personsfragment.list.helpers

import android.content.Context
import com.example.rickandmorty.personsfragment.list.helpers.listfilter.Filters
import com.example.rickandmorty.personsfragment.list.helpers.listfilter.PersonsFilter
import javax.inject.Inject

class FiltersManager @Inject constructor(context: Context) {
    private val prefs = context.getSharedPreferences(FILTERS, Context.MODE_PRIVATE)

    fun getSavedFilters(): List<Filters>{
        return getSavedFiltersAsStringSetOfNumbers().toList().convertToFilterEnum()
    }

    fun doesFilterContainFavourite(): Boolean =
        getSavedFilters().contains(Filters.FAVOURITE)

    fun saveSelectedFilters(selectedItems: List<Int>) {
        val editor = prefs.edit()
        editor.putStringSet(FILTERS_KEY, selectedItems.map { it.toString() }.toSet())
        editor.apply()
    }

    private fun getSavedFiltersAsStringSetOfNumbers(): Set<String> {
        return prefs.getStringSet(FILTERS_KEY, null) ?: mutableSetOf()
    }

    private fun List<String>.convertToFilterEnum(): List<Filters> {
        return this.map { intValue ->
            when (intValue) {
                "0", PersonsFilter.FAVOURITES -> Filters.FAVOURITE
                "1", PersonsFilter.DEAD -> Filters.DEAD
                "2", PersonsFilter.ALIVE -> Filters.ALIVE
                else -> Filters.UNKNOWN_FILTER
            }
        }
    }

    companion object {
        const val FILTERS = "Filters"
        const val FILTERS_KEY = "SelectedOptions"
    }
}

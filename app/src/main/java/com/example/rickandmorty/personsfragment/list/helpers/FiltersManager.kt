package com.example.rickandmorty.personsfragment.list.helpers

import android.content.Context
class FiltersManager(context: Context) {
    private val prefs = context.getSharedPreferences(FILTERS, Context.MODE_PRIVATE)

    fun getSavedFilters(): Set<String>? {
        return prefs.getStringSet(FILTERS_KEY, null)
    }

    fun saveSelectedFilters(selectedItems: List<Int>) {
        val editor = prefs.edit()
        editor.putStringSet(FILTERS_KEY, selectedItems.map { it.toString() }.toSet())
        editor.apply()
    }

    companion object{
        const val FILTERS = "Filters"
        const val FILTERS_KEY = "SelectedOptions"
    }
}

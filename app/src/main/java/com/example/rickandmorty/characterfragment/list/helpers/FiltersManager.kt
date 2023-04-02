package com.example.rickandmorty.characterfragment.list.helpers

import android.content.Context

class FiltersManager(context: Context) {
    private val prefs = context.getSharedPreferences("Filters", Context.MODE_PRIVATE)

    fun getSelectedFilters(): Set<String>? {
        return prefs.getStringSet("SelectedItems", null)
    }

    fun saveSelectedFilters(selectedItems: List<Int>) {
        val editor = prefs.edit()
        editor.putStringSet("SelectedItems", selectedItems.map { it.toString() }.toSet())
        editor.apply()
    }
}
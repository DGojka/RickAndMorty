package com.example.rickandmorty.characterfragment.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.rickandmorty.R
import com.example.rickandmorty.network.CharacterNetwork
import com.example.rickandmorty.network.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonsListViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(
        PersonsListUiState(
            true,
            mutableListOf(), null
        )
    )
    val uiState: StateFlow<PersonsListUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                allPersons = CharacterNetwork.getAllCharacters()
            )
        }
    }

    fun moreDetails(person: Person, findNavController: NavController) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                clickedPerson = person
            )
            findNavController.navigate(R.id.action_to_more_details_fragment)
        }
    }

  /*  fun getClickedPerson(): Person {
        val moreDetailsState = _uiState.value as PersonsListUiState.MoreDetails
        return moreDetailsState.person
    }*/

    fun onBackClick() {
    //    emitAllPersonsList()
    }

    fun saveSelectedFilters(context: Context, selectedItems: List<Int>) {
        val prefs = context.getSharedPreferences("Filters", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val selectedSet = selectedItems.map { it.toString() }.toSet()
        editor.putStringSet("SelectedItems", selectedSet)
        editor.apply()
    }

    fun getSavedFilters(context: Context): MutableSet<String>? {
        val prefs = context.getSharedPreferences("Filters", Context.MODE_PRIVATE)
        return prefs.getStringSet("SelectedItems", null)
    }

/*    private fun emitAllPersonsList() {
        viewModelScope.launch {
            _uiState.emit(PersonsListUiState.Loaded(CharacterNetwork.getAllCharacters()))
        }
    }*/
}
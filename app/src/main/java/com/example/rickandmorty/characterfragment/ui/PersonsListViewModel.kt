package com.example.rickandmorty.characterfragment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.rickandmorty.R
import com.example.rickandmorty.characterfragment.list.helpers.FiltersManager
import com.example.rickandmorty.network.CharacterNetwork
import com.example.rickandmorty.network.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonsListViewModel @Inject constructor(val filtersManager: FiltersManager) : ViewModel() {
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
            findNavController.navigate(R.id.action_to_more_details_fragment) //move this to personslistfragment
        }
    }

    fun saveSelectedFilters(selectedItems: List<Int>) {
     /*   val prefs = context.getSharedPreferences("Filters", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val selectedSet = selectedItems.map { it.toString() }.toSet()
        editor.putStringSet("SelectedItems", selectedSet)
        editor.apply()*/
        filtersManager.saveSelectedFilters(selectedItems)
    }

    fun getSavedFilters(): Set<String> {
      /*  val prefs = context.getSharedPreferences("Filters", Context.MODE_PRIVATE)
        return prefs.getStringSet("SelectedItems", null)*/
        return filtersManager.getSelectedFilters()?: setOf()
    }

/*    private fun emitAllPersonsList() {
        viewModelScope.launch {
            _uiState.emit(PersonsListUiState.Loaded(CharacterNetwork.getAllCharacters()))
        }
    }*/
}
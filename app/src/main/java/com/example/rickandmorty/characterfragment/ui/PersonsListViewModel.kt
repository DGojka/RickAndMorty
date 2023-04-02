package com.example.rickandmorty.characterfragment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.rickandmorty.R
import com.example.rickandmorty.characterfragment.list.helpers.FiltersManager
import com.example.rickandmorty.repository.Person
import com.example.rickandmorty.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonsListViewModel @Inject constructor(
    private val filtersManager: FiltersManager,
    private val repository: Repository
) :
    ViewModel() {
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
                allPersons = repository.getAllPersons()
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
        filtersManager.saveSelectedFilters(selectedItems)
    }

    fun getSavedFilters(): MutableSet<String> {
        return filtersManager.getSelectedFilters()?.toMutableSet() ?: mutableSetOf()
    }
}
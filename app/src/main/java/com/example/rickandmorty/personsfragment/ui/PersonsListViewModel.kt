package com.example.rickandmorty.personsfragment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.rickandmorty.R
import com.example.rickandmorty.personsfragment.helpers.FavouritePersonsDb
import com.example.rickandmorty.personsfragment.list.helpers.FiltersManager
import com.example.rickandmorty.personsfragment.list.helpers.listfilter.PersonsFilter
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
    private val repository: Repository,
    private val personsFilter: PersonsFilter,
    private val favouritePersonsDbImpl: FavouritePersonsDb
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(
        PersonsListUiState(
            true
        )
    )
    val uiState: StateFlow<PersonsListUiState> = _uiState

    init {
        viewModelScope.launch {
            val personsList = repository.getPersonsByPage(1)
            personsFilter.onPersonsListUpdated(personsList)
            favouritePersonsDbImpl.onPersonsListUpdated(personsList)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                allFetchedPersons = personsList,
                filteredPersons = personsFilter.filter()
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

    fun saveSelectedFilters(selectedItems: List<Int>) =
        filtersManager.saveSelectedFilters(selectedItems)


    fun getSavedFilters(): MutableSet<String> =
        filtersManager.getSavedFilters().toMutableSet()


    fun loadMorePersons() {
        _uiState.value = _uiState.value.copy(
            isLoading = true
        )
        viewModelScope.launch {
            val nextPage = _uiState.value.currentPersonsPage + 1
            val currentPersons = _uiState.value.allFetchedPersons
            val newList = currentPersons + repository.getPersonsByPage(nextPage)

            personsFilter.onPersonsListUpdated(newList)
            favouritePersonsDbImpl.onPersonsListUpdated(newList)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                currentPersonsPage = nextPage,
                allFetchedPersons = newList,
                filteredPersons = personsFilter.filter()
            )
        }
    }

    fun applyFilters() {
        _uiState.value = _uiState.value.copy(
            filteredPersons = personsFilter.filter()
        )
    }

    fun isPersonFavourite(person: Person): Boolean =
        favouritePersonsDbImpl.getAllFavouritePersons().contains(person)

    fun addPersonToFavourite(person: Person) {
        favouritePersonsDbImpl.addPersonToFavourite(person)
    }

    fun removePersonFromFavourite(person: Person) {
        favouritePersonsDbImpl.removePersonFromFavourite(person)
    }

}
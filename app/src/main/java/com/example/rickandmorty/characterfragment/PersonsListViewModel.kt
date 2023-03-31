package com.example.rickandmorty.characterfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.rickandmorty.R
import com.example.rickandmorty.network.CharacterNetwork
import com.example.rickandmorty.network.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PersonsListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PersonsListUiState>(PersonsListUiState.Init)
    val uiState: StateFlow<PersonsListUiState> = _uiState

    init {
        emitAllPersonsList()
    }

    fun moreDetails(person: Person, findNavController: NavController) {
        viewModelScope.launch {
            _uiState.emit(PersonsListUiState.MoreDetails(person))
            findNavController.navigate(R.id.action_to_more_details_fragment)
        }
    }

    fun getClickedPerson(): Person {
        val moreDetailsState = _uiState.value as PersonsListUiState.MoreDetails
        return moreDetailsState.person
    }

    fun onBackClick() {
        emitAllPersonsList()
    }

    private fun emitAllPersonsList() {
        viewModelScope.launch {
            _uiState.emit(PersonsListUiState.Loaded(CharacterNetwork.getAllCharacters()))
        }
    }
}
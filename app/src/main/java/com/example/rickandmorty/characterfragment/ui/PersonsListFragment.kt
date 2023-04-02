package com.example.rickandmorty.characterfragment.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rickandmorty.characterfragment.list.CharacterListAdapter
import com.example.rickandmorty.databinding.FragmentCharactersListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class PersonsListFragment : Fragment() {
    private var _binding: FragmentCharactersListBinding? = null
    private val binding get() = _binding

    private lateinit var adapter: CharacterListAdapter
    private val viewModel: PersonsListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharactersListBinding.inflate(inflater, container, false)
        adapter = CharacterListAdapter(requireContext()) { person ->
            viewModel.moreDetails(person, findNavController())
        }
        binding?.recyclerView?.adapter = adapter
        _binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        binding?.manageFiltersButton?.setOnClickListener {
            val options = arrayOf("favourites", "Dead", "Alive")
            val selectedItems = ArrayList<Int>()
            viewModel.getSavedFilters().let {
                for (i in it) {
                    selectedItems.add(i.toInt())
                }
            }

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Select Options")
            builder.setMultiChoiceItems(options, null) { _, which, isChecked ->
                if (isChecked) {
                    selectedItems.add(which)
                } else if (selectedItems.contains(which)) {
                    selectedItems.remove(Integer.valueOf(which))
                }
            }

            builder.setPositiveButton("OK") { _, _ ->
                val selectedFilterOptions = mutableListOf<String>()
                for (i in selectedItems.indices) {
                    Log.d("TAG", options[selectedItems[i]])
                    selectedFilterOptions.add(options[selectedItems[i]])
                }
                viewModel.saveSelectedFilters(selectedItems)
                adapter.applyFilters()
            }

            val dialog = builder.create()
            dialog.setOnShowListener {
                for (i in selectedItems) {
                    dialog.listView.setItemChecked(i, true)
                }
            }
            dialog.show()
        }
        return binding?.root
    }

 /*   private fun showFiltersDialog() {
        val options = arrayOf("favourites", "Dead", "Alive")
        val selectedItems = getSelectedItemsFromStorage()
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Options")
        builder.setMultiChoiceItems(
            options,
            getCheckedItems(selectedItems)
        ) { _, which, isChecked ->
            if (isChecked) {
                selectedItems.add(which)
            } else if (selectedItems.contains(which)) {
                selectedItems.remove(which)
            }
        }
        builder.setPositiveButton("OK") { _, _ ->
          //  saveSelectedItemsToStorage(selectedItems)
            adapter.applyFilters()
        }
        val dialog = builder.create()
        dialog.show()
        setCheckedItems(dialog, selectedItems)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.allPersons)
                adapter.setData(state.allPersons)
               /* if(state.clickedPerson!=null){
                    findNavController().navigate(R.id.action_to_more_details_fragment)
                }*/
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.example.rickandmorty.characterfragment.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rickandmorty.characterfragment.list.CharacterListAdapter
import com.example.rickandmorty.databinding.FragmentCharactersListBinding
import kotlinx.coroutines.*

class PersonsListFragment : Fragment() {
    private var _binding: FragmentCharactersListBinding? = null
    private val binding get() = _binding

    private lateinit var adapter: CharacterListAdapter
    private val viewModel: PersonsListViewModel by lazy {
        ViewModelProvider(requireActivity())[PersonsListViewModel::class.java]
    }

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
            val prefs = requireContext().getSharedPreferences("Filters", Context.MODE_PRIVATE)
            val savedItems = prefs.getStringSet("SelectedItems", null)
            savedItems?.let {
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
                // handle OK button click
                val selectedFilterOptions = mutableListOf<String>()
                for (i in selectedItems.indices) {
                    Log.d("TAG", options[selectedItems[i]])
                    selectedFilterOptions.add(options[selectedItems[i]])
                }
                // Save selected items to storage
                val editor = prefs.edit()
                val selectedSet = HashSet<String>()
                for (i in selectedItems) {
                    selectedSet.add(i.toString())
                }
                editor.putStringSet("SelectedItems", selectedSet)
                editor.apply()
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

    private fun showFiltersDialog() {
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
            saveSelectedItemsToStorage(selectedItems)
            adapter.applyFilters()
        }
        val dialog = builder.create()
        dialog.show()
        setCheckedItems(dialog, selectedItems)
    }


    private fun getSelectedItemsFromStorage(): ArrayList<Int> {
        val prefs = requireContext().getSharedPreferences("Filters", Context.MODE_PRIVATE)
        val savedItems = prefs.getStringSet("SelectedItems", null)
        val selectedItems = ArrayList<Int>()
        savedItems?.let {
            for (i in it) {
                selectedItems.add(i.toInt())
            }
        }
        return selectedItems
    }

    private fun saveSelectedItemsToStorage(selectedItems: ArrayList<Int>) {
        val prefs = requireContext().getSharedPreferences("Filters", Context.MODE_PRIVATE)
        val selectedSet = HashSet<String>()
        for (i in selectedItems) {
            selectedSet.add(i.toString())
        }
        prefs.edit().putStringSet("SelectedItems", selectedSet).apply()
    }

    private fun getCheckedItems(selectedItems: ArrayList<Int>?): BooleanArray? {
        return selectedItems?.map { true }?.toBooleanArray()
    }

    private fun setCheckedItems(dialog: AlertDialog, selectedItems: ArrayList<Int>) {
        dialog.setOnShowListener {
            for (i in selectedItems) {
                dialog.listView.setItemChecked(i, true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is PersonsListUiState.Loaded -> {
                        adapter.submitList(state.persons)
                        adapter.setData(state.persons)
                    }
                    is PersonsListUiState.MoreDetails -> {}//view.findNavController().navigate(R.id.action_to_more_details_fragment)
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
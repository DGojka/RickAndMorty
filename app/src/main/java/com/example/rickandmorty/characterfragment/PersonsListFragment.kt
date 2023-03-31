package com.example.rickandmorty.characterfragment

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
    private val viewModel: PersonsListViewModel by lazy{
        ViewModelProvider(requireActivity())[PersonsListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharactersListBinding.inflate(inflater, container, false)
        adapter = CharacterListAdapter { person ->
            Log.e("clicked", person.toString())
            viewModel.moreDetails(person,findNavController())
        }
        binding?.recyclerView?.adapter = adapter

        _binding!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        }
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    PersonsListUiState.Init -> {}
                    is PersonsListUiState.Loaded -> {
                        adapter.submitList(state.persons)
                        adapter.setData(state.persons)
                    }
                    is PersonsListUiState.MoreDetails -> {}//view.findNavController().navigate(R.id.action_to_more_details_fragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
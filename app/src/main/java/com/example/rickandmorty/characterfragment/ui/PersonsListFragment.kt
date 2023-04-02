package com.example.rickandmorty.characterfragment.ui

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.R
import com.example.rickandmorty.characterfragment.list.PersonListAdapter
import com.example.rickandmorty.characterfragment.list.helpers.listfilter.Filters
import com.example.rickandmorty.databinding.FragmentCharactersListBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class PersonsListFragment : Fragment() {
    private var _binding: FragmentCharactersListBinding? = null
    private val binding get() = _binding

    private lateinit var adapter: PersonListAdapter
    private val viewModel: PersonsListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharactersListBinding.inflate(inflater, container, false)
        adapter = PersonListAdapter(requireContext()) { person ->
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
            showFiltersDialog()
        }

        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition == totalItemCount - 1) {
                   viewModel.loadMorePersons()
                }
            }
        })

        return binding?.root
    }

    private fun getCurrentFilters(): ArrayList<Int> {
        val selectedItems = ArrayList<Int>()
        viewModel.getSavedFilters().let {
            for (i in it) {
                selectedItems.add(i.toInt())
            }
        }
        return selectedItems
    }

    private fun showFiltersDialog() {
        val options = arrayOf(
            Filters.FAVOURITE.toString(),
            Filters.DEAD.toString(),
            Filters.ALIVE.toString()
        )
        val selectedItems = getCurrentFilters()
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_filters)
        builder.setMultiChoiceItems(options, null) { _, which, isChecked ->
            if (isChecked) {
                selectedItems.add(which)
            } else if (selectedItems.contains(which)) {
                selectedItems.remove(Integer.valueOf(which))
            }
        }
        builder.setPositiveButton(R.string.apply_filters) { _, _ ->
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            if (isNetworkAvailable(requireContext())) {
                viewModel.uiState.collect { state ->
                    with(state){
                        adapter.submitList(allFetchedPersons)
                        adapter.setData(allFetchedPersons)
                        if(isLoading){
                            _binding!!.loadingBar.visibility = View.VISIBLE
                        }else{
                            _binding!!.loadingBar.visibility = View.GONE
                        }
                    }
                }
            }else{
                Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_LONG).show()
            }

        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
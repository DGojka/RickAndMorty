package com.example.rickandmorty.personsfragment.ui

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
import com.example.rickandmorty.personsfragment.list.PersonListAdapter
import com.example.rickandmorty.personsfragment.list.helpers.FavouritePersonsDb
import com.example.rickandmorty.personsfragment.list.helpers.listfilter.PersonsFilter.Companion.ALIVE
import com.example.rickandmorty.personsfragment.list.helpers.listfilter.PersonsFilter.Companion.DEAD
import com.example.rickandmorty.personsfragment.list.helpers.listfilter.PersonsFilter.Companion.FAVOURITES
import com.example.rickandmorty.databinding.FragmentPersonsListBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PersonsListFragment : Fragment() {
    private var _binding: FragmentPersonsListBinding? = null
    private val binding get() = _binding

    private lateinit var adapter: PersonListAdapter
    private val viewModel: PersonsListViewModel by activityViewModels()
    @Inject
    lateinit var favouritePersonsDb: FavouritePersonsDb

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonsListBinding.inflate(inflater, container, false)
        adapter = PersonListAdapter(requireContext(),favouritePersonsDb) { person ->
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

                if (lastVisibleItemPosition == adapter.getAllItemsCount() - 1) {
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
            FAVOURITES,
            DEAD,
            ALIVE
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
            if(adapter.itemCount == 0){
                binding?.noPersonOnList?.visibility = View.VISIBLE
            }else{
                binding?.noPersonOnList?.visibility = View.GONE
            }
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
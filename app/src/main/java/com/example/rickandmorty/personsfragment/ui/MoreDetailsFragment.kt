package com.example.rickandmorty.personsfragment.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.personsfragment.helpers.FavouritePersonsDb
import com.example.rickandmorty.personsfragment.list.helpers.listfilter.PersonsFilter.Companion.ALIVE
import com.example.rickandmorty.personsfragment.list.helpers.listfilter.PersonsFilter.Companion.DEAD
import com.example.rickandmorty.databinding.FragmentMoreDetailsBinding
import com.example.rickandmorty.repository.Person
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MoreDetailsFragment : Fragment() {
    private var _binding: FragmentMoreDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonsListViewModel by activityViewModels()
    @Inject
    lateinit var favouritePersonsDb: FavouritePersonsDb

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                with(state.clickedPerson) {
                    if (this != null) {
                        Glide.with(view)
                            .load(image)
                            .placeholder(R.drawable.placeholder)
                            .into(binding.moreInfoImage)
                        binding.apply {
                            moreInfoCharacterName.text = name
                            moreInfoStatus.text = status
                            genderTextView.text = String.format(
                                resources.getString(R.string.attribute_gender),
                                gender
                            )
                            locationName.text = String.format(
                                resources.getString(R.string.attribute_location),
                                location.name
                            )
                            speciesTextView.text =
                                String.format(
                                    resources.getString(R.string.attribute_species),
                                    species
                                )
                            originTextview.text = String.format(
                                resources.getString(R.string.attribute_origin),
                                origin.name
                            )
                            moreInfoStatus.setTextColor(
                                when (status) {
                                    ALIVE -> Color.GREEN
                                    DEAD -> Color.RED
                                    else -> Color.LTGRAY
                                }
                            )
                        }
                        val favPersons =
                            favouritePersonsDb.getFavouritePersons(state.allFetchedPersons)

                        val imageResId =
                            if (favPersons.contains(this)) R.drawable.ic_fav else R.drawable.ic_fav_border
                        binding.favouriteButton.setImageResource(imageResId)
                        binding.favouriteButton.setOnClickListener {
                            handleFavouriteButtonOnClick(this, favPersons)
                        }
                    }
                }
            }
        }
    }

    private fun handleFavouriteButtonOnClick(
        person: Person,
        favPersons: MutableList<Person>
    ) {
        if (favPersons.contains(person)) {
            favPersons.remove(person)
            binding.favouriteButton.setImageResource(R.drawable.ic_fav_border)
        } else {
            favPersons.add(person)
            binding.favouriteButton.setImageResource(R.drawable.ic_fav)
        }
        favouritePersonsDb.saveCurrentFavPersonsList(favPersons)
    }
}
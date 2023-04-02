package com.example.rickandmorty.characterfragment.ui

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
import com.example.rickandmorty.characterfragment.list.helpers.listfilter.FilterPersons.Companion.ALIVE
import com.example.rickandmorty.characterfragment.list.helpers.listfilter.FilterPersons.Companion.DEAD
import com.example.rickandmorty.databinding.FragmentMoreDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoreDetailsFragment : Fragment() {
    private var _binding: FragmentMoreDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonsListViewModel by activityViewModels()

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
                        binding.moreInfoCharacterName.text = name
                        binding.moreInfoStatus.text = status

                        binding.gender.text =
                            String.format(resources.getString(R.string.attribute_gender), gender)
                        binding.locationName.text = String.format(
                            resources.getString(R.string.attribute_location),
                            location.name
                        )
                        binding.species.text =
                            String.format(resources.getString(R.string.attribute_species), species)
                        binding.origin.text = String.format(
                            resources.getString(R.string.attribute_origin),
                            origin.name
                        )
                        binding.moreInfoStatus.setTextColor(if (status == ALIVE) Color.GREEN else if (status == DEAD) Color.RED else Color.LTGRAY)
                    }
                }
            }
        }
    }
}
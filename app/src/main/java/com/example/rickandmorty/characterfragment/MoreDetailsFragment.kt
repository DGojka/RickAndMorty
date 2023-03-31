package com.example.rickandmorty.characterfragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentMoreDetailsBinding

class MoreDetailsFragment : Fragment() {
    private var _binding: FragmentMoreDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonsListViewModel by lazy {
        ViewModelProvider(requireActivity())[PersonsListViewModel::class.java]
    }

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
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onBackClick()
            }
        }
        callback.handleOnBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(view: View) {
        with(viewModel.getClickedPerson()) {
            Glide.with(view)
                .load(image)
                .placeholder(R.drawable.placeholder)
                .into(binding.moreInfoImage)
            binding.moreInfoCharacterName.text = name
            binding.moreInfoStatus.text = status

            binding.gender.text = "Gender: $gender"
            binding.locationName.text = "Location: ${location.name}"
            binding.species.text = "Species: $species"
            binding.origin.text = "Origin: ${origin.name}"
            binding.moreInfoStatus.setTextColor(if (status == "Alive") Color.GREEN else Color.RED)
        }
    }
}
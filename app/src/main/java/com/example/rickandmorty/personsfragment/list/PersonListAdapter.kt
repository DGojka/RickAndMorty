package com.example.rickandmorty.personsfragment.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ListItemCharacterBinding
import com.example.rickandmorty.personsfragment.list.helpers.FavouritesListener
import com.example.rickandmorty.repository.Person


class PersonListAdapter(
    private val favouritesListener: FavouritesListener,
    private val onPersonClick: (Person) -> Unit
) :
    ListAdapter<Person, PersonListAdapter.CharacterViewHolder>(
        PersonDiffCallback()
    ), Filterable {

    private val personsList = mutableListOf<Person>()
    private val filteredByName = mutableListOf<Person>()

    inner class CharacterViewHolder(private val binding: ListItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person) {
            with(binding) {
                characterName.text = person.name
                root.setOnClickListener { onPersonClick(person) }
                Glide.with(itemView)
                    .load(person.image)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.characterImage)

                favouriteButton.setImageResource(if (favouritesListener.isPersonFavourite(person = person)) R.drawable.ic_fav else R.drawable.ic_fav_border)
                favouriteButton.setOnClickListener {
                    handleFavouriteButtonClick(binding, person)
                }
            }
        }

        private fun handleFavouriteButtonClick(binding: ListItemCharacterBinding, person: Person) {
            if (favouritesListener.isPersonFavourite(person)) {
                favouritesListener.removePersonFromFavourite(person)
                binding.favouriteButton.setImageResource(R.drawable.ic_fav_border)
            } else {
                favouritesListener.addPersonToFavourite(person)
                binding.favouriteButton.setImageResource(R.drawable.ic_fav)
            }
        }
    }

    fun setData(data: List<Person>, currentQuery: String = "") {
        val filteredList = filterByNameOrStatus(currentQuery)
        submitList(filteredList)
        personsList.apply {
            clear()
            addAll(data)
        }
        filteredByName.apply {
            clear()
            addAll(filteredList)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CharacterViewHolder(
            ListItemCharacterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        holder.bind(character)
    }

    override fun getItemCount() = filteredByName.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                filterResults.values = filterByNameOrStatus(constraint)

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredByName.apply {
                    clear()
                    addAll(results?.values as MutableList<Person>)
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun getItem(position: Int): Person {
        return filteredByName[position]
    }

    private fun filterByNameOrStatus(constraint: CharSequence?): List<Person> {
        return if (!constraint.isNullOrEmpty()) personsList.filter {
            it.name.contains(
                constraint.toString(),
                ignoreCase = true
            ) || it.status.contains(constraint.toString(), ignoreCase = true)
        } else {
            personsList
        }
    }
}
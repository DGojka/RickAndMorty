package com.example.rickandmorty.personsfragment.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.personsfragment.helpers.FavouritePersonsDb
import com.example.rickandmorty.databinding.ListItemCharacterBinding
import com.example.rickandmorty.repository.Person


class PersonListAdapter(
    private val favouritePersonsDb: FavouritePersonsDb,
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

                val isFavourite =
                    favouritePersonsDb.getFavouritePersons(personsList).contains(person)
                favouriteButton.setImageResource(if (isFavourite) R.drawable.ic_fav else R.drawable.ic_fav_border)
                favouriteButton.setOnClickListener {
                    handleFavouriteButtonClick(binding, person)
                }
            }
        }

        private fun handleFavouriteButtonClick(binding: ListItemCharacterBinding, person: Person) {
            val favPersons = favouritePersonsDb.getFavouritePersons(personsList)
            val isFavourite = favPersons.contains(person)

            if (isFavourite) {
                favPersons.remove(person)
                binding.favouriteButton.setImageResource(R.drawable.ic_fav_border)
            } else {
                favPersons.add(person)
                binding.favouriteButton.setImageResource(R.drawable.ic_fav)
            }
            favouritePersonsDb.saveCurrentFavPersonsList(favPersons)
        }
    }

    fun setData(data: List<Person>, currentQuery: String = "") {
        personsList.apply {
            clear()
            addAll(data)
        }
        filteredByName.apply {
            clear()
            addAll(filterByNameOrStatus(currentQuery))
        }
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
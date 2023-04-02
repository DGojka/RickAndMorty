package com.example.rickandmorty.characterfragment.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.characterfragment.list.helpers.FAVOURITE_PERSONS
import com.example.rickandmorty.characterfragment.list.helpers.getFavouritePersons
import com.example.rickandmorty.characterfragment.list.helpers.listfilter.PersonsFilter
import com.example.rickandmorty.databinding.ListItemCharacterBinding
import com.example.rickandmorty.repository.Person


class PersonListAdapter(
    private val context: Context,
    private val onPersonClick: (Person) -> Unit
) :
    ListAdapter<Person, PersonListAdapter.CharacterViewHolder>(
        PersonDiffCallback()
    ), Filterable {

    private val allPersons = mutableListOf<Person>()
    private val filteredList = mutableListOf<Person>()
    private var personsFilter: PersonsFilter = PersonsFilter(emptyList(), context)

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

                val isFavourite = getFavouritePersons(context, allPersons).contains(person)
                favouriteButton.setImageResource(if (isFavourite) R.drawable.ic_fav else R.drawable.ic_fav_border)
                favouriteButton.setOnClickListener {
                    handleFavouriteButtonClick(binding, person)
                }
            }
        }

        private fun handleFavouriteButtonClick(binding: ListItemCharacterBinding, person: Person) {
            val editor =
                itemView.context.getSharedPreferences(FAVOURITE_PERSONS, Context.MODE_PRIVATE)
                    .edit()
            val favPersons = getFavouritePersons(context, allPersons)
            val isFavourite = favPersons.contains(person)

            if (isFavourite) {
                favPersons.remove(person)
                binding.favouriteButton.setImageResource(R.drawable.ic_fav_border)
            } else {
                favPersons.add(person)
                binding.favouriteButton.setImageResource(R.drawable.ic_fav)
            }

            editor.putStringSet(FAVOURITE_PERSONS, favPersons.map { it.name }.toSet())
            editor.apply()
        }
    }

    fun setData(data: List<Person>) {
        allPersons.apply {
            clear()
            addAll(data)
        }
        personsFilter = PersonsFilter(allPersons, context)
        filteredList.apply {
            clear()
            addAll(personsFilter.filter(""))
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

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                filterResults.values = personsFilter.filter(constraint)

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList.apply {
                    clear()
                    addAll(results?.values as MutableList<Person>)
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun getItem(position: Int): Person {
        return filteredList[position]
    }

    fun applyFilters() {
        filteredList.clear()
        filteredList.addAll(personsFilter.filter(""))
        notifyDataSetChanged()
    }
}
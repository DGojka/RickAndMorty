package com.example.rickandmorty.characterfragment.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentCharactersListBinding
import com.example.rickandmorty.databinding.ListItemCharacterBinding
import com.example.rickandmorty.network.Person


class CharacterListAdapter(
    private val context: Context,
    private val onItemClick: (Person) -> Unit
) :
    ListAdapter<Person, CharacterListAdapter.CharacterViewHolder>(
        CharacterDiffCallback()
    ), Filterable {

    private var personList = mutableListOf<Person>()
    private var filteredList = mutableListOf<Person>()
    var favouritePersons = mutableListOf<Person>()

    inner class CharacterViewHolder(private val binding: ListItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person) {
            binding.characterName.text = person.name
            binding.root.setOnClickListener { onItemClick(person) }
            Glide.with(itemView)
                .load(person.image)
                .placeholder(R.drawable.placeholder)
                .into(binding.characterImage)

            val isFavourite = favouritePersons.contains(person)
            binding.favouriteButton.setImageResource(if (isFavourite) R.drawable.ic_fav else R.drawable.ic_fav_border)
            binding.favouriteButton.setOnClickListener {
                val editor =
                    itemView.context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()
                if (isFavourite) {
                    favouritePersons.remove(person)
                    binding.favouriteButton.setImageResource(R.drawable.ic_fav_border)
                } else {
                    favouritePersons.add(person)
                    Log.e("clicked",person.name)
                    binding.favouriteButton.setImageResource(R.drawable.ic_fav)
                }
                editor.putStringSet(
                    "FavouritePersons",
                    favouritePersons.map { it.name }.toSet()
                )
                Log.e(
                    "context.getSharedPreferences(\"MyPrefs\", Context.MODE_PRIVATE)",
                    context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        .getStringSet("FavouritePersons", mutableSetOf()).toString()
                )
                Log.e("asd",favouritePersons.toString())
                editor.apply()
                notifyDataSetChanged()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view =
            ListItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding =
            FragmentCharactersListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })

        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        holder.bind(character)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun setData(data: List<Person>) {
        personList.clear()
        personList.addAll(data)
        filter("")
        updateFavouritePersons()
    }

    private fun filter(query: String?) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(personList)
        } else {
            val filtered = personList.filter {
                it.name.contains(query, ignoreCase = true) || it.status.contains(
                    query,
                    ignoreCase = true
                )
            }
            filteredList.addAll(filtered)
        }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filtered = personList.filter {
                    it.name.contains(
                        constraint.toString(),
                        ignoreCase = true
                    ) || it.status.contains(constraint.toString(), ignoreCase = true)
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList.clear()
                filteredList.addAll(results?.values as List<Person>)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItem(position: Int): Person {
        return filteredList[position]
    }

    override fun submitList(list: List<Person>?) {
        super.submitList(list)
        updateFavouritePersons()
    }

    private fun stringSetToPersonList(stringSet: Set<String>): MutableList<Person> {
        return stringSet.map { name -> personList.find { it.name == name }
        }.filterNotNull().toMutableList()
    }

    private fun updateFavouritePersons() {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        favouritePersons =
            stringSetToPersonList(
                prefs.getStringSet("FavouritePersons", mutableSetOf()) ?: mutableSetOf()
            )
    }
}
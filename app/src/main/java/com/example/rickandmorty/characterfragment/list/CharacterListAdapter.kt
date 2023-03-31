package com.example.rickandmorty.characterfragment.list

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


class CharacterListAdapter(private val onItemClick: (Person) -> Unit) :
    ListAdapter<Person, CharacterListAdapter.CharacterViewHolder>(
        CharacterDiffCallback()
    ), Filterable {

    private var personList = mutableListOf<Person>()
    private var filteredList = mutableListOf<Person>()

    inner class CharacterViewHolder(private val binding: ListItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person) {
            binding.characterName.text = person.name
            binding.root.setOnClickListener { onItemClick(person) }
            Glide.with(itemView)
                .load(person.image)
                .placeholder(R.drawable.placeholder)
                .into(binding.characterImage)
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
}
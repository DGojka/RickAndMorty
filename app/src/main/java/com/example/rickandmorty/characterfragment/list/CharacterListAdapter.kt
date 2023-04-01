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
import com.example.rickandmorty.characterfragment.list.helpers.getFavouritePersons
import com.example.rickandmorty.characterfragment.list.helpers.listfilter.FilterPersons
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
    private var filterPersons: FilterPersons = FilterPersons(mutableListOf(), context)

    inner class CharacterViewHolder(private val binding: ListItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person) {
            binding.characterName.text = person.name
            binding.root.setOnClickListener { onItemClick(person) }
            Glide.with(itemView)
                .load(person.image)
                .placeholder(R.drawable.placeholder)
                .into(binding.characterImage)

            val isFavourite = getFavouritePersons(context, personList).contains(person)
            binding.favouriteButton.setImageResource(if (isFavourite) R.drawable.ic_fav else R.drawable.ic_fav_border)
            binding.favouriteButton.setOnClickListener {
                val editor =
                    itemView.context.getSharedPreferences("asd", Context.MODE_PRIVATE).edit()
                val favPersons = getFavouritePersons(context, personList)
                if (isFavourite) {
                    favPersons.remove(person)
                    binding.favouriteButton.setImageResource(R.drawable.ic_fav_border)
                } else {
                    favPersons.add(person)
                    binding.favouriteButton.setImageResource(R.drawable.ic_fav)
                }

                editor.putStringSet(
                    "FavouritePersons",
                    favPersons.map { it.name }.toSet()
                )
                editor.apply()
                /*         filteredList.clear()
                         filteredList.addAll(filterPersons.filter(""))
                         notifyDataSetChanged()*/
            }

        }
    }

    fun setData(data: List<Person>) {
        personList.clear()
        personList.addAll(data)
        filterPersons = FilterPersons(personList, context)
        filteredList.addAll(filterPersons.filter(""))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view =
            ListItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        holder.bind(character)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                filterResults.values = filterPersons.filter(constraint)

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList.clear()
                filteredList = results?.values as MutableList<Person>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItem(position: Int): Person {
        return filteredList[position]
    }

    fun applyFilters() {
        filteredList.clear()
        filteredList.addAll(filterPersons.filter(""))
        notifyDataSetChanged()
    }

    companion object {
        const val FAVOURITES = "favourites"
        const val ALIVE = "Alive"
        const val DEAD = "Dead"
        const val UNKNOWN = "unknown"
    }
}
package com.example.rickandmorty.characterfragment.list

import androidx.recyclerview.widget.DiffUtil
import com.example.rickandmorty.network.Person

class CharacterDiffCallback : DiffUtil.ItemCallback<Person>() {

    override fun areItemsTheSame(
        oldItem: Person,
        newItem: Person
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Person,
        newItem: Person
    ): Boolean {
        return oldItem == newItem
    }
}

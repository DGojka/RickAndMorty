package com.example.rickandmorty.personsfragment.list

import androidx.recyclerview.widget.DiffUtil
import com.example.rickandmorty.repository.Person

class PersonDiffCallback : DiffUtil.ItemCallback<Person>() {

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

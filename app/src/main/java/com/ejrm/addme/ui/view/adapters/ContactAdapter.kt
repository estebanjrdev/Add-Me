package com.ejrm.addme.ui.view.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ejrm.addme.R
import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.databinding.CardItemRecyclerBinding


class ContactAdapter(
    private var contactList: List<Contact> = emptyList(),
    private val onClickListener: (Contact) -> Unit
) :
    RecyclerView.Adapter<ContactViewHolder>() {

    fun updateList(list: List<Contact>) {
        contactList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ContactViewHolder(layoutInflater.inflate(R.layout.card_item_recycler, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position], onClickListener)
    }

    override fun getItemCount(): Int = contactList.size
}
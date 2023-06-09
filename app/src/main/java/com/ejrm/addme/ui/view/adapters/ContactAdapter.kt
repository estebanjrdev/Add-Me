package com.ejrm.addme.ui.view.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ejrm.addme.R
import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.databinding.CardItemRecyclerBinding


class ContactAdapter(
    private val onClickListener: (Contact) -> Unit,
    private val onClickWhatsapp: (String) -> Unit,
    private val onClickInstagram: (String) -> Unit,
    private val onClickFacebook: (String) -> Unit
) :
    RecyclerView.Adapter<ContactViewHolder>() {
    private var contactList = listOf<Contact>()
    fun updateList(newList: List<Contact>) {
        val contactDiff = ContactDiffUtil(contactList,newList)
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(contactDiff)
        contactList = newList
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ContactViewHolder(layoutInflater.inflate(R.layout.card_item_recycler, parent, false))
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position],onClickListener,onClickWhatsapp,onClickInstagram,onClickFacebook)
    }

    override fun getItemCount(): Int = contactList.size
}
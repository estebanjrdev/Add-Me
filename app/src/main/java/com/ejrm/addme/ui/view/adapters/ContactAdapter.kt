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
    private var contactList: List<Contact>,
    private val onClickListener: ContactAdapterListener
) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    interface ContactAdapterListener {
        fun onContactSelected(contact: Contact)
    }

    fun setContacList(contactList: List<Contact>) {
        this.contactList = contactList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.card_item_recycler, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contactList[position], onClickListener)
    }

    fun updateStations(contactList: List<Contact>) {
        this.contactList = contactList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = contactList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emisoraCardBinding = CardItemRecyclerBinding.bind(view)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(contact: Contact, onClickListener: ContactAdapterListener) {
            emisoraCardBinding.txtName.text = contact.name
            emisoraCardBinding.btnWhatsApp.tooltipText = contact.phone
            emisoraCardBinding.btnInstagram.tooltipText = contact.instagram
            emisoraCardBinding.btnFacebook.tooltipText = contact.facebook
            itemView.setOnClickListener { onClickListener.onContactSelected(contact) }
        }
    }
}
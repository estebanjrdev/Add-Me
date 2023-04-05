package com.ejrm.addme.ui.view.adapters

import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.databinding.CardItemRecyclerBinding

class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val emisoraCardBinding = CardItemRecyclerBinding.bind(view)

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(contact: Contact, onClickListener: (Contact) -> Unit) {
        emisoraCardBinding.txtName.text = contact.name
        emisoraCardBinding.btnWhatsApp.setOnClickListener { Toast.makeText(itemView.context,contact.phone,Toast.LENGTH_SHORT).show() }
        emisoraCardBinding.btnInstagram.setOnClickListener { Toast.makeText(itemView.context,contact.instagram,Toast.LENGTH_SHORT).show() }
        emisoraCardBinding.btnFacebook.setOnClickListener { Toast.makeText(itemView.context,contact.facebook,Toast.LENGTH_SHORT).show() }
        itemView.setOnClickListener { onClickListener(contact) }
    }
}
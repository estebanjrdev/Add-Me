package com.ejrm.addme.ui.view.adapters

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.databinding.CardItemRecyclerBinding
import java.io.ByteArrayInputStream

class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val emisoraCardBinding = CardItemRecyclerBinding.bind(view)


    fun bind(
        contact: Contact,
        onClickListener: (Contact) -> Unit,
        onClickWhatsapp: (String) -> Unit,
        onClickInstagram: (String) -> Unit,
        onClickFacebook: (String) -> Unit
    ) {
        emisoraCardBinding.txtName.text = contact.name
        emisoraCardBinding.CodeCountry.setCountryForPhoneCode(contact.country.toInt())
        emisoraCardBinding.btnWhatsApp.setOnClickListener { onClickWhatsapp(contact.phone) }
        emisoraCardBinding.btnInstagram.setOnClickListener {
            if (contact.instagram == "") {
                emisoraCardBinding.btnInstagram.isEnabled = false
                Toast.makeText(itemView.context, "Sin Instagram", Toast.LENGTH_SHORT).show()
            } else {
                onClickInstagram(contact.instagram)
            }
        }
        emisoraCardBinding.btnFacebook.setOnClickListener {
            if (contact.facebook == "") {
                emisoraCardBinding.btnFacebook.isEnabled = false
                Toast.makeText(itemView.context, "Sin Facebbok", Toast.LENGTH_SHORT).show()
            } else {
                onClickFacebook(contact.facebook)
            }
        }
        itemView.setOnClickListener { onClickListener(contact) }
    }
}
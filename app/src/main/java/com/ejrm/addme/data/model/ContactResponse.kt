package com.ejrm.addme.data.model

data class ContactResponse(
    val success: Boolean,
    val message: String,
    val id_contacto: String,
    val name: String,
    val country: String,
    val phone: String,
    val instagram: String,
    val facebook: String,
    val password: String
)

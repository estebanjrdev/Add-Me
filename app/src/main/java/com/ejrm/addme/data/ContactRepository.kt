package com.ejrm.addme.data

import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.network.ContactService
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val api: ContactService
) {
    suspend fun getAllContact(): List<Contact> {
        return api.getContact()
    }
}
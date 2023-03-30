package com.ejrm.addme.data

import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.network.ContactService
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val apiService: ContactService
) {
    suspend fun getAllContact(): List<Contact> {
        return apiService.getContact()
    }
    suspend fun getSearchContact(search: String): List<Contact> {
        return apiService.getSearch(search)
    }
}
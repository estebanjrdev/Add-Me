package com.ejrm.addme.data.network

import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.model.ContactResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections.emptyList
import javax.inject.Inject

class ContactService @Inject constructor(private val api: ContactApiClient) {

    suspend fun getContact(): List<Contact> {
        return withContext(Dispatchers.IO) {
            val response = api.getAllContact()
            response.body() ?: emptyList()
        }
    }
    suspend fun getSearch(search: String): List<Contact> {
        return withContext(Dispatchers.IO) {
            val response = api.searchContact(search)
            response.body() ?: emptyList()
        }
    }
    suspend fun addContact(image:String, name: String, phone: String, instagram: String, facebook: String): List<ContactResponse> {
        return withContext(Dispatchers.IO) {
            val response = api.addContact(image, name,phone,instagram,facebook)
            response.body() ?: emptyList()
        }
    }
}
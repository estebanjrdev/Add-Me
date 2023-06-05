package com.ejrm.addme.data

import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.model.ContactResponse
import com.ejrm.addme.data.network.ContactService
import okhttp3.MultipartBody
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
    suspend fun addContact(name: String, country:String, phone: String, instagram: String, facebook: String): List<ContactResponse> {
        return apiService.addContact(name,country,phone,instagram,facebook)
    }
}
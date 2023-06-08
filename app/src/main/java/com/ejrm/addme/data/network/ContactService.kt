package com.ejrm.addme.data.network

import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.model.ContactResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
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
    suspend fun addContact(name: String, country:String, phone: String, instagram: String, facebook: String, password: String): List<ContactResponse> {
        return withContext(Dispatchers.IO) {
            val response = api.addContact(name,country,phone,instagram,facebook,password)
            response.body() ?: emptyList()
        }
    }

    suspend fun loginService(phone: String, password: String): List<ContactResponse> {
        return withContext(Dispatchers.IO) {
            val response = api.loginApi(phone,password)
            response.body() ?: emptyList()
        }
    }
}
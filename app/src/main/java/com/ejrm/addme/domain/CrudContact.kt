package com.ejrm.addme.domain

import com.ejrm.addme.data.ContactRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class CrudContact @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke() = repository.getAllContact()
    suspend fun invokeSearch(search: String) = repository.getSearchContact(search)
    suspend fun invokeAdd(name:String, country:String, phone: String, instagram: String, facebook: String) = repository.addContact(name,country,phone,instagram,facebook)
}
package com.ejrm.addme.domain

import com.ejrm.addme.data.ContactRepository
import com.ejrm.addme.data.model.Contact
import javax.inject.Inject

class CrudContact @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke() = repository.getAllContact()
    suspend fun invokeSearch(search: String) = repository.getSearchContact(search)
    suspend fun invokeAdd(image:String, name: String, phone: String, instagram: String, facebook: String) = repository.addContact(image,name,phone,instagram,facebook)
}
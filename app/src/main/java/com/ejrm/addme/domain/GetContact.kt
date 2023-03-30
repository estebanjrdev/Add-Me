package com.ejrm.addme.domain

import com.ejrm.addme.data.ContactRepository
import javax.inject.Inject

class GetContact @Inject constructor(private val repository: ContactRepository) {
    suspend operator fun invoke() = repository.getAllContact()
    suspend fun invokeSearch(search: String) = repository.getSearchContact(search)
}
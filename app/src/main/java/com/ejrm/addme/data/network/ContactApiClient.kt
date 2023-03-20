package com.ejrm.addme.data.network

import com.ejrm.addme.data.model.Contact
import retrofit2.Response
import retrofit2.http.GET


interface ContactApiClient {
    @GET("/.json")
    suspend fun getAllContact(): Response<List<Contact>>
}
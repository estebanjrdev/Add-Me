package com.ejrm.addme.data.network

import com.ejrm.addme.data.model.Contact
import retrofit2.Response
import retrofit2.http.*


interface ContactApiClient {
    @GET("/")
    suspend fun getAllContact(): Response<List<Contact>>

    @GET("/search")
    suspend fun searchContact(@Query("search") search: String): Response<List<Contact>>

    @POST("/add")
    suspend fun addContact(@Body contact: Contact): Response<Contact>
}
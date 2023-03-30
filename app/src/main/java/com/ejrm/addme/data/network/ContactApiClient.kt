package com.ejrm.addme.data.network

import com.ejrm.addme.data.model.Contact
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ContactApiClient {
    @GET("/")
    suspend fun getAllContact(): Response<List<Contact>>

    @GET("/search.php")
    suspend fun searchContact(@Query("search") search: String): Response<List<Contact>>
}
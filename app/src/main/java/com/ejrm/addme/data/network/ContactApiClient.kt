package com.ejrm.addme.data.network

import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.model.ContactResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ContactApiClient {
    @GET("/")
    suspend fun getAllContact(): Response<List<Contact>>

    @GET("/search")
    suspend fun searchContact(@Query("search") search: String): Response<List<Contact>>

    @FormUrlEncoded
    @POST("/add")
    suspend fun addContact(
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("instagram") instagram: String,
        @Field("facebok") facebook: String
    ): Response<List<ContactResponse>>
}
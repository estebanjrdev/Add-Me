package com.ejrm.addme.data.network

import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.model.ContactResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ContactApiClient {
    @GET("/api/")
    suspend fun getAllContact(): Response<List<Contact>>

    @GET("/api/search/index.php")
    suspend fun searchContact(@Query("search") search: String): Response<List<Contact>>

    @FormUrlEncoded
    @POST("/api/add/index.php")
    suspend fun addContact(
        @Field("image") image: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("instagram") instagram: String,
        @Field("facebook") facebook: String
    ): Response<List<ContactResponse>>
}
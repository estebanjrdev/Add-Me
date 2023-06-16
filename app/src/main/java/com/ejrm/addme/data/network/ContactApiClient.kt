package com.ejrm.addme.data.network

import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.model.ContactResponse
import okhttp3.MultipartBody
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
        @Field("name") name: String,
        @Field("country") country: String,
        @Field("phone") phone: String,
        @Field("instagram") instagram: String,
        @Field("facebook") facebook: String,
        @Field("password") password: String
    ): Response<List<ContactResponse>>

    @FormUrlEncoded
    @POST("/api/update/index.php")
    suspend fun updateContactApi(
        @Field("id_contacto") id_contacto: String,
        @Field("name") name: String,
        @Field("country") country: String,
        @Field("phone") phone: String,
        @Field("instagram") instagram: String,
        @Field("facebook") facebook: String,
        @Field("password") password: String
    ): Response<List<ContactResponse>>

    @FormUrlEncoded
    @POST("/api/login/index.php")
    suspend fun loginApi(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Response<List<ContactResponse>>
}
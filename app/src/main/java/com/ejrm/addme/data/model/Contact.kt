package com.ejrm.addme.data.model

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("name") var name: String,
    @SerializedName("country") var country: String,
    @SerializedName("phone") var phone: String,
    @SerializedName("instagram") var instagram: String,
    @SerializedName("facebook") var facebook: String,
    @SerializedName("password") var password: String
)
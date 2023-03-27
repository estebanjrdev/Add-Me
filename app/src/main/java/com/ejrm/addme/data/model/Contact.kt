package com.ejrm.addme.data.model

import com.google.gson.annotations.SerializedName

data class Contact (
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("instagram") val instagram: String,
    @SerializedName("facebook") val facebook: String
        )
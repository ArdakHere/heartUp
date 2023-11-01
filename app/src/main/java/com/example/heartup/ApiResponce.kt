package com.example.heartup

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("prediction")
    val prediction: String
)
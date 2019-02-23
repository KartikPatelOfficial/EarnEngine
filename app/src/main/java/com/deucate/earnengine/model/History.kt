package com.deucate.earnengine.model

data class History(
    val id: String,
    val mobileNumber: String,
    var status: Boolean,
    val Amount: Long
)
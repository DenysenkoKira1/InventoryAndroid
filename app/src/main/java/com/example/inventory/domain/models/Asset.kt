package com.example.inventory.domain.models


data class Asset(
    val invNumber: String = "",
    val srnNumber: String = "",
    val name: String = "",
    val location: String = "",
    val state: Boolean = false,
    val id: Long = 0,
    val quantity: Int = 1,
    val unit: String = "EA",
    val conditionCode: String = "",
    val comment: String = "",
    val photoFileName: String? = null
)
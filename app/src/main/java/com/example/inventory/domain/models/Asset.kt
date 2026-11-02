package com.example.inventory.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Asset(
    val invNumber: String = "",
    val srnNumber: String = "",
    val name: String = "",
    val location: String = "",
    val state: Boolean = false,
) : Parcelable

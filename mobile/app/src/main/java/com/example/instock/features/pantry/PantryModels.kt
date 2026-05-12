package com.example.instock.features.pantry

data class PantryItem(
    val id: Long,
    val name: String,
    val createdAt: String? = null
)

data class PantryListResponse(
    val success: Boolean,
    val message: String,
    val data: List<PantryItem>
)

data class PantryAddRequest(
    val name: String
)

data class PantrySingleResponse(
    val success: Boolean,
    val message: String,
    val data: PantryItem?
)

package com.example.stores

import androidx.room.Entity

@Entity(tableName = "StoreEntity")
data class StoreEntity (
  var id: Long = 0,
    var name: String,
    var phone: String = "",
    var website: String = "",
    var isFavorite: Boolean = false)
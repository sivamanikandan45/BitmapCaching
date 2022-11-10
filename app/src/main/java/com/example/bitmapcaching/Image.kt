package com.example.bitmapcaching

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image(@PrimaryKey val imageId:Int, val imageUrl:String)
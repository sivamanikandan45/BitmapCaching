package com.example.bitmapcaching

import androidx.room.Dao
import androidx.room.Index
import androidx.room.Insert

@Dao
interface ImageDao {
    @Insert
    fun insertList(list:List<Image>)
}
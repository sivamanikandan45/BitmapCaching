package com.example.bitmapcaching

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Image::class], version = 1)
abstract class ImageDb :RoomDatabase(){
    abstract fun getImageDao():ImageDao
    companion object{
        @Volatile
        private var instance:ImageDb?=null
        fun getDB(context: Context?):ImageDb{
            val temp= instance
            if(temp != null){
                return temp
            }
            synchronized(this){
                println("Created")
                val newInstance= Room.databaseBuilder(context!!, ImageDb::class.java, "ProductDatabase").build()
                instance=newInstance
                return newInstance
            }
        }
    }
}
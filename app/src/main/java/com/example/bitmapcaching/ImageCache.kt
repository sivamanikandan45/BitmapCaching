package com.example.bitmapcaching

import android.graphics.Bitmap
import android.util.LruCache

object ImageCache {

    fun getBitmapFromMemCache(imageKey: String): Bitmap? {
        return memoryCache.get(imageKey)
    }

    fun addBitmapToCache(key: String, bitmap: Bitmap) {
        // Add to memory cache as before
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap)
        }
        // Also add to disk cache
        /*synchronized(this) {
            memoryCache.apply {
                //if (!containsKey(key)) {
                    put(key, bitmap)
                //}
            }
        }*/
    }

    /*private fun containsKey(key: String): Boolean{
        for(a in 0 until memoryCache.size()){
            if(memoryCache.get(ke))
                return true
        }
        return false
    }*/

    val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

    // Use 1/8th of the available memory for this memory cache.
    val cacheSize = maxMemory / 5

    val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }


}
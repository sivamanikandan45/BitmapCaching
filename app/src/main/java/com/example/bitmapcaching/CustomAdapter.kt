package com.example.bitmapcaching

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class CustomAdapter:RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private lateinit var list:List<Image>

    fun setData(list:List<Image>){
        println("SetData called with $list")
        this.list=list
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imageView:ImageView=view.findViewById(R.id.imageView)
        fun bind(image:Image) {
            var bitmapValue: Bitmap? = null
//            fun loadBitmap(resId: Int, imageView: ImageView) {
                val imageKey: String = image.imageId.toString()

                val bitmap: Bitmap? = ImageCache.getBitmapFromMemCache(imageKey)?.also {
                    println("Fetched from cache at $adapterPosition")
                    imageView.setImageBitmap(it)
                } ?: run {
                    GlobalScope.launch {
                        val job = launch(Dispatchers.IO) {
                            val imageUrl = URL(list[position].imageUrl)
                            //println("image is ${list[position]}")
                            bitmapValue = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
                            withContext(Dispatchers.Main) {
                                imageView.setImageBitmap(bitmapValue)
                                ImageCache.addBitmapToCache(image.imageId.toString(),bitmapValue!!)
                                //ImageCache.memoryCache.put()
                                println("Image set using coroutine at $adapterPosition")
                            }
                        }
                        job.join()
                    }
                    null
                }
//            }


            //imageView.setImageBitmap(bitmapValue)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_image,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.imageView.setImageBitmap()
        holder.bind(list[position])
    }



    override fun getItemCount(): Int {
        return list.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.imageView.setImageBitmap(null)
        super.onViewRecycled(holder)
    }

}
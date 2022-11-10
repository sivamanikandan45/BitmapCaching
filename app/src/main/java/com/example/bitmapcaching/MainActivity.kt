package com.example.bitmapcaching

import android.graphics.Movie
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var adapter:CustomAdapter
    private lateinit var recyclerView:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView=findViewById<RecyclerView>(R.id.recycler)
        adapter=CustomAdapter()

        GlobalScope.launch {
            var list= listOf<Image>()
            val job=launch {
                loadData()
            }
            job.join()
            withContext(Dispatchers.Main){
                /*adapter.setData(listOf<Image>())
                recyclerView.adapter=adapter
                recyclerView.layoutManager=GridLayoutManager(this@MainActivity,2)*/
            }
        }
    }

    private suspend fun loadData(){
        withContext(Dispatchers.IO){
            val url="https://api.themoviedb.org/3/trending/movie/day?api_key=08e4a6a03c5c292c1893f7127324e5f3"
            val connection= URL(url).openConnection() as HttpURLConnection
            val reader= BufferedReader(InputStreamReader(connection.inputStream))
            var response=""
            var line=reader.readLine()
            while(line!=null){
                response+=line
                line=reader.readLine()
            }

            if(response.isNotEmpty()){
                val jsonObject= JSONTokener(response).nextValue() as JSONObject
                val jsonArray=jsonObject.getJSONArray("results")
                var list:MutableList<Image> = mutableListOf()
                for(i in 0 until jsonArray.length()){
                    val link="https://image.tmdb.org/t/p/w500/"
                    val id=jsonArray.getJSONObject(i).getString("id").toInt()
                    val imgURI=jsonArray.getJSONObject(i).getString("poster_path")
                    /*val bgURI=jsonArray.getJSONObject(i).getString("backdrop_path")
                    val overview=jsonArray.getJSONObject(i).getString("overview")
                    val title=jsonArray.getJSONObject(i).getString("original_title")
                    val popularity=jsonArray.getJSONObject(i).getString("popularity")*/
                    val movie=Image(id,(link+imgURI.toString()))
//                        Movie(id,title,(link+imgURI.toString()),overview,popularity.toDouble(),(link+bgURI.toString()))
                    //movieListViewModel.movieList.add(movie)
                    //dbInstance.getDao().addMovie(movie)
                    list.add(movie)
                    //println(list)
                }
                println(list)
                //insertMovieList(list)
                //val viewModel=ViewModelProvider(this@MainActivity)[ImageViewModel::class.java]
                withContext(Dispatchers.Main){
                    adapter= CustomAdapter()
                    adapter.setData(list)
                    //adapter.notifyDataSetChanged()
                    recyclerView.adapter=adapter
                    recyclerView.layoutManager=GridLayoutManager(this@MainActivity,2)
                }
            }
        }
    }

    private fun insertMovieList(list: MutableList<Image>) {
        val dao=ImageDb.getDB(this).getImageDao()
        dao.insertList(list)
    }
}
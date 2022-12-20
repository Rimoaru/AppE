package com.appe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.appe.db_room.SurahDB
import com.appe.retrofit.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val db by lazy {
        SurahDB(this)
    }
    val surahAdapter = MainAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set theme Normal
        setTheme(R.style.Theme_AppE)
        setContentView(R.layout.activity_main)

        // Pemanggilan Adapter
        findViewById<RecyclerView>(R.id.rvSurah).adapter = surahAdapter

        // SwipeToRefresh
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.blue_500)
        )
        swipeRefresh.setOnRefreshListener {
            updateData()
        }

    }

    override fun onStart() {
        super.onStart()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val emptyText = findViewById<TextView>(R.id.tvEmpty)

        lifecycleScope.launchWhenStarted {
            progressBar.isVisible = true
            try {
                val surah = db.surahDao().getSurah()
                if(surah.isEmpty()){
                    emptyText.isVisible = true
                }else{
                    surahAdapter.setData( surah )
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Ade Error pulak wak.. ni ha -> $e")
                progressBar.isVisible = false
                emptyText.isVisible = true
            }
            progressBar.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.tbRefresh -> updateData()
        }
        return true
    }

    private fun updateData(){
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        swipeRefresh.isRefreshing = true
        lifecycleScope.launchWhenStarted {
            val response = try {
                RetrofitInstance.api.getSurah()
            } catch (e: IOException) {
                val surah = RetrofitInstance.api.getSurah()
                Log.e("MainActivity", surah.body()!!.toString())
                Toast.makeText(this@MainActivity, "Terjadi Error", Toast.LENGTH_LONG).show()
                swipeRefresh.isRefreshing = false
                return@launchWhenStarted
            } catch (e: HttpException) {
                Log.e("MainActivity", "HttpException, Salah Link dak wak??, tak ade response")
                Toast.makeText(this@MainActivity, "Terjadi Error", Toast.LENGTH_LONG).show()
                swipeRefresh.isRefreshing = false
                return@launchWhenStarted
            }

            if (response.isSuccessful && response.body() != null){
                val surahRoomDB = db.surahDao().getSurah()
                if (surahRoomDB.isEmpty()){
                    response.body()!!.forEach {
                            item ->
                        db.surahDao().insertSurah(item)
                    }
                    refreshContent()
                    Toast.makeText(this@MainActivity, "Data Berhasil di Tambahkan", Toast.LENGTH_LONG).show()
                }else{
                    db.surahDao().deleteAllSurah()
                    response.body()!!.forEach {
                            item ->
                        db.surahDao().insertSurah(item)
                    }
                    refreshContent()
                    Toast.makeText(this@MainActivity, "Data Berhasil di Update", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.e("MainActivity", "Response gagal pulak wak.. Payah kadang")
            }
            swipeRefresh.isRefreshing = false
        }
    }

    private fun refreshContent(){
        val restartIntent = Intent(this@MainActivity, MainActivity::class.java)
        startActivity(restartIntent)
        finish()
    }

//    private fun listSurah() {
//        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
//        val myAdapter = MainAdapter()
//
//        findViewById<RecyclerView>(R.id.rvSurah).adapter = myAdapter
//
//        lifecycleScope.launchWhenCreated {
//            progressBar.isVisible = true
//            try {
//                val surah = db.surahDao().getSurah()
//                myAdapter.surah = surah
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Ade Error pulak wak.. ni ha -> $e")
//                progressBar.isVisible = false
//                return@launchWhenCreated
//            }
//            progressBar.isVisible = false
//        }
//    }
//

}
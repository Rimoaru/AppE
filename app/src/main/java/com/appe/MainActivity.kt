package com.appe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
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
import com.appe.ToolsObject.updateData
import com.appe.db_room.BukuDB
import com.appe.retrofit.RetrofitInstance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val db by lazy {
        BukuDB(this)
    }
    val bukuAdapter = MainAdapter(this@MainActivity, arrayListOf())
    val layout = R.layout.activity_main

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        // Pemanggilan Adapter
        findViewById<RecyclerView>(R.id.rvBuku).adapter = bukuAdapter

        // SwipeToRefresh
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.blue_500)
        )
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            updateData(this@MainActivity, layout)
        }

    }

    override fun onStart() {
        super.onStart()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val emptyText = findViewById<TextView>(R.id.tvEmpty)

        lifecycleScope.launchWhenStarted {
            progressBar.isVisible = true
            try {
                val kelas = intent.getStringExtra("extraKelas")
                val kategori = intent.getStringExtra("extraKategori")
                val buku = db.bukuDao().getBuku()
                val filterBuku = buku.filter { it.kelas == kelas && it.kategori == kategori }
                if(buku.isEmpty()){
                    emptyText.isVisible = true
                }else{
                    bukuAdapter.setData( filterBuku )
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
    @OptIn(DelicateCoroutinesApi::class)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.tbRefresh -> {
                val swipeReload = findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
                swipeReload.isRefreshing = true
                updateData(this@MainActivity, layout)
            }
        }
        return true
    }
}
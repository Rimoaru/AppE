package com.appe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.appe.ToolsObject.updateData
import com.appe.db_room.BukuDB
import kotlinx.coroutines.DelicateCoroutinesApi

class KategoriActivity : AppCompatActivity() {
    private val db by lazy {
        BukuDB(this)
    }
    val kategoriAdapter = KategoriAdapter(this@KategoriActivity, arrayListOf())
    val layout = R.layout.activity_kategori

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        // Pemanggilan Adapter
        findViewById<RecyclerView>(R.id.rvKategori).adapter = kategoriAdapter

        // SwipeToRefresh
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.blue_500)
        )
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            updateData(this@KategoriActivity, layout)
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
                val buku = db.bukuDao().getBuku()
                val kategori = buku.filter { it.kelas == kelas }
                if(buku.isEmpty()){
                    emptyText.isVisible = true
                }else{
                    kategoriAdapter.setData( kategori )
                }
            } catch (e: Exception) {
                Log.e("KategoriActivity", "Ade Error pulak wak.. ni ha -> $e")
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
                updateData(this@KategoriActivity, layout)
            }
        }
        return true
    }
}
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

class KelasActivity : AppCompatActivity() {
    val db by lazy {
        BukuDB(this@KelasActivity)
    }
    val kelasAdapter = KelasAdapter(this@KelasActivity, arrayListOf())
    val layout = R.layout.activity_kelas

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set normal theme
        setTheme(R.style.Theme_AppE)
        setContentView(layout)

        // Pemanggilan Adapter
        findViewById<RecyclerView>(R.id.rvKelas).adapter = kelasAdapter

        // SwipeToRefresh
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.blue_500)
        )
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            updateData(this@KelasActivity, layout)
        }

    }

    override fun onStart() {
        super.onStart()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val emptyText = findViewById<TextView>(R.id.tvEmpty)

        lifecycleScope.launchWhenStarted {
            progressBar.isVisible = true
            try {
                val buku = db.bukuDao().getBuku()
                if(buku.isEmpty()){
                    emptyText.isVisible = true
                }else{
                    kelasAdapter.setData( buku )
                }
            } catch (e: Exception) {
                Log.e("KelasActivity", "Ade Error pulak wak.. ni ha -> $e")
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
                updateData(this@KelasActivity, layout)
            }
        }
        return true
    }



}
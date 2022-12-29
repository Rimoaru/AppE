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
import com.appe.db_room.BukuDB
import com.appe.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class KategoriActivity : AppCompatActivity() {
    private val db by lazy {
        BukuDB(this)
    }
    val kategoriAdapter = KategoriAdapter(this@KategoriActivity, arrayListOf())
    val linkFile = "https://web-appe.000webhostapp.com/assets/files/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kategori)

        // Pemanggilan Adapter
        findViewById<RecyclerView>(R.id.rvKategori).adapter = kategoriAdapter

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
                RetrofitInstance.api.getBuku()
            } catch (e: IOException) {
                val buku = RetrofitInstance.api.getBuku()
                Log.e("KategoriActivity", buku.body()!!.toString())
                Toast.makeText(this@KategoriActivity, "Terjadi Error", Toast.LENGTH_LONG).show()
                swipeRefresh.isRefreshing = false
                return@launchWhenStarted
            } catch (e: HttpException) {
                Log.e("KategoriActivity", "HttpException, Salah Link dak wak??, tak ade response")
                Toast.makeText(this@KategoriActivity, "Terjadi Error", Toast.LENGTH_LONG).show()
                swipeRefresh.isRefreshing = false
                return@launchWhenStarted
            }

            if (response.isSuccessful && response.body() != null){
                val bukuRoomDB = db.bukuDao().getBuku()
                if (bukuRoomDB.isEmpty()){
                    response.body()!!.forEach {
                            item ->
                        db.bukuDao().insertBuku(item)

                        val fileName = item.file
                        val path = baseContext.filesDir
                        val file = File(path, fileName)
                        Log.e("KategoriActivity", "path directorinye wak : $path")
                        val link = linkFile + item.file
                        lifecycleScope.async(Dispatchers.IO){
                            downloadBuku(link, file)
                        }.await()
                    }
                    refreshContent()
                    Toast.makeText(this@KategoriActivity, "Data Berhasil di Tambahkan", Toast.LENGTH_LONG).show()
                }else{
                    db.bukuDao().deleteAllBuku()
                    val path = baseContext.filesDir
                    deleteAllBuku(path)
                    response.body()!!.forEach {
                            item ->
                        db.bukuDao().insertBuku(item)

                        val fileName = item.file
                        val file = File(path, fileName)
                        Log.e("KategoriActivity", "path directorinye wak : $path")
                        val link = linkFile + item.file
                        lifecycleScope.async(Dispatchers.IO){
                            downloadBuku(link, file)
                        }.await()
                    }
                    refreshContent()
                    Toast.makeText(this@KategoriActivity, "Data Berhasil di Update", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.e("KategoriActivity", "Response gagal pulak wak.. Payah kadang")
            }
            swipeRefresh.isRefreshing = false
        }
    }

    private fun refreshContent(){
        val intent = Intent(this@KategoriActivity, KelasActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(intent)
    }

    private fun downloadBuku(url: String, destinationFile: File): File {
        val link = URL(url)
        val connection = link.openConnection()
        connection.connect()
        val input = connection.getInputStream()
        val output = FileOutputStream(destinationFile)
        val buffer = ByteArray(1024)
        var read = input.read(buffer)
        while (read != -1) {
            output.write(buffer, 0, read)
            read = input.read(buffer)
        }
        output.close()
        input.close()

        return destinationFile
    }

    private fun deleteAllBuku(folder: File) {
        val files = folder.listFiles()
        if (files != null) {
            for (file in files) {
                file.delete()
            }
        }
    }
}
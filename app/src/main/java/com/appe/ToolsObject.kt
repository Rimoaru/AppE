package com.appe

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.appe.db_room.BukuDB
import com.appe.retrofit.RetrofitInstance
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL


@DelicateCoroutinesApi
object ToolsObject {

    private const val linkAPIFile = "https://web-appe.000webhostapp.com/assets/files/"

    fun updateData(context: Context, layoutId: Int){
        val db by lazy { BukuDB(context) }

        println("Memulai Proses Update Data wak!")
        val layout = LayoutInflater.from(context).inflate(layoutId, null)
        val path = context.applicationContext.filesDir
        val swipeRefresh = layout.findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)

        GlobalScope.launch {
            val response = try {
                RetrofitInstance.api.getBuku()
            } catch (e: IOException) {
                val buku = RetrofitInstance.api.getBuku()
                Log.e("$context", buku.body()!!.toString())
                showToast(context, "Terjadi Error!")
                swipeRefresh.isRefreshing = false
                return@launch
            } catch (e: HttpException) {
                Log.e("$context", "HttpException, Salah Link dak wak??, tak ade response")
                showToast(context, "Terjadi Error!")
                swipeRefresh.isRefreshing = false
                return@launch
            }

            if (response.isSuccessful && response.body() != null){
                println("Data Api tak Kosong wak, Amann :D")
                val bukuRoomDB = db.bukuDao().getBuku()
                if (bukuRoomDB.isEmpty()){
                    println("Data di Local Database Kosong wak!")
                    deleteAllBuku(path)
                    response.body()!!.forEach { item ->
                        db.bukuDao().insertBuku(item)

                        showToast(context, "Download ${item.judul_buku}")
                        val file = File(path, item.file)
                        val link = linkAPIFile + item.file
                        coroutineScope {
                            downloadBuku(link, file, item.file)
                        }
                    }
                    refreshContent(context)
                    showToast(context, "Data Berhasil di Tambahkan")
                }else{
                    val idBuku = db.bukuDao().getBuku().map { it.id }

                    // Mengapus Data yang tidak ada di API
                    val idAPI = response.body()!!.map{ it.id }
                    db.bukuDao().getBuku().forEach room@{ buku ->
                        if ( buku.id in idAPI){
                            return@room
                        } else {
                            println("Menghapus Data dan File: $buku gare tak ade kat API wak")
                            db.bukuDao().deleteBuku(buku)
                            File(path, buku.file).delete()
                        }
                    }

                    // Mengedit dan Menambahkan Data sesuai yang ada di API
                    response.body()!!.forEach outer@{ item ->
                        if (item.id in idBuku){
                            println("Data dengan ID ${item.id} ade kat Local Database wak!")
                            db.bukuDao().getBuku().forEach inner@{ buku ->
                                // Cek kalau data ada paperhanger atau tidak
                                if(item.id != buku.id){
                                    return@inner
                                }else{
                                    if (item.time == buku.time){
                                        println("Data ID: ${buku.id} Tidak ada perubahan Dari API")
                                        return@outer
                                    } else if (item.file == buku.file) {
                                        // Mengubah data Room tanpa mengubah file
                                        println("Data ID: ${buku.id} Mengalami Perubahan pada Local Database Room, tidak Dengan File")
                                        db.bukuDao().deleteBuku(buku)
                                        db.bukuDao().insertBuku(item)
                                    } else {
                                        // Mengubah Data Room dan File di Room dengan yang baru
                                        println("Data ID: ${buku.id} Mengalami Perubahan Total")
                                        // Menggantikan data dengan data yang baru
                                        db.bukuDao().deleteBuku(buku)
                                        db.bukuDao().insertBuku(item)

                                        // Delete file buku di room
                                        File(path, buku.file).delete()


                                        // Download file baru dari API
                                        showToast(context, "Download ${item.judul_buku}")
                                        val file = File(path, item.file)
                                        val link = linkAPIFile + item.file
                                        coroutineScope {
                                            downloadBuku(link, file, item.file)
                                        }
                                    }
                                }
                            }
                        } else {
                            println("Data Baru ni Wak!!: ${item.id}")
                            db.bukuDao().insertBuku(item)

                            showToast(context, "Download ${item.judul_buku}")
                            val file = File(path, item.file)
                            val link = linkAPIFile + item.file
                            coroutineScope {
                                downloadBuku(link, file, item.file)
                            }
                        }
                    }
                    refreshContent(context)
                    showToast(context, "Data Berhasil Diupdate!")

                }
            } else {
                Log.e("$context", "Response gagal pulak wak.. Payah kadang")
            }
            swipeRefresh.isRefreshing = false
        }
    }

    private fun refreshContent(context: Context){
        println("Memulai Ulang Aplikasi...")
        val intent = Intent(context, KelasActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }


    private fun downloadBuku(url: String, destinationFile: File, fileName: String): File {
        println("Lagi Download File: $fileName wak!")
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

        return destinationFile.also {
            println("Download Selesai...")
        }
    }

    private fun deleteAllBuku(folder: File) {
        println("Ni Lagi hapus semue file kat local database wak!")
        val files = folder.listFiles()
        if (files != null) {
            for (file in files) {
                file.delete()
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        handler.post(runnable)
    }
}
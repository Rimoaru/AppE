package com.appe

import android.content.Context
import android.content.Intent
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
                val bukuRoomDB = db.bukuDao().getBuku()
                if (bukuRoomDB.isEmpty()){
                    response.body()!!.forEach {
                            item ->
                        db.bukuDao().insertBuku(item)

                        val fileName = item.file
                        val file = File(path, fileName)
                        val link = linkAPIFile + item.file
                        withContext(Dispatchers.IO) {
                            downloadBuku(link, file)
                        }
                    }
                    refreshContent(context)
                    showToast(context, "Data Berhasil di Tambahkan")
                }else{
                    db.bukuDao().deleteAllBuku()
                    deleteAllBuku(path)
                    response.body()!!.forEach {
                            item ->
                        db.bukuDao().insertBuku(item)

                        val fileName = item.file
                        val file = File(path, fileName)
                        println("Lagi Download File: $fileName wak!!")
                        val link = linkAPIFile + item.file
                        withContext(Dispatchers.IO) {
                            downloadBuku(link, file)
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
        val intent = Intent(context, KelasActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
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

    private fun showToast(context: Context, message: String) {
        Looper.prepare()
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
        Looper.loop()
    }
}
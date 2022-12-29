package com.appe

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.appe.db_room.Buku

class KelasAdapter(private val context: Context,private val kelas: ArrayList<Buku>): RecyclerView.Adapter<KelasAdapter.KelasViewHolder>() {
    inner class KelasViewHolder(val view: View): RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasViewHolder{
        return KelasViewHolder(
            LayoutInflater.from(parent.context).inflate( R.layout.adapter_kelas, parent, false )
        )
    }

    override fun getItemCount() = kelas.size

    override fun onBindViewHolder(holder: KelasViewHolder, position: Int) {
        val buku = kelas[position]
        holder.view.findViewById<Button>(R.id.btnKelas).text = buku.kelas
        holder.view.findViewById<Button>(R.id.btnKelas).setOnClickListener {
            Intent(context, KategoriActivity::class.java).also {
                it.putExtra("extraKelas", buku.kelas)
                Log.e("KelasActivity", buku.kelas)
                context.startActivity(it)
            }
        }
    }

    fun setData(list: List<Buku>){
        kelas.clear()
        // Membuat agar tidak ada kelas yang duplicate saat ditampilkan
        val listKelas = list.distinctBy { it.kelas }
        kelas.addAll(listKelas.sortedBy { it.kelas })
        notifyDataSetChanged()
    }
}
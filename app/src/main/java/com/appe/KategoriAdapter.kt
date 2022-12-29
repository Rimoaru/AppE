package com.appe

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.appe.db_room.Buku

class KategoriAdapter(private val context: Context, private val kategori: ArrayList<Buku>): RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder>() {
    inner class KategoriViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriAdapter.KategoriViewHolder {
        return KategoriViewHolder(
            LayoutInflater.from(parent.context).inflate( R.layout.adapter_kategori, parent, false )
        )
    }

    override fun getItemCount() = kategori.size

    override fun onBindViewHolder(holder: KategoriViewHolder, position: Int) {
        val buku = kategori[position]
        holder.view.findViewById<Button>(R.id.btnKategori).text = buku.kategori
        holder.view.findViewById<Button>(R.id.btnKategori).setOnClickListener {
            Intent(context, MainActivity::class.java).also {
                it.putExtra("extraKelas", buku.kelas)
                it.putExtra("extraKategori", buku.kategori)
                context.startActivity(it)
            }
        }
    }

    fun setData(list: List<Buku>){
        kategori.clear()
        // Membuat agar tidak ada kategori yang duplicate saat ditampilkan
        val listKategori = list.distinctBy { it.kategori }
        kategori.addAll(listKategori.sortedBy { it.kategori })
        notifyDataSetChanged()
    }
}
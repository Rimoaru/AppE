package com.appe

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appe.db_room.Buku

class MainAdapter(private val context: Context, private val buku: ArrayList<Buku>): RecyclerView.Adapter<MainAdapter.BukuViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder{
        return BukuViewHolder(
            LayoutInflater.from(parent.context).inflate( R.layout.adapter_main, parent, false )
        )
    }

    override fun getItemCount() = buku.size

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int) {
        val buku = buku[position]
        holder.view.findViewById<TextView>(R.id.tvJudulBuku).text = buku.judul_buku
        holder.view.findViewById<ConstraintLayout>(R.id.rvItemBuku).setOnClickListener {
            Intent(context, PdfActivity::class.java).also {
                it.putExtra("extraFile", buku.file)
                context.startActivity(it)
            }
        }
    }

    inner class BukuViewHolder(val view: View): RecyclerView.ViewHolder(view)

    fun setData(list: List<Buku>){
        buku.clear()
        buku.addAll(list)
        notifyDataSetChanged()
    }
}
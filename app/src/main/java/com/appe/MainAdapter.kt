package com.appe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appe.db_room.Surah

class MainAdapter(private val surah: ArrayList<Surah>): RecyclerView.Adapter<MainAdapter.SurahViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder{
        return SurahViewHolder(
            LayoutInflater.from(parent.context).inflate( R.layout.adapter_main, parent, false )
        )
    }

    override fun getItemCount() = surah.size

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        val surah = surah[position]
        holder.view.findViewById<TextView>(R.id.tvNamaSurah).text = surah.nama_surah
        holder.view.findViewById<TextView>(R.id.tvJmlAyat).text = surah.jml_ayat.toString() + " Ayat"
    }

    inner class SurahViewHolder(val view: View): RecyclerView.ViewHolder(view)

    fun setData(list: List<Surah>){
        surah.clear()
        surah.addAll(list)
        notifyDataSetChanged()
    }
}
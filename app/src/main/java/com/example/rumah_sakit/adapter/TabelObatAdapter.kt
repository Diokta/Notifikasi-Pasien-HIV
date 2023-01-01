package com.example.rumah_sakit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Obat
import kotlinx.android.synthetic.main.tabel_obat_row_layout.view.*

class TabelObatAdapter(private var obatArrayList : ArrayList<Obat> ) :
    RecyclerView.Adapter<TabelObatAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListerner(listener : onItemClickListener){
        mListener = listener
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TabelObatAdapter.ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tabel_obat_row_layout, viewGroup, false)

        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(viewHolder: TabelObatAdapter.ViewHolder, i: Int) {
        viewHolder.tvNamaObat.text = obatArrayList[i].nama
        viewHolder.tvKategoriObat.text = obatArrayList[i].kategori
    }

    override fun getItemCount(): Int {
        return obatArrayList.size
    }

    inner class ViewHolder(itemView: View, listener : TabelObatAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvNamaObat : TextView = itemView.findViewById(R.id.tv_nama_obat)
        val tvKategoriObat : TextView = itemView.findViewById(R.id.tv_kategori_obat)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}
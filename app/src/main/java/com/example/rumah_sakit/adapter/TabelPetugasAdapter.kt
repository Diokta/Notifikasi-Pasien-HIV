package com.example.rumah_sakit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Petugas

class TabelPetugasAdapter(private var petugasArrayList : ArrayList<Petugas>) :
    RecyclerView.Adapter<TabelPetugasAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) : TabelPetugasAdapter.ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tabel_patugas_row_layout, viewGroup, false)

        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(viewHolder: TabelPetugasAdapter.ViewHolder, i: Int){
        viewHolder.tvNama.text = petugasArrayList[i].nama
        viewHolder.tvBagian.text = petugasArrayList[i].bagian

    }

    override fun getItemCount(): Int {
        return petugasArrayList.size
    }

    inner class ViewHolder(itemView: View, listener : TabelPetugasAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama)
        val tvBagian : TextView = itemView.findViewById(R.id.tv_bagian)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}
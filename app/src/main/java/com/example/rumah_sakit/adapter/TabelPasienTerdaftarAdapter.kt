package com.example.rumah_sakit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Pasien

class TabelPasienTerdaftarAdapter(private var pasienArrayList: ArrayList<Pasien>) :
    RecyclerView.Adapter<TabelPasienTerdaftarAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemCLickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) : ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tabel_pasien_terdaftar_row_layout, viewGroup, false)

        return ViewHolder(v, mListener)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int){
        viewHolder.tvNoRm.text = pasienArrayList[i].no_rm
        viewHolder.tvNama.text = pasienArrayList[i].nama
        if (pasienArrayList[i].no_rm == ""){
            viewHolder.imgTerdaftar.setVisibility(View.INVISIBLE)
        }

    }

    override fun getItemCount(): Int {
        return pasienArrayList.size
    }



    inner class ViewHolder(itemView: View, listener : onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvNoRm: TextView = itemView.findViewById(R.id.tv_no_rm)
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama)
        val imgTerdaftar : ImageView = itemView.findViewById(R.id.img_terdaftar)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}
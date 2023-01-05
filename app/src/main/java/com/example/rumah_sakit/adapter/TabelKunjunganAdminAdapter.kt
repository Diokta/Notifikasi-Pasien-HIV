package com.example.rumah_sakit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.DaftarKunjunganAdmin

class TabelKunjunganAdminAdapter(private var kunjunganArrayList: ArrayList<DaftarKunjunganAdmin>) :
    RecyclerView.Adapter<TabelKunjunganAdminAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvNama : TextView = itemView.findViewById(R.id.tv_nama)
        val tvTglKunjungan : TextView = itemView.findViewById(R.id.tv_tgl_kunjungan)
        val tvWaktuKunjungan : TextView = itemView.findViewById(R.id.tv_waktu_kunjungan)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tabel_kunjungan_admin_row_layout, viewGroup, false)

        return ViewHolder(v, mListener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvNama.text = kunjunganArrayList[position].pasien.nama
        holder.tvTglKunjungan.text = kunjunganArrayList[position].kunjungan.tgl_datang
        val waktu = kunjunganArrayList[position].kunjungan.waktu_datang
        if (waktu != null){
            holder.tvWaktuKunjungan.text = waktu
        }
    }

    override fun getItemCount(): Int {
        return kunjunganArrayList.size
    }
}
package com.example.rumah_sakit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Kunjungan

class TabelKunjunganAdapter(private var kunjunganArrayList : ArrayList<Kunjungan>) :
    RecyclerView.Adapter<TabelKunjunganAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tabel_kunjungan_pendamping_row_layout, viewGroup, false)

        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.tvTanggal.text = kunjunganArrayList[i].tgl_datang
        viewHolder.tvWaktu.text = kunjunganArrayList[i].waktu_datang
        viewHolder.tvKeterangan.text = kunjunganArrayList[i].keterangan
    }

    override fun getItemCount(): Int {
        return kunjunganArrayList.size
    }

    class ViewHolder(itemView : View, listener: TabelKunjunganAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvTanggal : TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvWaktu : TextView = itemView.findViewById(R.id.tv_waktu)
        val tvKeterangan : TextView = itemView.findViewById(R.id.tv_keterangan)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}
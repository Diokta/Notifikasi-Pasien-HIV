package com.example.rumah_sakit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Jadwal
import com.example.rumah_sakit.data.Obat
import com.google.firebase.database.*

class TabelJadwalMinumObatPendampingAdapter(private var jadwalObatArrayList : ArrayList<Jadwal>):
    RecyclerView.Adapter<TabelJadwalMinumObatPendampingAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListener
    private lateinit var database: DatabaseReference

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TabelJadwalMinumObatPendampingAdapter.ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tabel_jadwal_minum_obat_row_layout, viewGroup, false)

        return TabelJadwalMinumObatPendampingAdapter.ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.tvWaktu.text = jadwalObatArrayList[i].waktu_minum

        database = FirebaseDatabase.getInstance().getReference("Obat")
        database.child(jadwalObatArrayList[i].key_obat.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val obat = snapshot.getValue(Obat::class.java)
                    viewHolder.tvNamaObat.text = obat?.nama
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun getItemCount(): Int {
        return jadwalObatArrayList.size
    }

    class ViewHolder(itemView : View, listener: TabelJadwalMinumObatPendampingAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val tvWaktu : TextView = itemView.findViewById(R.id.tv_waktu)
        val tvNamaObat : TextView = itemView.findViewById(R.id.tv_nama_obat)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
//                itemView.background =itemView.context.getDrawable(R.color.teal_200)
            }
        }
    }
}
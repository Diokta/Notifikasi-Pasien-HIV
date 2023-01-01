package com.example.rumah_sakit.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.DaftarJadwalObatPasien
import com.example.rumah_sakit.data.Obat
import com.google.firebase.database.*
import com.google.rpc.context.AttributeContext.Resource

class TabelObatPasienAdapter(private var daftarJadwalObatPasienArrayList: ArrayList<DaftarJadwalObatPasien>):
    RecyclerView.Adapter<TabelObatPasienAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListener
    private lateinit var database: DatabaseReference
    private var selectedItemPosition: Int = -1

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener : onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TabelObatPasienAdapter.ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tabel_obat_pasien_row_layout, viewGroup, false)

        return TabelObatPasienAdapter.ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.tvWaktu.text = daftarJadwalObatPasienArrayList[i].jadwal.waktu_minum

        database = FirebaseDatabase.getInstance().getReference("Obat")
        database.child(daftarJadwalObatPasienArrayList[i].jadwal.key_obat.toString()).addValueEventListener(object :
            ValueEventListener {
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
        if (daftarJadwalObatPasienArrayList[i].laporanMinumObat.waktu_minum == null){
            viewHolder.imgDiminum.visibility = View.INVISIBLE
        }
        viewHolder.llData.setOnClickListener{
            selectedItemPosition = viewHolder.position
            notifyDataSetChanged()
        }

        if (selectedItemPosition == i){
            viewHolder.tvWaktu.setBackgroundColor(viewHolder.itemView.context.resources.getColor(R.color.carolina))
            viewHolder.tvNamaObat.setBackgroundColor(viewHolder.itemView.context.resources.getColor(R.color.carolina))
            viewHolder.rlDiminum.setBackgroundColor(viewHolder.itemView.context.resources.getColor(R.color.carolina))
        }else{
            viewHolder.tvWaktu.setBackgroundColor(viewHolder.itemView.context.resources.getColor(R.color.white))
            viewHolder.tvNamaObat.setBackgroundColor(viewHolder.itemView.context.resources.getColor(R.color.white))
            viewHolder.rlDiminum.setBackgroundColor(viewHolder.itemView.context.resources.getColor(R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return daftarJadwalObatPasienArrayList.size
    }

    class ViewHolder(itemView : View, listener: TabelObatPasienAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvWaktu : TextView = itemView.findViewById(R.id.tv_waktu)
        val tvNamaObat : TextView = itemView.findViewById(R.id.tv_nama_obat)
        val imgDiminum : ImageView = itemView.findViewById(R.id.img_diminum)
        val rlDiminum : RelativeLayout = itemView.findViewById(R.id.rl_diminum)
        val llData : LinearLayout = itemView.findViewById(R.id.ll_data)
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)

            }
        }
    }
}
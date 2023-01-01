package com.example.rumah_sakit.pendamping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.adapter.TabelKunjunganAdapter
import com.example.rumah_sakit.data.Kunjungan
import com.google.firebase.database.*

class DaftarKunjunganPendampingActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var kunjunganRecyclerView : RecyclerView
    private var kunjunganArrayList = ArrayList<Kunjungan>()
    private var keyArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_kunjungan_pendamping)

        var key: String? = intent.getStringExtra("key")
        database = FirebaseDatabase.getInstance().getReference("Kunjungan").child(key.toString())

        kunjunganRecyclerView = findViewById(R.id.tabel_kunjungan)
        kunjunganRecyclerView.layoutManager = LinearLayoutManager(this)
        kunjunganRecyclerView.setHasFixedSize(true)

        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    kunjunganArrayList.clear()
                    keyArrayList.clear()
                    for (kunjunganSnapshot in snapshot.children){
                        val kunjungan = kunjunganSnapshot.getValue(Kunjungan::class.java)
                        val key = kunjunganSnapshot.key
                        keyArrayList.add(key.toString())
                        kunjunganArrayList.add(kunjungan!!)
                    }
                    kunjunganArrayList.reverse()
                    keyArrayList.reverse()

                    var adapter = TabelKunjunganAdapter(kunjunganArrayList)
                    kunjunganRecyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : TabelKunjunganAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                        }
                    })
                }else {
                    Toast.makeText(this@DaftarKunjunganPendampingActivity, "Belum Ada Data Kunjungan", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
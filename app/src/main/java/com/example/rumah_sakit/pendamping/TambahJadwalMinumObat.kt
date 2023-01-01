package com.example.rumah_sakit.pendamping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.adapter.TabelJadwalMinumObatPendampingAdapter
import com.example.rumah_sakit.data.Jadwal
import com.example.rumah_sakit.data.Obat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_tambah_jadwal_minum_obat.*

class TambahJadwalMinumObat : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private lateinit var databaseObat : DatabaseReference
    private lateinit var adapter : ArrayAdapter<String>

    private lateinit var jadwalRecyclerView: RecyclerView

    private var spinnerList = ArrayList<String>()
    private var keyObatArrayList = ArrayList<String>()

    private var jadwalArrayList = ArrayList<Jadwal>()
    private var keyJadwalArrayList = ArrayList<String>()
    private var waktu : String? = null
    private var namaObatIndex : Long? = null
    private var key : String? = null
    private var keySelectedJadwal : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_jadwal_minum_obat)

        key = intent.getStringExtra("key")

        jadwalRecyclerView = findViewById(R.id.tabel_jadwal_minum_obat)
        jadwalRecyclerView.layoutManager = LinearLayoutManager(this)
        jadwalRecyclerView.setHasFixedSize(true)

        btn_timepicker.setOnClickListener {
            val builder: MaterialTimePicker = MaterialTimePicker.Builder()
                .setTitleText("Waktu Kunjungan")
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setPositiveButtonText("Simpan")
                .setNegativeButtonText("Batal")
                .build()


            builder.addOnPositiveButtonClickListener {
//                val pickedHour: Int = builder.hour
                val pickedMinute: Int = builder.minute

                val formattedTime: String = when {
                    pickedMinute < 10 -> {
                        "${builder.hour}:0${builder.minute}"
                    }
                    else -> {
                        "${builder.hour}:${builder.minute}"
                    }
                }
                edt_waktu_minum_obat.setText(formattedTime)
            }
            builder.show(supportFragmentManager, "MyTAG")
        }

        getDataObat()
        adapter = ArrayAdapter<String>(this, androidx.transition.R.layout.support_simple_spinner_dropdown_item, spinnerList)
        spn_nama_obat.adapter = adapter


        database = FirebaseDatabase.getInstance().getReference("Jadwal")
        database.child(key.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    jadwalArrayList.clear()
                    keyJadwalArrayList.clear()
                    for (jadwalSnapshot in snapshot.children){
                        val jadwal = jadwalSnapshot.getValue(Jadwal::class.java)
                        val keyJadwal = jadwalSnapshot.key
                        jadwalArrayList.add(jadwal!!)
                        keyJadwalArrayList.add(keyJadwal!!)
                    }
                    jadwalArrayList.reverse()
                    keyJadwalArrayList.reverse()

                    var adapter = TabelJadwalMinumObatPendampingAdapter(jadwalArrayList)
                    jadwalRecyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : TabelJadwalMinumObatPendampingAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            edt_waktu_minum_obat.setText(jadwalArrayList[position].waktu_minum)
                            val obatIndex = keyObatArrayList.indexOf(jadwalArrayList[position].key_obat)
                            spn_nama_obat.setSelection(obatIndex)
                            btn_tambah_minum_obat.text = "UBAH"
                            btn_batal_minum_obat.isClickable = true
                            btn_batal_minum_obat.setBackgroundColor(getResources().getColor(R.color.teal_700))
                            keySelectedJadwal = keyJadwalArrayList[position]
                            Toast.makeText(this@TambahJadwalMinumObat, keySelectedJadwal, Toast.LENGTH_SHORT).show()
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        btn_tambah_minum_obat.setOnClickListener {
            waktu = edt_waktu_minum_obat.text.toString()
            namaObatIndex = spn_nama_obat.selectedItemId

            if (waktu!!.isEmpty()){
                edt_waktu_minum_obat.error = "Waktu Minum Obat Harus Diisi"
                return@setOnClickListener
            }

            val btnText = btn_tambah_minum_obat.text.toString()
            val jadwal = Jadwal(waktu, keyObatArrayList[namaObatIndex!!.toInt()])
//            Toast.makeText(this, keyObatArrayList[namaObatIndex!!.toInt()], Toast.LENGTH_SHORT).show()
            if (btnText == "TAMBAH"){
                database = FirebaseDatabase.getInstance().getReference("Jadwal")
                val id = database.push().key.toString()
                database.child(key.toString()).child(id).setValue(jadwal).addOnSuccessListener {
                    edt_waktu_minum_obat.setText("")
                    Toast.makeText(this, "Jadwal Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Jadwal Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
                }
            } else if (btnText == "UBAH"){
                database = FirebaseDatabase.getInstance().getReference("Jadwal")
                database.child(key.toString()).child(keySelectedJadwal!!).setValue(jadwal).addOnSuccessListener {
                    edt_waktu_minum_obat.setText("")
                    btn_tambah_minum_obat.text = "TAMBAH"
                    btn_batal_minum_obat.isClickable = false
                    btn_batal_minum_obat.setBackgroundColor(resources.getColor(R.color.grey))
                    Toast.makeText(this, "Kunjungan Berhasil Diubah", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Kunjungan Gagal Diubah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btn_batal_minum_obat.setOnClickListener {
            edt_waktu_minum_obat.setText("")
            spn_nama_obat.setSelection(0)
            btn_tambah_minum_obat.text="TAMBAH"
            btn_batal_minum_obat.isClickable = false
            btn_batal_minum_obat.setBackgroundColor(resources.getColor(R.color.grey))
        }
    }

    private fun getDataObat() {
        databaseObat = FirebaseDatabase.getInstance().getReference("Obat")
        databaseObat.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                spinnerList.clear()
                keyObatArrayList.clear()
                if (snapshot.exists()){
                    for (obatSnapshot in snapshot.children){
                        val obat = obatSnapshot.getValue(Obat::class.java)
                        spinnerList.add(obat?.nama.toString())
                        keyObatArrayList.add(obatSnapshot.key.toString())
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
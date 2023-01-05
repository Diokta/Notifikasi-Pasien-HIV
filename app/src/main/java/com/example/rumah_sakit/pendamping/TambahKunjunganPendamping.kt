package com.example.rumah_sakit.pendamping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Kunjungan
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_tambah_kunjungan_pendamping.*
import java.util.*

class TambahKunjunganPendamping : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private var tglKunjungan: String? = null
    private var waktuKunjungan: String? = null
    private var keterangan: String? = null
    private var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_kunjungan_pendamping)

        database = FirebaseDatabase.getInstance().getReference()
        key = intent.getStringExtra("key")

        //Button pilih tanggal lahir
        btn_datepicker.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val datePicker = builder.build()

            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = Date(it)
                var tanggal = ""
                if  (calendar.get(Calendar.MONTH) + 1 >= 10){
                    tanggal = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
                } else {
                    tanggal = "${calendar.get(Calendar.DAY_OF_MONTH)}-0${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
                }
                edt_tgl_kunjungan.setText(tanggal)

            }
            datePicker.show(supportFragmentManager, "MyTAG")
        }

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
                edt_waktu_kunjungan.setText(formattedTime)
            }
            builder.show(supportFragmentManager, "MyTAG")
        }

        btn_simpan_kunjungan.setOnClickListener {
            tglKunjungan = edt_tgl_kunjungan.text.toString()
            waktuKunjungan = edt_waktu_kunjungan.text.toString()
            keterangan = edt_keterangan_kunjungan.text.toString()

            if (tglKunjungan!!.isEmpty()) {
                edt_tgl_kunjungan.error = "Tanggal Kunjungan Harus Diisi"
                return@setOnClickListener
            }

            if (waktuKunjungan!!.isEmpty()) {
                edt_waktu_kunjungan.error = "Waktu Kunjungan Harus Diisi"
                return@setOnClickListener
            }

            if (keterangan!!.isEmpty()) {
                edt_keterangan_kunjungan.error = "Keterangan Kunjungan Harus Diisi"
                edt_keterangan_kunjungan.requestFocus()
                return@setOnClickListener
            }

            simpanKunjungan()
        }
    }

    private fun simpanKunjungan() {
        val kunjungan = Kunjungan(tglKunjungan, waktuKunjungan, keterangan)
        val id = database.push().key.toString()
        database.child("Kunjungan").child(key.toString()).child(id).setValue(kunjungan)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Kunjungan Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DetailPasienPendampingActivity::class.java)
                intent.putExtra("key", key)
                startActivity(intent)
            }.addOnFailureListener {
            Toast.makeText(this, "Data Kunjungan Gagal Disimpan", Toast.LENGTH_SHORT).show()
        }
    }
}
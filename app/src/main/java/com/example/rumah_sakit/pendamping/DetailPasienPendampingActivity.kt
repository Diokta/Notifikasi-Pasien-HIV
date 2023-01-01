package com.example.rumah_sakit.pendamping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.NumberPicker.OnValueChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Janji
import com.example.rumah_sakit.data.Kunjungan
import com.example.rumah_sakit.data.Pasien
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_pasien_pendamping.*
import java.util.*

class DetailPasienPendampingActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var databaseJanji : DatabaseReference
    private lateinit var databaseKunjungan : DatabaseReference
    private lateinit var noRegNas: String
    private lateinit var tglKunjunganRutin: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pasien_pendamping)

        var key: String? = intent.getStringExtra("key")

        database = FirebaseDatabase.getInstance().getReference("Pasien").child("$key")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Pasien::class.java)
                tv_nik_content.text = value?.nik
                tv_no_rm_content.text = value?.no_rm
                tv_no_reg_nas_content.text = value?.no_reg_nasional
                tv_no_bpjs_content.text = value?.no_bpjs
                tv_nama_content.text = value?.nama
                tv_tgl_lahir_content.text = value?.tanggal_lahir
                if (value?.tanggal_kunjungan_rutin != null){
                    tv_tgl_kunjungan_content.text = value?.tanggal_kunjungan_rutin.toString()
                } else {
                    tv_tgl_kunjungan_content.text = null
                }
                tglKunjunganRutin = value?.tanggal_kunjungan_rutin.toString()
                noRegNas = value?.no_reg_nasional.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        btn_nomor_reg_nas.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogLayout =
                layoutInflater.inflate(R.layout.dialog_nomor_reg_nas_pendamping, null)

            val edtNomorRegNas = dialogLayout.findViewById<EditText>(R.id.edt_no_reg_nas)
            edtNomorRegNas.setText(noRegNas)

            with(builder) {
                setTitle("Masukan Nomor Registrasi Nasional")
                setPositiveButton("Simpan") { dialog, id ->
                    SimpanNoRegNas(edtNomorRegNas.text.toString())
                }
                setNegativeButton("Batal") { dialog, id ->
                    dialog.cancel()
                }
                setView(dialogLayout)
                show()
            }
        }

        btn_tgl_kunjungan_rutin.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogLayout =
                layoutInflater.inflate(R.layout.dialog_jadwal_kunjungan_pendamping, null)

            val NpTanggal : NumberPicker = dialogLayout.findViewById(R.id.np_tanggal)

            NpTanggal.maxValue = 31
            NpTanggal.minValue = 1
            var tanggal : Int = 1
            NpTanggal.setOnValueChangedListener(object : OnValueChangeListener{
                override fun onValueChange(p0: NumberPicker?, oldNumber: Int, newNumber: Int) {
                    tanggal = newNumber
                }

            })

            with(builder) {
                setTitle("Masukan Tanggal Registrasi Rutin")
                setPositiveButton("Simpan") { dialog, id ->
                    SimpanTglKunjunganRutin(tanggal)
                }
                setNegativeButton("Batal") { dialog, id ->
                    dialog.cancel()
                }
                setView(dialogLayout)
                show()
            }
        }

        btn_janji.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogLayout = layoutInflater.inflate(R.layout.dialog_janji_kunjungan_pendamping, null)

            val TvJanji : TextView = dialogLayout.findViewById(R.id.tv_janji_kunjungan)

            databaseJanji = FirebaseDatabase.getInstance().getReference("Janji").child("$key")
            databaseJanji.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val value = snapshot.getValue(Janji::class.java)
                        TvJanji.text = "Pasien memiliki janji pada tanggal ${value?.tgl_janji} jam ${value?.waktu_janji}"
                    } else {
                        TvJanji.text = "Pasien belum memiliki janji kunjungan"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            with(builder) {
                setTitle("Informasi")
                setNegativeButton("Tutup") { dialog, id ->
                    dialog.cancel()
                }
                setView(dialogLayout)
                show()
            }
        }
        btn_kunjungan.setOnClickListener {
            val intent = Intent(this, TambahKunjunganPendamping::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }

        btn_laporan_kunjungan.setOnClickListener {
            val intent = Intent(this, DaftarKunjunganPendampingActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }

        btn_Jadwal_obat.setOnClickListener {
            val intent = Intent(this, TambahJadwalMinumObat::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }
    }

    private fun SimpanTglKunjunganRutin(tanggal: Int) {
        database.child("tanggal_kunjungan_rutin").setValue(tanggal).addOnSuccessListener {
            Toast.makeText(this, "Tanggal Kunjungan Rutin Berhasil Disimpan", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun SimpanNoRegNas(no_reg_nas: String) {
        database.child("no_reg_nasional").setValue(no_reg_nas).addOnSuccessListener {
            Toast.makeText(this, "Nomor Registrasi Nasional Berhasil Disimpan", Toast.LENGTH_SHORT)
                .show()
        }
    }


//    override fun onBackPressed() {
//        val intent = Intent(this, DaftarPasienPendampingActivity::class.java)
//        startActivity(intent)
//    }
}
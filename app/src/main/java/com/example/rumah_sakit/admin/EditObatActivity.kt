package com.example.rumah_sakit.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Obat
import com.example.rumah_sakit.data.ObatPref
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_obat.*

class EditObatActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var obatPref : ObatPref

    private var nama : String? = null
    private var kategori : String? = null
    private var deskripsi : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_obat)

        val key : String? = intent.getStringExtra("key")

        obatPref = ObatPref(this)
        val obat : Obat = obatPref.getObat()

        initializeValue(obat)
        
        btn_simpan_obat_edit.setOnClickListener { 
            nama = edt_nama_obat_edit.text.toString()
            kategori = edt_kategori_obat_edit.text.toString()
            deskripsi = edt_deskripsi_obat_edit.text.toString()

            if(nama!!.isEmpty()){
                edt_nama_obat_edit.error = "Nama Obat Harus Diisi"
                edt_nama_obat_edit.requestFocus()
                return@setOnClickListener
            }

            if (kategori!!.isEmpty()){
                edt_kategori_obat_edit.error = "Kategori Obat Harus Diisi"
                edt_kategori_obat_edit.requestFocus()
                return@setOnClickListener
            }

            if (deskripsi!!.isEmpty()){
                edt_deskripsi_obat_edit.error = "Deskripsi Obat Harus Diisi"
                edt_deskripsi_obat_edit.requestFocus()
                return@setOnClickListener
            }
            
            editFirebase(key)
        }
    }

    private fun editFirebase(key: String?) {
        database = FirebaseDatabase.getInstance().getReference("Obat")
        key?.let {
            val obat = Obat(nama, kategori, deskripsi)
            database.child(key).setValue(obat).addOnSuccessListener {
                Toast.makeText(this, "Data Obat Berhasi Diubah", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DaftarObatActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initializeValue(obat: Obat) {
        edt_nama_obat_edit.setText(obat.nama)
        edt_kategori_obat_edit.setText(obat.kategori)
        edt_deskripsi_obat_edit.setText(obat.deskripsi)
    }
}
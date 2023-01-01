package com.example.rumah_sakit.admin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Obat
import com.example.rumah_sakit.data.ObatPref
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_obat.*

class DetailObatActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var obatPref : ObatPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_obat)

        val key : String? = intent.getStringExtra("key")

        obatPref = ObatPref(this)

        database = FirebaseDatabase.getInstance().getReference("Obat").child("$key")

        var obat : Obat
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Obat::class.java)
                tv_nama_obat_content.text = value?.nama
                tv_kategori_obat_content.text = value?.kategori
                tv_deskripsi_obat_content.text = value?.deskripsi

                obat = Obat(
                    value?.nama,
                    value?.kategori,
                    value?.deskripsi
                )

                obatPref.setObat(obat)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        btn_ubah_obat.setOnClickListener {
            val intent = Intent(this, EditObatActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }

        btn_hapus_obat.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setMessage("Apakah anda yakin?")
                .setCancelable(false)
                .setPositiveButton("Yakin", DialogInterface.OnClickListener {
                        dialog, id -> hapusFirebase(key)
                })
                .setNegativeButton("Batal", DialogInterface.OnClickListener{
                        dialog, id -> dialog.cancel()
                })

            val alert = builder.create()
            alert.setTitle("Konfirmasi")
            alert.show()
        }
    }

    private fun hapusFirebase(key: String?) {
        database = FirebaseDatabase.getInstance().getReference("Obat")
        key?.let {
            database.child(key).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DaftarObatActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
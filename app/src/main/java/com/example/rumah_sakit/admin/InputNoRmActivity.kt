package com.example.rumah_sakit.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.rumah_sakit.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_input_no_rm.*

class InputNoRmActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_no_rm)

        val key : String? = intent.getStringExtra("key")

        btn_simpan.setOnClickListener {
            val no_rm : String= edt_no_rm.text.toString()
            val no_regis : String = edt_no_regis.text.toString()
            database = FirebaseDatabase.getInstance().getReference("Pasien")
            key?.let {
                database.child(key).child("no_rm").setValue(no_rm).addOnSuccessListener {
                    Toast.makeText(this, "Pendaftaran Disetujui", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DaftarPasienTerdaftarActivity::class.java)
                    startActivity(intent)
                }
//                database.child(key).child("no_regis")
            }
        }
    }
}
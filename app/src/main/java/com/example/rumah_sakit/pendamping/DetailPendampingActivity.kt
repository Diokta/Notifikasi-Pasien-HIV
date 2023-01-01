package com.example.rumah_sakit.pendamping

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.rumah_sakit.LoginActivity
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Petugas
import com.example.rumah_sakit.data.PetugasPref
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_pendamping.*

class DetailPendampingActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var petugasPref : PetugasPref
    private lateinit var userPref : UserPref
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pendamping)
        val key : String? = intent.getStringExtra("key")

        petugasPref = PetugasPref(this)

        userPref = UserPref(this)
        user = userPref.getUser()

        database = FirebaseDatabase.getInstance().getReference("Petugas").child("$key")

        var petugas : Petugas
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(Petugas::class.java)
                tv_nik_content.text = value?.nik
                tv_nama_content.text = value?.nama
                tv_tgl_lahir_content.text = value?.tanggal_lahir
                tv_email_content.text = value?.email
                tv_telepon_content.text = value?.telepon
                tv_jenis_kelamin_content.text = value?.jenis_kelamin
                tv_bagian_content.text = value?.bagian

                petugas = Petugas(
                    value?.email,
                    value?.password,
                    value?.nik,
                    value?.nama,
                    value?.tanggal_lahir,
                    value?.telepon,
                    value?.jenis_kelamin,
                    value?.bagian,
                )
//
                petugasPref.setPetugas(petugas)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        btn_ubah.setOnClickListener {
            val intent = Intent(this, EditPendampingActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }
    }
}
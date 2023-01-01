package com.example.rumah_sakit.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Pasien
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_pasien.*

class DetailPasienActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pasien)

        val key : String? = intent.getStringExtra("key")

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
                tv_email_content.text = value?.email
                tv_telepon_content.text = value?.telepon
                tv_jenis_kelamin_content.text = value?.jenis_kelamin

                if (value?.no_rm != ""){
                    btn_setuju_daftar.visibility = View.INVISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        btn_setuju_daftar.setOnClickListener {
            val intent = Intent(this, InputNoRmActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }
    }

}
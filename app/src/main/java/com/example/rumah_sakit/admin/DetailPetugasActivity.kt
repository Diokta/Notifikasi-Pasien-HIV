package com.example.rumah_sakit.admin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.rumah_sakit.LoginActivity
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Petugas
import com.example.rumah_sakit.data.PetugasPref
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_petugas.*

class DetailPetugasActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var petugasPref : PetugasPref
    private lateinit var userPref : UserPref
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_petugas)

        val key : String? = intent.getStringExtra("key")

        petugasPref = PetugasPref(this)

        userPref = UserPref(this)
        user = userPref.getUser()

        if (user.uid == key){
            btn_hapus.visibility = View.INVISIBLE
        }

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
            val intent = Intent(this, EditPetugasActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }

        btn_hapus.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setMessage("Apakah anda yakin?")
                .setCancelable(false)
                .setPositiveButton("Yakin", DialogInterface.OnClickListener {
                    dialog, id -> hapusFirebase(key)
                })
                .setNegativeButton("Batal",DialogInterface.OnClickListener{
                    dialog, id -> dialog.cancel()
                })

            val alert = builder.create()
            alert.setTitle("Konfirmasi")
            alert.show()
        }
    }

    private fun hapusFirebase(key: String?) {
        database = FirebaseDatabase.getInstance().getReference("Petugas")
        key?.let {
            database.child(key).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                if (key == user.uid){
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val intent = Intent(this, DaftarPetugasActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}
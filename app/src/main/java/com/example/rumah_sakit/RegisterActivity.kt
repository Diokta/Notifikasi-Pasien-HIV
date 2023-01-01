package com.example.rumah_sakit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.RadioButton
import android.widget.Toast
import com.example.rumah_sakit.data.Pasien
import com.example.rumah_sakit.data.Pendaftaran
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database: DatabaseReference

    private var email: String? = null
    private var password: String? = null
    private var nik: String? = null
    private var no_bpjs: String? = null
    private var nama: String? = null
    private var tanggal_lahir: String? = null
    private var telepon: String? = null
    private var jenis_kelamin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().getReference()

        rg_jenis_kelamin_register.check(rb_perempuan_register.id)

        tv_to_login.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Button pilih tanggal lahir
        btn_datepicker.setOnClickListener{
            val builder = MaterialDatePicker.Builder.datePicker()
            val datePicker = builder.build()

            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = Date(it)
                edt_tl_register.setText("${calendar.get(Calendar.YEAR)}-" +
                        "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}")

            }
            datePicker.show(supportFragmentManager, "MyTAG")
        }

        btn_register.setOnClickListener{
            email = edt_email_register.text.toString()
            password = edt_password_register.text.toString()
            nik = edt_nik_register.text.toString()
            nama = edt_nama_petugas_register.text.toString()
            tanggal_lahir = edt_tl_register.text.toString()
            telepon = edt_telepon_register.text.toString()
            no_bpjs = edt_no_bpjs.text.toString()

            val radio : RadioButton = findViewById(rg_jenis_kelamin_register.checkedRadioButtonId)
            jenis_kelamin = radio.text.toString()

            //Validasi Email
            if(email!!.isEmpty()){
                edt_email_register.error = "Email Harus Diisi"
                edt_email_register.requestFocus()
                return@setOnClickListener
            }

            //Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edt_email_register.error = "Email Tidak Valid"
                edt_email_register.requestFocus()
                return@setOnClickListener
            }

            //Validasi password
            if(password!!.isEmpty()){
                edt_password_register.error = "Password Harus Diisi"
                edt_password_register.requestFocus()
                return@setOnClickListener
            }

            //Validasi panjang password
            if (password!!.length < 6){
                edt_password_register.error = "Password Minimal 6 Karakter"
                edt_password_register.requestFocus()
                return@setOnClickListener
            }

            //Validasi NIK
            if (nik!!.isEmpty()){
                edt_nik_register.error = "NIK Harus Diisi"
                edt_nik_register.requestFocus()
                return@setOnClickListener
            }

            //Validasi NIK
            if (no_bpjs!!.isEmpty()){
                edt_nik_register.error = "NIK Harus Diisi"
                edt_nik_register.requestFocus()
                return@setOnClickListener
            }

            //validasi panjang NIK
            if (nik!!.length != 16){
                edt_nik_register.error = "NIK Harus 16 Karakter"
                edt_nik_register.requestFocus()
                return@setOnClickListener
            }

            //validasi nama
            if (nama!!.isEmpty()){
                edt_nama_petugas_register.error = "Nama Harus Diisi"
                edt_nama_petugas_register.requestFocus()
                return@setOnClickListener
            }

            //validasi tanggal lahir
            if (tanggal_lahir!!.isEmpty()){
                edt_tl_register.error = "Tanggal Lahir Harus Diisi"
                edt_tl_register.requestFocus()
                return@setOnClickListener
            }

            //validasi telepon
            if (telepon!!.isEmpty()){
                edt_telepon_register.error = "Telepon Harus Diisi"
                edt_telepon_register.requestFocus()
                return@setOnClickListener
            }

            registerFirebase(email!!,password!!)
        }
    }

//    private fun registerFirebase(email: String, password: String) {
//        val empId = database.push().key!!
//        val User = User(email, password)
//        database.setValue(User).addOnCompleteListener{
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//    }

    private fun registerFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){

                    val pasien = Pasien(email, password, nik, nama, tanggal_lahir, telepon, jenis_kelamin, "", no_bpjs, "")
                    database.child("Pasien").child(auth.uid.toString()).setValue(pasien).addOnCompleteListener {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val tanggal = LocalDateTime.now().format(formatter)
                        val pendaftaran = Pendaftaran(tgl_daftar = tanggal.toString())
                        database.child("Pendaftaran").child(auth.uid.toString()).setValue(pendaftaran).addOnSuccessListener {
                            Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }.addOnFailureListener {
                            Toast.makeText(this, "Gagal Menyimpan Data Registrasi", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Gagal Menyimpan Data Pasien", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
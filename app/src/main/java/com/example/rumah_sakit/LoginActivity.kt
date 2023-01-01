package com.example.rumah_sakit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.rumah_sakit.admin.DaftarPasienTerdaftarActivity
import com.example.rumah_sakit.data.Pasien
import com.example.rumah_sakit.data.Petugas
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.example.rumah_sakit.pasien.DaftarObatPasienActivity
import com.example.rumah_sakit.pendamping.DaftarPasienPendampingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userPref : UserPref

    override fun onCreate(savedInstanceState: Bundle?) {

        userPref = UserPref(this)
//        if (userPref.preference.contains(UserPref.uid)){
//            if (userPref.getUser().job == "Administrasi"){
//                val intent = Intent(this@LoginActivity, AdminDaftarPasienTerdaftarActivity::class.java)
//                startActivity(intent)
//            }
//        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        tv_to_register.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            val email = edt_email_login.text.toString()
            val password = edt_password_login.text.toString()

            //Validasi Email
            if(email.isEmpty()){
                edt_email_login.error = "Email Harus Diisi"
                edt_password_login.requestFocus()
                return@setOnClickListener
            }

            //Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edt_email_login.error = "Email Tidak Valid"
                edt_password_login.requestFocus()
                return@setOnClickListener
            }

            //Validasi password
            if(password.isEmpty()){
                edt_password_login.error = "Password Harus Diisi"
                edt_password_login.requestFocus()
                return@setOnClickListener
            }
            //Validasi panjang password
            if (password.length < 6){
                edt_password_login.error = "Password Minimal 6 Karakter"
                edt_password_login.requestFocus()
                return@setOnClickListener
            }

            LoginFirebase(email,password)
        }
    }

    private fun LoginFirebase(email: String, password: String) {
        var user : User
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    val uid = auth.uid
                    database = FirebaseDatabase.getInstance().getReference("Petugas").child(uid.toString())
                    database.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val petugas = snapshot.getValue(Petugas::class.java)
                                user = User(
                                    uid,
                                    petugas?.email,
                                    petugas?.nama,
                                    petugas?.bagian
                                )
                                userPref.setUser(user)
                                if (petugas?.bagian == "Administrator"){
                                    val intent = Intent(this@LoginActivity, DaftarPasienTerdaftarActivity::class.java)
                                    startActivity(intent)
                                } else if (petugas?.bagian == "Pendamping"){
                                    val intent = Intent(this@LoginActivity, DaftarPasienPendampingActivity::class.java)
                                    startActivity(intent)
                                }
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Selamat Datang ${petugas?.nama}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

                    database = FirebaseDatabase.getInstance().getReference("Pasien").child(uid.toString())
                    database.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val pasien = snapshot.getValue(Pasien::class.java)
                                user = User(
                                    uid,
                                    pasien?.email,
                                    pasien?.nama,
                                    "Pasien"
                                )
                                userPref.setUser(user)
                                val intent = Intent(this@LoginActivity, DaftarObatPasienActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Selamat Datang ${pasien?.nama}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
//                    Toast.makeText(this, "Selamat Datang $uid", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
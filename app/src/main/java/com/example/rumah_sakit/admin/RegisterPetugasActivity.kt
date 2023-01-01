package com.example.rumah_sakit.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Petugas
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register_petugas.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.util.*

class RegisterPetugasActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var userPref : UserPref
    private lateinit var user : User

    private var email: String? = null
    private var password: String? = null
    private var nik: String? = null
    private var nama: String? = null
    private var tanggal_lahir: String? = null
    private var telepon: String? = null
    private var jenis_kelamin: String? = null
    private var bagian : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        database = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_petugas)

        val toogle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toogle)
        toogle.syncState()

        userPref = UserPref(this)
        user = userPref.getUser()
        setProfile(user)

        nav_view.setNavigationItemSelectedListener(this)

        rg_jenis_kelamin_edit.check(rb_perempuan_edit.id)
        rg_bagian_edit.check(rb_administrator_edit.id)

        //Button pilih tanggal lahir
        btn_datepicker.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val datePicker = builder.build()

            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = Date(it)
                edt_tl_petugas_register.setText("${calendar.get(Calendar.YEAR)}-" +
                        "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}")

            }
            datePicker.show(supportFragmentManager, "MyTAG")
        }

        btn_register.setOnClickListener{
            email = edt_email_petugas_register.text.toString()
            password = edt_password_petugas_register.text.toString()
            nik = edt_nik_petugas_register.text.toString()
            nama = edt_nama_petugas_register.text.toString()
            tanggal_lahir = edt_tl_petugas_register.text.toString()
            telepon = edt_telepon_petugas_register.text.toString()

            val selectedJenisKelamin : RadioButton = findViewById(rg_jenis_kelamin_edit.checkedRadioButtonId)
            jenis_kelamin = selectedJenisKelamin.text.toString()

            val selectedBagian : RadioButton = findViewById(rg_bagian_edit.checkedRadioButtonId)
            bagian = selectedBagian.text.toString()

            //Validasi Email
            if(email!!.isEmpty()){
                edt_email_petugas_register.error = "Email Harus Diisi"
                edt_email_petugas_register.requestFocus()
                return@setOnClickListener
            }

            //Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edt_email_petugas_register.error = "Email Tidak Valid"
                edt_email_petugas_register.requestFocus()
                return@setOnClickListener
            }

            //Validasi password
            if(password!!.isEmpty()){
                edt_password_petugas_register.error = "Password Harus Diisi"
                edt_password_petugas_register.requestFocus()
                return@setOnClickListener
            }

            //Validasi panjang password
            if (password!!.length < 6){
                edt_password_petugas_register.error = "Password Minimal 6 Karakter"
                edt_password_petugas_register.requestFocus()
                return@setOnClickListener
            }

            //Validasi NIK
            if (nik!!.isEmpty()){
                edt_nik_petugas_register.error = "NIK Harus Diisi"
                edt_nik_petugas_register.requestFocus()
                return@setOnClickListener
            }

            //validasi panjang NIK
            if (nik!!.length != 16){
                edt_nik_petugas_register.error = "NIK Harus 16 Karakter"
                edt_nik_petugas_register.requestFocus()
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
                edt_tl_petugas_register.error = "Tanggal Lahir Harus Diisi"
                edt_tl_petugas_register.requestFocus()
                return@setOnClickListener
            }

            //validasi telepon
            if (telepon!!.isEmpty()){
                edt_telepon_petugas_register.error = "Telepon Harus Diisi"
                edt_telepon_petugas_register.requestFocus()
                return@setOnClickListener
            }

            registerFirebase(email!!,password!!)
        }
    }

    private fun setProfile(user: User) {
        nav_view.getHeaderView(0).tv_nama_nav.text = "${user.nama} (${user.job})"
        nav_view.getHeaderView(0).tv_email_nav.text  = user.email
    }

    private fun registerFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){

                    val petugas = Petugas(email, password, nik, nama, tanggal_lahir, telepon, jenis_kelamin, bagian)
                    database.child("Petugas").child(auth.uid.toString()).setValue(petugas).addOnCompleteListener {
                        Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterPetugasActivity, DaftarPetugasActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener {
                        Toast.makeText(this, "Register Gagal", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.mi_profil){
            val intent = Intent(this@RegisterPetugasActivity, DetailPetugasActivity::class.java)
            intent.putExtra("key", user.uid)
            startActivity(intent)
        }  else if (id == R.id.mi_daftar_pasien){
            val intent = Intent(this@RegisterPetugasActivity, DaftarPasienTerdaftarActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_tambah_petugas){
            val intent = Intent(this@RegisterPetugasActivity, RegisterPetugasActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_daftar_petugas){
            val intent = Intent(this@RegisterPetugasActivity, DaftarPetugasActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_tambah_obat){
            val intent = Intent(this, RegisterObatActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_daftar_obat){
            val intent = Intent(this, DaftarObatActivity::class.java)
            startActivity(intent)
        }

        val drawer : DrawerLayout = drawer_layout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
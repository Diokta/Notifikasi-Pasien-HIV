package com.example.rumah_sakit.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Obat
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register_obat.*
import kotlinx.android.synthetic.main.nav_header.view.*

class RegisterObatActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var database : DatabaseReference
    private lateinit var userPref : UserPref
    private lateinit var user: User

    private var namaObat: String? = null
    private var deskripsiObat : String? = null
    private var kategoriObat : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_obat)
        setSupportActionBar(toolbar)

        database = FirebaseDatabase.getInstance().getReference()

        val toogle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toogle)
        toogle.syncState()

        userPref = UserPref(this)
        user = userPref.getUser()
        setProfile(user)

        nav_view.setNavigationItemSelectedListener(this)

        btn_simpan_obat_register.setOnClickListener {
            namaObat = edt_nama_obat_register.text.toString()
            kategoriObat = edt_kategori_obat_register.text.toString()
            deskripsiObat = edt_deskripsi_obat_register.text.toString()

            if(namaObat!!.isEmpty()){
                edt_nama_obat_register.error = "Nama Obat Harus Diisi"
                edt_nama_obat_register.requestFocus()
                return@setOnClickListener
            }

            if (kategoriObat!!.isEmpty()){
                edt_kategori_obat_register.error = "Kategori Obat Harus Diisi"
                edt_kategori_obat_register.requestFocus()
                return@setOnClickListener
            }

            if (deskripsiObat!!.isEmpty()){
                edt_deskripsi_obat_register.error = "Deskripsi Obat Harus Diisi"
                edt_deskripsi_obat_register.requestFocus()
                return@setOnClickListener
            }

            registerObat()
        }
    }

    private fun registerObat() {
        val obat = Obat(namaObat, kategoriObat, deskripsiObat)
        val id = database.push().key.toString()
        database.child("Obat").child(id).setValue(obat).addOnCompleteListener{
            Toast.makeText(this, "Data Obat Berhasi Disimpan", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DaftarObatActivity::class.java)
            startActivity(intent)
        }.addOnFailureListener {
            Toast.makeText(this, "Data Obat Gagal Disimpan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setProfile(user: User) {
        nav_view.getHeaderView(0).tv_nama_nav.text = "${user.nama} (${user.job})"
        nav_view.getHeaderView(0).tv_email_nav.text  = user.email
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.mi_profil){
            val intent = Intent(this, DetailPetugasActivity::class.java)
            intent.putExtra("key", user.uid)
            startActivity(intent)
        }  else if (id == R.id.mi_daftar_pasien){
            val intent = Intent(this, DaftarPasienTerdaftarActivity::class.java)
            startActivity(intent)
        }else if (id == R.id.mi_daftar_kunjungan){
            val intent = Intent(
                this,
                DaftarKunjunganAdminActivity::class.java
            )
            startActivity(intent)
        } else if (id == R.id.mi_tambah_petugas){
            val intent = Intent(this, RegisterPetugasActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_daftar_petugas){
            val intent = Intent(this, DaftarPetugasActivity::class.java)
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

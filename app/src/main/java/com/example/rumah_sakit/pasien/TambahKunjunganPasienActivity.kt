package com.example.rumah_sakit.pasien

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Kunjungan
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_tambah_kunjungan_pasien.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TambahKunjunganPasienActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var database: DatabaseReference
    private lateinit var databaseKunjungan: DatabaseReference

    private lateinit var userPref: UserPref
    private lateinit var user: User

    private var tgl_kunjungan: String? = null
    private var keterangan: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_kunjungan_pasien)

        setSupportActionBar(toolbar)

        val toogle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toogle)
        toogle.syncState()

        val navigationView: NavigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)

        userPref = UserPref(this)
        user = userPref.getUser()

        setProfile(user)

        //Button pilih tanggal lahir
        btn_datepicker.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val datePicker = builder.build()

            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = Date(it)
                var tanggal = ""
                if  (calendar.get(Calendar.MONTH) + 1 >= 10){
                    tanggal = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
                } else {
                    tanggal = "${calendar.get(Calendar.DAY_OF_MONTH)}-0${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
                }
                edt_tgl_kunjungan.setText(tanggal)

            }
            datePicker.show(supportFragmentManager, "MyTAG")
        }

        btn_daftar_kunjungan.setOnClickListener {
            tgl_kunjungan = edt_tgl_kunjungan.text.toString()
            keterangan = edt_keterangan_kunjungan.text.toString()

            if (tgl_kunjungan!!.isEmpty()) {
                edt_tgl_kunjungan.error = "Tanggal Harus Diisi"
                return@setOnClickListener
            }

            if (keterangan!!.isEmpty()) {
                edt_keterangan_kunjungan.error = "Keterangan Kunjungan Harus Diisi"
                edt_keterangan_kunjungan.requestFocus()
                return@setOnClickListener
            }

            daftarKunjungan()

        }
        getDataKunjungan()
    }

    private fun getDataKunjungan() {
        databaseKunjungan =
            FirebaseDatabase.getInstance().getReference("Janji").child(user.uid.toString())
        val formatter = DateTimeFormatter.ofPattern("d-MM-yyyy")
        val tanggal = LocalDateTime.now().format(formatter)

        var pesan = ""
        databaseKunjungan.child(tanggal).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val kunjungan = snapshot.getValue(Kunjungan::class.java)
                    if (kunjungan!!.waktu_datang == null) {
                        pesan =
                            "Anda memiliki jadwal kunjungan pada tanggal ${kunjungan.tgl_datang}, namun belum disetujui oleh admin"
                    } else {
                        pesan =
                            "Anda memiliki jadwal kunjungan pada tanggal ${kunjungan.tgl_datang}, pukul ${kunjungan.waktu_datang}"
                    }
                    tv_pesan.text = pesan
                } else {
                    databaseKunjungan = FirebaseDatabase.getInstance().getReference("Janji")
                        .child(user.uid.toString())
                    val formatter = DateTimeFormatter.ofPattern("d-MM-yyyy")
                    val tanggal = LocalDate.now().plusDays(1)

                    databaseKunjungan.child(tanggal.format(formatter))
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val kunjungan = snapshot.getValue(Kunjungan::class.java)
                                    if (kunjungan!!.waktu_datang == null) {
                                        pesan =
                                            "Anda memiliki jadwal kunjungan pada tanggal ${kunjungan.tgl_datang}, namun belum disetujui oleh admin"
                                    } else {
                                        pesan =
                                            "Anda memiliki jadwal kunjungan pada tanggal ${kunjungan.tgl_datang}, pukul ${kunjungan.waktu_datang}"
                                    }

                                    tv_pesan.text = pesan
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun daftarKunjungan() {
        database = FirebaseDatabase.getInstance().getReference()
        val formatter = DateTimeFormatter.ofPattern("d-MM-yyyy")
        val tanggal = LocalDateTime.now().format(formatter)
        val id = database.push().key.toString()

        val kunjungan = Kunjungan(tgl_kunjungan, null, keterangan)
        database.child("Kunjungan").child(user.uid.toString()).child(tgl_kunjungan!!).setValue(kunjungan)
            .addOnSuccessListener {
                edt_tgl_kunjungan.setText("")
                edt_keterangan_kunjungan.setText("")

                Toast.makeText(this, "Berhasil Mendaftarkan Kunjungan", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setProfile(user: User) {
        nav_view.getHeaderView(0).tv_nama_nav.text = "${user.nama} (${user.job})"
        nav_view.getHeaderView(0).tv_email_nav.text = user.email
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if ((id == R.id.mi_profil)) {
//            val intent = Intent(this, DetailPetugasActivity::class.java)
//            startActivity(intent)
        } else if (id == R.id.mi_lapor_obat) {
            val intent = Intent(this, DaftarObatPasienActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_tambah_kunjungan) {
            val intent = Intent(this, TambahKunjunganPasienActivity::class.java)
            startActivity(intent)
        }

        val drawer: DrawerLayout = drawer_layout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
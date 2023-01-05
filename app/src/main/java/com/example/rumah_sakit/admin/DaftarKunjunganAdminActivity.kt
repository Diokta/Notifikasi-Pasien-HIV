package com.example.rumah_sakit.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.adapter.TabelKunjunganAdminAdapter
import com.example.rumah_sakit.data.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_daftar_kunjungan_admin.drawer_layout
import kotlinx.android.synthetic.main.activity_daftar_kunjungan_admin.nav_view
import kotlinx.android.synthetic.main.activity_daftar_kunjungan_admin.toolbar
import kotlinx.android.synthetic.main.nav_header.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DaftarKunjunganAdminActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var database: DatabaseReference
    private lateinit var databasePasien: DatabaseReference
    private lateinit var databaseKunjungan: DatabaseReference

    private lateinit var kunjunganRecyclerView: RecyclerView
    private var kunjunganArrayList = ArrayList<DaftarKunjunganAdmin>()
    private lateinit var pasien: Pasien

    lateinit var userPref: UserPref
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_kunjungan_admin)

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

        kunjunganRecyclerView = findViewById(R.id.tabel_kunjungan_admin)
        kunjunganRecyclerView.layoutManager = LinearLayoutManager(this)
        kunjunganRecyclerView.setHasFixedSize(true)

        userPref = UserPref(this)
        user = userPref.getUser()

        setProfile(user)
        getPasienData()
    }

    private fun getPasienData() {
        database = FirebaseDatabase.getInstance().getReference("Janji")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kunjunganArrayList.clear()
                if (snapshot.exists()) {
                    for (kunjunganSnapshot in snapshot.children) {
                        val pasienKey = kunjunganSnapshot.key
                        databasePasien = FirebaseDatabase.getInstance().getReference("Pasien")
                        databasePasien.child(pasienKey.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(pasienSnapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        pasien = pasienSnapshot.getValue(Pasien::class.java)!!
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                        databaseKunjungan = FirebaseDatabase.getInstance().getReference("Janji")
                        val formatter = DateTimeFormatter.ofPattern("d-MM-yyyy")
                        val tanggalNow = LocalDate.now()
                        databaseKunjungan.child(pasienKey.toString())
                            .child(tanggalNow.format(formatter))
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(kunjunganSnapshot: DataSnapshot) {
                                    if (kunjunganSnapshot.exists()) {
                                        val kunjungan =
                                            kunjunganSnapshot.getValue(Kunjungan::class.java)
                                        val daftarKunjunganAdmin =
                                            DaftarKunjunganAdmin(pasien, kunjungan!!)
                                        kunjunganArrayList.add(daftarKunjunganAdmin)

                                        var adapter = TabelKunjunganAdminAdapter(kunjunganArrayList)
                                        kunjunganRecyclerView.adapter = adapter
                                        adapter.setOnItemClickListener(object :
                                            TabelKunjunganAdminAdapter.onItemClickListener {
                                            override fun onItemClick(position: Int) {
                                                tambahWaktu(pasienKey, kunjungan.tgl_datang)
                                            }

                                        })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        databaseKunjungan = FirebaseDatabase.getInstance().getReference("Janji")
                        val tanggalTomorrow = LocalDate.now().plusDays(1)
                        databaseKunjungan.child(pasienKey.toString())
                            .child(tanggalTomorrow.format(formatter))
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(kunjunganSnapshot: DataSnapshot) {
                                    if (kunjunganSnapshot.exists()) {
                                        val kunjungan =
                                            kunjunganSnapshot.getValue(Kunjungan::class.java)
                                        val daftarKunjunganAdmin =
                                            DaftarKunjunganAdmin(pasien, kunjungan!!)
                                        kunjunganArrayList.add(daftarKunjunganAdmin)

                                        var adapter = TabelKunjunganAdminAdapter(kunjunganArrayList)
                                        kunjunganRecyclerView.adapter = adapter
                                        adapter.setOnItemClickListener(object :
                                            TabelKunjunganAdminAdapter.onItemClickListener {
                                            override fun onItemClick(position: Int) {
                                                tambahWaktu(pasienKey, kunjungan.tgl_datang)
                                            }

                                        })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun tambahWaktu(pasienKey: String?, tglDatang: String?) {
        val builder: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Waktu Kunjungan")
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setPositiveButtonText("Simpan")
            .setNegativeButtonText("Batal")
            .build()


        builder.addOnPositiveButtonClickListener {
//                val pickedHour: Int = builder.hour
            val pickedMinute: Int = builder.minute

            val formattedTime: String = when {
                pickedMinute < 10 -> {
                    "${builder.hour}:0${builder.minute}"
                }
                else -> {
                    "${builder.hour}:${builder.minute}"
                }
            }
            databaseKunjungan = FirebaseDatabase.getInstance().getReference("Kunjungan")
            databaseKunjungan.child(pasienKey!!).child(tglDatang!!).child("waktu_datang")
                .setValue(formattedTime).addOnSuccessListener {
                    Toast.makeText(this, "Data Waktu Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show(supportFragmentManager, "MyTAG")
    }

    private fun setProfile(user: User) {
        nav_view.getHeaderView(0).tv_nama_nav.text = "${user.nama} (${user.job})"
        nav_view.getHeaderView(0).tv_email_nav.text = user.email
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.mi_profil) {
            val intent =
                Intent(this, DetailPetugasActivity::class.java)
            intent.putExtra("key", user.uid)
            startActivity(intent)
        } else if (id == R.id.mi_daftar_pasien) {
            val intent = Intent(
                this,
                DaftarPasienTerdaftarActivity::class.java
            )
            startActivity(intent)
        } else if (id == R.id.mi_daftar_kunjungan) {
            val intent = Intent(
                this,
                DaftarKunjunganAdminActivity::class.java
            )
            startActivity(intent)
        } else if (id == R.id.mi_tambah_petugas) {
            val intent = Intent(this, RegisterPetugasActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_daftar_petugas) {
            val intent = Intent(this, DaftarPetugasActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_tambah_obat) {
            val intent = Intent(this, RegisterObatActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_daftar_obat) {
            val intent = Intent(this, DaftarObatActivity::class.java)
            startActivity(intent)
        }

        val drawer: DrawerLayout = drawer_layout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
package com.example.rumah_sakit.pasien

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.PasienService
import com.example.rumah_sakit.R
import com.example.rumah_sakit.adapter.TabelObatPasienAdapter
import com.example.rumah_sakit.admin.DetailPetugasActivity
import com.example.rumah_sakit.data.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.*
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.drawer_layout
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.nav_view
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.toolbar
import kotlinx.android.synthetic.main.activity_daftar_petugas.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DaftarObatPasienActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var database: DatabaseReference
    private lateinit var databaseLaporanObat: DatabaseReference
    private lateinit var databaseLaporan: DatabaseReference
    private lateinit var jadwalObatRecyclerView: RecyclerView

    private var jadwalObatArrayList = ArrayList<DaftarJadwalObatPasien>()
    private var waktu: String? = null
    private lateinit var userPref: UserPref
    private lateinit var user: User
    private lateinit var selectedJadwal: String


    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_obat_pasien)

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

        jadwalObatRecyclerView = findViewById(R.id.tabel_jadwal_minum_obat_pasien)
        jadwalObatRecyclerView.layoutManager = LinearLayoutManager(this)
        jadwalObatRecyclerView.setHasFixedSize(true)

        val navigationView: NavigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)

        userPref = UserPref(this)
        user = userPref.getUser()

        setProfile(user)

        btn_timepicker.setOnClickListener {
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
                edt_waktu_minum_obat.setText(formattedTime)
            }
            builder.show(supportFragmentManager, "MyTAG")
        }

        getPasienData()

        btn_tambah_minum_obat.setOnClickListener {
            waktu = edt_waktu_minum_obat.text.toString()

            if (waktu!!.isEmpty()){
                edt_waktu_minum_obat.error = "Waktu Minum Obat Harus Diisi"
                return@setOnClickListener
            }
            val formatter = DateTimeFormatter.ofPattern("d-MM-yyyy")
            val current = LocalDateTime.now().format(formatter)
            val laporanMinumObat = LaporanMinumObat(waktu)
            databaseLaporan = FirebaseDatabase.getInstance().getReference().child("Laporan Obat")
            databaseLaporan.child(user.uid.toString()).child(current).child(selectedJadwal).setValue(laporanMinumObat).addOnSuccessListener {
                Toast.makeText(this, "Obat Telah Diminum", Toast.LENGTH_SHORT).show()
                getPasienData()
            }
        }

        Intent(this, PasienService::class.java).also { intent ->
            startService(intent)
        }
    }


    private fun getPasienData() {
        database = FirebaseDatabase.getInstance().getReference("Jadwal")

        user.uid?.let {
            val uid = it
            database.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        jadwalObatArrayList.clear()
                        for (jadwalObatSnapshot in snapshot.children) {
                            val formatter = DateTimeFormatter.ofPattern("d-MM-yyyy")
                            val current = LocalDateTime.now().format(formatter)
                            val jadwalObat = jadwalObatSnapshot.getValue(Jadwal::class.java)
                            val key = jadwalObatSnapshot.key
                            databaseLaporanObat =
                                FirebaseDatabase.getInstance().getReference("Laporan Obat")
                                    .child(uid)
                            databaseLaporanObat.child(current).child(key.toString())
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshotLaporan: DataSnapshot) {
                                        if (snapshotLaporan.exists()) {
                                            val laporanMinumObat =
                                                snapshotLaporan.getValue(LaporanMinumObat::class.java)
                                            jadwalObatArrayList.add(
                                                DaftarJadwalObatPasien(
                                                    jadwalObat!!,
                                                    key,
                                                    laporanMinumObat!!
                                                )
                                            )
                                        } else {
                                            val laporanMinumObat = LaporanMinumObat(null)
                                            jadwalObatArrayList.add(
                                                DaftarJadwalObatPasien(
                                                    jadwalObat!!,
                                                    key,
                                                    laporanMinumObat
                                                )
                                            )
                                        }
                                        var adapter = TabelObatPasienAdapter(jadwalObatArrayList)
                                        jadwalObatRecyclerView.adapter = adapter
                                        adapter.setOnItemClickListener(object :
                                            TabelObatPasienAdapter.onItemClickListener {
                                            override fun onItemClick(position: Int) {
                                                selectedJadwal =
                                                    jadwalObatArrayList[position].key.toString()
                                            }
                                        })
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

        if ((id == R.id.mi_profil)){
//            val intent = Intent(this, DetailPetugasActivity::class.java)
//            startActivity(intent)
        } else if (id == R.id.mi_lapor_obat){
            val intent = Intent(this, DaftarObatPasienActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.mi_tambah_kunjungan){
            val intent = Intent(this, TambahKunjunganPasienActivity::class.java)
            startActivity(intent)
        }

        val drawer : DrawerLayout = drawer_layout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
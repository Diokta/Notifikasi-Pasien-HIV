package com.example.rumah_sakit.pasien

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.adapter.TabelObatPasienAdapter
import com.example.rumah_sakit.data.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.*
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.btn_timepicker
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.drawer_layout
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.edt_waktu_minum_obat
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.nav_view
import kotlinx.android.synthetic.main.activity_daftar_obat_pasien.toolbar
import kotlinx.android.synthetic.main.activity_daftar_pasien_pendamping.*
import kotlinx.android.synthetic.main.activity_tambah_jadwal_minum_obat.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DaftarObatPasienActivity : AppCompatActivity() ,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var database : DatabaseReference
    private lateinit var databaseLaporanObat : DatabaseReference
    private lateinit var jadwalObatRecyclerView: RecyclerView

    private var jadwalObatArrayList = ArrayList<DaftarJadwalObatPasien>()
    private lateinit var userPref : UserPref
    private lateinit var user : User
    private lateinit var selectedJadwal : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_obat_pasien)

        setSupportActionBar(toolbar)

        val toogle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toogle)
        toogle.syncState()

        jadwalObatRecyclerView = findViewById(R.id.tabel_jadwal_minum_obat_pasien)
        jadwalObatRecyclerView.layoutManager = LinearLayoutManager(this)
        jadwalObatRecyclerView.setHasFixedSize(true)

        val navigationView : NavigationView = nav_view
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

        

    }

    private fun getPasienData() {
        database = FirebaseDatabase.getInstance().getReference("Jadwal")

        user.uid?.let {
            val uid = it
            database.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        jadwalObatArrayList.clear()
                        for (jadwalObatSnapshot in snapshot.children){
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-d")
                            val current = LocalDateTime.now().format(formatter)
                            val jadwalObat = jadwalObatSnapshot.getValue(Jadwal::class.java)
                            val key = jadwalObatSnapshot.key
                            databaseLaporanObat = FirebaseDatabase.getInstance().getReference("Laporan Obat").child(uid)
                            databaseLaporanObat.child(current).child(key.toString()).addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshotLaporan: DataSnapshot) {
                                    if (snapshotLaporan.exists()){
                                        val laporanMinumObat = snapshotLaporan.getValue(LaporanMinumObat::class.java)
                                        jadwalObatArrayList.add(DaftarJadwalObatPasien(jadwalObat!!, key, laporanMinumObat!!))
                                    } else {
                                        val laporanMinumObat = LaporanMinumObat(null)
                                        jadwalObatArrayList.add(DaftarJadwalObatPasien(jadwalObat!!, key, laporanMinumObat))
                                    }
                                    var adapter = TabelObatPasienAdapter(jadwalObatArrayList)
                                    jadwalObatRecyclerView.adapter = adapter
                                    adapter.setOnItemClickListener(object : TabelObatPasienAdapter.onItemClickListener{
                                        override fun onItemClick(position: Int) {
                                            selectedJadwal = jadwalObatArrayList[position].key.toString()

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
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }
    private fun setProfile(user: User) {
        nav_view.getHeaderView(0).tv_nama_nav.text = "${user.nama} (${user.job})"
        nav_view.getHeaderView(0).tv_email_nav.text  = user.email
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }
}
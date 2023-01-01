package com.example.rumah_sakit.pendamping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rumah_sakit.R
import com.example.rumah_sakit.adapter.TabelPasienPendampingAdapter
import com.example.rumah_sakit.admin.DetailPetugasActivity
import com.example.rumah_sakit.data.Pasien
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_daftar_pasien_pendamping.drawer_layout
import kotlinx.android.synthetic.main.activity_daftar_pasien_pendamping.nav_view
import kotlinx.android.synthetic.main.activity_daftar_pasien_pendamping.toolbar
import kotlinx.android.synthetic.main.nav_header.view.*

class DaftarPasienPendampingActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var database : DatabaseReference
    private lateinit var pasienRecyclerView: RecyclerView
    private var pasienArrayList = ArrayList<Pasien>()
    private var keyArrayList = ArrayList<String>()
    private lateinit var userPref: UserPref
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_pasien_pendamping)

        setSupportActionBar(toolbar)

        val toogle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toogle)
        toogle.syncState()

        pasienRecyclerView = findViewById(R.id.tabel_pasien_pendamping)
        pasienRecyclerView.layoutManager = LinearLayoutManager(this)
        pasienRecyclerView.setHasFixedSize(true)

        val navigationView : NavigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)

        pasienArrayList = arrayListOf<Pasien>()

        userPref = UserPref(this)
        user = userPref.getUser()

        setProfile(user)
        getPasienData()
    }

    private fun getPasienData() {
        database = FirebaseDatabase.getInstance().getReference("Pasien")

        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    pasienArrayList.clear()
                    keyArrayList.clear()
                    for (pasienSnapshot in snapshot.children){
                        val pasien = pasienSnapshot.getValue(Pasien::class.java)
                        val key = pasienSnapshot.key
                        keyArrayList.add(key.toString())
                        pasienArrayList.add(pasien!!)
                    }
                    pasienArrayList.reverse()
                    keyArrayList.reverse()

                    var adapter = TabelPasienPendampingAdapter(pasienArrayList)
                    pasienRecyclerView.adapter = adapter
                    adapter.setOnItemCLickListener(object :
                    TabelPasienPendampingAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@DaftarPasienPendampingActivity, DetailPasienPendampingActivity::class.java)
                            intent.putExtra("key", keyArrayList[position])
                            startActivity(intent)
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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
        val id = item.itemId

        if (id == R.id.mi_profil){
            val intent = Intent(this, DetailPendampingActivity::class.java)
            intent.putExtra("key", user.uid)
            startActivity(intent)
        }else if (id == R.id.mi_daftar_pasien){
            val intent = Intent(this, DaftarPasienPendampingActivity::class.java)
            startActivity(intent)
        }

        val drawer : DrawerLayout = drawer_layout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
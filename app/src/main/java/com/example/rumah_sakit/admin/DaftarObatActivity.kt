package com.example.rumah_sakit.admin

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
import com.example.rumah_sakit.adapter.TabelObatAdapter
import com.example.rumah_sakit.data.Obat
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_daftar_obat.*
import kotlinx.android.synthetic.main.activity_daftar_obat.drawer_layout
import kotlinx.android.synthetic.main.activity_daftar_obat.nav_view
import kotlinx.android.synthetic.main.activity_daftar_obat.toolbar
import kotlinx.android.synthetic.main.nav_header.view.*

class DaftarObatActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var database: DatabaseReference
    private lateinit var obatRecyclerView : RecyclerView
    private var obatArrayList = ArrayList<Obat>()
    private var keyArrayList = ArrayList<String>()
    private lateinit var userPref : UserPref
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_obat)

        val toogle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toogle)
        toogle.syncState()

        val navigationView : NavigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)

        obatRecyclerView = tabel_obat
        obatRecyclerView.layoutManager = LinearLayoutManager(this)
        obatRecyclerView.setHasFixedSize(true)

        userPref = UserPref(this)
        user = userPref.getUser()

        setProfile(user)
        getObatData()
    }

    private fun getObatData() {
        database = FirebaseDatabase.getInstance().getReference("Obat")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    obatArrayList.clear()
                    keyArrayList.clear()
                    for (obatSnapshoot in snapshot.children){
                        val obat = obatSnapshoot.getValue(Obat::class.java)
                        val key = obatSnapshoot.key
                        keyArrayList.add(key.toString())
                        obatArrayList.add(obat!!)
                    }

                    obatArrayList.reverse()
                    keyArrayList.reverse()
                    var adapter = TabelObatAdapter(obatArrayList)
                    obatRecyclerView.adapter = adapter
                    adapter.setOnItemClickListerner(object : TabelObatAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@DaftarObatActivity, DetailObatActivity::class.java)
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

    private fun setProfile(user: User) {
        nav_view.getHeaderView(0).tv_nama_nav.text = "${user.nama} (${user.job})"
        nav_view.getHeaderView(0).tv_email_nav.text  = user.email
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
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
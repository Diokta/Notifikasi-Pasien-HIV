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
import com.example.rumah_sakit.adapter.TabelPetugasAdapter
import com.example.rumah_sakit.data.Petugas
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_daftar_petugas.*
import kotlinx.android.synthetic.main.activity_daftar_petugas.drawer_layout
import kotlinx.android.synthetic.main.activity_daftar_petugas.nav_view
import kotlinx.android.synthetic.main.activity_daftar_petugas.toolbar
import kotlinx.android.synthetic.main.nav_header.view.*

class DaftarPetugasActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var database: DatabaseReference
    private lateinit var petugasRecyrlerView : RecyclerView
    private var petugasArrayList = ArrayList<Petugas>()
    private var keyArrayList = ArrayList<String> ()
    private lateinit var userPref : UserPref
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_petugas)
        setSupportActionBar(toolbar)

        val toogle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toogle)
        toogle.syncState()

        val navigationView : NavigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)

        petugasRecyrlerView  = tabel_petugas
        petugasRecyrlerView.layoutManager = LinearLayoutManager(this)
        petugasRecyrlerView.setHasFixedSize(true)

        petugasArrayList = arrayListOf<Petugas>()

        userPref = UserPref(this)
        user = userPref.getUser()

        setProfile(user)
        getPetugasData()
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

    private fun getPetugasData() {
        database = FirebaseDatabase.getInstance().getReference("Petugas")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                petugasArrayList.clear()
                keyArrayList.clear()
                if (snapshot.exists()){
                    for (petugasSnapshot in snapshot.children){
                        val petugas = petugasSnapshot.getValue(Petugas::class.java)
                        val key = petugasSnapshot.key
                        keyArrayList.add(key.toString())
                        petugasArrayList.add(petugas!!)
                    }
                    petugasArrayList.reverse()
                    keyArrayList.reverse()
                    var adapter = TabelPetugasAdapter(petugasArrayList)
                    petugasRecyrlerView.adapter = adapter
                    adapter.setOnItemClickListener(object :
                        TabelPetugasAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
//                            Toast.makeText(this@DaftarPetugasActivity, keyArrayList[position], Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@DaftarPetugasActivity, DetailPetugasActivity::class.java)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.mi_profil){
            val intent = Intent(this, DetailPetugasActivity::class.java)
            intent.putExtra("key", user.uid)
            startActivity(intent)
        }  else if (id == R.id.mi_daftar_pasien){
            val intent = Intent(this, DaftarPasienTerdaftarActivity::class.java)
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
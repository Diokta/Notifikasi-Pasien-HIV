package com.example.rumah_sakit.pendamping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.RadioButton
import android.widget.Toast
import com.example.rumah_sakit.LoginActivity
import com.example.rumah_sakit.R
import com.example.rumah_sakit.data.Petugas
import com.example.rumah_sakit.data.PetugasPref
import com.example.rumah_sakit.data.User
import com.example.rumah_sakit.data.UserPref
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_pendamping.*
import java.text.SimpleDateFormat
import java.util.*

class EditPendampingActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var petugasPref : PetugasPref

    private var email: String? = null
    private var nik: String? = null
    private var nama: String? = null
    private var tanggal_lahir: String? = null
    private var telepon: String? = null
    private var jenis_kelamin: String? = null
    private var bagian : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pendamping)

        val key : String? = intent.getStringExtra("key")

        petugasPref = PetugasPref(this)
        val petugas : Petugas = petugasPref.getPetugas()

        initializeValue(petugas)

        //Button pilih tanggal lahir
        btn_datepicker.setOnClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            val formatString = "yyyy-MM-dd"
            val dateFormat = SimpleDateFormat(formatString)
            val date : Date = dateFormat.parse(edt_tl_edit.text.toString())
            calendar.time = date
            calendar.add(Calendar.DAY_OF_MONTH, 1)

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Lahir")
                .setSelection(calendar.timeInMillis)
                .build()

            datePicker.addOnPositiveButtonClickListener {

                calendar.time = Date(it)
                edt_tl_edit.setText("${calendar.get(Calendar.YEAR)}-" +
                        "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}")

            }
            datePicker.show(supportFragmentManager, "MyTAG")
        }

        btn_simpan.setOnClickListener {
            email = edt_email_edit.text.toString()
            nik = edt_nik_edit.text.toString()
            nama = edt_nama_edit.text.toString()
            tanggal_lahir = edt_tl_edit.text.toString()
            telepon = edt_telepon_edit.text.toString()

            val selectedJenisKelamin : RadioButton = findViewById(rg_jenis_kelamin_edit.checkedRadioButtonId)
            jenis_kelamin = selectedJenisKelamin.text.toString()

            val selectedBagian : RadioButton = findViewById(rg_bagian_edit.checkedRadioButtonId)
            bagian = selectedBagian.text.toString()

            //Validasi Email
            if(email!!.isEmpty()){
                edt_email_edit.error = "Email Harus Diisi"
                edt_email_edit.requestFocus()
                return@setOnClickListener
            }

            //Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edt_email_edit.error = "Email Tidak Valid"
                edt_email_edit.requestFocus()
                return@setOnClickListener
            }

            //Validasi NIK
            if (nik!!.isEmpty()){
                edt_nik_edit.error = "NIK Harus Diisi"
                edt_nik_edit.requestFocus()
                return@setOnClickListener
            }

            //validasi panjang NIK
            if (nik!!.length != 16){
                edt_nik_edit.error = "NIK Harus 16 Karakter"
                edt_nik_edit.requestFocus()
                return@setOnClickListener
            }

            //validasi nama
            if (nama!!.isEmpty()){
                edt_nama_edit.error = "Nama Harus Diisi"
                edt_nama_edit.requestFocus()
                return@setOnClickListener
            }

            //validasi tanggal lahir
            if (tanggal_lahir!!.isEmpty()){
                edt_tl_edit.error = "Tanggal Lahir Harus Diisi"
                edt_tl_edit.requestFocus()
                return@setOnClickListener
            }

            //validasi telepon
            if (telepon!!.isEmpty()){
                edt_telepon_edit.error = "Telepon Harus Diisi"
                edt_telepon_edit.requestFocus()
                return@setOnClickListener
            }

            editFirebase(key, petugas.password, )
        }
    }

    private fun editFirebase(petugasKey: String?, password: String?) {
        database = FirebaseDatabase.getInstance().getReference("Petugas")
        petugasKey?.let {
            val petugas = Petugas(email, password, nik, nama, tanggal_lahir, telepon, jenis_kelamin, bagian)
            database.child(petugasKey).setValue(petugas).addOnSuccessListener {
                Toast.makeText(this, "Data Petugas Berhasi Diubah", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun initializeValue(petugas: Petugas) {
        edt_email_edit.setText(petugas.email)
        edt_nik_edit.setText(petugas.nik)
        edt_nama_edit.setText(petugas.nama)
        edt_tl_edit.setText(petugas.tanggal_lahir)
        edt_telepon_edit.setText(petugas.telepon)
        if (petugas.jenis_kelamin == "Perempuan"){
            rg_jenis_kelamin_edit.check(rb_perempuan_edit.id)
        } else {
            rg_jenis_kelamin_edit.check(rb_laki_edit.id)
        }

        if (petugas.bagian == "Administrator"){
            rg_bagian_edit.check(rb_administrator_edit.id)
        } else {
            rg_bagian_edit.check(rb_pendamping_edit.id)
        }
    }
}
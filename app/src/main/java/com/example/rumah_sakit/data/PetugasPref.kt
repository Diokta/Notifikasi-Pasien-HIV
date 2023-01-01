package com.example.rumah_sakit.data

import android.content.Context

class PetugasPref(context: Context){
    companion object{
        const val SP_NAME = "petugas_pref"
        const val email = "email"
        const val password = "password"
        const val nik = "nik"
        const val nama = "nama"
        const val tanggal_lahir = "tanggal_lahir"
        const val telepon = "telepon"
        const val jenis_kelamin = "jenis_kelamin"
        const val bagian = "bagian"
    }

    val preference = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun setPetugas(petugas: Petugas){
        val prefEditor = preference.edit()
        prefEditor.putString(email, petugas.email)
        prefEditor.putString(password, petugas.password)
        prefEditor.putString(nik, petugas.nik)
        prefEditor.putString(nama, petugas.nama)
        prefEditor.putString(tanggal_lahir, petugas.tanggal_lahir)
        prefEditor.putString(telepon, petugas.telepon)
        prefEditor.putString(jenis_kelamin, petugas.jenis_kelamin)
        prefEditor.putString(bagian, petugas.bagian)
        prefEditor.apply()
    }

    fun getPetugas() : Petugas {
        val petugas = Petugas()
        petugas.email = preference.getString(email, "")
        petugas.password = preference.getString(password, "")
        petugas.nik = preference.getString(nik, "")
        petugas.nama = preference.getString(nama, "")
        petugas.tanggal_lahir = preference.getString(tanggal_lahir, "")
        petugas.telepon = preference.getString(telepon, "")
        petugas.jenis_kelamin = preference.getString(jenis_kelamin, "")
        petugas.bagian = preference.getString(bagian, "")
        return petugas
    }
}

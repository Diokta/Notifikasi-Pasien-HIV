package com.example.rumah_sakit.data

import android.content.Context

class ObatPref(context : Context) {
    companion object{
        const val SP_NAME = "obat_pref"
        const val nama = "nama"
        const val kategori = "kategori"
        const val deskripsi = "deskripsi"
    }

    val preference = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun setObat(obat: Obat){
        val prefEditor = preference.edit()
        prefEditor.putString(nama, obat.nama)
        prefEditor.putString(kategori, obat.kategori)
        prefEditor.putString(deskripsi, obat.deskripsi)
        prefEditor.apply()
    }

    fun getObat() : Obat {
        val obat = Obat()
        obat.nama = preference.getString(nama, "")
        obat.kategori = preference.getString(kategori, "")
        obat.deskripsi = preference.getString(deskripsi, "")
        return obat
    }
}
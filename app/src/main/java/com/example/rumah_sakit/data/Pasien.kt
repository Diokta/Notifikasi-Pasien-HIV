package com.example.rumah_sakit.data

data class Pasien(
    val email : String? = null,
    val password : String? = null,
    val nik : String? = null,
    val nama : String? = null,
    val tanggal_lahir : String? = null,
    val telepon : String? = null,
    val jenis_kelamin : String? = null,
    val no_rm : String? = null,
    val no_bpjs : String? = null,
    val no_reg_nasional : String? = null,
    val tanggal_kunjungan_rutin : Int? = null
)

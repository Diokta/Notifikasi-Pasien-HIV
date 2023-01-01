package com.example.rumah_sakit.data

data class DaftarJadwalObatPasien(
    var jadwal : Jadwal,
    var key : String?,
    var laporanMinumObat: LaporanMinumObat,
)

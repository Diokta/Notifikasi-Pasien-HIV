package com.example.rumah_sakit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    private fun daftar(kalimat : String) {
        if (kalimat.length > 6){
            Toast.makeText(this, kalimat, Toast.LENGTH_SHORT).show()
        }
    }
}
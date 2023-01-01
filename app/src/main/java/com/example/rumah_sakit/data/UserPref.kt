package com.example.rumah_sakit.data

import android.content.Context

class UserPref(context: Context) {
    companion object{
        const val SP_NAME = "user_pref"
        const val uid = "uid"
        const val email = "email"
        const val nama = "nama"
        const val job = "job"
    }

    val preference = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun setUser(user: User){
        val prefEditor = preference.edit()
        prefEditor.putString(uid, user.uid)
        prefEditor.putString(email, user.email)
        prefEditor.putString(nama, user.nama)
        prefEditor.putString(job, user.job)
        prefEditor.apply()
    }

    fun getUser(): User{
        val user = User()
        user.uid = preference.getString(uid,"")
        user.email = preference.getString(email, "")
        user.nama = preference.getString(nama, "")
        user.job = preference.getString(job, "")
        return user
    }
}
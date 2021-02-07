package com.ar.contactUtils.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(val id: String,val contactName:String,val contactNumber:String) : Parcelable{

    var numbers = ArrayList<String>()

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val (id1) = obj as Contact
        return id == id1
    }


}
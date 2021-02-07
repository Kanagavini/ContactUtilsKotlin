package com.ar.contactUtils.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.ar.contactUtils.model.Contact
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class SharedPreference {

    companion object {
        const val PREFS_NAME = "CONTACT_UTILS"
        const val CONTACT_CHECK = "Contact_check"
    }

    private fun saveContacts(context: Context, contacts: List<Contact?>?) {
        val editor: SharedPreferences.Editor
        val settings: SharedPreferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = settings.edit()
        val gson = Gson()
        val jsonContact = gson.toJson(contacts)
        editor.putString(CONTACT_CHECK, jsonContact)
        editor.commit()
    }



     fun addContact(context: Context,contact:Contact?){

         var contacts: MutableList<Contact?>? = getContacts(context)
         if(contacts == null) contacts = ArrayList()

         contacts.add(contact)
         saveContacts(context,contacts)
         getContacts(context)
         Log.d("dataRAte000#########","${contact.toString()}")
     }

    fun removeContact(context:Context,contact:Contact?){

        var contacts:MutableList<Contact?>? = getContacts(context)
        if(contacts == null) contacts = ArrayList()
        contacts.remove(contact)
        saveContacts(context,contacts)
        getContacts(context)
        Log.d("dataRAte000#########","${contacts.toString()}")
    }


     fun getContacts(context: Context): ArrayList<Contact?>? {

        var contact: List<Contact>
        val settings: SharedPreferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE)
        if(settings.contains(CONTACT_CHECK)){
            val jsonContact = settings.getString(CONTACT_CHECK,null)
            val gson = Gson()
            val contactItems = gson.fromJson(jsonContact,Array<Contact>::class.java)

            contact = Arrays.asList(*contactItems)
            contact = ArrayList(contact)
        }else return null
        Log.d("dataRAte000#########","${contact.toString()}")

        return contact
    }

}
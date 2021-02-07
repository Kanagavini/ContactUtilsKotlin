package com.ar.contactUtils.viewModel

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ar.contactUtils.model.Contact
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ContactViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {

    private val contactsData = MutableLiveData<ArrayList<Contact>>()
    val contactsLiveData:LiveData<ArrayList<Contact>> = contactsData

    fun getContactsDetails() {
        viewModelScope.launch {
            val contactNumbersAsync = async { getContactNumbers() }
            val contactsListAsync = async { getPhoneContacts() }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()

            contacts.forEach {
                contactNumbers[it.id]?.let { numbers ->
                    it.numbers = numbers
                }

            }
            contactsData.postValue(contacts)
        }
    }

    private suspend fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val contactsCursor = mApplication.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val phone = contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)


                if (name != null) {
                    contactsList.add(Contact(id, name,""))
                }
            }
            contactsCursor.close()
        }

        Log.d("dataLog","${contactsList.toString()}")
        return contactsList
    }

    private suspend fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = mApplication.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex = phoneCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                //check if the map contains key or not, if not then create a new array list with number
                /*if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    */
                contactsNumberMap[contactId] = arrayListOf(number)
                Log.d("dataLogMap","${arrayListOf(number)}")
                Log.d("dataLogMap123","$contactId")

                //}
            }
            //contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap



    }
}
package com.ar.contactUtils.receiver

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.ar.contactUtils.model.Contact
import com.ar.contactUtils.ui.CustomDialog
import com.ar.contactUtils.utils.SharedPreference
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber


@RequiresApi(Build.VERSION_CODES.N)
class CallingService : CallScreeningService() {
    lateinit var sharedPreference: SharedPreference
    private lateinit var contactName:String
    private val tag = "CallingService"
    lateinit var contactID: String

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle.schemeSpecificPart
        //Toast.makeText(applicationContext, "Incoming call  $phoneNumber", Toast.LENGTH_SHORT).show()
        sharedPreference = SharedPreference()
        var contacts = sharedPreference?.getContacts(applicationContext)
        Log.d(tag, "$phoneNumber")

        if (checkItem(phoneNumber)) {
            val intent = Intent(applicationContext, CustomDialog::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("phone_no", phoneNumber)
            intent.putExtra("phone_name", contactName)
            startActivity(intent)
            Log.d(tag, "$contactName")
        }


        val response = CallResponse.Builder()
        respondToCall(callDetails, response.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
   /* private fun checkItem(phoneNumber: String): Boolean {
        Log.d(tag,"RINGING + ${phoneNumber}")

        var check = false
        val contacts: List<Contact?>? = sharedPreference.getContacts(applicationContext)
        if (contacts != null) {
            for (contact in contacts) {
                contactName = contact?.contactName!!
                var contactNumber = contact?.numbers
                var str: String? = null
                try {
                    parsePhoneNumber(phoneNumber)?.let {
                        Log.d(tag, it)
                        str = it
                    }
                    if (contact?.numbers?.get(0)?.equals(str)!!) {
                        Log.d(tag,"RINGING + ${contact?.numbers?.get(0)}")

                        check = true
                        break
                    }
                } catch (e: Exception) {
                }
            }
        }
        return check
    }*/
   private fun checkItem(phoneNumber: String): Boolean {
       var check = false
       val contacts: List<Contact?>? = sharedPreference.getContacts(applicationContext)
       if (contacts != null) {
           var contactIncomingNum: String? = null
           var contactSharedNum: String? = null
           var contactSharedData:String? = null

           parsePhoneNumber(phoneNumber)?.let {
               Log.d(tag, it)
               contactIncomingNum = it
           }
           for (contact in contacts) {
               contactID = contact?.id!!

               var contactNumber = contact?.numbers
               var contactNumberID = contact?.id
               try {
                   Log.d(tag, contactNumberID)

                   contacts.forEach{
                       contactID = contact?.id!!
                       Log.d(tag, contact.numbers.toString())

                       if(contactIncomingNum in contact.numbers)
                       {
                           contactSharedData = contact.id
                           check = true

                           if(contactID == contactSharedData){
                               contactName = contact?.contactName!!
                           }

                       }
                   }
               } catch (e: Exception) {
               }
           }
       }
       return check
   }


    private fun parsePhoneNumber(phoneNo: String?): String? {
        return try {
            // phone must begin with '+'
            val phoneUtil = PhoneNumberUtil.getInstance()
            val numberProto: Phonenumber.PhoneNumber = phoneUtil.parse(phoneNo, "")
            val countryCode: Int = numberProto.getCountryCode()
            val nationalNumber: Long = numberProto.getNationalNumber()
            Log.i("code", "code $countryCode")
            Log.i("code", "national number $nationalNumber")
            nationalNumber.toString()
        } catch (e: NumberParseException) {
            Log.d("code", "national number " + e.toString())
            phoneNo
        }
    }


}
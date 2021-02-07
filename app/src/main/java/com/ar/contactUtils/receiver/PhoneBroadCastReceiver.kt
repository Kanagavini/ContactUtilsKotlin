package com.ar.contactUtils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.telephony.PhoneNumberUtils
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import com.ar.contactUtils.model.Contact
import com.ar.contactUtils.ui.CustomDialog
import com.ar.contactUtils.utils.SharedPreference
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber


class PhoneBroadCastReceiver : BroadcastReceiver() {
    lateinit var mcontext: Context
    lateinit var sharedPreference: SharedPreference
    lateinit var contactName: String
    lateinit var contactID: String
    private val tag = "Receiver_State"

    override fun onReceive(context: Context?, intent: Intent?) {
        //Toast.makeText(context, "Incoming call  ", Toast.LENGTH_SHORT).show()

        //  context!!.sendBroadcast(Intent(context.applicationContext, BroadcastService::class.java))

        val telephonyManager =
            context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val monitor = StateMonitor()
        mcontext = context
        telephonyManager.listen(monitor, PhoneStateListener.LISTEN_CALL_STATE)
        sharedPreference = SharedPreference()

    }


    private inner class StateMonitor : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> Log.d(tag, "IDLE")
                TelephonyManager.CALL_STATE_OFFHOOK -> Log.d(tag, "OFF-HOOK")
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d(tag, "RINGING")
                    var contacts = sharedPreference?.getContacts(mcontext)
                    Log.d(tag, "$phoneNumber")


                    if (phoneNumber?.let { checkItem(it) }!!) {
                        Log.d(tag, "RINGINGCOND")

                        //start activity which has dialog
                        //  displayDialog();
                        val intent = Intent(mcontext, CustomDialog::class.java)

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra("phone_no", phoneNumber)
                        intent.putExtra("phone_name", contactName)

                        Log.d(tag, "$contactName")
                        Handler().postDelayed({ mcontext.startActivity(intent) }, 1500)
                        Log.d(tag, "$contacts")


                    }

                }
            }
        }
    }


    fun checkItem(phoneNumber: String): Boolean {
        var check = false
        val contacts: List<Contact?>? = sharedPreference.getContacts(mcontext)
        if (contacts != null) {
            var contactIncomingNum: String? = null
            var contactSharedNum: String? = null
            var contactSharedData:String? = null

            parsePhoneNumber(phoneNumber)?.let {
                Log.d("formatNumber00", it)
                contactIncomingNum = it
            }


            for (contact in contacts) {
                contactID = contact?.id!!

                var contactNumber = contact?.numbers
                var contactNumberID = contact?.id


                try {
                    Log.d("dataRAt=====!!", contactNumberID)

                    contacts.forEach{
                        contactID = contact?.id!!

                        Log.d("dataRAt=====", contact.numbers.toString())
                        Log.d("dataRAt=====!", contact.id)

                        if(contactIncomingNum in contact.numbers)

                        {
                            Log.d("dataRAt=====!##", contact.id)
                            contactSharedData = contact.id
                            check = true

                            if(contactID == contactSharedData){

                            contactName = contact?.contactName!!

                            }

                        }


                    }

                   Log.d("dataRAt!!!!!!!!!!!!", "DAAAAA+ $contactID")
                    Log.d("dataRAt!!!!!!!!!!!!", "DBBBB+ $contact?.id")




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
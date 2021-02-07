package com.ar.contactUtils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast

class PhoneCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.let {
            if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                if (TelephonyManager.EXTRA_STATE_RINGING == state) {
                    val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    number?.let {
                        Toast.makeText(context, "Incoming call from $it", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
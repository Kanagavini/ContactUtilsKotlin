package com.ar.contactUtils.receiver

import android.app.Service
import android.content.Intent
import android.os.IBinder

class BroadcastService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(Intent("android.intent.action.PHONE_STATE"))
    }
}
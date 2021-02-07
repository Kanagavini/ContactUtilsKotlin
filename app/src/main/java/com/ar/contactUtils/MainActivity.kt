package com.ar.contactUtils

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_CONFIGURATION_CHANGED
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.ar.contactUtils.model.Contact
import com.ar.contactUtils.receiver.PhoneBroadCastReceiver
import com.ar.contactUtils.ui.ContactAdapter
import com.ar.contactUtils.utils.SharedPreference
import com.ar.contactUtils.viewModel.ContactViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ContactAdapter.ContactClickListener {
    var sharedPreference: SharedPreference? = null
    private val contactsViewModel by viewModels<ContactViewModel>()
    companion object {
        const val CONTACTS_READ_REQ_CODE = 100
        const val CONTACT_PERMISSION_REQUEST = 111
        const val REQUEST_CODE_OVER = 2

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreference = SharedPreference()
        init()
    }

    override fun onContactClickListener(contact: Contact?, pos: Int, checkStatus: Boolean) {
        if (checkStatus) {
            sharedPreference?.addContact(applicationContext, contact)
        } else {
            sharedPreference?.removeContact(applicationContext, contact)
        }


    }

    private fun init() {

        val adapter = ContactAdapter(this, this)
        contactRecycleView.adapter = adapter
        contactsViewModel.contactsLiveData.observe(this, Observer {
            adapter.contacts = it
        })

        if (hasAllPermission()) {
            contactsViewModel.getContactsDetails()
        } else{
            askPermissions()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!hasScreeningRole()) {
                requestScreeningRole()
            }
        }
        else{
            registerReceiver(PhoneBroadCastReceiver(), IntentFilter(ACTION_CONFIGURATION_CHANGED))
        }

        /** check if we already  have permission to draw over other apps  */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, REQUEST_CODE_OVER)
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACT_PERMISSION_REQUEST) {
            if (hasAllPermission())
            {
                if (hasAllPermission()) {
                    GlobalScope.launch(Dispatchers.IO) {

                    }

                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestScreeningRole() {
        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        val isHeld = roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        if (!isHeld) {
            //ask the user to set your app as the default screening app
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            startActivityForResult(intent, 123)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun hasScreeningRole(): Boolean {
        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        return roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent()
        intent.action = "android.intent.action.PHONE_STATE"
        sendBroadcast(intent)
        Log.d("debug", "Service Destory")

    }
    private fun askPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG
                ),
                CONTACT_PERMISSION_REQUEST
            )
        }
    }

    private fun hasAllPermission(): Boolean {

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CALL_LOG
                ) == PackageManager.PERMISSION_GRANTED
    }
}

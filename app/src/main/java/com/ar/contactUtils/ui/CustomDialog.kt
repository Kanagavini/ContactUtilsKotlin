package com.ar.contactUtils.ui

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.ar.contactUtils.databinding.ActivityDialogBinding

class CustomDialog: AppCompatActivity() {

   private lateinit var binding:ActivityDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityDialogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val params = window.attributes
        params.x = -100
        params.height = 600
        params.width = 1100
        params.y = -50

        this.window.attributes = params

        val contactNo:String = intent.getStringExtra("phone_no").toString()
        val contactName:String = intent.getStringExtra("phone_name").toString()

        binding.txtContactName.text = contactName
        binding.txtContactNumber.text = contactNo

        binding.imgClose.setOnClickListener(View.OnClickListener {

            finish()
        })

    }

}
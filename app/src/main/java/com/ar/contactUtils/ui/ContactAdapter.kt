package com.ar.contactUtils.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ar.contactUtils.R
import com.ar.contactUtils.databinding.ItemContactListBinding
import com.ar.contactUtils.model.Contact
import com.ar.contactUtils.utils.SharedPreference

class ContactAdapter(var context:Context,
                     private val contactClickListener: ContactClickListener):RecyclerView.Adapter<ContactAdapter.viewHolder>(){
    var sharedPreference: SharedPreference? = null

    var contacts = ArrayList<Contact>()
    set(value){
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
     val binding: ItemContactListBinding = DataBindingUtil.inflate(LayoutInflater.
     from(context), R.layout.item_contact_list,parent,false)
        sharedPreference = SharedPreference()

        return viewHolder(binding)
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val contactData = contacts[position]
        holder.binding.contact = contactData

        holder.binding.checkBox.isChecked = checkContactItem(contactData)
        with(holder.itemView){
            contactData.numbers.forEach{
                holder.binding.txtContactNumber.text=it
            }
        }
    }

    inner class viewHolder(binding: ItemContactListBinding) :RecyclerView.ViewHolder(binding.root){
        val binding:  ItemContactListBinding = binding

        init {
            binding.checkBox.setOnClickListener { view ->
                if (binding.checkBox.isChecked){
                    contactClickListener.onContactClickListener(contacts[adapterPosition], adapterPosition, true)
                }
                else{
                    contactClickListener.onContactClickListener(contacts[adapterPosition], adapterPosition, false)

                }
            }

        }



    }

    private fun checkContactItem(checkContact: Contact?): Boolean {
        var check = false
        val contacts: List<Contact?>? = sharedPreference!!.getContacts(context)
        if (contacts != null) {
            for (contact in contacts) {
                if (contact!! == checkContact) {
                    check = true
                    break
                }

            }
        }
        return check
    }

    interface ContactClickListener {
        fun onContactClickListener(contact: Contact?, pos: Int, checkStatus: Boolean)
    }

}
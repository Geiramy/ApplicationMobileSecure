package com.example.fil_rouge

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_web_service.*

class ContactAdapter (var c: Context, var contactList: List<Result>) : RecyclerView.Adapter<ContactHolder>(){

    //VIEWHOLDER IS INITIALIZED
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.model, null)
        return ContactHolder(v)
    }

    //DATA IS BOUND TO VIEWS
    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val contact = contactList[position]
        holder.nameTxt.text =contact.name.title.capitalize()+" "+ contact.name.last.toUpperCase() + " "+contact.name.first.capitalize()
        Picasso.get().load(contact.picture.large).into(holder.img)
        //holder.img.setImageResource(teacher.picture.large)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }


}
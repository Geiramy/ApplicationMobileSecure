package com.example.fil_rouge

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var img: ImageView
    var nameTxt: TextView

    lateinit var myItemClickListener: ItemClickListener

    init {

        nameTxt = itemView.findViewById(R.id.txtModelTotalName)
        img = itemView.findViewById(R.id.imgContact)
    }

    fun setItemClickListener(ic: ItemClickListener) {
        this.myItemClickListener = ic
    }

    override fun onClick(v: View) {
        this.myItemClickListener.onItemClick(v, layoutPosition)
    }
}
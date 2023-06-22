package com.ejrm.addme.ui.view.adapters

import androidx.recyclerview.widget.DiffUtil
import com.ejrm.addme.data.model.Contact

class ContactDiffUtil(private val oldList: List<Contact>, private val newList: List<Contact>):
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].phone == newList[newItemPosition].phone
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}
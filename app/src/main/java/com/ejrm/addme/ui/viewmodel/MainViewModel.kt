package com.ejrm.addme.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.model.ContactResponse
import com.ejrm.addme.domain.CrudContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getContactDomain: CrudContact) : ViewModel() {
    private var livedatalist: MutableLiveData<List<Contact>> = MutableLiveData()
    val isSuccefull: MutableLiveData<List<ContactResponse>> = MutableLiveData()

    fun getLiveDataObserver(): MutableLiveData<List<Contact>> = livedatalist
    fun getisSuccefull(): MutableLiveData<List<ContactResponse>> = isSuccefull

    fun getAllContact() {
        viewModelScope.launch {
            val contacts = getContactDomain.invoke()
            livedatalist.postValue(contacts)
        }
    }
    fun search(search: String) {
        viewModelScope.launch {
            val contacts = getContactDomain.invokeSearch(search)
            livedatalist.postValue(contacts)
        }
    }
    fun add(name: String, phone: String, instagram: String, facebook: String) {
        viewModelScope.launch {
            val Succefull = getContactDomain.invokeAdd(name,phone,instagram,facebook)
            println(Succefull)
            isSuccefull.postValue(Succefull)
        }
    }
}
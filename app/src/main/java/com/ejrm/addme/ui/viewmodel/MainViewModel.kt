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
    private var _livedatalist: MutableLiveData<List<Contact>> = MutableLiveData(listOf())
    val livedatalist: MutableLiveData<List<Contact>> = _livedatalist
    val isSuccessfull: MutableLiveData<List<ContactResponse>> = MutableLiveData()

    fun getLiveDataObserver(): MutableLiveData<List<Contact>> = livedatalist
    fun getSuccessfulObserver(): MutableLiveData<List<ContactResponse>> = isSuccessfull

    fun getAllContact() {
        viewModelScope.launch {
            val contacts = getContactDomain.invoke()
            _livedatalist.postValue(contacts)
        }
    }

    fun search(search: String) {
        viewModelScope.launch {
            val contacts = getContactDomain.invokeSearch(search)
            println(contacts)
            livedatalist.postValue(contacts)
        }
    }

    fun add(image:String, name: String, phone: String, instagram: String, facebook: String) {
        viewModelScope.launch {
            val Succefull = getContactDomain.invokeAdd(image, name, phone, instagram, facebook)
            isSuccessfull.postValue(Succefull)
        }
    }
}
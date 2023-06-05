package com.ejrm.addme.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.model.ContactResponse
import com.ejrm.addme.domain.CrudContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getContactDomain: CrudContact) : ViewModel() {

    private val _livedatalist = MutableLiveData<List<Contact>>()
    val livedatalist: LiveData<List<Contact>> = _livedatalist
    val isSuccessfull = MutableLiveData<List<ContactResponse>>()

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
            _livedatalist.postValue(contacts)
        }
    }

    fun add(name: String, country:String, phone: String, instagram: String, facebook: String) {
        viewModelScope.launch {
            val Succefull = getContactDomain.invokeAdd(name,country, phone, instagram, facebook)
            isSuccessfull.postValue(Succefull)
        }
    }
}
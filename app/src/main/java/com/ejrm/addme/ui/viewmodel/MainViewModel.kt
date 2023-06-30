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
    val livedatalist: LiveData<List<Contact>>
        get() = _livedatalist
    private val _isSuccessfull = MutableLiveData<List<ContactResponse>>()
    val isSuccessfull: LiveData<List<ContactResponse>>
        get() = _isSuccessfull

    init {
        getAllContact()
    }

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

    fun add(
        name: String,
        country: String,
        phone: String,
        instagram: String,
        facebook: String,
        password: String
    ) {
        viewModelScope.launch {
            val Succefull =
                getContactDomain.invokeAdd(name, country, phone, instagram, facebook, password)
            _isSuccessfull.postValue(Succefull)
        }
    }

    fun updateContactViewModel(
        id_contacto: String,
        name: String,
        country: String,
        phone: String,
        instagram: String,
        facebook: String,
        password: String
    ) {
        viewModelScope.launch {
            val Succefull = getContactDomain.invokeUpdateCrud(
                id_contacto,
                name,
                country,
                phone,
                instagram,
                facebook,
                password
            )
            _isSuccessfull.postValue(Succefull)
        }
    }

    fun loginViewModel(phone: String, password: String) {
        viewModelScope.launch {
            val Succefull = getContactDomain.loginCrud(phone, password)
            _isSuccessfull.postValue(Succefull)
        }
    }
}
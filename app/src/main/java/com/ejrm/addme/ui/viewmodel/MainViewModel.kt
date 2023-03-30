package com.ejrm.addme.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.domain.GetContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getContactDomain: GetContact) : ViewModel() {
    private var livedatalist: MutableLiveData<List<Contact>> = MutableLiveData()

    fun getLiveDataObserver(): MutableLiveData<List<Contact>> {
        return livedatalist
    }

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
}
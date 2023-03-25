package com.ejrm.addme.ui.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ejrm.addme.R
import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.databinding.ActivityMainBinding
import com.ejrm.addme.ui.view.adapters.ContactAdapter
import com.ejrm.addme.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.sql.DriverManager
import java.sql.DriverManager.println
import java.util.jar.Manifest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    private lateinit var adapter: ContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.recyclerContact.layoutManager = LinearLayoutManager(this)
        val listempty: List<Contact> = emptyList()
        adapter = ContactAdapter(listempty, ContactItemClickListener())
        binding.recyclerContact.adapter = adapter
        viewModel.getLiveDataObserver().observe(this, Observer {
            println(it.size)
            adapter.setContacList(it)
            adapter.notifyDataSetChanged()
        })
        viewModel.getAllContact()
    }

    private inner class ContactItemClickListener : ContactAdapter.ContactAdapterListener {
        override fun onContactSelected(contact: Contact) {
            //listener
        }
    }

    fun checkForInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    suspend fun dataConexion(url: String): Boolean {
        delay(1500)
        val httpConnection: HttpURLConnection =
            URL(url)
                .openConnection() as HttpURLConnection
        if (checkForInternet(this)) {
            try {
                httpConnection.setRequestProperty("User-Agent", "Android")
                httpConnection.connectTimeout = 1500
                httpConnection.connect()
                return httpConnection.responseCode == 200
            } catch (e: Exception) {
                println(e.toString())
                return false
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        //registerReceiver(estadoRed, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun initRecyclerView() {

    }

    private val estadoRed = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (checkForInternet(baseContext)) {
                GlobalScope.launch(Dispatchers.Main) {
                    var response =
                        withContext(Dispatchers.IO) { dataConexion("https://www.google.com") }
                    if (!response) {
                        Snackbar.make(
                            binding.root,
                            "No tiene conexión con la red",
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                        binding.viewLoading.isVisible = true
                        binding.searchView.isVisible = false
                        binding.recyclerContact.isVisible = false
                    } else {
                        binding.viewLoading.isVisible = false
                        binding.searchView.isVisible = true
                        binding.recyclerContact.isVisible = true
                        Snackbar.make(binding.root, "Conectado!!!", Snackbar.LENGTH_LONG).show()
                    }
                }
            } else {
                val snackbar: Snackbar = Snackbar.make(
                    binding.root,
                    "Active sus datos móviles o wifi",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.show()
                binding.viewLoading.isVisible = true
                binding.searchView.isVisible = false
                binding.recyclerContact.isVisible = false
            }
        }
    }
}
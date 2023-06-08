package com.ejrm.addme.ui.view

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ejrm.addme.R
import com.ejrm.addme.data.model.Contact
import com.ejrm.addme.data.model.ContactResponse
import com.ejrm.addme.databinding.ActivityMainBinding
import com.ejrm.addme.databinding.AddContactBinding
import com.ejrm.addme.ui.view.adapters.ContactAdapter
import com.ejrm.addme.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.hbb20.CountryCodePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.sql.DriverManager
import java.sql.DriverManager.println
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var addContactBinding: AddContactBinding
    lateinit var viewModel: MainViewModel
    private lateinit var menu: Menu
    var dialog: AlertDialog? = null
    private lateinit var adapter: ContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        initViewModel()
        binding.swipe.setOnRefreshListener {
            initViewModel()
            binding.swipe.isRefreshing = false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.livedatalist.observe(this@MainActivity, Observer {
                    if (it != null) {
                        adapter.updateList(it)
                        adapter.notifyDataSetChanged()
                    } else {
                        Snackbar.make(binding.root, "No hay resultados", Snackbar.LENGTH_LONG)
                            .show()
                    }
                })
                viewModel.search(p0!!)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.equals("")) {
                    initViewModel()
                }
                return true
            }
        })
    }

    private fun initRecyclerView() {
        binding.recyclerContact.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(
            onClickListener = { contact -> onItemSelected(contact) },
            onClickWhatsapp = { whatsapp -> onClickWhatsapp(whatsapp) },
            onClickInstagram = { instagram -> onClickInstagram(instagram) },
            onClickFacebook = { facebook -> onClickFacebook(facebook) })
        binding.recyclerContact.adapter = adapter
    }

    fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.livedatalist.observe(this@MainActivity, Observer {
            adapter.updateList(it)
            adapter.notifyDataSetChanged()
        })
        viewModel.getAllContact()
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun onItemSelected(contact: Contact) {

    }

    private fun onClickWhatsapp(whatsapp: String) {
        openLink("https://wa.me/$whatsapp?text=Hola%20vengo%20desde%20AddMe%20la%20aplicación,%20agregame%20para%20ver%20estados.")
    }

    private fun onClickInstagram(instagram: String) {
        openLink("https://www.instagram.com/$instagram")
    }

    private fun onClickFacebook(facebook: String) {
        openLink("https://www.facebook.com/$facebook")
    }

    fun Context.checkForInternet(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            } ?: false
        } else {
            @Suppress("DEPRECATION")
            connectivityManager?.activeNetworkInfo?.isConnected ?: false
        }
    }
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET])
    suspend fun dataConexion(url: String): Boolean = try {
        withContext(Dispatchers.IO) {
            URL(url).openConnection() as HttpURLConnection
        }.apply {
            requestMethod = "HEAD"
            setRequestProperty("User-Agent", "Android")
            connectTimeout = 1500
            readTimeout = 1500
        }.let {
            it.connect()
            it.responseCode == HttpURLConnection.HTTP_OK
        }
    } catch (e: Exception) {
        println("Error al conectar: ${e.message}")
        false
    }
    override fun onResume() {
        super.onResume()
        registerReceiver(estadoRed, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private val estadoRed = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (checkForInternet()) {
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
                        menu.findItem(R.id.add)?.isVisible = false
                        binding.searchView.isVisible = false
                        binding.recyclerContact.isVisible = false
                    } else {
                        binding.viewLoading.isVisible = false
                        menu.findItem(R.id.add)?.isVisible = true
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
                menu.findItem(R.id.add)?.isVisible = false
                binding.searchView.isVisible = false
                binding.recyclerContact.isVisible = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        this.menu = menu!!
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                addContactBinding = AddContactBinding.inflate(layoutInflater)
                val alertdialog = AlertDialog.Builder(this)
                alertdialog.setTitle("Add Me")
                alertdialog.setView(addContactBinding.root)
                addContactBinding.login.setOnClickListener(View.OnClickListener {
                    addContactBinding.name.isVisible = false
                    addContactBinding.instagram.isVisible = false
                    addContactBinding.facebook.isVisible = false
                    addContactBinding.btnAddContact.isVisible = false
                    addContactBinding.btnloginContact.isVisible = true
                })
                addContactBinding.CodeCountry.registerCarrierNumberEditText(addContactBinding.whatsapp)

                addContactBinding.btnloginContact.setOnClickListener {
                    if (addContactBinding.whatsapp.text.toString() != "" || addContactBinding.password.text.toString() != "") {
                        if (addContactBinding.CodeCountry.isValidFullNumber) {
                            addContactBinding.progress.isVisible = true
                            val phoneNumber = addContactBinding.CodeCountry.fullNumber.toString()
                            viewModel.loginViewModel(
                                phoneNumber,
                                addContactBinding.password.text.toString()
                            )
                        } else {
                            Snackbar.make(binding.root, "Teléfono Incorrecto", Snackbar.LENGTH_LONG)
                                .show()
                        }
                        viewModel.getSuccessfulObserver().observe(this, Observer {
                            val response: ContactResponse = it[0]
                            addContactBinding.progress.isVisible = false
                            Snackbar.make(
                                binding.root,
                                response.message,
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            dialog!!.dismiss()
                            if (response.success){
                                menu.findItem(R.id.face)?.isVisible = true
                                menu.findItem(R.id.add)?.isVisible = false
                            }
                        })
                        viewModel.getSuccessfulObserver()
                        viewModel.getAllContact()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "El teléfono y contraseña son obligatorios",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                }
                addContactBinding.btnAddContact.setOnClickListener {
                    if (addContactBinding.name.text.toString() != "" || addContactBinding.whatsapp.text.toString() != "" || addContactBinding.password.text.toString() != "") {
                        if (addContactBinding.CodeCountry.isValidFullNumber) {
                            addContactBinding.progress.isVisible = true
                            val phoneNumber = addContactBinding.CodeCountry.fullNumber.toString()
                            val country = addContactBinding.CodeCountry.selectedCountryCode
                            viewModel.add(
                                addContactBinding.name.text.toString(),
                                country,
                                phoneNumber,
                                addContactBinding.instagram.text.toString(),
                                addContactBinding.facebook.text.toString(),
                                addContactBinding.password.text.toString()
                            )
                        } else {
                            Snackbar.make(binding.root, "Teléfono Incorrecto", Snackbar.LENGTH_LONG)
                                .show()
                        }
                        viewModel.getSuccessfulObserver().observe(this, Observer {
                            val response: ContactResponse = it[0]
                            addContactBinding.progress.isVisible = false
                            Snackbar.make(
                                binding.root,
                                response.message,
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            dialog!!.dismiss()
                        })
                        viewModel.getSuccessfulObserver()
                        viewModel.getAllContact()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "El nombre, teléfono y contraseña son obligatorios",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                dialog = alertdialog.create()
                dialog!!.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
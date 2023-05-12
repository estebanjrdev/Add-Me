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
import com.ejrm.addme.databinding.ActivityMainBinding
import com.ejrm.addme.databinding.AddContactBinding
import com.ejrm.addme.ui.view.adapters.ContactAdapter
import com.ejrm.addme.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.sql.DriverManager.println
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var addContactBinding: AddContactBinding
    lateinit var baos: ByteArrayOutputStream
    lateinit var viewModel: MainViewModel
    lateinit var contact: Contact
    lateinit var bitmap: Bitmap
    private var imageUri: Uri? = null
    var dialog: AlertDialog? = null
    private lateinit var adapter: ContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
       // bitmap = BitmapFactory.decodeResource(this.resources,R.drawable.ic_person_24)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getLiveDataObserver().observe(this@MainActivity, Observer {
           // if (it.isNotEmpty()) {
                adapter.updateList(it)
           /* } else {
                Snackbar.make(binding.root, "No Contactos", Snackbar.LENGTH_LONG)
                    .show()
            }*/
        })
        viewModel.getAllContact()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.getLiveDataObserver().observe(this@MainActivity, Observer {
                    if (it.isNotEmpty()) {
                        adapter.updateList(it)
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
                    initRecyclerView()
                }
                return true
            }
        })

    }

    fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    fun onItemSelected(contact: Contact) {

    }

    fun initRecyclerView() {
        binding.recyclerContact.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter { contact -> onItemSelected(contact) }
        binding.recyclerContact.adapter = adapter
    }
    fun Context.checkForInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            } ?: false
        } else {
            @Suppress("DEPRECATION")
            connectivityManager?.activeNetworkInfo?.isConnected ?: false
        }
    }

   /* fun checkForInternet(context: Context): Boolean {
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
    }*/
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

  /*  @RequiresPermission(anyOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET])
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
    }*/

    override fun onResume() {
        super.onResume()
        initRecyclerView()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                addContactBinding = AddContactBinding.inflate(layoutInflater)
                val alertdialog = AlertDialog.Builder(this)
                alertdialog.setTitle("Add Contacto")
                alertdialog.setView(addContactBinding.root)
               /* addContactBinding.imgPerfil.setOnClickListener {
                    checkPermissionAndOpenImageSelector()
                }*/
                addContactBinding.btnAddContact.setOnClickListener {
                    addContactBinding.progress.isVisible = true
                    viewModel.add(addContactBinding.name.text.toString(),
                        addContactBinding.whatsapp.text.toString(),
                        addContactBinding.instagram.text.toString(),
                        addContactBinding.facebook.text.toString())
                    viewModel.getSuccessfulObserver().observe(this, Observer {
                        if (it.isNotEmpty()) {
                            addContactBinding.progress.isVisible = false
                            Snackbar.make(binding.root, "Contacto Agregado", Snackbar.LENGTH_LONG)
                                .show()
                            dialog!!.dismiss()
                        } else {
                            Snackbar.make(
                                binding.root,
                                "Contacto No Agregado",
                                Snackbar.LENGTH_LONG
                            ).show()
                            dialog!!.dismiss()
                        }
                    })
                    viewModel.getSuccessfulObserver()
                }
                dialog = alertdialog.create()
                dialog!!.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*fun getStringImage(bitmap: Bitmap): String {
        baos = ByteArrayOutputStream()
       bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val array: ByteArray = baos.toByteArray()
        return Base64.encodeToString(array,Base64.DEFAULT)
    }

    private fun checkPermissionAndOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openImageSelector()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
            }
        }
    }

    private fun openImageSelector() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    imageUri = uri
                    val foto = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    bitmap  = Bitmap.createScaledBitmap(foto, 150, 150, true)
                    addContactBinding.imgPerfil.setImageBitmap(bitmap)
                }
            }
        }*/

    fun getRequestBodyFromUri( uri: Uri): RequestBody {
        val inputStream = this.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        val MEDIA_TYPE_JPEG = "image/jpeg".toMediaTypeOrNull()
        return RequestBody.create(MEDIA_TYPE_JPEG, bytes!!)
    }

   /* @RequiresApi(Build.VERSION_CODES.O)
    fun upLoadMultiPart(name: String, phone: String, facebook:String, instagram: String){
       /* val textName = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val textWhatsApp = RequestBody.create("text/plain".toMediaTypeOrNull(), phone)
        val textFacebook = RequestBody.create("text/plain".toMediaTypeOrNull(), facebook)
        val textInstagram = RequestBody.create("text/plain".toMediaTypeOrNull(), instagram)*/
        val image = getRequestBodyFromUri(uri)
        val currentDateTime = LocalDateTime.now()
        val multipartFile = MultipartBody.Part.createFormData("image", name+"_"+currentDateTime.toString()+".png", image)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(multipartFile)
            .addFormDataPart("name",name)
            .addFormDataPart("phone",phone)
            .addFormDataPart("facebook",facebook)
            .addFormDataPart("instagram",instagram)
            .build()
        viewModel.add(name,phone,facebook,instagram)
    }*/

   /* override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageSelector()
            }
        }
    }

    companion object {
        private const val PERMISSION_CODE = 100
    }*/
}
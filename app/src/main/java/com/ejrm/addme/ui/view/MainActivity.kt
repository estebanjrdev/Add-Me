package com.ejrm.addme.ui.view

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.ejrm.addme.ui.viewmodel.NetworkStateViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.sql.DriverManager.println
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var addContactBinding: AddContactBinding
    lateinit var viewModel: MainViewModel
    lateinit var networkviewmodel: NetworkStateViewModel
    private lateinit var menu: Menu
    private lateinit var contact: Contact
    var dialog: AlertDialog? = null
    private lateinit var adapter: ContactAdapter

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        networkviewmodel = ViewModelProvider(this).get(NetworkStateViewModel::class.java)
        networkviewmodel.isNetworkAvailable.observe(this, Observer { isAvailable ->
            if (isAvailable) {
                initRecyclerView()
                initViewModel()
            } else {
                Snackbar.make(
                    binding.root,
                    "Verifique su conexión a internet",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
                binding.viewLoading.isVisible = true
                menu.findItem(R.id.add)?.isVisible = false
                binding.searchView.isVisible = false
                binding.recyclerContact.isVisible = false
            }
        })

        binding.swipe.setOnRefreshListener {
            viewModel.getAllContact()
            binding.swipe.isRefreshing = false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.livedatalist.observe(this@MainActivity, Observer {
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
                    viewModel.getAllContact()
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
            if (it.isNotEmpty()) {
                adapter.updateList(it)
                binding.viewLoading.isVisible = false
                menu.findItem(R.id.add)?.isVisible = true
                binding.searchView.isVisible = true
                binding.recyclerContact.isVisible = true
            }
        })
        viewModel.getAllContact()
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun onItemSelected(contact: Contact) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            if (saveContact(this, contact.name, "+" + contact.phone)) {
                Snackbar.make(
                    binding.root,
                    "Contacto Guardado",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            } else {
                Snackbar.make(
                    binding.root,
                    "Ya existe el contacto",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    }

    private fun saveContact(context: Context, nombre: String, numero: String): Boolean {
        val contentResolver = context.contentResolver

        val existingContactUri = buscarContactoPorNumero(context, numero)
        if (existingContactUri != null) {
            return false
        }

        val contentValues = ContentValues().apply {
            put(ContactsContract.RawContacts.ACCOUNT_TYPE, null as String?)
            put(ContactsContract.RawContacts.ACCOUNT_NAME, null as String?)
        }

        val rawContactUri =
            contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)

        val rawContactId = rawContactUri?.lastPathSegment

        val values = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nombre)
        }

        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)

        val phoneValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, numero)
            put(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )
        }

        contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)

        return true
    }

    private fun buscarContactoPorNumero(context: Context, numero: String): Uri? {
        val contentResolver = context.contentResolver

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        val selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?"
        val selectionArgs = arrayOf(numero)
        val sortOrder = null

        contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val contactIdColumnIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val contactId = cursor.getString(contactIdColumnIndex)
                return ContactsContract.Contacts.getLookupUri(
                    contactId.toLong(),
                    ContactsContract.Contacts.LOOKUP_KEY
                )
            }
        }

        return null
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

    fun validPassword(password: String): Boolean {
        return password.length >= 8
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
                alertdialog.setTitle("Agregame")
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
                    if (addContactBinding.whatsapp.text.toString() != "" && addContactBinding.password.text.toString() != "") {
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
                        viewModel.isSuccessfull.observe(this, Observer {
                            val response: ContactResponse = it[0]
                            addContactBinding.progress.isVisible = false
                            Snackbar.make(
                                binding.root,
                                response.message,
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                            dialog!!.dismiss()
                            if (response.success) {
                                val phone = response.phone.subSequence(
                                    response.country.length,
                                    response.phone.length
                                ).toString()
                                contact = Contact(
                                    response.id_contacto,
                                    response.name,
                                    response.country,
                                    phone,
                                    response.instagram,
                                    response.facebook,
                                    response.password
                                )
                                saveContact(this, contact)
                                menu.findItem(R.id.face)?.isVisible = true
                                menu.findItem(R.id.add)?.isVisible = false
                            }
                        })
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
                    if (addContactBinding.name.text.toString() != "" && addContactBinding.whatsapp.text.toString() != "" && addContactBinding.password.text.toString() != "") {
                        if (validPassword(addContactBinding.password.text.toString())) {
                            if (addContactBinding.CodeCountry.isValidFullNumber) {
                                addContactBinding.progress.isVisible = true
                                val phoneNumber =
                                    addContactBinding.CodeCountry.fullNumber.toString()
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
                                Snackbar.make(
                                    binding.root,
                                    "Teléfono Incorrecto",
                                    Snackbar.LENGTH_LONG
                                )
                                    .show()
                            }
                            viewModel.isSuccessfull.observe(this, Observer {
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
                            viewModel.getAllContact()
                        } else {
                            Snackbar.make(
                                binding.root,
                                "La contraseña debe tener mínimo 8 caracteres",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
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
            R.id.face -> {
                addContactBinding = AddContactBinding.inflate(layoutInflater)
                val alertdialog = AlertDialog.Builder(this)
                alertdialog.setTitle("Mi Contacto")
                alertdialog.setView(addContactBinding.root)
                addContactBinding.login.isVisible = false
                addContactBinding.btncloseSession.isVisible = true
                addContactBinding.whatsapp.isEnabled = false
                addContactBinding.btnAddContact.text = "Actualizar Contacto"
                addContactBinding.CodeCountry.registerCarrierNumberEditText(addContactBinding.whatsapp)
                contact = getContact(this)

                addContactBinding.name.text =
                    Editable.Factory.getInstance().newEditable(contact.name)
                addContactBinding.whatsapp.text =
                    Editable.Factory.getInstance().newEditable(contact.phone)
                addContactBinding.facebook.text =
                    Editable.Factory.getInstance().newEditable(contact.facebook)
                addContactBinding.instagram.text =
                    Editable.Factory.getInstance().newEditable(contact.instagram)
                addContactBinding.password.text =
                    Editable.Factory.getInstance().newEditable(contact.password)
                addContactBinding.btnAddContact.setOnClickListener {
                    if (addContactBinding.name.text.toString() != "" || addContactBinding.password.text.toString() != "") {
                        if (addContactBinding.CodeCountry.isValidFullNumber) {
                            addContactBinding.progress.isVisible = true
                            val phoneNumber = addContactBinding.CodeCountry.fullNumber.toString()
                            println(addContactBinding.whatsapp.text.toString())
                            val country = addContactBinding.CodeCountry.selectedCountryCode
                            viewModel.updateContactViewModel(
                                contact.id_contacto,
                                addContactBinding.name.text.toString(),
                                country,
                                phoneNumber,
                                addContactBinding.instagram.text.toString(),
                                addContactBinding.facebook.text.toString(),
                                addContactBinding.password.text.toString()
                            )
                            contact = Contact(
                                contact.id_contacto,
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
                        viewModel.isSuccessfull.observe(this, Observer {
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
                        viewModel.getAllContact()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "El nombre y contraseña son obligatorios",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                addContactBinding.btncloseSession.setOnClickListener {
                    menu.findItem(R.id.face)?.isVisible = false
                    menu.findItem(R.id.add)?.isVisible = true
                    dialog!!.dismiss()
                }
                dialog = alertdialog.create()
                dialog!!.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveContact(context: Context, contact: Contact) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyContact", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("id_contacto", contact.id_contacto)
        editor.putString("name", contact.name)
        editor.putString("country", contact.country)
        editor.putString("phone", contact.phone)
        editor.putString("instagram", contact.instagram)
        editor.putString("facebook", contact.facebook)
        editor.putString("password", contact.password)
        editor.apply()
    }

    private fun getContact(context: Context): Contact {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyContact", Context.MODE_PRIVATE)
        val id_contacto = sharedPreferences.getString("id_contacto", "") ?: ""
        val name = sharedPreferences.getString("name", "") ?: ""
        val country = sharedPreferences.getString("country", "") ?: ""
        val phone = sharedPreferences.getString("phone", "") ?: ""
        val instagram = sharedPreferences.getString("instagram", "") ?: ""
        val facebook = sharedPreferences.getString("facebook", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""
        return Contact(id_contacto, name, country, phone, instagram, facebook, password)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso Concedido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
package com.example.stores.mainModule

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.*
import com.example.stores.common.utils.MainAux
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.ActivityMainBinding
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.mainModule.adapter.OnClickListener
import com.example.stores.mainModule.adapter.StoreAdapter
import com.example.stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {
    private lateinit var nbinding: ActivityMainBinding
    private lateinit var nadapter: StoreAdapter
    private lateinit var nGridLayout: GridLayoutManager
    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(nbinding.root)

        nbinding.fab.setOnClickListener {
            launchEditFragment()
        }

        setupViewModel()

        setupRecyclerView()
    }

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mMainViewModel.getStores().observe(this) { stores ->
            nadapter.setStores(stores)
        }
    }

    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()
        if (args != null) fragment.arguments = args
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.commit()
        hideFab()
    }

    private fun setupRecyclerView() {
        nadapter = StoreAdapter(mutableListOf(), this)
        nGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
//        getStores()

        nbinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = nGridLayout
            adapter = nadapter
        }
    }

//    private fun getStores(){
//
//        doAsync {
//            val stores = StoreApplication.database.storDao().getAllStores()
//            uiThread {
//                nadapter.setStores(stores)
//            }
//        }
//    }

    //OnClickListener
    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)
        launchEditFragment(args)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        doAsync {
            StoreApplication.database.storDao().updateStore(storeEntity)
            uiThread {
                updateStore(storeEntity)
            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {

        val items = resources.getStringArray(R.array.array_options_item)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { dialog, which ->
                when(which){
                    0 -> confirmDelete(storeEntity)

                    1 -> dial(storeEntity.phone)

                    2 -> goToWebsite(storeEntity.website)
                }
            }
            .show()
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        doAsync {
            StoreApplication.database.storDao().deleteStore(storeEntity)
            uiThread {
                nadapter.delete(storeEntity)
            }
        }
    }

    private fun dial(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        starIntent(callIntent)
    }

    private fun goToWebsite(webSite: String){
        if (webSite.isEmpty()){
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        }else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(webSite)
            }
            starIntent(websiteIntent)
        }
    }

    private fun starIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_SHORT).show()
    }
    /*
    *MainAux
    */

    override fun hideFab(isVisible: Boolean) {
        if (isVisible) nbinding.fab.show() else nbinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        nadapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        nadapter.update(storeEntity)
    }
}
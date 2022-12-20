package com.example.stores.mainModule

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.*
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.TypeError
import com.example.stores.databinding.ActivityMainBinding
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.adapter.OnClickListener
import com.example.stores.mainModule.adapter.StoreAdapter
import com.example.stores.mainModule.adapter.StoreListAdapter
import com.example.stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var nbinding: ActivityMainBinding
    private lateinit var nadapter: StoreListAdapter
    private lateinit var nGridLayout: GridLayoutManager
    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel
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
            nbinding.progressBar.visibility = View.GONE
            nadapter.submitList(stores)
        }
        mMainViewModel.isLoading.observe(this){
            nbinding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        mMainViewModel.getTypeError().observe(this){
            val msgRes = when(it){
                TypeError.GET -> getString(R.string.main_error_get)
                TypeError.INSERT -> getString(R.string.main_error_insert)
                TypeError.UPDATE -> getString(R.string.main_error_update)
                TypeError.DELETE -> getString(R.string.main_error_delete)
                else -> getString(R.string.main_error_unknow)
            }
            Snackbar.make(nbinding.root, msgRes, Snackbar.LENGTH_LONG).show()
        }

        mEditStoreViewModel = ViewModelProvider(this).get(EditStoreViewModel::class.java)
        mEditStoreViewModel.getShowFab().observe(this){ isVisible ->
            if (isVisible) nbinding.fab.show() else nbinding.fab.hide()
        }
    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {
        mEditStoreViewModel.setShowFab(false)
        mEditStoreViewModel.setStoreSelect(storeEntity)
        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.commit()
    }

    private fun setupRecyclerView() {
        nadapter = StoreListAdapter( this)
        nGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
        nbinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = nGridLayout
            adapter = nadapter
        }
    }
    //OnClickListener
    override fun onClick(storeEntity: StoreEntity) {

        launchEditFragment(storeEntity)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {

        mMainViewModel.updateStore(storeEntity)
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
        mMainViewModel.deleteStore(storeEntity)
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
}
package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {
    private lateinit var nbinding: ActivityMainBinding
    private lateinit var nadapter: StoreAdapter
    private lateinit var nGridLayout: GridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(nbinding.root)

        nbinding.fab.setOnClickListener {
            launchEditFragment()
        }
        setupRecyclerView()
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
        nGridLayout = GridLayoutManager(this, 2)
        getStores()

        nbinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = nGridLayout
            adapter = nadapter
        }
    }

    private fun getStores(){

        doAsync {
            val stores = StoreApplication.database.storDao().getAllStores()
            uiThread {
                nadapter.setStores(stores)
            }
        }
    }

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
                nadapter.update(storeEntity)
            }
        }
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        doAsync {
            StoreApplication.database.storDao().deleteStore(storeEntity)
            uiThread {
                nadapter.delete(storeEntity)
            }
        }
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

    override fun update(storeEntity: StoreEntity) {
        TODO("Not yet implemented")
    }

}
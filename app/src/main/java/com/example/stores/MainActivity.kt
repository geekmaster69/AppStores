package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var nbinding: ActivityMainBinding
    private lateinit var nadapter: StoreAdapter
    private lateinit var nGridLayout: GridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(nbinding.root)

        nbinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = nbinding.edName.text.toString().trim())
            Thread {
                StoreApplication.database.storDao().addtStore(store)
            }.start()
            nadapter.add(store)
        }
        setupRecyclerView()
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
    override fun onClick(storeEntity: StoreEntity) {

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


}
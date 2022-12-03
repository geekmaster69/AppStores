package com.example.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding

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

        nbinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = nGridLayout
            adapter = nadapter
        }
    }

    //OnClickListener
    override fun onClick(storeEntity: StoreEntity) {
        TODO("Not yet implemented")
    }
}
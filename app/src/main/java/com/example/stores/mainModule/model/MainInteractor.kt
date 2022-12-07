package com.example.stores.mainModule.model

import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainInteractor {

    interface StoresCallback{
        fun getStoresCallback(callback: MutableList<StoreEntity>)
    }

    fun getStoresCallback(stores: StoresCallback){
        doAsync {
            val storesList = StoreApplication.database.storDao().getAllStores()
            uiThread {
                stores.getStoresCallback(storesList)

            }
        }
    }
}
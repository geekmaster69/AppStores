package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.common.entities.StoreEntity
import com.example.stores.mainModule.model.MainInteractor

class MainViewModel: ViewModel() {
    private val stores: MutableLiveData<MutableList<StoreEntity>> = MutableLiveData()
    private var interactor: MainInteractor = MainInteractor()



    fun getStores(): LiveData<MutableList<StoreEntity>> {
        loadStores()
        return stores
    }

    private fun loadStores(){
        interactor.getStoresCallback(object : MainInteractor.StoresCallback{
            override fun getStoresCallback(callback: MutableList<StoreEntity>) {

                this@MainViewModel.stores.value = callback
            }
        })
    }
}
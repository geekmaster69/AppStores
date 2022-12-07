package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.common.entities.StoreEntity
import com.example.stores.mainModule.model.MainInteractor

class MainViewModel: ViewModel() {

    private var interactor: MainInteractor = MainInteractor()
    private var storeLIst: MutableList<StoreEntity> = mutableListOf()

    fun getStores(): LiveData<MutableList<StoreEntity>> {
        return stores
    }

    private val stores: MutableLiveData<MutableList<StoreEntity>> by lazy{
        MutableLiveData<MutableList<StoreEntity>>().also { loadStores() }
    }

    private fun loadStores(){
        interactor.getStores {
            stores.value = it
            storeLIst = it

        }
    }

     fun deleteStore(storeEntity: StoreEntity){
        interactor.deleteStore(storeEntity){
            val index = storeLIst.indexOf(storeEntity)
            if (index != -1){
                storeLIst.removeAt(index)
                stores.value = storeLIst
            }
        }
    }

     fun updateStore(storeEntity: StoreEntity){
         storeEntity.isFavorite = !storeEntity.isFavorite
        interactor.updateStore(storeEntity){
            val index = storeLIst.indexOf(storeEntity)
            if (index != -1){
                storeLIst.set(index, storeEntity)
                stores.value = storeLIst
            }
        }
    }
}
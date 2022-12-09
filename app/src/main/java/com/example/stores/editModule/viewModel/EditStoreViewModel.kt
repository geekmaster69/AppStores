package com.example.stores.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.common.entities.StoreEntity
import com.example.stores.editModule.model.EditStoreInteractor

class EditStoreViewModel : ViewModel(){
    private var interactor: EditStoreInteractor = EditStoreInteractor()
    private val storeSelect  = MutableLiveData<StoreEntity>()
    private val showFab = MutableLiveData<Boolean>()
    private val result = MutableLiveData<Any>()



    fun setStoreSelect(storeEntity: StoreEntity){
        storeSelect.value = storeEntity
    }

    fun getStoreSelected(): LiveData<StoreEntity>{
        return storeSelect
    }

    fun setShowFab(isVisible: Boolean){
        showFab.value = isVisible
    }

    fun getShowFab(): LiveData<Boolean>{
        return showFab
    }

    fun setResult(value: Any){
        result.value = value
    }

    fun getResult(): LiveData<Any>{
        return result
    }

    fun saveStore(storeEntity: StoreEntity){
        interactor.saveStore(storeEntity){ newId ->
            result.value = newId
        }
    }

    fun updateStore(storeEntity: StoreEntity){
        interactor.updateStore(storeEntity){ storeUpdate ->
            result.value = storeUpdate
        }
    }
}
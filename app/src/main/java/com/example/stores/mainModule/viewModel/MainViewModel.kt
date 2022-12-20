package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.example.stores.common.utils.StoresException
import com.example.stores.common.utils.TypeError
import com.example.stores.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class MainViewModel: ViewModel() {

    private var interactor: MainInteractor = MainInteractor()
    var isLoading = MutableLiveData<Boolean>()

    fun getStores(): LiveData<MutableList<StoreEntity>> {
        return stores
    }
    private val typeError: MutableLiveData<TypeError> = MutableLiveData()

    fun getTypeError(): MutableLiveData<TypeError> = typeError

    private val stores = interactor.stores


     fun deleteStore(storeEntity: StoreEntity){

         executeAction { interactor.deleteStore(storeEntity) }
    }

     fun updateStore(storeEntity: StoreEntity){

         storeEntity.isFavorite = !storeEntity.isFavorite
         executeAction { interactor.updateStore(storeEntity) }
    }

    private fun executeAction(block: suspend  () -> Unit): Job {
        return viewModelScope.launch {
            isLoading.postValue(Constants.SHOW)
            try {
                block()
            }catch (e: StoresException){
                typeError.value = e.typeError
            }finally {
                isLoading.postValue(Constants.HIDE)
            }
        }
    }
}
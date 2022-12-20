package com.example.stores.editModule.model

import androidx.lifecycle.LiveData
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.StoresException
import com.example.stores.common.utils.TypeError

class EditStoreInteractor {

    fun getStoreById(id: Long): LiveData<StoreEntity>{
        return StoreApplication.database.storeDao().getStoreById(id)
    }

    suspend fun saveStore(storeEntity: StoreEntity){
        val result = StoreApplication.database.storeDao().addStore(storeEntity)
        if (result == 0L) throw StoresException(TypeError.INSERT)

    }

    suspend fun updateStore(storeEntity: StoreEntity){
        val result = StoreApplication.database.storeDao().updateStore(storeEntity)
        if (result == 0) throw StoresException(TypeError.UPDATE)

    }
}
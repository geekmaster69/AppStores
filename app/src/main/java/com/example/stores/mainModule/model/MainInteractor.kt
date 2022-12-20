package com.example.stores.mainModule.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.example.stores.common.utils.StoresException
import com.example.stores.common.utils.TypeError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainInteractor {

    val stores: LiveData<MutableList<StoreEntity>> = liveData {

        val storesLiveData = StoreApplication.database.storeDao().getAllStores()
       delay(1000)// Para pruebas

        emitSource(storesLiveData.map { stores ->
            stores.sortedBy { it.name }.toMutableList()

        })

    }


//    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
//        var storeList = mutableListOf<StoreEntity>()
//        val url = Constants.STORES_URL + Constants.GET_ALL_PATH
//        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
//
//            val status = response.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)
//            if (status == Constants.SUCCES){
//
//                val jsonList = response.optJSONArray(Constants.STORES_PROPERTY)?.toString()
//
//                if(jsonList != null) {
//                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>() {}.type
//                     storeList = Gson().fromJson(jsonList, mutableListType)
//
//                    callback(storeList)
//                    return@JsonObjectRequest
//                }
//            }
//            callback(storeList)
//        },{
//            it.printStackTrace()
//            callback(storeList)
//        })
//          StoreApplication.storeAPI.addToRequestQueue(jsonObjectRequest)
//    }

//    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
//        doAsync {
//            val storesList = StoreApplication.database.storeDao().getAllStores()
//            uiThread {
//                val json = Gson().toJson(storesList)
//                Log.i("Gson", json)
//                callback(storesList)
//            }
//        }
//    }



    suspend fun deleteStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        delay(500)
        val result = StoreApplication.database.storeDao().deleteStore(storeEntity)
        if (result == 0) throw StoresException(TypeError.DELETE)


    }

    suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        delay(500)
        val result = StoreApplication.database.storeDao().updateStore(storeEntity)
        if (result == 0) throw StoresException(TypeError.UPDATE)
    }
}
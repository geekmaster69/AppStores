package com.example.stores.mainModule.model

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainInteractor {


    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
        val url = Constants.STORES_URL + Constants.GET_ALL_PATH

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            Log.i("Response", response.toString())

            val status = response.getInt(Constants.STATUS_PROPERTY)

            if (status == Constants.SUCCES){
              Log.i("status", status.toString())

//                val jsonObject = Gson().fromJson(response.getJSONArray(Constants.STORES_PROPERTY)
//                    .get(0).toString(), StoreEntity::class.java)
//                Log.i("store", jsonObject.toString())

                val jsonList = response.getJSONArray(Constants.STORES_PROPERTY).toString()
                val mutableListType = object : TypeToken<MutableList<StoreEntity>>(){}.type
                val storeList = Gson().fromJson<MutableList<StoreEntity>>(jsonList, mutableListType)

                callback(storeList)

            }


        },{
            it.printStackTrace()
        })

          StoreApplication.storeAPI.addToRequestQueue(jsonObjectRequest)

    }

    fun getStoresRoom(callback: (MutableList<StoreEntity>) -> Unit){
        doAsync {
            val storesList = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                val json = Gson().toJson(storesList)
                Log.i("Gson", json)
                callback(storesList)
            }
        }
    }



    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity)-> Unit){
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }

    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }
}
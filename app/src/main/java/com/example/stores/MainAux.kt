package com.example.stores

interface MainAux {
    fun hideFab(isVisible: Boolean = false)
    fun addStore(storeEntity: StoreEntity)
    fun update(storeEntity: StoreEntity)
}
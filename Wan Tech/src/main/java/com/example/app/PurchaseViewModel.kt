package com.example.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.PurchaseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PurchaseViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).purchaseDao()

    fun savePurchase(purchase: PurchaseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertPurchase(purchase)
        }
    }
}
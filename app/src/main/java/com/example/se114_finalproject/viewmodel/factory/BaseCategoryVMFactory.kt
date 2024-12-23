@file:Suppress("UNCHECKED_CAST")

package com.example.se114_finalproject.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.se114_finalproject.data.Category
import com.example.se114_finalproject.viewmodel.CategoryVM
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryVMFactory (
    private val firestore: FirebaseFirestore,
    private val category: Category
    ): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryVM(firestore,category) as T
        }
    }
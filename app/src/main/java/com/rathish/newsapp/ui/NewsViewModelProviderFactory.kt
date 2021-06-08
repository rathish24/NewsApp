package com.rathish.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rathish.newsapp.data.repository.NewsRepository

class NewsViewModelProviderFactory(
    val newsRepositry: NewsRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       return NewsViewModel(newsRepositry) as T
    }
}
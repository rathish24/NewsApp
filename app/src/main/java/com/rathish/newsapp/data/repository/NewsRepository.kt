package com.rathish.newsapp.data.repository

import com.rathish.newsapp.data.api.RetrofitInstance
import com.rathish.newsapp.db.ArticleDatabase

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String,pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

}
package com.rathish.newsapp.data.repository

import com.rathish.newsapp.data.api.RetrofitInstance
import com.rathish.newsapp.data.model.Article
import com.rathish.newsapp.db.ArticleDatabase

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String,pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery: String,pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) {
        db.getArticleDao().upsert(article)
    }

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun  deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}
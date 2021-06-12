package com.rathish.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rathish.newsapp.data.model.Article

import com.rathish.newsapp.data.model.NewsResponse
import com.rathish.newsapp.data.repository.NewsRepository
import com.rathish.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepositry: NewsRepository):ViewModel() {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    init {
        getBreakingNews("in")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        var response = newsRepositry.getBreakingNews(countryCode,breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse==null){
                    breakingNewsResponse = resultResponse
                }else{
                    val oldArticles = breakingNewsResponse?.articles
                    var newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        var response = newsRepositry.searchNews(searchQuery,searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse==null){
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticles = searchNewsResponse?.articles
                    var newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article){
        viewModelScope.launch {
            newsRepositry.upsert(article)
        }
    }
    fun getSavedNews() = newsRepositry.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepositry.deleteArticle(article)
    }
}
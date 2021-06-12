package com.rathish.newsapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rathish.newsapp.R
import com.rathish.newsapp.adapters.NewsAdapter
import com.rathish.newsapp.ui.NewsActivity
import com.rathish.newsapp.ui.NewsViewModel
import com.rathish.newsapp.util.Constants
import com.rathish.newsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.rathish.newsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var rvSearchNews: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var etSearch: EditText
    val TAG = "SearchNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupRecyclerView()
        setupViewModel()
        setupSearch()

    }

    private fun setupSearch() {
        var job: Job? = null
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_NEWS_TIME_DELAY)
                    s?.let {
                        if (s.toString().isNotEmpty()) {
                            viewModel.searchNews(s.toString())
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun setupViewModel() {
        viewModel = (activity as NewsActivity).viewModel
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if(isLastPage){
                            rvSearchNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "error ----- $message")
                    }
                }
            }
        })
    }

    private fun setupViews(view: View) {
        rvSearchNews = view.findViewById(R.id.rvSearchNews)
        progressBar = view.findViewById(R.id.paginationProgressBar)
        etSearch = view.findViewById(R.id.etSearch)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            rvSearchNews.addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

//        newsAdapter.setOnItemClickListener {
//            val bundle = Bundle().apply {
//                putSerializable("article",it)
//            }
//            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment,bundle)
//        }
        newsAdapter.onItemClickListener = {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment,bundle)
        }
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPAge = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBegining = firstVisibleItemPosition >=0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPAge && isAtLastItem && isNotAtBegining && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }
}
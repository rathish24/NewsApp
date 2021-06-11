package com.rathish.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.rathish.newsapp.R
import com.rathish.newsapp.ui.NewsActivity
import com.rathish.newsapp.ui.NewsViewModel

class ArticleFragment: Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    private val args by navArgs<ArticleFragmentArgs>()
    lateinit var webView: WebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        viewModel = (activity as NewsActivity).viewModel
        var article  = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

    }

}
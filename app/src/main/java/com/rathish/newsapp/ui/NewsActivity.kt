package com.rathish.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rathish.newsapp.R
import com.rathish.newsapp.data.repository.NewsRepository
import com.rathish.newsapp.db.ArticleDatabase


class NewsActivity : AppCompatActivity() {
    lateinit var viewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navigationHost =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navigationHost.navController
        navView.setupWithNavController(navController)
    }

    private fun setupViewModel() {
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)
    }


}
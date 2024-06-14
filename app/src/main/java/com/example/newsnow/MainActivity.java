package com.example.newsnow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private List<Article> newsArticles = new ArrayList<>();
    private NewsRecyclerAdapter newsAdapter;
    private LinearProgressIndicator progressBar;
    private SearchView newsSearchView;
    private Button[] categoryButtons;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        configureSearchView();
        setupRecyclerView();
        loadNews("General", null);
    }

    private void initializeViews() {
        newsRecyclerView = findViewById(R.id.news_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        newsSearchView = findViewById(R.id.search_view);

        categoryButtons = new Button[]{
                findViewById(R.id.btn_1),
                findViewById(R.id.btn_2),
                findViewById(R.id.btn_3),
                findViewById(R.id.btn_4),
                findViewById(R.id.btn_5),
                findViewById(R.id.btn_6),
                findViewById(R.id.btn_7)
        };

        for (Button button : categoryButtons) {
            button.setOnClickListener(this::onCategoryButtonClick);
        }
    }

    private void configureSearchView() {
        newsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadNews("General", query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupRecyclerView() {
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsRecyclerAdapter(newsArticles);
        newsRecyclerView.setAdapter(newsAdapter);
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void loadNews(String category, String query) {
        showProgressBar(true);

        NewsApiClient newsApiClient = new NewsApiClient("enter your api key");
        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest.Builder()
                        .language("en")
                        .category(category)
                        .q(query)
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        runOnUiThread(() -> {
                            showProgressBar(false);
                            newsArticles = response.getArticles();
                            newsAdapter.updateData(newsArticles);
                            newsAdapter.notifyDataSetChanged();
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("News API Error", throwable.getMessage());
                    }
                }
        );
    }

    private void onCategoryButtonClick(View view) {
        Button button = (Button) view;
        String category = button.getText().toString();
        loadNews(category, null);
    }
}

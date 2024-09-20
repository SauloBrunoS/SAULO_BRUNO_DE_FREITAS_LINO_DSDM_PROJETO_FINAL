package com.example.gamesnews.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gamesnews.R;
import com.example.gamesnews.activity.MainActivity;
import com.example.gamesnews.adapter.NewsAdapter;
import com.example.gamesnews.model.NewsResult;
import com.example.gamesnews.retrofit.NewsCallback;
import com.example.gamesnews.retrofit.NewsFetcher;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameFragment extends Fragment {

    private static final String ARG_GAME_TITLE = "game_title";
    private String gameTitle;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private NewsAdapter newsAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public static GameFragment newInstance(String gameTitle) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GAME_TITLE, gameTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        recyclerView = view.findViewById(R.id.rv_jogos_mais_populares);
        progressBar = view.findViewById(R.id.pb_jogos_mais_populares);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_main_news);

        if (getArguments() != null) {
            gameTitle = getArguments().getString(ARG_GAME_TITLE);
            fetchNewsForGame(gameTitle);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNewsForGame(gameTitle);
                // Usar Handler para definir o refresh como falso após 2 segundos (2000 milissegundos)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });


        return view;
    }

    private void fetchNewsForGame(String gameTitle) {
        switch (gameTitle) {
            case "Minecraft":
                fetchNews("CAAqJAgKIh5DQkFTRUFvS0wyMHZNRGwyTm10d1p4SUNaVzRvQUFQAQ");
                break;
            case "GTA V":
                fetchNews("CAAqJAgKIh5DQkFTRUFvS0wyMHZNR2huYm5wcWFCSUNaVzRvQUFQAQ");
                break;
            case "Fortnite":
                fetchNews("CAAqJAgKIh5DQkFTRUFvS0wyMHZNR3M1WW1NNU1CSUNaVzRvQUFQAQ");
                break;
            case "Among Us":
                fetchNews("CAAqKAgKIiJDQkFTRXdvTkwyY3ZNVEZtYlhoMGEyWnJlaElDWlc0b0FBUAE");
                break;
            case "League of Legends":
                fetchNews("CAAqJAgKIh5DQkFTRUFvS0wyMHZNRFJ1TTNjeWNoSUNaVzRvQUFQAQ");
                break;
            case "God of War Ragnarök":
                fetchNews("CAAqLAgKIiZDQkFTRmdvTkwyY3ZNVEZzYXpGbWNtNDFjQklGY0hRdFFsSW9BQVAB");
                break;
            case "Valorant":
                fetchNews("CAAqLAgKIiZDQkFTRmdvTkwyY3ZNVEZxT0hkMmNubzRlaElGY0hRdFFsSW9BQVAB");
                break;
        }
    }


    private void fetchNews(String topicToken) {
        progressBar.setVisibility(View.VISIBLE);
        NewsFetcher newsFetcher = new NewsFetcher();
        String apiKey = "f38f24b84b5e00e51e9daf3ff7efe3812676d0940d06f83630f94abca914b083";

        newsFetcher.fetchNews(getContext(),null, null, topicToken, null, null, null, new NewsCallback() {
            @Override
            public void onSuccess(List<NewsResult> newsResults) {
                progressBar.setVisibility(View.GONE);
                newsAdapter = new NewsAdapter(newsResults, getContext());
                recyclerView.setAdapter(newsAdapter);
            }

            @Override
            public void onError(Throwable exception) {
                progressBar.setVisibility(View.GONE);
                // Tratar erro
                Log.e("GameFragment", "Erro ao realizar requisição a api do Google: ", exception);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarregar notícias ao retomar o fragmento
        fetchNewsForGame(gameTitle);
    }
}


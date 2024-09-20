package com.example.gamesnews.retrofit;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gamesnews.R;
import com.example.gamesnews.model.NewsResponse;
import com.example.gamesnews.retrofit.GoogleNewsApiService;
import com.example.gamesnews.retrofit.NewsCallback;
import com.example.gamesnews.utils.ZonedDateTimeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFetcher {

    private static final String BASE_URL = "https://serpapi.com";

    @NonNull
    private GoogleNewsApiService createNewsService() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(GoogleNewsApiService.class);
    }

    public void fetchNews(Context context, String query, @Nullable Integer sorting_method, String topicToken, String publicationToken, String sectionToken, String storyToken, NewsCallback callback) {
        GoogleNewsApiService service = createNewsService();

        // Carregar as preferências salvas de país e linguagem
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.nome_arquivo_preferencias_do_usuario), Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "pt-br");
        String country = sharedPreferences.getString("country", "br");

        // Chamada de API com parâmetros
        Call<NewsResponse> call = service.getNews(
                "google_news",   // engine
                query,         // query
                language,            // language
                country,            // country
                sorting_method,  // sort by date or relevance
                "48ab75695f66758856e84daa297522fde042e2d43de7d5e92ff9c1680c265dfe",
                topicToken,      // Token de tópico (opcional)
                publicationToken, // Token de publicação (opcional)
                sectionToken,    // Token de seção (opcional)
                storyToken       // Token de história (opcional)
        );

        // Executa a chamada
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewsResponse newsResponse = response.body();
                    callback.onSuccess(newsResponse.getNewsResults());
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void fetchFullCoverageNews(Context context, String query, @Nullable Integer sorting_method, String topicToken, String publicationToken, String sectionToken, String storyToken, FullCoverageNewsCallback callback) {
        GoogleNewsApiService service = createNewsService();

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.nome_arquivo_preferencias_do_usuario), Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "pt-br");
        String country = sharedPreferences.getString("country", "br");

        // Chamada de API com parâmetros
        Call<NewsResponse> call = service.getNews(
                "google_news",   // engine
                query,         // query
                language,            // language
                country,            // country
                sorting_method,  // sort by date or relevance
                "48ab75695f66758856e84daa297522fde042e2d43de7d5e92ff9c1680c265dfe",
                topicToken,      // Token de tópico (opcional)
                publicationToken, // Token de publicação (opcional)
                sectionToken,    // Token de seção (opcional)
                storyToken       // Token de história (opcional)
        );

        // Executa a chamada
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewsResponse newsResponse = response.body();
                    callback.onSuccess(newsResponse);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }
}
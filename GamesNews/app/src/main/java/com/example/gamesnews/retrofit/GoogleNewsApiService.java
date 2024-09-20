package com.example.gamesnews.retrofit;

import com.example.gamesnews.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleNewsApiService {
    @GET("/search")
    Call<NewsResponse> getNews(
            @Query("engine") String engine,
            @Query("q") String query,
            @Query("hl") String language,
            @Query("gl") String country,
            @Query("so") Integer sortBy,
            @Query("api_key") String apiKey,
            @Query("topic_token") String topicToken,           // Opcional
            @Query("publication_token") String publicationToken, // Opcional
            @Query("section_token") String sectionToken,       // Opcional
            @Query("story_token") String storyToken            // Opcional
    );
}
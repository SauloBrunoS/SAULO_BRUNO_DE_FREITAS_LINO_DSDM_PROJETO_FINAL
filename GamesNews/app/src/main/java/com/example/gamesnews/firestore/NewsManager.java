package com.example.gamesnews.firestore;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gamesnews.R;
import com.example.gamesnews.activity.FavoriteNewsActivity;
import com.example.gamesnews.adapter.NewsAdapter;
import com.example.gamesnews.model.Highlight;
import com.example.gamesnews.model.NewsResult;
import com.example.gamesnews.model.Story;
import com.example.gamesnews.utils.ZonedDateTimeDeserializer;
import com.example.gamesnews.utils.ZonedDateTimeDeserializer2;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class NewsManager {

    private FirebaseFirestore firestore;
    private Context context;

    public NewsManager(Context context) {
        firestore = FirebaseFirestore.getInstance();
        this.context = context;
    }

    // Função para salvar uma notícia favorita
    public void saveFavoriteNews(FirebaseUser user, NewsResult newsResult, FavoriteCallback callback) {
        Map<String, Object> newsData = createNewsDataMap(newsResult);

        firestore.collection("users")
                .document(user.getUid())
                .collection("favorites")
                .add(newsData)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    // Função para salvar um destaque favorito (highlight)
    public void saveFavoriteHighlight(FirebaseUser user, Highlight highlight, FavoriteCallback callback) {
        Map<String, Object> highlightData = new HashMap<>();
        highlightData.put("title", highlight.getTitle());
        highlightData.put("link", highlight.getLink());
        highlightData.put("thumbnail", highlight.getThumbnail());
        highlightData.put("date", highlight.getDate().toString());

        Map<String, Object> sourceData = new HashMap<>();
        sourceData.put("name", highlight.getSource().getName());
        sourceData.put("icon", highlight.getSource().getIcon());
        sourceData.put("authors", highlight.getSource().getAuthors());

        highlightData.put("source", sourceData);

        firestore.collection("users")
                .document(user.getUid())
                .collection("favorite_highlights")
                .add(highlightData)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    // Função para salvar uma story favorita
    public void saveFavoriteStory(FirebaseUser user, Story story, FavoriteCallback callback) {
        Map<String, Object> storyData = new HashMap<>();
        storyData.put("title", story.getTitle());
        storyData.put("link", story.getLink());
        storyData.put("thumbnail", story.getThumbnail());

        storyData.put("date", story.getDate().toString());

        Map<String, Object> sourceData = new HashMap<>();
        sourceData.put("name", story.getSource().getName());
        sourceData.put("icon", story.getSource().getIcon());

        storyData.put("source", sourceData);

        firestore.collection("users")
                .document(user.getUid())
                .collection("favorite_stories")
                .add(storyData)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    // Função para buscar notícias favoritas
    public void fetchSavedNews(FirebaseUser currentUser, ArrayList<NewsResult> favoriteNewsList, NewsAdapter newsAdapter, FetchDataCallback callback) {
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoriteNewsList.clear();
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer2())
                                .create();
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            String json = gson.toJson(data);
                            NewsResult newsResult = gson.fromJson(json, NewsResult.class);
                            if (newsResult != null) {
                                favoriteNewsList.add(newsResult);
                            }
                        }
                        newsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Erro ao carregar notícias salvas.", Toast.LENGTH_SHORT).show();
                    }
                    callback.onComplete();  // Chama o callback ao final da execução
                });
    }


    public void fetchSavedHighlights(FirebaseUser currentUser, ArrayList<NewsResult> favoriteNewsList, NewsAdapter newsAdapter, FetchDataCallback callback) {
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("favorite_highlights")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer2())
                                .create();
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            String json = gson.toJson(data);
                            Highlight highlight = gson.fromJson(json, Highlight.class);
                            if (highlight != null) {
                                NewsResult newsResult = new NewsResult();
                                newsResult.setHighlight(highlight);
                                favoriteNewsList.add(newsResult);
                            }
                        }
                        newsAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("FavoriteNewsActivity", "Erro ao carregar destaques: ", task.getException());
                    }
                    callback.onComplete();  // Chama o callback ao final da execução
                });
    }


    public void fetchSavedStories(FirebaseUser currentUser, ArrayList<NewsResult> favoriteNewsList, NewsAdapter newsAdapter, FetchDataCallback callback) {
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("favorite_stories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer2())
                                .create();
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            String json = gson.toJson(data);
                            Story story = gson.fromJson(json, Story.class);
                            if (story != null) {
                                NewsResult newsResult = new NewsResult();
                                List<Story> stories = new ArrayList<>();
                                stories.add(story);
                                newsResult.setStories(stories);
                                favoriteNewsList.add(newsResult);
                            }
                        }
                        newsAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("FavoriteNewsActivity", "Erro ao carregar stories: ", task.getException());
                    }
                    callback.onComplete();  // Chama o callback ao final da execução
                });
    }


    // Função para remover uma notícia favorita
    public void removeFavorite(FirebaseUser currentUser, NewsResult newsResult, FavoriteCallback callback) {
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .whereEqualTo("link", newsResult.getLink())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            firestore.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("favorites")
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onFailure(e));
                        }
                    }
                });
    }

    // Função para remover um destaque favorito
    public void removeFavoriteHighlight(FirebaseUser currentUser, Highlight highlight, FavoriteCallback callback) {
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("favorite_highlights")
                .whereEqualTo("link", highlight.getLink())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            firestore.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("favorite_highlights")
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onFailure(e));
                        }
                    }
                });
    }

    // Função para remover uma story favorita
    public void removeFavoriteStory(FirebaseUser currentUser, Story story, FavoriteCallback callback) {
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("favorite_stories")
                .whereEqualTo("link", story.getLink())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            firestore.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("favorite_stories")
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onFailure(e));
                        }
                    }
                });
    }


    // Função auxiliar para criar o mapa de dados da notícia
    private Map<String, Object> createNewsDataMap(NewsResult newsResult) {
        Map<String, Object> newsData = new HashMap<>();
        newsData.put("title", newsResult.getTitle());
        newsData.put("link", newsResult.getLink());
        newsData.put("thumbnail", newsResult.getThumbnail());
        newsData.put("date", newsResult.getDate().toString());

        Map<String, Object> sourceData = new HashMap<>();
        sourceData.put("name", newsResult.getSource().getName());
        sourceData.put("icon", newsResult.getSource().getIcon());
        sourceData.put("authors", newsResult.getSource().getAuthors());

        newsData.put("source", sourceData);
        return newsData;
    }

}

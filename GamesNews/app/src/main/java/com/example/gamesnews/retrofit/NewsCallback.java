package com.example.gamesnews.retrofit;

import com.example.gamesnews.model.NewsResult;

import java.util.List;

public interface NewsCallback {
    void onSuccess(List<NewsResult> newsResults);
    void onError(Throwable exception);
}

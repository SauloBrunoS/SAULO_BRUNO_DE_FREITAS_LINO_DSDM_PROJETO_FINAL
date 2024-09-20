package com.example.gamesnews.retrofit;

import com.example.gamesnews.model.NewsResponse;
import com.example.gamesnews.model.NewsResult;

import java.util.List;

public interface FullCoverageNewsCallback {
    void onSuccess(NewsResponse newsResponse);
    void onError(Throwable exception);
}

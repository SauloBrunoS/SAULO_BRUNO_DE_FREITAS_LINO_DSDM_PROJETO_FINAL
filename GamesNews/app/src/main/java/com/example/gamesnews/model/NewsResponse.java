package com.example.gamesnews.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

import com.example.gamesnews.model.NewsResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class NewsResponse {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("news_results")
    @Expose
    private List<NewsResult> newsResults = new ArrayList<NewsResult>();

    public NewsResponse() {
    }

    public NewsResponse(String title, List<NewsResult> newsResults) {
        this.title = title;
        this.newsResults = newsResults;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NewsResult> getNewsResults() {
        return newsResults;
    }

    public void setNewsResults(List<NewsResult> newsResults) {
        this.newsResults = newsResults;
    }
}
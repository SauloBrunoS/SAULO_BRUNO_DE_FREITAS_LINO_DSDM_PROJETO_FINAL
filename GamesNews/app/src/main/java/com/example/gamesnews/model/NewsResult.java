package com.example.gamesnews.model;

import java.time.ZonedDateTime;
import java.util.List;

import javax.annotation.processing.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class NewsResult {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("source")
    @Expose
    private Source source;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("date")
    @Expose
    private ZonedDateTime date;
    @SerializedName("highlight")
    @Expose
    private Highlight highlight;
    @SerializedName("stories")
    @Expose
    private List<Story> stories;
    @SerializedName("story_token")
    @Expose
    private String storyToken;

    public NewsResult() {
    }

    public NewsResult(String title, Source source, String link, String thumbnail, ZonedDateTime date, Highlight highlight, List<Story> stories, String storyToken) {
        this.title = title;
        this.source = source;
        this.link = link;
        this.thumbnail = thumbnail;
        this.date = date;
        this.highlight = highlight;
        this.stories = stories;
        this.storyToken = storyToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Highlight getHighlight() {
        return highlight;
    }

    public void setHighlight(Highlight highlight) {
        this.highlight = highlight;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public String getStoryToken() {
        return storyToken;
    }

    public void setStoryToken(String storyToken) {
        this.storyToken = storyToken;
    }
}

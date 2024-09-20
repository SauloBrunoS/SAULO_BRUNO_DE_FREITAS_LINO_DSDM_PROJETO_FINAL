package com.example.gamesnews.model;

import javax.annotation.processing.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

@Generated("jsonschema2pojo")
public class Story {

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

    public Story() {
    }

    public Story(String title, Source source, String link, String thumbnail, ZonedDateTime date) {
        this.title = title;
        this.source = source;
        this.link = link;
        this.thumbnail = thumbnail;
        this.date = date;
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
}
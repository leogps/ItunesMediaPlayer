package com.gps.itunes.media.player.updater.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;

import java.util.Date;

/**
 * Created by leogps on 2/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Release {

    //    "url": "https://api.github.com/repos/rg3/youtube-dl/releases/5563602",
    @JsonProperty("url")
    private String url;

    //            "html_url": "https://github.com/rg3/youtube-dl/releases/tag/2017.02.24.1",
    @JsonProperty("html_url")
    private String htmlUrl;

    //            "id": 5563602,
    @JsonProperty("id")
    private long id;

    //            "tag_name": "2017.02.24.1",
    @JsonProperty("tag_name")
    private String tagName;

    //            "name": "youtube-dl 2017.02.24.1",
    @JsonProperty("name")
    private String name;

    @JsonProperty("prerelease")
    private boolean prerelease;

    @JsonSerialize(using = DateSerializer.class)
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonSerialize(using = DateSerializer.class)
    @JsonProperty("published_at")
    private Date publishedAt;

    @JsonProperty("assets")
    private Asset[] assets;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrerelease() {
        return prerelease;
    }

    public void setPrerelease(boolean prerelease) {
        this.prerelease = prerelease;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Asset[] getAssets() {
        return assets;
    }

    public void setAssets(Asset[] assets) {
        this.assets = assets;
    }
}

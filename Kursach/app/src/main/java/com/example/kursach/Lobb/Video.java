package com.example.kursach.Lobb;

public class Video {
    private String id;
    private String publishedAt;
    private String title;
    private String description;
    private String thumbnails;

    public Video() {
    }

    public Video(String id, String publishedAt, String title, String description, String thumbnails) {
        this.id = id;
        this.publishedAt = publishedAt;
        this.title = title;
        this.description = description;
        this.thumbnails = thumbnails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }
}

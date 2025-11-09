package com.proyecto.cabapro.model;

public class NewsArticle {
    private String title;
    private String description;
    private String link;
    private String imageUrl;
    private String source;

    public NewsArticle(String title, String description, String link, String imageUrl, String source) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.imageUrl = imageUrl;
        this.source = source;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLink() { return link; }
    public String getImageUrl() { return imageUrl; }
    public String getSource() { return source; }
}

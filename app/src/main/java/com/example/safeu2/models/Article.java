package com.example.safeu2.models;

public class Article {
    private String title;
    private String description;
    private String content;

    public Article(String title, String description, String content) {
        this.title = title;
        this.description = description;
        this.content = content;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
}

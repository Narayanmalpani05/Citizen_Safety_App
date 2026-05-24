package com.example.safeu2.api;

public class PredictionRequest {
    private String url;

    public PredictionRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

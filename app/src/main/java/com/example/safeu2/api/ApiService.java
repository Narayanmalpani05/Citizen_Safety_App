package com.example.safeu2.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("predict")
    Call<PredictionResponse> predictUrl(@Body PredictionRequest request);
}

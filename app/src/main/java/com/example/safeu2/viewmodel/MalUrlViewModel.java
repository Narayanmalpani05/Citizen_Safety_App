package com.example.safeu2.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.safeu2.api.ApiClient;
import com.example.safeu2.api.ApiService;
import com.example.safeu2.api.PredictionRequest;
import com.example.safeu2.api.PredictionResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MalUrlViewModel extends ViewModel {

    private MutableLiveData<PredictionResponse> predictionLiveData;
    private MutableLiveData<String> errorLiveData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> statusMessage;
    private ApiService apiService;

    private int retryCount = 0;
    private static final int MAX_RETRIES = 2;
    private String currentUrl = "";

    public MalUrlViewModel() {
        predictionLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        statusMessage = new MutableLiveData<>();
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<PredictionResponse> getPrediction() {
        return predictionLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public void predictUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            errorLiveData.postValue("URL cannot be empty.");
            return;
        }

        currentUrl = url;
        retryCount = 0;
        statusMessage.postValue("Scanning URL...");
        makePredictionRequest();
    }

    private void makePredictionRequest() {
        isLoading.postValue(true);
        PredictionRequest request = new PredictionRequest(currentUrl);

        apiService.predictUrl(request).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    predictionLiveData.postValue(response.body());
                } else {
                    errorLiveData.postValue("Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                if (retryCount < MAX_RETRIES) {
                    retryCount++;
                    statusMessage.postValue("Connecting to security server... (Attempt " + (retryCount + 1) + ")");
                    makePredictionRequest();
                } else {
                    isLoading.postValue(false);
                    errorLiveData.postValue("Unable to connect to security server. Please check your internet connection and try again.");
                }
            }
        });
    }
}

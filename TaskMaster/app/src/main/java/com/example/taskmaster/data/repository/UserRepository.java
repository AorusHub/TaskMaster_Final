package com.example.taskmaster.data.repository;

import com.example.taskmaster.api.ApiService;
import com.example.taskmaster.api.RetrofitClient;
import com.example.taskmaster.api.response.UserResponse;
import com.example.taskmaster.model.UserStats;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final ApiService apiService;

    public UserRepository() {
        this.apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    /**
     * Fetch user data asynchronously
     * @param callback Callback to handle the response
     */
    public void getUserData(UserDataCallback callback) {
        Call<UserResponse> call = apiService.getUserData();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch user data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Callback interface for user data operations
     */
    public interface UserDataCallback {
        void onSuccess(UserResponse userResponse);
        void onError(String errorMessage);
    }
}
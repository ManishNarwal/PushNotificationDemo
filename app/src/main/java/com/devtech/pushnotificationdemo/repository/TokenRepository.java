package com.devtech.pushnotificationdemo.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.devtech.pushnotificationdemo.api.ApiService;
import com.devtech.pushnotificationdemo.model.UserModel;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class TokenRepository {

    @Inject
    ApiService apiService;
    public TokenRepository(ApiService apiService){
        this.apiService = apiService;
    }

    public void getUser(MutableLiveData<UserModel> userLiveData){
        Call<UserModel> call = apiService.getSingleUser();
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                if(response.isSuccessful()){
                    Timber.e(response.body().toString());
                    userLiveData.postValue(response.body());
                }
                else{
                    userLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                userLiveData.postValue(null);
            }
        });
    }
}

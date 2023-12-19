package com.devtech.pushnotificationdemo.api;


import com.devtech.pushnotificationdemo.model.UserModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("api/users/2")
    Call<UserModel> getSingleUser();
}

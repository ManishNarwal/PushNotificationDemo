package com.devtech.pushnotificationdemo.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devtech.pushnotificationdemo.model.UserModel;
import com.devtech.pushnotificationdemo.repository.TokenRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TokenViewModel extends ViewModel {

    @Inject
    TokenRepository tokenRepository;
    MutableLiveData<UserModel> userLiveData;
    @Inject
    public TokenViewModel(){
        userLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<UserModel> getUserLiveData() {
        return userLiveData;
    }

    public void getUserData(){
        tokenRepository.getUser(userLiveData);
    }
}

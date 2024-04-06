package com.theindiecorp.khelodapp.Repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.theindiecorp.khelodapp.Model.GetCricketMatchesResponse;
import com.theindiecorp.khelodapp.Model.GetMatchDetailsResponse;
import com.theindiecorp.khelodapp.Network.CricketAPI;
import com.theindiecorp.khelodapp.Network.CricketRetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CricketRepository {

    private static CricketRepository repository;
    private CricketAPI cricketAPI;

    private static String TAG = "CricketRepository";
    private static String apiKey = "gm3kpRB3LqeVamF1GywRWrBeJGG3";

    public static CricketRepository getInstance(){
        if(repository == null)
            repository = new CricketRepository();

        return repository;
    }

    public CricketRepository(){
        cricketAPI = CricketRetrofitService.getInterface();
    }

    public MutableLiveData<GetCricketMatchesResponse> getCricketMatches(){
        final MutableLiveData<GetCricketMatchesResponse> mutableLiveData = new MutableLiveData<>();
        cricketAPI.getAllMatches(apiKey).enqueue(new Callback<GetCricketMatchesResponse>() {
            @Override
            public void onResponse(Call<GetCricketMatchesResponse> call, Response<GetCricketMatchesResponse> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: Cricket all matches successful" + response.code());
                    mutableLiveData.setValue(response.body());
                }

                Log.d(TAG, response.message() + response.code());
            }

            @Override
            public void onFailure(Call<GetCricketMatchesResponse> call, Throwable t) {
                mutableLiveData.setValue(null);
                Log.d(TAG, "onFailure: Cricket all matches failed " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<GetMatchDetailsResponse> getMatchDetails(long uniqueId){
        final MutableLiveData<GetMatchDetailsResponse> mutableLiveData = new MutableLiveData<>();
        cricketAPI.getMatchDetails(apiKey, uniqueId).enqueue(new Callback<GetMatchDetailsResponse>() {
            @Override
            public void onResponse(Call<GetMatchDetailsResponse> call, Response<GetMatchDetailsResponse> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: Details  successful" + response.code());
                    mutableLiveData.setValue(response.body());
                }

                Log.d(TAG, response.message() + response.code());
            }

            @Override
            public void onFailure(Call<GetMatchDetailsResponse> call, Throwable t) {
                mutableLiveData.setValue(null);
                Log.d(TAG, "onFailure: Details failed " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

}

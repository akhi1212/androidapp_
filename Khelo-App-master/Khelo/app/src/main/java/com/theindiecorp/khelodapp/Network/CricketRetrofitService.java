package com.theindiecorp.khelodapp.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CricketRetrofitService {
    private static final String BASE_URL = "https://cricapi.com/";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static CricketAPI getInterface() {
        return retrofit.create(CricketAPI.class);
    }
}

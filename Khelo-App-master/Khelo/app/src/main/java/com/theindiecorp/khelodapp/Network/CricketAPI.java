package com.theindiecorp.khelodapp.Network;

import com.theindiecorp.khelodapp.Model.GetCricketMatchesResponse;
import com.theindiecorp.khelodapp.Model.GetMatchDetailsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CricketAPI {

    @GET("/api/matches/")
    Call<GetCricketMatchesResponse> getAllMatches(@Query("apikey") String apiKey);

    @GET("/api/cricketScore/")
    Call<GetMatchDetailsResponse> getMatchDetails(@Query("apikey") String apiKey, @Query("unique_id") long uniqueId);

}

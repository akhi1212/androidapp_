package com.theindiecorp.khelodapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.theindiecorp.khelodapp.Model.GetCricketMatchesResponse;
import com.theindiecorp.khelodapp.Model.GetMatchDetailsResponse;
import com.theindiecorp.khelodapp.Repository.CricketRepository;

public class CricketViewModel extends ViewModel {

    private CricketRepository cricketRepository;

    public void init(){
        cricketRepository = CricketRepository.getInstance();
    }

    public LiveData<GetCricketMatchesResponse> getCricketMatches(){
        return cricketRepository.getCricketMatches();
    }

    public LiveData<GetMatchDetailsResponse> getMatchDetails(long uniqueId){
        return cricketRepository.getMatchDetails(uniqueId);
    }

}

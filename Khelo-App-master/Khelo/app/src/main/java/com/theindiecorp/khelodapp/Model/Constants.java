package com.theindiecorp.khelodapp.Model;

import java.util.ArrayList;

public class Constants {

    private ArrayList<String> iplTeams;

    public Constants() {
        this.iplTeams = new ArrayList<>();
        iplTeams.add("Mumbai Indians");
        iplTeams.add("Chennai Super Kings");
        iplTeams.add("Delhi Capitals");
        iplTeams.add("Kings XI Punjab");
        iplTeams.add("Kolkata Knight Riders");
        iplTeams.add("Rajasthan Royals");
        iplTeams.add("Royal Challengers Bangalore");
        iplTeams.add("Sunrisers Hyderabad");
    }

    public ArrayList<String> getIplTeams(){
        return iplTeams;
    }

}

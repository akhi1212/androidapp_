package com.theindiecorp.khelodapp.Model;

import java.util.ArrayList;

public class Leaderboard {
    private int score;
    private boolean submitted;
    private ArrayList<Answer> answers;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}

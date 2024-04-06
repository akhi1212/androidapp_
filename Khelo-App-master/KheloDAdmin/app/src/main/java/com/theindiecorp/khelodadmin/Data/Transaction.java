package com.theindiecorp.khelodadmin.Data;

public class Transaction {

    private String type, postId;
    private int points;
    private String date;
    private String id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    private boolean pending;

    public Transaction(){

    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Transaction(String type, int points, String date) {
        this.type = type;
        this.points = points;
        this.date = date;
        this.pending = false;
    }

    public Transaction(String type, int points, String date, boolean pending) {
        this.type = type;
        this.points = points;
        this.date = date;
        this.pending = pending;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

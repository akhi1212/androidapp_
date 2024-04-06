package com.theindiecorp.khelodapp.Model;

public class BuyPointsObject {

    private String cost;
    private String id, discountedPrice;

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public BuyPointsObject(String cost) {
        this.cost = cost;
    }
}

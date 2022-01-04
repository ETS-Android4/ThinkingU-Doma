package com.example.doma.Model;

import java.io.Serializable;

public class UserData implements Serializable {

    private String fullName;
    private String phoneNumber;
    private String imageUrl;
    private int energyPercentage;
    private String latestThought;

    public UserData() {
    }

    public UserData(String fullName, String phoneNumber, String imageUrl, int energyPercentage, String latestThought) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
        this.energyPercentage = energyPercentage;
        this.latestThought =  latestThought;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getEnergyPercentage() {
        return energyPercentage;
    }

    public void setEnergyPercentage(int energyPercentage) {
        this.energyPercentage = energyPercentage;
    }

    public String getLatestThought() {
        return latestThought;
    }

    public void setLatestThought(String latestThought) {
        this.latestThought = latestThought;
    }
}

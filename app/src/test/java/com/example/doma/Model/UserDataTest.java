package com.example.doma.Model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserDataTest {

    UserData userData = new UserData("Ahsan", "25643564", "https://media.image.com/ahsna.jpeg", 50, "a thought for you");

    @Test
    void getFullName() {
        assertEquals("Ahsan", userData.getFullName());
    }

    @org.junit.jupiter.api.Test
    void setFullName() {
    }

    @Test
    void getPhoneNumber() {
        assertEquals("25643564", userData.getPhoneNumber());
    }

    @org.junit.jupiter.api.Test
    void setPhoneNumber() {
    }

    @Test
    void getImageUrl() {
        assertEquals("https://media.image.com/ahsna.jpeg", userData.getImageUrl());
    }

    @org.junit.jupiter.api.Test
    void setImageUrl() {
    }

    @Test
    void getEnergyPercentage() {
        assertEquals(50, userData.getEnergyPercentage());
    }

    @org.junit.jupiter.api.Test
    void setEnergyPercentage() {
    }

    @Test
    void getLatestThought() {
        assertEquals("a thought for you", userData.getLatestThought());
    }

    @org.junit.jupiter.api.Test
    void setLatestThought() {
    }
}
package com.pet.numwan.numwan_pet;

public class Card_Pet {
    private String User,imageURL,pet_care,pet_des,pet_feel,pet_history,pet_name,pet_uni;
   public Card_Pet(){

   }

    public Card_Pet(String user, String imageURL, String pet_care, String pet_des, String pet_feel, String pet_history, String pet_name, String pet_uni) {
        this.User = user;
        this.imageURL = imageURL;
        this.pet_care = pet_care;
        this.pet_des = pet_des;
        this.pet_feel = pet_feel;
        this.pet_history = pet_history;
        this.pet_name = pet_name;
        this.pet_uni = pet_uni;
    }

    public String getUser() {
        return User;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getPet_care() {
        return pet_care;
    }

    public String getPet_des() {
        return pet_des;
    }

    public String getPet_feel() {
        return pet_feel;
    }

    public String getPet_history() {
        return pet_history;
    }

    public String getPet_name() {
        return pet_name;
    }

    public String getPet_uni() {
        return pet_uni;
    }

    public void setUser(String user) {
        User = user;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setPet_care(String pet_care) {
        this.pet_care = pet_care;
    }

    public void setPet_des(String pet_des) {
        this.pet_des = pet_des;
    }

    public void setPet_feel(String pet_feel) {
        this.pet_feel = pet_feel;
    }

    public void setPet_history(String pet_history) {
        this.pet_history = pet_history;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public void setPet_uni(String pet_uni) {
        this.pet_uni = pet_uni;
    }
}

package com.example.myemechanic;



public class Technician {
    String firstNamee;
    String secondNamee;
    String emaill;
    String mobilee;

    public String getMechanicRating() {
        return mechanicRating;
    }

    public void setMechanicRating(String mechanicRating) {
        this.mechanicRating = mechanicRating;
    }

    private  String mechanicRating;

    public String getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(String currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(String currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public   String currentLongitude;
   public   String currentLatitude;





    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    String profilePhotoUrl;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    String userid;






    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    String typingTo;
    public Technician(){

    }

    public Technician(String firstNamee, String secondNamee, String emaill, String mobilee, String userid,String onlineStatus){
        this.firstNamee = firstNamee;
        this.userid=userid;
        this.secondNamee = secondNamee;
        this.emaill = emaill;
        this.mobilee = mobilee;



    }

    public void setFirstNamee(String firstNamee) {
        this.firstNamee = firstNamee;
    }

    public void setSecondNamee(String secondNamee) {
        this.secondNamee = secondNamee;
    }

    public void setEmaill(String emaill) {
        this.emaill = emaill;
    }

    public void setMobilee(String mobilee) {
        this.mobilee = mobilee;
    }




    public String getFirstNamee() {
        return firstNamee;
    }

    public String getSecondNamee() {
        return secondNamee;
    }

    public String getEmaill() {
        return emaill;
    }

    public String getMobilee() {
        return mobilee;
    }



}

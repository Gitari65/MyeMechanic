package com.example.myemechanic;



public class Technician {
    String firstNamee;
    String secondNamee;
    String emaill;
    String mobilee;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    String userid;
    String location;

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    String onlineStatus;

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    String typingTo;
    public Technician(){

    }

    public Technician(String firstNamee, String secondNamee, String emaill, String mobilee, String userid, String location,String onlineStatus,String typingTo){
        this.firstNamee = firstNamee;
        this.userid=userid;
        this.secondNamee = secondNamee;
        this.emaill = emaill;
        this.mobilee = mobilee;
        this.location = location;
        this.typingTo=typingTo;
        this.onlineStatus=onlineStatus;
    }

    public void setFirstNamee(String firstNamee) {
        this.firstNamee = firstNamee;
    }

    public void setScondNamee(String secondNamee) {
        this.secondNamee = secondNamee;
    }

    public void setEmaill(String emaill) {
        this.emaill = emaill;
    }

    public void setMobilee(String mobilee) {
        this.mobilee = mobilee;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getLocation() {
        return location;
    }
}

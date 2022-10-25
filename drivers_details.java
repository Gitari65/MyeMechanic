package com.example.myemechanic;

public class drivers_details {
    private String driverDate;

    public String getDriverToken() {
        return driverToken;
    }

    public void setDriverToken(String driverToken) {
        this.driverToken = driverToken;
    }

    private String driverToken;
    private  String currentD_userId;

    public String getDriverCurrentLongitude() {
        return driverCurrentLongitude;
    }

    public void setDriverCurrentLongitude(String driverCurrentLongitude) {
        this.driverCurrentLongitude = driverCurrentLongitude;
    }

    private  String driverCurrentLongitude;
    private String licenceUrl;
    private String profilePhotoUrl;
    private String driverSecondName;
    private String driverFirstName;

    public String getDriverCurrentLatitude() {
        return driverCurrentLatitude;
    }

    public void setDriverCurrentLatitude(String driverCurrentLatitude) {
        this.driverCurrentLatitude = driverCurrentLatitude;
    }

    private String driverCurrentLatitude;
    private String idNumber;
    private String driverPhoneNumber;
    private String driverEmail;


    public drivers_details() {
        // empty constructor
        // required for Firebase.
    }

    // Constructor for all variables.
    public drivers_details(String driverFirstName,String driverDate,String licenceUrl,String profilePhotoUrl, String driverSecondName,String idNumber ,String driverPhoneNumber,String driverEmail) {
        this.driverFirstName = driverFirstName;
        this.currentD_userId=currentD_userId;
        this.driverCurrentLatitude=driverCurrentLatitude;
        this.driverDate=driverDate;
        this.licenceUrl=licenceUrl;
        this.profilePhotoUrl=profilePhotoUrl;
        this.driverSecondName= driverSecondName;
        this.idNumber = idNumber;
        this.driverPhoneNumber=driverPhoneNumber;
        this.driverEmail=driverEmail;
    }

    // getter methods for all variables.
    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public String getCurrentD_userId() {
        return currentD_userId;
    }

    public void setCurrentD_userId(String currentD_userId) {
        this.currentD_userId = currentD_userId;
    }


    public String getDriverSecondName() {
        return driverSecondName;
    }

    public void setDriverSecondName(String driverSecondName) {
        this.driverSecondName = driverSecondName;
    }
    // setter method for all variables.
    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverDate() {
        return driverDate;
    }
    public void setDriverDate(String driverDate) {
        this.driverDate = driverDate;
    }

    public String getLicenceUrl() {
        return licenceUrl;
    }
    public void setLicenceUrl(String licenceUrl) {
        this.licenceUrl= licenceUrl;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl= profilePhotoUrl;
    }


}



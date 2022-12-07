package com.example.myemechanic;

public class mechanic_details {
    public String getMechanicCurrentLatitude() {
        return mechanicCurrentLatitude;
    }

    public void setMechanicCurrentLatitude(String mechanicCurrentLatitude) {
        this.mechanicCurrentLatitude = mechanicCurrentLatitude;
    }

    private  String mechanicCurrentLatitude;

    public String getMechanicCurrentLongitude() {
        return mechanicCurrentLongitude;
    }

    public void setMechanicCurrentLongitude(String mechanicCurrentLongitude) {
        this.mechanicCurrentLongitude = mechanicCurrentLongitude;
    }

    private  String mechanicCurrentLongitude;


    public Double getMechanicRating() {
        return mechanicRating;
    }

    public void setMechanicRating(Double mechanicRating) {
        this.mechanicRating = mechanicRating;
    }

    private  Double mechanicRating;

    private String registrationStatus;

    public String getMechanicPartSpecialization() {
        return mechanicPartSpecialization;
    }

    public void setMechanicPartSpecialization(String mechanicPartSpecialization) {
        this.mechanicPartSpecialization = mechanicPartSpecialization;
    }

    private String mechanicPartSpecialization;
    private  String current_userId;
    private String licenceUrl;
    private String profilePhotoUrl;
    private String secondName;
    private String firstName;

    public String getMechanicToken() {
        return mechanicToken;
    }

    public void setMechanicToken(String mechanicToken) {
        this.mechanicToken = mechanicToken;
    }

    private String mechanicToken;

    public String getGarageLocation() {
        return garageLocation;
    }

    public void setGarageLocation(String garageLocation) {
        this.garageLocation = garageLocation;
    }

    private String garageLocation;
    private String idNumber;
    private String phoneNumber;
    private String mechanicEmail;

    public String getGarageName() {
        return garageName;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }

    private String garageName;








    public mechanic_details() {
        // empty constructor
        // required for Firebase.
    }



    // Constructor for all variables.
    public mechanic_details(String firstName,String garageName,String registrationStatus,String licenceUrl,String profilePhotoUrl, String secondName,String idNumber ,String phoneNumber,String mechanicEmail) {
        this.firstName = firstName;
        this.current_userId=current_userId;
        this.registrationStatus=registrationStatus;
        this.licenceUrl=licenceUrl;
        this.garageName=garageName;
        this.profilePhotoUrl=profilePhotoUrl;
        this.secondName= secondName;
        this.idNumber = idNumber;
        this.phoneNumber=phoneNumber;
        this.mechanicEmail=mechanicEmail;
    }

    // getter methods for all variables.
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCurrent_userId() {
        return current_userId;
    }

    public void setCurrent_userId(String current_userId) {
        this.current_userId = current_userId;
    }


    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
    // setter method for all variables.
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getMechanicEmail() {
        return mechanicEmail;
    }

    public void setMechanicEmail(String mechanicEmail) {
        this.mechanicEmail = mechanicEmail;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }
    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
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



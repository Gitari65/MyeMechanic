package com.example.myemechanic;

public class request_details {




    public request_details(String carModel, String carPart, String carProblemDescription, String driversId) {
        this.carModel = carModel;
        this.carPart = carPart;
        this.driversId = driversId;
        this.carProblemDescription = carProblemDescription;
    }

    public request_details() {

    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    private  String carModel;

    public String getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    private  String mechanicId;

    public String getCarPart() {
        return carPart;
    }

    public void setCarPart(String carPart) {
        this.carPart = carPart;
    }

    private  String carPart;

    public String getCarProblemDescription() {
        return carProblemDescription;
    }

    public void setCarProblemDescription(String carProblemDescription) {
        this.carProblemDescription = carProblemDescription;
    }

    private  String carProblemDescription;

    public String getDriversId() {
        return driversId;
    }

    public void setDriversId(String driversId) {
        this.driversId = driversId;
    }

    private  String driversId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private  String date;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private  String status;

}

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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    private  String paymentStatus;

    public String getWorkProblem() {
        return workProblem;
    }

    public void setWorkProblem(String workProblem) {
        this.workProblem = workProblem;
    }

    private  String workProblem;

    public String getWorkPrice() {
        return workPrice;
    }

    public void setWorkPrice(String workPrice) {
        this.workPrice = workPrice;
    }

    public String getWorkExpense() {
        return workExpense;
    }

    public void setWorkExpense(String workExpense) {
        this.workExpense = workExpense;
    }

    private  String workPrice;
    private  String workExpense;

    public String getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    private  String mechanicId;

    public String getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }

    private  String responseDate;

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    private  String driverFirstName;

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    private  String problem;

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    private  String cost;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    private  String amount;

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

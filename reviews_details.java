package com.example.myemechanic;

public class reviews_details {
  public reviews_details(Float rating, String name, String review, String date) {
    this.rating = rating;
    this.name = name;
    this.review = review;
    this.date = date;
  }

  public reviews_details() {
  }

  public float getRating() {
    return rating;
  }

  public void setRating(Float rating) {
    this.rating = rating;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getReview() {
    return review;
  }

  public void setReview(String review) {
    this.review = review;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  private  Float rating;
  private String name;
  private  String review;
  private  String date;
}

package com.ggc.theaterkarten;

import javafx.beans.value.ObservableValue;

public class TheaterTicket {
    private String playName, customerName;
    private double price;
    private int seatNumber;
    private String isSold;
    private Date date;
    private Time time;

    public TheaterTicket(String playName, String customerName, double price, int seatNumber, String isSold, Date date, Time time) {
        this.playName = playName;
        this.customerName = customerName;
        this.price = price;
        this.seatNumber = seatNumber;
        this.isSold = isSold;
        this.date = date;
        this.time = time;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String isSold() {
        return isSold;
    }

    public void setSold(String sold) {
        isSold = sold;
    }



    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

}

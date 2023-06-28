package com.ggc.theaterkarten;

public class Customer {
    String name, password;
    double credit;
    public Customer(String name, String password, double credit) {
        this.name = name;
        this.password = password;
        this.credit = credit;
    }


    public Customer(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getCredit() {
        return credit;
    }

    public void topUpCredit(double credit) {
        this.credit = credit;
    }
}

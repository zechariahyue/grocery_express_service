package edu.gatech.cs6310.assignment5.model;

import javax.persistence.*;

@Entity
@Table(name = "customer")
public class Customer extends User {
    @Id
    private String account;
    private int rating;
    private double credit;
    private double pendingTotalCost = 0;

    @OneToOne(cascade = CascadeType.ALL)
    private Location currentLocation;


    public Customer() {
        super();
    }

    public Customer(String account, String firstName, String lastName, String phoneNumber, int rating, double credit) {
        super(firstName, lastName, phoneNumber);
        this.account = account;
        this.rating = rating;
        this.credit = credit;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public boolean canAffordNewItem(int quantity, double unitPrice) {
        return credit >= pendingTotalCost + quantity * unitPrice;
    }

    public void deductCredit(double cost) {
        credit -= cost;
    }

    public void addPendingTotalCost(double cost) {
        pendingTotalCost += cost;
    }

    public void clearPendingTotalCost() {
        pendingTotalCost = 0;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}

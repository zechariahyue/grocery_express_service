package edu.gatech.cs6310.assignment5.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store")
public class Store {
    @Id
    private String name;
    private double revenue;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Drone> drones = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    private int numberOfPurchases = 0;
    private int transfers = 0;

    @OneToOne
    private Location location;


    public Store() {
    }

    public Store(String name, double revenue) {
        this.name = name;
        this.revenue = revenue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public int getNumberOfPurchases() {
        return numberOfPurchases;
    }

    public void increaseNumberOfPurchases() {
        numberOfPurchases++;
    }

    public int getTransfers() {
        return transfers;
    }

    public void increaseTransfers() {
        transfers++;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Item getItem(String itemName) {
        return items.stream().filter(item -> item.getName().equals(itemName)).findFirst().orElse(null);
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean hasItem(String itemName) {
        return items.stream().anyMatch(item -> item.getName().equals(itemName));
    }

    public void addDrone(Drone drone) {
        drones.add(drone);
        drone.setStore(this);
    }

    public boolean hasDrone(String droneId) {
        return drones.stream().anyMatch(drone -> drone.getId().equals(droneId));
    }

    public Drone getDrone(String droneId) {
        return drones.stream().filter(drone -> drone.getId().equals(droneId)).findFirst().orElse(null);
    }

    public List<Drone> getDrones() {
        return drones;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setStore(this);
    }

    public boolean hasOrder(String orderId) {
        return orders.stream().anyMatch(order -> order.getId().equals(orderId));
    }

    public Order getOrder(String orderId) {
        return orders.stream().filter(order -> order.getId().equals(orderId)).findFirst().orElse(null);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void removeOrder(String orderId) {
        Order order = getOrder(orderId);
        if (order != null) {
            orders.remove(order);
            order.setStore(null);
            order.setDrone(null);
        }
    }

    public void addRevenue(double cost) {
        revenue += cost;
    }

    public int getTotalOverloads() {
        int totalOverloads = 0;
        for (Drone drone : drones) {
            totalOverloads += drone.getOverloads();
        }
        return totalOverloads;
    }
}

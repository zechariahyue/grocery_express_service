package edu.gatech.cs6310.assignment5.model;

import javax.persistence.*;
import java.util.Map;
import java.util.TreeMap;

@Entity
@Table(name = "drone")
public class Drone {
    @EmbeddedId
    private DroneId droneId;
    private int liftCapacity;
    private int remainingTrips;

    @OneToOne(mappedBy = "controlledDrone", cascade = CascadeType.ALL)
    private DronePilot controlledByPilot = null;

    @OneToMany(mappedBy = "drone", cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<String, Order> carriedOrders = new TreeMap<>();
    private int overloads;

    @ManyToOne
    @MapsId("storeName")
    @JoinColumn(name = "store_name")
    private Store store;

    @OneToOne
    private Location currentLocation;
    protected int fuelLevel;
    protected int maxFuelCapacity;
    protected double fuelConsumptionRate;

    public Drone() {
    }

    public Drone(String storeName, String id, int liftCapacity, int remainingTrips) {
        this.droneId = new DroneId(storeName, id);
        this.liftCapacity = liftCapacity;
        this.remainingTrips = remainingTrips;
    }

    public String getId() {
        return droneId.getId();
    }

    public void setId(String id) {
        droneId.setId(id);
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getLiftCapacity() {
        return liftCapacity;
    }

    public void setLiftCapacity(int liftCapacity) {
        this.liftCapacity = liftCapacity;
    }

    public int getRemainingTrips() {
        return remainingTrips;
    }

    public void setRemainingTrips(int remainingTrips) {
        this.remainingTrips = remainingTrips;
    }

    public void setControlledByPilot(DronePilot pilot) {
        this.controlledByPilot = pilot;
    }

    public DronePilot getControlledByPilot() {
        return this.controlledByPilot;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(int fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public void setMaxFuelCapacity(int maxFuelCapacity) {
        this.maxFuelCapacity = maxFuelCapacity;
    }

    public int getMaxFuelCapacity() {
        return maxFuelCapacity;
    }

    public double getFuelConsumptionRate() {
        return fuelConsumptionRate;
    }

    public void setFuelConsumptionRate(double fuelConsumptionRate) {
        this.fuelConsumptionRate = fuelConsumptionRate;
    }

    public int calculateTotalWeight() {
        int weight = 0;
        for (Order order : carriedOrders.values()) {
            for (Line line : order.getLines()) {
                weight += line.calculateWeight();
            }
        }
        return weight;
    }

    public boolean canCarryNewItem(int weight) {
        return liftCapacity - calculateTotalWeight() >= weight;
    }

    public void reduceRemainingTrips() {
        remainingTrips -= 1;
    }

    public int getNumberOfOrders() {
        return carriedOrders.size();
    }

    public int getRemainingCapacity() {
        return liftCapacity - calculateTotalWeight();
    }

    public void addOrder(String orderId, Order order) {
        carriedOrders.put(orderId, order);
    }

    public void removeOrder(String orderId) {
        carriedOrders.remove(orderId);
    }

    public int getOverloads() {
        return overloads;
    }

    public void addOverloads() {
        overloads += carriedOrders.size() - 1;
    }

    public boolean canMove(Location destination) {
        double distance = currentLocation.getDistance(destination);
        if (distance * fuelConsumptionRate > fuelLevel) {
            return false;
        }
        return true;
    }

    public boolean move(Location destination) {
        if (destination == null) {
            return false;
        }

        double distance = currentLocation.getDistance(destination);
        currentLocation = destination;
        fuelLevel -= distance * fuelConsumptionRate;
        return true;
    }
}

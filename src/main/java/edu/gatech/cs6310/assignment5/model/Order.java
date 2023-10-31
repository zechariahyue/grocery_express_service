package edu.gatech.cs6310.assignment5.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Entity
@Table(name = "customer_order")
public class Order {
    @Id
    private String id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Drone drone;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "customer_account")
    private Customer customer;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "store_name")
    private Store store;

    @OneToMany(cascade = CascadeType.ALL)
    private Map<String, Line> lines = new TreeMap<>();

    public Order() {
    }

    public Order(String id, Drone drone, Customer customer) {
        this.id = id;
        this.drone = drone;
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean hasItem(String itemName) {
        return lines.containsKey(itemName);
    }

    public List<Line> getLines() {
        List<Line> lineList = new ArrayList<>();
        lines.forEach((itemName, line) -> lineList.add(line));
        return lineList;
    }

    public void addLine(Line newLine) {
        lines.put(newLine.getItem().getName(), newLine);
    }

    public double calculateCost() {
        double cost = 0;
        for (Line line : lines.values()) {
            cost += line.calculateCost();
        }
        return cost;
    }

    public int calculateWeight() {
        int weight = 0;
        for (Line line : lines.values()) {
            weight += line.calculateWeight();
        }
        return weight;
    }
}

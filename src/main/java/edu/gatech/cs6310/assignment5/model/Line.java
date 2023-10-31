package edu.gatech.cs6310.assignment5.model;

import javax.persistence.*;

@Entity
@Table(name = "line")
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "store_name", referencedColumnName = "store_name"),
            @JoinColumn(name = "item_name", referencedColumnName = "name")
    })
    private Item item;
    private int quantity;
    private double unitPrice;

    public Line() {
    }

    public Line(Item item, int quantity, double unitPrice) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int calculateWeight() {
        return quantity * item.getWeight();
    }

    public double calculateCost() {
        return quantity * unitPrice;
    }
}

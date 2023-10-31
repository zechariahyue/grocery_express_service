package edu.gatech.cs6310.assignment5.model;

import javax.persistence.*;

@Entity
@Table(name = "item")
public class Item {
    @EmbeddedId
    private ItemId itemId;

    @Column(nullable = false)
    private int weight;

    @ManyToOne
    @MapsId("storeName")
    @JoinColumn(name = "store_name")
    private Store store;

    public Item() {
    }

    public Item(String storeName, String name, int weight) {
        this.itemId = new ItemId(storeName, name);
        this.weight = weight;
    }

    public String getName() {
        return itemId.getName();
    }

    public void setName(String name) {
        itemId.setName(name);
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

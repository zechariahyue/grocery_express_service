package edu.gatech.cs6310.assignment5.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ItemId implements Serializable {
    private String storeName;
    private String name;

    public ItemId() {
    }

    public ItemId(String storeName, String name) {
        this.storeName = storeName;
        this.name = name;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemId itemId = (ItemId) o;
        return Objects.equals(storeName, itemId.storeName) && Objects.equals(name, itemId.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, name);
    }
}

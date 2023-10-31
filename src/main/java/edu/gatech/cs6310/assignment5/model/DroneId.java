package edu.gatech.cs6310.assignment5.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DroneId implements Serializable {
    private String storeName;
    private String id;

    public DroneId() {
    }

    public DroneId(String storeName, String id) {
        this.storeName = storeName;
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DroneId dronePK = (DroneId) o;
        return Objects.equals(storeName, dronePK.storeName) && Objects.equals(id, dronePK.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, id);
    }
}

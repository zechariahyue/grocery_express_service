package edu.gatech.cs6310.assignment5.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "refueling_station")
public class RefuelingStation {
    @Id
    private String name;

    @OneToOne
    private Location location;

    public RefuelingStation() {
    }

    public RefuelingStation(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void refillFuel(Drone drone) {
        drone.setFuelLevel(drone.getMaxFuelCapacity());
    }
}

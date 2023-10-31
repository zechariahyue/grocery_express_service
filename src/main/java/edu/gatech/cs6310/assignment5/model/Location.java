package edu.gatech.cs6310.assignment5.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(Location.class)
@Table(name = "location")
@Embeddable
public class Location implements Serializable {
    @Id
    private int x;

    @Id
    private int y;

    public Location() {
    }

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x && y == location.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    // get distance between this location and other location
    public double getDistance(Location other) {
        if (other == null) {
            return 0.0;
        }
        int distanceX = this.x - other.x;
        int distanceY = this.y - other.y;
        return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
    }
}

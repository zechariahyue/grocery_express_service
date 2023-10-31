package edu.gatech.cs6310.assignment5.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
public class RefuelingDrone extends Drone {
    public RefuelingDrone() {
        super();
    }

    public RefuelingDrone(String storeName, String id, int liftCapacity, int remainingTrips) {
        super(storeName, id, liftCapacity, remainingTrips);
    }

    // Refuels the given drone with the specified amount of fuel.
    public void refuelDrone(Drone targetDrone, int fuelAmount) {
        int fuelToTransfer = Math.min(fuelAmount, this.fuelLevel);
        int targetDroneFuelCapacity = targetDrone.getMaxFuelCapacity() - targetDrone.getFuelLevel();

        if (fuelToTransfer > targetDroneFuelCapacity) {
            fuelToTransfer = targetDroneFuelCapacity;
        }

        // Ensure refueling drone has enough fuel left to return to the store
        double fuelRequiredToReturn = calculateFuelRequiredToReturn();
        if (this.fuelLevel - fuelToTransfer < fuelRequiredToReturn) {
            fuelToTransfer = this.fuelLevel - (int) fuelRequiredToReturn;
        }

        targetDrone.setFuelLevel(targetDrone.getFuelLevel() + fuelToTransfer);
        this.setFuelLevel(this.getFuelLevel() - fuelToTransfer);
    }

    // Calculates the fuel required for the refueling drone to return to the store.
    private double calculateFuelRequiredToReturn() {
        Store store = this.getStore();
        double distance = this.getCurrentLocation().getDistance(store.getLocation());
        double requiredFuel = distance * this.fuelConsumptionRate;
        return requiredFuel;
    }
}

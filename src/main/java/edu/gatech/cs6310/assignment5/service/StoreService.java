package edu.gatech.cs6310.assignment5.service;

import edu.gatech.cs6310.assignment5.constants.ErrorMessage;
import edu.gatech.cs6310.assignment5.constants.OkMessage;
import edu.gatech.cs6310.assignment5.model.*;
import edu.gatech.cs6310.assignment5.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class StoreService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RefuelingStationRepository refuelingStationRepository;


    public boolean makeStore(String name, double revenue) {
        if (storeRepository.existsById(name)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_ALREADY_EXISTS);
            return false;
        }
        storeRepository.save(new Store(name, revenue));
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public void displayStores() {
        Iterable<Store> stores = storeRepository.findAll();
        stores.forEach((store) -> {
            String storeInfo = "name:" + store.getName() + ",revenue:" + String.format("%.0f", store.getRevenue());

            Location location = store.getLocation();
            if (location != null) {
                storeInfo += ",location:(x:" + location.getX() + ",y:" + location.getY() + ")";
            }
            System.out.println(storeInfo);
        });
        System.out.println(OkMessage.DISPLAY_COMPLETED);
    }

    public boolean hasStore(String storeName) {
        return storeRepository.existsById(storeName);
    }

    public Store getStore(String storeName) {
        Optional<Store> optionalStore = storeRepository.findById(storeName);
        return optionalStore.orElse(null);
    }

    // place a store to a location at x, y
    public boolean placeStore(String name, int x, int y) {
        if (!hasStore((name))) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = getStore(name);
        Location location = store.getLocation();
        Location newLocation;
        if (location == null) {
            newLocation = new Location(x, y);
            locationRepository.save(newLocation);
            store.setLocation(newLocation);
        } else {
            Store storeAtLocation = storeRepository.findByLocation(new Location(x, y)).orElse(null);
            // another store at this location
            if (storeAtLocation != null && !storeAtLocation.getName().equals(name)) {
                System.out.println(ErrorMessage.STORE_ALREADY_EXISTS_AT_THIS_LOCATION);
                return false;
            }
            location.setX(x);
            location.setY(y);
            newLocation = location;
        }

        // Save the store after setting the location
        storeRepository.save(store);

        // The store's location is always a default refueling station
        // assume that the name is the same as the store name
        RefuelingStation newRefuelingStation = new RefuelingStation(name, newLocation);
        refuelingStationRepository.save(newRefuelingStation);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    @Transactional
    public boolean sellItem(String storeName, String itemName, int weight) {
        // invalid store name
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        // item already exist
        if (store.hasItem(itemName)) {
            System.out.println(ErrorMessage.ITEM_IDENTIFIER_ALREADY_EXISTS);
            return false;
        }
        Item item = new Item(storeName, itemName, weight);
        item.setStore(store);
        itemRepository.save(item);
        store.addItem(item);
        storeRepository.save(store);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    @Transactional
    public void displayItems(String storeName) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return;
        }
        Store store = storeRepository.findById(storeName).get();
        for (Item item: store.getItems()) {
            System.out.println(item.getName() + "," + item.getWeight());
        }
        System.out.println(OkMessage.DISPLAY_COMPLETED);
    }

    @Transactional
    public boolean makeDrone(String storeName, String droneId, int liftCapacity, int remainingTrips) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        if (store.hasDrone(droneId)) {
            System.out.println(ErrorMessage.DRONE_IDENTIFIER_ALREADY_EXISTS);
            return false;
        }
        Drone drone = new Drone(storeName, droneId, liftCapacity, remainingTrips);
        // Set the current location of the drone to the store's location
        drone.setCurrentLocation(store.getLocation());
        // Add the drone to the store before saving it
        store.addDrone(drone);
        // Save the drone to the repository
        droneRepository.save(drone);
        // Update the store in the repository
        storeRepository.save(store);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        droneRepository.save(drone);
        return true;
    }

    @Transactional
    public boolean makeRefuelingDrone(String storeName, String droneId, int maxFuelCapacity) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        if (store.hasDrone(droneId)) {
            System.out.println(ErrorMessage.DRONE_IDENTIFIER_ALREADY_EXISTS);
            return false;
        }
        // assume refueling drone cannot carry orders
        int liftCapacity = 0;
        // assume refueling drone has maximum remaining trips
        int remainingTrips = Integer.MAX_VALUE;
        RefuelingDrone refuelingDrone = new RefuelingDrone(storeName, droneId, liftCapacity, remainingTrips);
        refuelingDrone.setMaxFuelCapacity(maxFuelCapacity);
        // first place the drone to the store
        refuelingDrone.setCurrentLocation(store.getLocation());
        // Add the drone to the store before saving it
        store.addDrone(refuelingDrone);

        // Save the drone to the repository
        droneRepository.save(refuelingDrone);

        // Update the store in the repository
        storeRepository.save(store);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    @Transactional
    public boolean setMaxFuelCapacityOfDrone(String storeName, String droneId, int maxFuelCapacity) {
        if (!hasStore(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = getStore(storeName);
        if (!store.hasDrone(droneId)) {
            System.out.println(ErrorMessage.DRONE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Drone drone = store.getDrone(droneId);
        drone.setMaxFuelCapacity(maxFuelCapacity);
        droneRepository.save(drone);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    @Transactional
    public boolean setFuelConsumptionRateOfDrone(String storeName, String droneId, double fuelConsumptionRate) {
        if (!hasStore(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = getStore(storeName);
        if (!store.hasDrone(droneId)) {
            System.out.println(ErrorMessage.DRONE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Drone drone = store.getDrone(droneId);
        drone.setFuelConsumptionRate(fuelConsumptionRate);
        droneRepository.save(drone);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    @Transactional
    public void displayDrones(String storeName) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return;
        }
        Store store = storeRepository.findById(storeName).get();
        for (Drone drone : droneRepository.findAllByStore(store)) {
            String droneInfo = "droneID:" + drone.getId() + ",total_cap:" + drone.getLiftCapacity() +
                    ",num_orders:" + drone.getNumberOfOrders() + ",remaining_cap:" + drone.getRemainingCapacity() +
                    ",trips_left:" + drone.getRemainingTrips();
            if (drone.getControlledByPilot() != null) {
                DronePilot pilot = drone.getControlledByPilot();
                droneInfo += ",flown_by:" + pilot.getFullName();
            }
            Location currentLocation = drone.getCurrentLocation();
            if (currentLocation != null) {
                droneInfo += ",at_location: (x: " + currentLocation.getX() + ",y: " + currentLocation.getY() + ")" +
                ",max_fuel_capacity:" + drone.getMaxFuelCapacity() + ",fuel_level:" + drone.getFuelLevel() +
                ",fuel_consumption_rate:" + drone.getFuelConsumptionRate() + ",refueling_drone:" + (drone instanceof RefuelingDrone);
            }
            System.out.println(droneInfo);
        }
        System.out.println(OkMessage.DISPLAY_COMPLETED);
    }

    @Transactional
    public void displayRefuelingDrones(String storeName) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return;
        }
        Store store = storeRepository.findById(storeName).get();
        for (Drone drone : droneRepository.findAllByStore(store)) {
            if (drone instanceof RefuelingDrone) {
                Location currentLocation = drone.getCurrentLocation();
                String droneInfo = "droneID:" + drone.getId() + ",at_location: (x: " + currentLocation.getX() + ",y: " + currentLocation.getY() + ")" +
                        ",max_fuel_capacity:" + drone.getMaxFuelCapacity() + ",fuel_level:" + drone.getFuelLevel() + ",fuel_consumption_rate:" + drone.getFuelConsumptionRate();
                if (drone.getControlledByPilot() != null) {
                    DronePilot pilot = drone.getControlledByPilot();
                    droneInfo += ",flown_by:" + pilot.getFullName();
                }
                System.out.println(droneInfo);
            }
        }
        System.out.println(OkMessage.DISPLAY_COMPLETED);
    }

    @Transactional
    public boolean requestRefuelingDrone(String storeName, String droneId, String refuelingDroneId) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        if (!store.hasDrone(droneId)) {
            System.out.println(ErrorMessage.DRONE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        if (!store.hasDrone(refuelingDroneId)) {
            System.out.println(ErrorMessage.REFUELING_DRONE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Drone drone = store.getDrone(droneId);
        RefuelingDrone refuelingDrone = (RefuelingDrone) store.getDrone(refuelingDroneId);

        if (refuelingDrone.getControlledByPilot() == null) {
            System.out.println(ErrorMessage.REFUELING_DRONE_NEEDS_PILOT);
            return false;
        }

        // refueling drone cannot go to location of drone
        if (!refuelingDrone.canMove(drone.getCurrentLocation())) {
            System.out.println(ErrorMessage.REFUELING_DRONE_NEEDS_FUEL);
            return false;
        }

        refuelingDrone.move(drone.getCurrentLocation());
        double distanceToStore = drone.getCurrentLocation().getDistance(store.getLocation());
        double amountToStore = distanceToStore * drone.getFuelConsumptionRate() - drone.getFuelLevel();

        // we get minimum amount for drone to return to store
        int fuelAmount = (int) Math.min(drone.getMaxFuelCapacity() - drone.getFuelLevel(), amountToStore);
        refuelingDrone.refuelDrone(drone, fuelAmount);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public void displayEfficiency() {
        for (Store store : storeRepository.findAll()) {
            System.out.println("name:" + store.getName() + ",purchases:" + store.getNumberOfPurchases() +
                    ",overloads:" + store.getTotalOverloads() + ",transfers:" + store.getTransfers());
        }
        System.out.println(OkMessage.DISPLAY_COMPLETED);
    }
}

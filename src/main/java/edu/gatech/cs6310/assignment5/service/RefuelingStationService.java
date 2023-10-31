package edu.gatech.cs6310.assignment5.service;

import edu.gatech.cs6310.assignment5.constants.OkMessage;
import edu.gatech.cs6310.assignment5.model.Drone;
import edu.gatech.cs6310.assignment5.model.Location;
import edu.gatech.cs6310.assignment5.model.RefuelingStation;
import edu.gatech.cs6310.assignment5.model.Store;
import edu.gatech.cs6310.assignment5.repository.LocationRepository;
import edu.gatech.cs6310.assignment5.repository.RefuelingStationRepository;
import edu.gatech.cs6310.assignment5.constants.ErrorMessage;
import edu.gatech.cs6310.assignment5.repository.StoreRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RefuelingStationService {
    private RefuelingStationRepository refuelingStationRepository;
    private LocationRepository locationRepository;
    private StoreRepository storeRepository;

    public RefuelingStationService(RefuelingStationRepository refuelingStationRepository, LocationRepository locationRepository, StoreRepository storeRepository) {
        this.refuelingStationRepository = refuelingStationRepository;
        this.locationRepository = locationRepository;
        this.storeRepository = storeRepository;
    }

    public boolean makeRefuelingStation(String name, int x, int y) {
        // name of refueling station is unique
        if (refuelingStationRepository.existsById(name)) {
            System.out.println(ErrorMessage.REFUELING_STATION_ALREADY_EXISTS);
            return false;
        }
        // check if there exists a refueling station at the location
        for (RefuelingStation station : refuelingStationRepository.findAll()) {
            Location location = station.getLocation();
            if (location.getX() == x && location.getY() == y) {
                System.out.println(ErrorMessage.REFUELING_STATION_ALREADY_EXISTS_IN_THIS_LOCATION);
                return false;
            }
        }
        Location newLocation = new Location(x, y);
        locationRepository.save(newLocation);
        RefuelingStation newStation = new RefuelingStation(name, newLocation);
        refuelingStationRepository.save(newStation);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public void displayRefuelingStations() {
        for (RefuelingStation refuelingStation : refuelingStationRepository.findAll()) {
            System.out.println("name:" + refuelingStation.getName() + " at location: " + "(x:" + refuelingStation.getLocation().getX() +
                    ", y:" + refuelingStation.getLocation().getY() + ")");
        }
        System.out.println(OkMessage.DISPLAY_COMPLETED);
    }

    @Transactional
    public boolean fuelDrone(String storeName, String droneId) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        if (!store.hasDrone(droneId)) {
            System.out.println(ErrorMessage.DRONE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Drone drone = store.getDrone(droneId);
        Location droneLocation = drone.getCurrentLocation();

        if (droneLocation == null) {
            System.out.println(ErrorMessage.DRONE_LOCATION_IS_NOT_PLACED);
            return false;
        }

        // drone is not in refueling station
        if (!refuelingStationRepository.existsByLocation(droneLocation)) {
            System.out.println(ErrorMessage.DRONE_IS_NOT_IN_A_REFUELING_STATION);
            return false;
        }

        RefuelingStation refuelingStation = refuelingStationRepository.findByLocation(droneLocation).get();
        refuelingStation.refillFuel(drone);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }
}

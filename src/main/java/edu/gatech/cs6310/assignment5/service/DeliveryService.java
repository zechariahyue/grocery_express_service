package edu.gatech.cs6310.assignment5.service;


import edu.gatech.cs6310.assignment5.constants.ErrorMessage;
import edu.gatech.cs6310.assignment5.constants.OkMessage;
import edu.gatech.cs6310.assignment5.model.Drone;
import edu.gatech.cs6310.assignment5.model.DronePilot;
import edu.gatech.cs6310.assignment5.model.Location;
import edu.gatech.cs6310.assignment5.model.Store;
import edu.gatech.cs6310.assignment5.repository.DronePilotRepository;
import edu.gatech.cs6310.assignment5.repository.DroneRepository;
import edu.gatech.cs6310.assignment5.repository.LocationRepository;
import edu.gatech.cs6310.assignment5.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class DeliveryService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private DronePilotRepository dronePilotRepository;

    @Autowired
    private LocationRepository locationRepository;

    public boolean flyDrone(String storeName, String droneId, String pilotId) {
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
        if (!dronePilotRepository.existsById(pilotId)) {
            System.out.println(ErrorMessage.PILOT_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        DronePilot pilot = dronePilotRepository.findById(pilotId).get();
        pilot.controlDrone(drone);
        droneRepository.save(drone);
        dronePilotRepository.save(pilot);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public boolean moveDrone(String storeName, String droneId, int x, int y) {
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
        Location currentLocation = drone.getCurrentLocation();
        Location destination = new Location(x, y);
        // destination is the same as current location
        if (currentLocation.equals(destination)) {
            System.out.println(OkMessage.DESTINATION_IS_CURRENT_LOCATION);
            return true;
        }
        if (drone.getControlledByPilot() == null) {
            System.out.println(ErrorMessage.DRONE_NEEDS_PILOT);
            return false;
        }
        if (!drone.canMove(destination)) {
            System.out.println(ErrorMessage.DRONE_NEEDS_FUEL);
            return false;
        }
        destination = locationRepository.save(destination);
        drone.move(destination);
        droneRepository.save(drone);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }
}

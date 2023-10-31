package edu.gatech.cs6310.assignment5.repository;

import edu.gatech.cs6310.assignment5.model.Drone;
import edu.gatech.cs6310.assignment5.model.DroneId;
import edu.gatech.cs6310.assignment5.model.Store;
import org.springframework.data.repository.CrudRepository;

public interface DroneRepository extends CrudRepository<Drone, DroneId> {
    Iterable<Drone> findAllByStore(Store store);
}

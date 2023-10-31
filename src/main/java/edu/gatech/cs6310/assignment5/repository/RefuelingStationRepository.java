package edu.gatech.cs6310.assignment5.repository;

import edu.gatech.cs6310.assignment5.model.Location;
import edu.gatech.cs6310.assignment5.model.RefuelingStation;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefuelingStationRepository extends CrudRepository<RefuelingStation, String> {
    Optional<RefuelingStation> findByLocation(Location location);
    boolean existsByLocation(Location location);
}

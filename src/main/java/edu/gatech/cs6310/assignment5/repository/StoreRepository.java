package edu.gatech.cs6310.assignment5.repository;

import edu.gatech.cs6310.assignment5.model.Location;
import edu.gatech.cs6310.assignment5.model.Store;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StoreRepository extends CrudRepository<Store, String> {
    Optional<Store> findByLocation(Location location);
}

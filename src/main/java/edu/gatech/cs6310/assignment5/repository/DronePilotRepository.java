package edu.gatech.cs6310.assignment5.repository;

import edu.gatech.cs6310.assignment5.model.DronePilot;
import org.springframework.data.repository.CrudRepository;

public interface DronePilotRepository extends CrudRepository<DronePilot, String> {
    boolean existsByLicenseId(String licenseId);
}

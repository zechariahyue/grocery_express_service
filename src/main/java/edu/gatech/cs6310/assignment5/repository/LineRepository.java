package edu.gatech.cs6310.assignment5.repository;

import edu.gatech.cs6310.assignment5.model.Line;
import org.springframework.data.repository.CrudRepository;

public interface LineRepository extends CrudRepository<Line, Long> {
}

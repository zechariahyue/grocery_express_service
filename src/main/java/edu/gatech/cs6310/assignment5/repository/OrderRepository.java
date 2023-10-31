package edu.gatech.cs6310.assignment5.repository;

import edu.gatech.cs6310.assignment5.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, String> {
}

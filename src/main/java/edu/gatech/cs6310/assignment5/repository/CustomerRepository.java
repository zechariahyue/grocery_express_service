package edu.gatech.cs6310.assignment5.repository;

import edu.gatech.cs6310.assignment5.model.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, String> {
}

package edu.gatech.cs6310.assignment5.repository;

import edu.gatech.cs6310.assignment5.authentication.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findByUsername(String username);
}

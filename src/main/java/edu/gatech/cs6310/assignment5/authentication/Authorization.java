package edu.gatech.cs6310.assignment5.authentication;


import edu.gatech.cs6310.assignment5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Authorization {

    @Autowired
    private UserRepository userRepository;

    public void checkAuthorization(String username, String requiredRoles) throws AccessDeniedException {
        Optional<User> usr = userRepository.findByUsername(username);
        if(!usr.isPresent()){
            throw new AccessDeniedException("Access denied");
        }
        boolean hasRequiredRole = usr.get().getRoles().stream()
                .anyMatch(requiredRoles::contains);

        if (!hasRequiredRole) {
            throw new AccessDeniedException("Access denied");
        }
    }
}

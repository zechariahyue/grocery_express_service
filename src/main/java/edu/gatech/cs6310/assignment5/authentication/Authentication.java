package edu.gatech.cs6310.assignment5.authentication;

import edu.gatech.cs6310.assignment5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Authentication implements UserDetailsService {

    private List<User> users = new ArrayList<>();

    @Autowired
    private UserRepository userRepository;

    public void getAllUsers() {
        users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
    }

    public UserDetails loadUserByUsername(String username,String password,String role) throws UsernameNotFoundException {
        Optional<User> optionalUser = users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getRoles().contains(role))
                .findFirst();

        if (optionalUser.isPresent()) {
            Optional<User> optionalPassword = users.stream()
                    .filter(user->user.getPassword().equals(password))
                    .findFirst();
            if(!optionalPassword.isPresent()){
                throw new BadCredentialsException("Invalid username or password");
            }
            User user = optionalUser.get();
            return user;

        }

        throw new BadCredentialsException("Invalid username or password");
    }

    public void logout() {
        // Clear the authenticated user from the SecurityContext
        System.out.println("Logging out");
        SecurityContextHolder.clearContext();
        System.exit(0);
    }

    public UserDetails login(final String userName,final String password,final String role) {
        getAllUsers();
        return loadUserByUsername(userName,password,role);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}

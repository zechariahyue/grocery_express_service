package edu.gatech.cs6310.assignment5.service;

import edu.gatech.cs6310.assignment5.model.Customer;
import edu.gatech.cs6310.assignment5.constants.ErrorMessage;
import edu.gatech.cs6310.assignment5.constants.OkMessage;
import edu.gatech.cs6310.assignment5.model.Location;
import edu.gatech.cs6310.assignment5.repository.CustomerRepository;
import edu.gatech.cs6310.assignment5.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;


    public boolean makeCustomer(String account, String firstName, String lastName, String phoneNumber, int rating, double credit) {
        if (customerRepository.existsById(account)) {
            System.out.println(ErrorMessage.CUSTOMER_IDENTIFIER_ALREADY_EXISTS);
            return false;
        }
        Customer customer = new Customer(account, firstName, lastName, phoneNumber, rating, credit);
        customerRepository.save(customer);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public void displayCustomers() {
        for (Customer customer : customerRepository.findAll()) {
            String customerInfo = "name:" + customer.getFullName() + ",phone:" + customer.getPhoneNumber() + ",rating:" +
                    customer.getRating() + ",credit:" + String.format("%.0f", customer.getCredit());
            Location currentLocation = customer.getCurrentLocation();
            if (currentLocation != null) {
                customerInfo += ",location:(x:" + currentLocation.getX() + ",y:" + currentLocation.getY() + ")";
            }
            System.out.println(customerInfo);
        }
        System.out.println(OkMessage.DISPLAY_COMPLETED);
    }

    public boolean hasCustomer(String account) {
        return customerRepository.existsById(account);
    }

    public Customer getCustomer(String account) {
        return customerRepository.findById(account).orElse(null);
    }

    @Transactional
    public boolean placeCustomer(String account, int x, int y) {
        if (!hasCustomer(account)) {
            System.out.println(ErrorMessage.CUSTOMER_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Customer customer = getCustomer(account);
        Location location = customer.getCurrentLocation();
        if (location == null) {
            Location newLocation = new Location(x, y);
            customer.setCurrentLocation(newLocation);
        } else {
            location.setX(x);
            location.setY(y);
        }
        customerRepository.save(customer);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }
}

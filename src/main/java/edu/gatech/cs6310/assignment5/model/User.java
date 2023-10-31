package edu.gatech.cs6310.assignment5.model;

import edu.gatech.cs6310.assignment5.service.EncryptionService;

import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User {
    @Convert(converter = EncryptionService.class)
    protected String firstName;
    @Convert(converter = EncryptionService.class)
    protected String lastName;
    @Convert(converter = EncryptionService.class)
    protected String phoneNumber;

    public User() {
    }

    public User(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return this.firstName + "_" + this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

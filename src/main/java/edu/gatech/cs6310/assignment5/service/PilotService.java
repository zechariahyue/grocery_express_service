package edu.gatech.cs6310.assignment5.service;

import edu.gatech.cs6310.assignment5.model.DronePilot;
import edu.gatech.cs6310.assignment5.constants.ErrorMessage;
import edu.gatech.cs6310.assignment5.constants.OkMessage;
import edu.gatech.cs6310.assignment5.repository.DronePilotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PilotService {
    @Autowired
    private DronePilotRepository dronePilotRepository;

    public boolean makePilot(String account, String firstName, String lastName, String phoneNumber, String taxIdentifier,
                              String licenseId, int successfulDeliveries) {
        if (dronePilotRepository.existsById(account)) {
            System.out.println(ErrorMessage.PILOT_IDENTIFIER_ALREADY_EXISTS);
            return false;
        }
        if (dronePilotRepository.existsByLicenseId(licenseId)) {
            System.out.println(ErrorMessage.PILOT_LICENSE_ALREADY_EXISTS);
            return false;
        }
        DronePilot pilot = new DronePilot(account, firstName, lastName, phoneNumber, taxIdentifier, licenseId, successfulDeliveries);
        dronePilotRepository.save(pilot);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public void displayPilots() {
        for (DronePilot pilot : dronePilotRepository.findAll()) {
            System.out.println("name:" + pilot.getFullName() + ",phone:" + pilot.getPhoneNumber() +
                    ",taxID:" + pilot.getTaxIdentifier() + ",licenseID:" + pilot.getLicenceId() + ",experience:" + pilot.getSuccessfulDeliveries());
        }
        System.out.println(OkMessage.DISPLAY_COMPLETED);
    }

    public boolean hasPilot(String pilotAccount) {
        return dronePilotRepository.existsById(pilotAccount);
    }

    public DronePilot getPilot(String pilotAccount) {
        return dronePilotRepository.findById(pilotAccount).get();
    }
}

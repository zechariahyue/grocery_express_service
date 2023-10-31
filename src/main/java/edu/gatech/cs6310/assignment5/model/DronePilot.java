package edu.gatech.cs6310.assignment5.model;

import edu.gatech.cs6310.assignment5.service.EncryptionService;

import javax.persistence.*;

@Entity
@Table(name = "drone_pilot")
public class DronePilot extends User {
    @Id
    private String account;

    @Convert(converter = EncryptionService.class)
    @Column(unique = true)
    private String taxIdentifier;
    @Convert(converter = EncryptionService.class)
    @Column(unique = true)
    private String licenseId;
    private int successfulDeliveries;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "store_name", referencedColumnName = "store_name"),
            @JoinColumn(name = "drone_id", referencedColumnName = "id")
    })
    private Drone controlledDrone = null;

    public DronePilot() {
        super();
    }

    public DronePilot(String account, String firstName, String lastName, String phoneNumber, String taxIdentifier,
                      String licenseId, int successfulDeliveries) {
        super(firstName, lastName, phoneNumber);
        this.account = account;
        this.taxIdentifier = taxIdentifier;
        this.licenseId = licenseId;
        this.successfulDeliveries = successfulDeliveries;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(String taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }

    public String getLicenceId() {
        return licenseId;
    }

    public void setLicenceId(String licenceId) {
        this.licenseId = licenceId;
    }

    public int getSuccessfulDeliveries() {
        return successfulDeliveries;
    }

    public void setSuccessfulDeliveries(int successfulDeliveries) {
        this.successfulDeliveries = successfulDeliveries;
    }

    public void controlDrone(Drone drone) {
        Drone originalControlledDrone = this.controlledDrone;
        DronePilot originalControlledPilot = drone.getControlledByPilot();

        this.controlledDrone = drone;
        this.controlledDrone.setControlledByPilot(this);

        if (originalControlledDrone != null) {
            originalControlledDrone.setControlledByPilot(null);
        }
        if (originalControlledPilot != null) {
            originalControlledPilot.controlledDrone = null;
        }

    }

    public void increaseSuccessfulDeliveries() {
        successfulDeliveries += 1;
    }
}

package edu.gatech.cs6310.assignment5.command;

import edu.gatech.cs6310.assignment5.authentication.Authentication;
import edu.gatech.cs6310.assignment5.authentication.Authorization;
import edu.gatech.cs6310.assignment5.authentication.User;
import edu.gatech.cs6310.assignment5.repository.UserRepository;
import edu.gatech.cs6310.assignment5.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CommandRunner {
    @Autowired
    private Authentication authentication;
    @Autowired
    private Authorization authorization;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private PilotService pilotService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private RefuelingStationService refuelingStationService;

    public void commandLoop() {
        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        final String DELIMITER = ",";
        System.out.print("Enter your username: ");
        String username = commandLineInput.next();
        System.out.print("Enter your password: ");
        String password = commandLineInput.next();
        System.out.print("Enter 1 for USER , 2 for ADMIN role ");
        int role =  commandLineInput.nextInt();
        String roleName = (role==1) ? "USER" : "ADMIN";
        try {
            UserDetails userDetails = authentication.login(username,password,roleName);
            if (userDetails!=null){
                System.out.println("User Authentication successful for : "+username);
            }
        } catch (final BadCredentialsException badCredentialsException) {
            System.out.println(badCredentialsException.getMessage());
            System.out.print("Do you want to signup(yes/no): ");
            String response = commandLineInput.next();
            if ("yes".equalsIgnoreCase(response)){
                System.out.print("Enter a different username: ");
                username = commandLineInput.next();
                System.out.print("Enter your password: ");
                password = commandLineInput.next();
                System.out.print("Enter 1 for USER , 2 for ADMIN role ");
                role =  commandLineInput.nextInt();
                roleName = (role==1)?"USER":"ADMIN";
                addUserToTable(username,password,roleName);
            } else {
                authentication.logout();
            }
        }
        commandLineInput.nextLine();
        while (true) {
            try {
                // Determine the next command and echo it to the monitor for testing purposes
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);
                System.out.println("> " + wholeInputLine);
                if (wholeInputLine.startsWith("//")) {
                    continue;
                }
                try{
                    if(tokens[0].startsWith("make") || tokens[0].startsWith("display_efficiency") || tokens[0].startsWith("transfer_order")){
                        authorization.checkAuthorization(username,"ADMIN");
                    }
                }catch(final AccessDeniedException accessDeniedException){
                    System.out.println(accessDeniedException.getMessage());
                    continue;
                }
                if (tokens[0].equals("make_store")) {
                    storeService.makeStore(tokens[1], Double.parseDouble(tokens[2]));
                } else if (tokens[0].equals("display_stores")) {
                    storeService.displayStores();
                } else if (tokens[0].equals("sell_item")) {
                    storeService.sellItem(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                } else if (tokens[0].equals("display_items")) {
                    storeService.displayItems(tokens[1]);
                } else if (tokens[0].equals("make_pilot")) {
                    pilotService.makePilot(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], Integer.parseInt(tokens[7]));
                } else if (tokens[0].equals("display_pilots")) {
                    pilotService.displayPilots();
                } else if (tokens[0].equals("make_drone")) {
                    storeService.makeDrone(tokens[1], tokens[2], Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]));
                } else if (tokens[0].equals("display_drones")) {
                    storeService.displayDrones(tokens[1]);
                } else if (tokens[0].equals("fly_drone")) {
                    deliveryService.flyDrone(tokens[1], tokens[2], tokens[3]);
                } else if (tokens[0].equals("make_customer")) {
                    customerService.makeCustomer(tokens[1], tokens[2], tokens[3], tokens[4], Integer.parseInt(tokens[5]), Double.parseDouble(tokens[6]));
                } else if (tokens[0].equals("display_customers")) {
                    customerService.displayCustomers();
                } else if (tokens[0].equals("start_order")) {
                    orderService.startOrder(tokens[1], tokens[2], tokens[3], tokens[4]);
                } else if (tokens[0].equals("display_orders")) {
                    orderService.displayOrders(tokens[1]);
                } else if (tokens[0].equals("request_item")) {
                    orderService.requestItem(tokens[1], tokens[2], tokens[3], Integer.parseInt(tokens[4]), Double.parseDouble(tokens[5]));
                } else if (tokens[0].equals("purchase_order")) {
                    orderService.purchaseOrder(tokens[1], tokens[2]);
                } else if (tokens[0].equals("cancel_order")) {
                    orderService.cancelOrder(tokens[1], tokens[2]);
                } else if (tokens[0].equals("transfer_order")) {
                    orderService.transferOrder(tokens[1], tokens[2], tokens[3]);
                } else if (tokens[0].equals("display_efficiency")) {
                    storeService.displayEfficiency();
                } else if (tokens[0].equals("stop")) {
                    System.out.println("stop acknowledged");
                    break;
                } else if (tokens[0].equals("place_store")) {
                    storeService.placeStore(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
                } else if (tokens[0].equals("make_refueling_station")) {
                    refuelingStationService.makeRefuelingStation(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
                } else if (tokens[0].equals("display_refueling_stations")) {
                    refuelingStationService.displayRefuelingStations();
                } else if (tokens[0].equals("set_drone_max_fuel_capacity")) {
                    storeService.setMaxFuelCapacityOfDrone(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                } else if (tokens[0].equals("place_customer")) {
                    customerService.placeCustomer(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
                } else if (tokens[0].equals("move_drone")) {
                    deliveryService.moveDrone(tokens[1], tokens[2], Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]));
                } else if (tokens[0].equals("fuel_drone")) {
                    refuelingStationService.fuelDrone(tokens[1], tokens[2]);
                } else if (tokens[0].equals("make_refueling_drone")) {
                    storeService.makeRefuelingDrone(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                } else if (tokens[0].equals("display_refueling_drones")) {
                    storeService.displayRefuelingDrones(tokens[1]);
                } else if (tokens[0].equals("set_fuel_consumption_rate")) {
                    storeService.setFuelConsumptionRateOfDrone(tokens[1], tokens[2], Double.parseDouble(tokens[3]));
                } else if (tokens[0].equals("request_refueling_drone")) {
                    storeService.requestRefuelingDrone(tokens[1], tokens[2], tokens[3]);
                } else if (tokens[0].equals("deliver_order")) {
                    orderService.deliverOrder(tokens[1], tokens[2]);
                } else {
                    System.out.println("command " + tokens[0] + " NOT acknowledged");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println();
            }
        }

        System.out.println("simulation terminated");
        commandLineInput.close();commandLineInput.close();
        authentication.logout();
    }

    private void addUserToTable(final String username, final String password,final String role){
        User user = new User(username,password);
        user.setRoles(role);
        userRepository.save(user);
        System.out.println(user.getUsername()+" added successfully");
    }
}

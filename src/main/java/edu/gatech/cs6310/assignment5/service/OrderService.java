package edu.gatech.cs6310.assignment5.service;

import edu.gatech.cs6310.assignment5.constants.ErrorMessage;
import edu.gatech.cs6310.assignment5.constants.OkMessage;
import edu.gatech.cs6310.assignment5.model.*;
import edu.gatech.cs6310.assignment5.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.PriorityQueue;

@Service
@Transactional
public class OrderService {
    private StoreRepository storeRepository;
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private LineRepository lineRepository;
    private RefuelingStationRepository refuelingStationRepository;
    private DroneRepository droneRepository;
    private DronePilotRepository dronePilotRepository;

    public OrderService() {
    }

    @Autowired
    public OrderService(StoreRepository storeRepository, CustomerRepository customerRepository,
                        OrderRepository orderRepository, LineRepository lineRepository, RefuelingStationRepository refuelingStationRepository,
                        DroneRepository droneRepository, DronePilotRepository dronePilotRepository) {
        this.storeRepository = storeRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.lineRepository = lineRepository;
        this.refuelingStationRepository = refuelingStationRepository;
        this.droneRepository = droneRepository;
        this.dronePilotRepository = dronePilotRepository;
    }

    public boolean startOrder(String storeName, String id, String droneId, String customerAccount) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        if (store.hasOrder(id)) {
            System.out.println(ErrorMessage.ORDER_IDENTIFIER_ALREADY_EXISTS);
            return false;
        }
        if (!store.hasDrone(droneId)) {
            System.out.println(ErrorMessage.DRONE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        if (!customerRepository.existsById(customerAccount)) {
            System.out.println(ErrorMessage.CUSTOMER_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Drone drone = store.getDrone(droneId);
        Customer customer = customerRepository.findById(customerAccount).get();
        Order order = new Order(id, drone, customer);
        store.addOrder(order);
        drone.addOrder(id, order);
        storeRepository.save(store);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public void displayOrders(String storeName) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return;
        }
        Store store = storeRepository.findById(storeName).get();
        for (Order order : store.getOrders()) {
            System.out.println("orderID:" + order.getId());
            for (Line line : order.getLines()) {
                System.out.println("item_name:" + line.getItem().getName() + ",total_quantity:" + line.getQuantity() +
                        ",total_cost:" + String.format("%.0f", line.calculateCost()) + ",total_weight:" + line.calculateWeight());
            }
        }
        System.out.println(OkMessage.DISPLAY_COMPLETED);
    }

    private boolean validateStoreAndOrder(String storeName, String orderId) {
        if (!storeRepository.existsById(storeName)) {
            System.out.println(ErrorMessage.STORE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        if (!store.hasOrder(orderId)) {
            System.out.println(ErrorMessage.ORDER_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        return true;
    }

    public boolean requestItem(String storeName, String orderId, String itemName, int quantity, double unitPrice) {
        if (!validateStoreAndOrder(storeName, orderId)) {
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        if (!store.hasItem(itemName)) {
            System.out.println(ErrorMessage.ITEM_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Order order = store.getOrder(orderId);
        if (order.hasItem(itemName)) {
            System.out.println(ErrorMessage.ITEM_ALREADY_ORDERED);
            return false;
        }
        Customer customer = order.getCustomer();
        if (!customer.canAffordNewItem(quantity, unitPrice)) {
            System.out.println(ErrorMessage.CUSTOMER_CANT_AFFORD_NEW_ITEM);
            return false;
        }
        Item item = store.getItem(itemName);
        Drone drone = order.getDrone();
        if (!drone.canCarryNewItem(quantity * item.getWeight())) {
            System.out.println(ErrorMessage.DRONE_CANT_CARRY_NEW_ITEM);
            return false;
        }
        Line newLine = new Line(item, quantity, unitPrice);
        lineRepository.save(newLine);
        order.addLine(newLine);
        customer.addPendingTotalCost(quantity * unitPrice);
        drone.addOrder(orderId, order);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public boolean purchaseOrder(String storeName, String orderId) {
        if (!validateStoreAndOrder(storeName, orderId)) {
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        Order order = store.getOrder(orderId);
        Customer customer = order.getCustomer();
        Drone drone = order.getDrone();
        DronePilot pilot = drone.getControlledByPilot();
        if (pilot == null) {
            System.out.println(ErrorMessage.DRONE_NEEDS_PILOT);
            return false;
        }
        if (drone.getRemainingTrips() == 0) {
            System.out.println(ErrorMessage.DRONE_NEEDS_FUEL);
            return false;
        }
        double cost = order.calculateCost();
        customer.deductCredit(cost);
        customer.clearPendingTotalCost();
        store.addRevenue(cost);
        drone.reduceRemainingTrips();
        pilot.increaseSuccessfulDeliveries();
        store.removeOrder(orderId);
        store.increaseNumberOfPurchases();
        drone.addOverloads();
        drone.removeOrder(orderId);

        // Save the modified entities
        storeRepository.save(store);
        customerRepository.save(customer);
        droneRepository.save(drone);
        dronePilotRepository.save(pilot);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public boolean deliverOrder(String storeName, String orderId) {
        if (!validateStoreAndOrder(storeName, orderId)) {
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        Order order = store.getOrder(orderId);
        Customer customer = order.getCustomer();
        Drone drone = order.getDrone();
        DronePilot pilot = drone.getControlledByPilot();
        if (pilot == null) {
            System.out.println(ErrorMessage.DRONE_NEEDS_PILOT);
            return false;
        }
        if (drone.getRemainingTrips() == 0) {
            System.out.println(ErrorMessage.DRONE_NEEDS_FUEL);
            return false;
        }
        Location customerLocation = customer.getCurrentLocation();
        if (customerLocation == null) {
            System.out.println(ErrorMessage.CUSTOMER_LOCATION_DOES_NOT_EXIST);
            return false;
        }

        // drone can directly move to customer location
        if (drone.canMove(customerLocation)) {
            drone.move(customerLocation);
            System.out.println(OkMessage.DRONE_HAS_DELIVERED_ORDER_TO_CUSTOMER);
            System.out.println(OkMessage.CHANGE_COMPLETED);
            return true;
        }

        PriorityQueue<RefuelingStation> nearestRefuelingStations = new PriorityQueue<>((a, b) -> {
            double distanceA = drone.getCurrentLocation().getDistance(a.getLocation());
            double distanceB = drone.getCurrentLocation().getDistance(b.getLocation());
            return Double.compare(distanceA, distanceB);
        });
        for (RefuelingStation refuelingStation : refuelingStationRepository.findAll()) {
            nearestRefuelingStations.offer(refuelingStation);
        }

        while (!nearestRefuelingStations.isEmpty()) {
            // get the nearest refueling station
            RefuelingStation refuelingStation = nearestRefuelingStations.poll();
            // drone cannot move to the nearest refueling station
            if (!drone.canMove(refuelingStation.getLocation())) {
                System.out.println(ErrorMessage.DRONE_NEEDS_FUEL);
                return false;
            }
            drone.move(refuelingStation.getLocation());
            refuelingStation.refillFuel(drone);
            if (drone.canMove(customerLocation)) {
                drone.move(customerLocation);
                System.out.println(OkMessage.DRONE_HAS_DELIVERED_ORDER_TO_CUSTOMER);
                System.out.println(OkMessage.CHANGE_COMPLETED);
                return true;
            }
        }
        System.out.println(ErrorMessage.DRONE_NEEDS_FUEL);
        return false;
    }

    public boolean cancelOrder(String storeName, String orderId) {
        if (!validateStoreAndOrder(storeName, orderId)) {
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        Order order = store.getOrder(orderId);
        Drone drone = order.getDrone();
        drone.removeOrder(orderId);
        store.removeOrder(orderId);
        orderRepository.deleteById(orderId);
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }

    public boolean transferOrder(String storeName, String orderId, String newDroneId) {
        if (!validateStoreAndOrder(storeName, orderId)) {
            return false;
        }
        Store store = storeRepository.findById(storeName).get();
        Order order = store.getOrder(orderId);
        Drone currentDrone = order.getDrone();
        if (!store.hasDrone(newDroneId)) {
            System.out.println(ErrorMessage.DRONE_IDENTIFIER_DOES_NOT_EXIST);
            return false;
        }
        Drone newDrone = store.getDrone(newDroneId);
        if (!newDrone.canCarryNewItem(order.calculateWeight())) {
            System.out.println(ErrorMessage.NEW_DRONE_DOES_NOT_HAVE_ENOUGH_CAPACITY);
            return false;
        }
        if (currentDrone.getId().equals(newDroneId)) {
            System.out.println(OkMessage.NEW_DRONE_IS_CURRENT_DRONE_NO_CHANGE);
            return true;
        }
        order.setDrone(newDrone);
        newDrone.addOrder(orderId, order);
        currentDrone.removeOrder(orderId);
        store.increaseTransfers();
        System.out.println(OkMessage.CHANGE_COMPLETED);
        return true;
    }
}

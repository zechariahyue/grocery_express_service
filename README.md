# Grocery Express Project

This repository contains the Java service of our group project.

# Build and Run App with Docker
## Run MySQL image
Step 1: Pull MYSQL Image
Pull the MySQL image from Docker Hub using the following command:
```
docker pull mysql
```

Step 2: Create a Docker Network
Create a Docker network to enable communication between the Spring Boot application and the MySQL database using the following command
```
docker network create springboot-mysql-net
```

Step 3: Run MySQL Image in Docker Container
Run the MySQL image in a Docker container in the same network using the following command:
```
docker run --name mysqldb --network springboot-mysql-net -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=CS6310-01-A5 -d mysql
```

Step 4: Access MySQL Database in Container
To access the MySQL database in a container, use the following command:
```
docker exec -it mysqldb bash
```
and login in using root and password root
```
mysql -u root -p
```
## Run Spring Boot App
Open a new terminal window, go to the project folder.
Step 5: Deploy Spring Boot to Docker Container
Follow the below steps to deploy Spring Boot to a Docker container:

Step 5.1: Maven Build the Project
Use the following command to build the project using Maven:
```
mvn clean package
```
Step 5.2: Build the Image
Navigate to the project folder and run the following command to build the Docker image:
```
docker build -t assignment5 .
```

Step 5.3: Run the Docker Image in a Container
Run the Docker image in a Docker container in the same network using the following command:
```
docker run --network springboot-mysql-net --name springboot-mysql-container -p 8080:8080 -it assignment5
```

# Database
The database is MySQL 8.0.32.

# Maven
This project uses [Apache Maven](https://maven.apache.org/) to manage itself.
You will see the dependencies defined in `pom.xml`, and the maven commands called by the backend's Dockerfile

# New commands
## Refueling Stations
1. `place_store`: Places the store to a location.
```
// Place store walmart at location (0,0)
place_store,walmart,0,0
```
2. `make_refueling_station`: Create a refueling station along with the name and location.
```
// Make a refueling station at location (100,0)
make_refueling_station,s1,100,0
```
3. `display_refueling_stations`: Display the information about the refueling stations.
4. `set_drone_max_fuel_capacity`: Set the max fuel capacity of a drone.
```
// Set the drone in store walmart, id 1, max fuel capacity 100
set_drone_max_fuel_capacity,walmart,1,100
```
5. `place_customer`: Place the customer to a location.
```
// Place the customer with account carli2 at location (50,50)
place_customer,carli2,50,50
```
6. `move_drone`: Move a drone to a location.
```
// Move the drone in walmart, id 1 to the location (200,0)
move_drone,walmart,1,200,0
```
7. `fuel_drone`: Fuel a drone to the max fuel level.
```
// Fuel the drone in store walmart, id 1 to the max fuel level
fuel_drone,walmart,1
```
8. `make_refueling_drone`: Make a refueling drone.
```
// Make a refueling drone in walmart, id rf1, max fuel capacity 1000
make_refueling_drone,walmart,rf1,1000
```
9. `display_refueling_drones`: Display refueling drones.
10. `set_fuel_consumption_rate`: Set the fuel consumption rate of a drone.
```
// Set the fuel consumption rate of drone in store walmart, id 1, 1 unit per 1 unit of distance
set_fuel_consumption_rate,walmart,1,1
```
11. `request_refueling_drone`: Request a refueling drone to refuel a drone.
```
// Drone in store walmart, id 1 request a refueling drone with id rf1
request_refueling_drone,walmart,1,rf1
```
12. `deliver_order`: Let drone deliver the order to the customer.
```
// Deliver the order in store walmart with order id ordA to the customer
deliver_order,walmart,ordA
```


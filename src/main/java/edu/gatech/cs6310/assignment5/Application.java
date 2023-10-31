package edu.gatech.cs6310.assignment5;

import edu.gatech.cs6310.assignment5.command.CommandRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        System.out.println("Welcome to the Grocery Express Delivery Service!");
        CommandRunner commandRunner = applicationContext.getBean(CommandRunner.class);
        commandRunner.commandLoop();
    }

}

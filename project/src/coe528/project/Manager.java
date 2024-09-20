package coe528.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Overview: The Manager class represents the bank manager in the application.
 * It is responsible for managing customers by adding and deleting customer files.
 * This class is mutable as the customer files can be created and deleted.
 */
public class Manager {
    private String username;
    private String password;
    
    /**
     * Abstraction Function: AF(c) = A manager with username c.username and password c.password.
     * 
     * Representation Invariant: username != null && !username.isEmpty() && password != null && !password.isEmpty()
     */
    public Manager(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    /**
     * Adds a new customer with the specified username and password by creating a new file.
     * @param username the username of the new customer
     * @param password the password of the new customer
     * @requires username is not already used by an existing customer
     * @modifies the file system by creating a new file for the customer
     * @effects creates a new file with the customer's username, password, and initial balance
     * @throws IOException if an I/O error occurs
     */
    public void addCustomer(String username, String password) throws IOException {
        File customerFile = new File(username + ".txt");
        if (customerFile.exists()) {
            throw new IllegalArgumentException("Customer already exists");
        }
        try (FileWriter writer = new FileWriter(customerFile)) {
            writer.write(username + "\n" + password + "\n" + "100.0\n" + "customer");
        }
    }
    
    /**
     * Deletes the customer with the specified username by removing their file.
     * @param username the username of the customer to delete
     * @requires a customer with the specified username exists
     * @modifies the file system by deleting the customer's file
     * @effects removes the file associated with the specified username
     */
    public void deleteCustomer(String username) {
        File customerFile = new File(username + ".txt");
        if (!customerFile.exists()) {
            throw new IllegalArgumentException("Customer does not exist");
        }
        customerFile.delete();
    }
    
    /**
     * Checks if the representation invariant holds.
     * @effects returns true if the rep invariant holds, false otherwise
     */
    public boolean repOK() {
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }
    
    @Override
    public String toString() {
        return "Manager{" + "username='" + username + '\'' + ", password='" + password + '\'' + '}';
    }
}

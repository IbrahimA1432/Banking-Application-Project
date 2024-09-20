package coe528.project;

/**
 * Overview: The Customer class represents a customer in the bank application. 
 * It is responsible for managing the customer's account balance, level, and performing transactions such as deposits, withdrawals, and online purchases.
 * This class is mutable as the account balance and level can change over time.
 */
public class Customer {
    private double balance;
    private String username;
    private String password;
    private CustomerLevelState levelState;
    
    /**
     * Abstraction Function: AF(c) = A customer with username c.username, password c.password, 
     * balance c.balance, and level c.level, where level is 1 (silver) if balance < 10000, 
     * 2 (gold) if 10000 <= balance < 20000, and 3 (platinum) if balance >= 20000.
     * 
     * Representation Invariant: c.balance >= 0
     */
    
    public Customer(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.levelState = new SilverState(); // Default level
        updateLevel();
    }
    
    // Getters for username and password
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    /**
     * Deposits the specified amount into the customer's account.
     * @param amount the amount to deposit
     * @requires amount > 0
     * @modifies this.balance
     * @effects adds amount to the balance
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        this.balance += amount;
        updateLevel();
    }
    
    /**
     * Withdraws the specified amount from the customer's account.
     * @param amount the amount to withdraw
     * @requires amount > 0 && amount <= balance
     * @modifies this.balance
     * @effects subtracts amount from the balance
     */
    public void withdraw(double amount) {
        if (amount <= 0 || amount > this.balance) {
            throw new IllegalArgumentException("Invalid withdrawal amount.");
        }
        this.balance -= amount;
        updateLevel();
    }
    
    /**
     * Performs an online purchase and deducts the purchase amount and any applicable fees from the customer's account.
     * @param purchaseAmount the amount of the purchase
     * @requires purchaseAmount >= 50
     * @modifies this.balance
     * @effects subtracts purchaseAmount and any applicable fees from the balance
     */
    public void onlinePurchase(double purchaseAmount) {
        if (purchaseAmount < 50) {
            throw new IllegalArgumentException("Purchase amount must be at least $50");
        }
        double fee = 0;
        if (this.getLevel().equals("Silver")) { // Silver
            fee = 20;
        } else if (this.getLevel().equals("Gold")) { // Gold
            fee = 10;
        }
        // No fee for Platinum level
        if (this.balance < purchaseAmount + fee) {
            throw new IllegalArgumentException("Insufficient balance for this transaction");
        }
        this.balance -= (purchaseAmount + fee);
        updateLevel();
    }
    
    /**
     * Returns the balance of the customer's account.
     * @effects returns the balance
     */
    public double getBalance() {
        return this.balance;
    }
    
    /**
     * Returns the level of the customer's account.
     * @effects returns the level
     */
    public String getLevel() {
        return levelState.getClass().getSimpleName().replace("State", "");
    }
    
    /**
     * Updates the level of the customer based on the current balance.
     * @modifies this.level
     * @effects sets the level based on the balance
     */
    private void updateLevel() {
        levelState.updateLevel(this);
    }
    
    /**
     * Sets the current level state of the customer
     * @param levelState the new level state
     */
    public void setLevelState(CustomerLevelState levelState){
        this.levelState = levelState;
    }
    
    /**
     * Overview: The CustomerLevelState interface represents the state of a customer's account level.
     * It is responsible for defining the behavior for updating a customer's level.
     * Implementations of this interface are mutable as they can change the level state of a customer.
     */
    public interface CustomerLevelState{
        /**
         * Updates the level of the customer based on the current balance.
         * @param customer the customer whose level is to be updated
         * @modifies customer.levelState
         * @effects sets the level state of the customer based on the balance
         */
        void updateLevel(Customer customer);
    }
    
    /**
     * Overview: The SilverState class represents a customer at the silver level.
     * It is responsible for updating the customer's level to gold if the balance is high enough.
     * This class is mutable as it can change the level state of a customer.
     */
    public class SilverState implements CustomerLevelState {
        @Override
        public void updateLevel(Customer customer) {
            if (customer.getBalance() >= 10000) {
                customer.setLevelState(new GoldState());
            }
        }
    }
    
    /**
     * Overview: The GoldState class represents a customer at the gold level.
     * It is responsible for updating the customer's level to platinum if the balance is high enough,
     * or downgrading to silver if the balance falls below the gold level threshold.
     * This class is mutable as it can change the level state of a customer.
     */
    public class GoldState implements CustomerLevelState {
        @Override
        public void updateLevel(Customer customer) {
            if (customer.getBalance() >= 20000) {
                customer.setLevelState(new PlatinumState());
            } else if (customer.getBalance() < 10000) {
                customer.setLevelState(new SilverState());
            }
        }
    }
    
    /**
     * Overview: The PlatinumState class represents a customer at the platinum level.
     * It is responsible for downgrading the customer's level to gold if the balance falls below the platinum level threshold.
     * This class is mutable as it can change the level state of a customer.
     */
    public class PlatinumState implements CustomerLevelState {
        @Override
        public void updateLevel(Customer customer) {
            if (customer.getBalance() < 20000) {
                customer.setLevelState(new GoldState());
            }
        }
    }
    
    /**
     * Checks if the representation invariant holds.
     * @effects returns true if the rep invariant holds, false otherwise
     */
    public boolean repOK() {
        return (balance >= 0);
    }
    
    @Override
    public String toString() {
        String levelString = getLevel(); // Convert level to string
        return "Customer: [username='" + username + "', password='" + password + "', balance=" + balance + ", level='" + levelString + "']";
    }
}


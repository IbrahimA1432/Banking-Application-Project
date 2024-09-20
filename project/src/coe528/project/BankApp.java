package coe528.project;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Overview: The BankApp class is the main application class for the bank system.
 * It is responsible for launching the application and managing the user interface.
 * This class is mutable as it changes scenes based on user interaction.
 *
 * Abstraction Function: AF(c) = A banking application with a primary stage c.primaryStage
 * and a manager c.manager.
 *
 * Representation Invariant: c.primaryStage != null && c.manager != null
 */
public class BankApp extends Application {

    private Stage primaryStage;
    private Manager manager;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Bank Application");
        primaryStage.setScene(createInitialScene());
        primaryStage.show();
        
        //Manager initialiation
        manager = new Manager("admin", "admin");
    }

    private Scene createInitialScene() {
        // Create initial scene with login options
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);
        
        Label label = new Label("Login");
        vbox.getChildren().add(label);

        Button customerBtn = new Button("Customer");
        customerBtn.setOnAction(e -> primaryStage.setScene(createLoginScene("Customer")));

        Button managerBtn = new Button("Manager");
        managerBtn.setOnAction(e -> primaryStage.setScene(createLoginScene("Manager")));

        vbox.getChildren().addAll(customerBtn, managerBtn);

        return new Scene(vbox, 300, 200);
    }

    private Scene createLoginScene(String role) {
        // Create login scene for customer or manager
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label roleLabel = new Label(role + " Login");
        GridPane.setConstraints(roleLabel, 0, 0);
        grid.getChildren().add(roleLabel);

        TextField userTextField = new TextField();
        userTextField.setPromptText("Username");
        GridPane.setConstraints(userTextField, 0, 1);
        grid.getChildren().add(userTextField);

        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Password");
        GridPane.setConstraints(pwBox, 0, 2);
        grid.getChildren().add(pwBox);
    
        Button loginBtn = new Button("Login");
        GridPane.setConstraints(loginBtn, 1, 1);
        grid.getChildren().add(loginBtn);
        
        Button backBtn = new Button("Back");
        GridPane.setConstraints(backBtn, 1, 2);
        grid.getChildren().add(backBtn);

        backBtn.setOnAction(e -> primaryStage.setScene(createInitialScene()));

        if (role.equals("Customer")) {
            loginBtn.setOnAction(e -> handleLogin(userTextField.getText(), pwBox.getText(), role));
        }else if(role.equals("Manager")){
            loginBtn.setOnAction(f -> handleLogin(userTextField.getText(), pwBox.getText(), role));
        }
        
        return new Scene(grid, 300, 200);
    }


    private void handleLogin(String username, String password, String role) {
        // Handle login logic for customer or manager
        try{
            if(role.equals("Manager")){
                if(manager.getUsername().equals(username) && manager.getPassword().equals(password)){
                    primaryStage.setScene(createManagerScene());
                }else{
                    showAlert("Login Error", "Invalid username or password.");
                }
            }else if(role.equals("Customer")){
                File customerFile = new File(username + ".txt");
                if(customerFile.exists()){
                    List<String> lines = Files.readAllLines(customerFile.toPath());
                    if(lines.size() == 4 && lines.get(3).equals("customer") && lines.get(1).equals(password)){
                        Customer customer = new Customer(lines.get(0), lines.get(1), Double.parseDouble(lines.get(2)));
                        primaryStage.setScene(createCustomerScene(customer));
                    }else{
                        showAlert("Login Error", "Invalid username or password.");
                    }
                }else{
                    showAlert("Login Error", "Customer does not exist.");
                }
            }
        }catch(IOException e){
            e.printStackTrace();
            showAlert("Error", "An error occured while accessing customer data.");
        }
    }

    private void showAlert(String title, String content) {
        // Show alert with given title and content
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void updateCustomerFile(Customer customer) throws IOException{
        // Update customer file with new balance
        File customerFile = new File(customer.getUsername() + ".txt");
        try(FileWriter writer = new FileWriter(customerFile, false)){
            writer.write(customer.getUsername() + "\n" + customer.getPassword() + "\n" + customer.getBalance() + "\n" + "customer");
        }
    }
    
    private Scene createCustomerScene(Customer customer) {
        // Create the sene for customer interactions
    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10, 10, 10, 10));
    grid.setVgap(5);
    grid.setHgap(5);

    Label balanceLabel = new Label("Balance: $" + customer.getBalance());
    GridPane.setConstraints(balanceLabel, 0, 0);
    grid.getChildren().add(balanceLabel);
    
    // Get Balance
    Button getBalanceBtn = new Button("Get Balance");
    GridPane.setConstraints(getBalanceBtn, 1, 0);
    grid.getChildren().add(getBalanceBtn);
    getBalanceBtn.setOnAction(e -> {
        showAlert("Balance", "Current Balance: $"+customer.getBalance());
        primaryStage.setScene(createCustomerScene(customer));
    });

    Label levelLabel = new Label("Level: " + customer.getLevel());
    GridPane.setConstraints(levelLabel, 0, 1);
    grid.getChildren().add(levelLabel);
    
    String[] transaction = {"None"};
    Label transactionLabel = new Label("Latest Transaction: " + transaction[0]);
    GridPane.setConstraints(transactionLabel, 0, 2, 2, 1);
    grid.getChildren().add(transactionLabel);
    
    
    TextField depositAmountField = new TextField();
    depositAmountField.setPromptText("Deposit Amount");
    GridPane.setConstraints(depositAmountField, 1, 3);
    grid.getChildren().add(depositAmountField);

    Button depositBtn = new Button("Deposit");
    GridPane.setConstraints(depositBtn, 0, 3);
    grid.getChildren().add(depositBtn);

    // Deposit
    depositBtn.setOnAction(e -> {
        try {
            double amount = Double.parseDouble(depositAmountField.getText());
            customer.deposit(amount);
            balanceLabel.setText("Balance: $" + String.format("%.2f", customer.getBalance()));
            levelLabel.setText("Level: " + customer.getLevel());
            transaction[0] = String.format("Deposited $%.2f", amount);
            transactionLabel.setText("Latest Transaction: " + transaction[0]);
            depositAmountField.clear();
        } catch (NumberFormatException ex) {// wrong format
            showAlert("Error", "Invalid amount.");
        } catch (IllegalArgumentException ex){// 0 or less
            showAlert("Error", ex.getMessage());
        }
        
        try{
            updateCustomerFile(customer);
        }catch(IOException ex){
            showAlert("Error", "Failed to upload customer data");
        }
    });
    
    TextField withdrawAmountField = new TextField();
    withdrawAmountField.setPromptText("Withdraw Amount");
    GridPane.setConstraints(withdrawAmountField, 1, 4);
    grid.getChildren().add(withdrawAmountField);

    Button withdrawBtn = new Button("Withdraw");
    GridPane.setConstraints(withdrawBtn, 0, 4);
    grid.getChildren().add(withdrawBtn);

    //Withdrawal
    withdrawBtn.setOnAction(e -> {
        try {
            double amount = Double.parseDouble(withdrawAmountField.getText());
            customer.withdraw(amount);
                balanceLabel.setText("Balance: $" + String.format("%.2f", customer.getBalance()));
            levelLabel.setText("Level: " + customer.getLevel());
            transaction[0] = String.format("Withdrew $%.2f", amount);
            transactionLabel.setText("Latest Transaction: " + transaction[0]);
            withdrawAmountField.clear();
        } catch (NumberFormatException ex) {
            showAlert("Error", "Invalid amount.");
        } catch (IllegalArgumentException ex) {
            showAlert("Error", ex.getMessage());
        }
        
        try{
            updateCustomerFile(customer);
        }catch(IOException ex){
            showAlert("Error", "Failed to upload customer data");
        }
    });
    
    TextField purchaseAmountField = new TextField();
    purchaseAmountField.setPromptText("Purchase Amount");
    GridPane.setConstraints(purchaseAmountField, 1, 5);
    grid.getChildren().add(purchaseAmountField);
    
    Button purchaseBtn = new Button("Make Purchase");
    GridPane.setConstraints(purchaseBtn, 0, 5);
    grid.getChildren().add(purchaseBtn);

    // Online Purchases
    purchaseBtn.setOnAction(e -> {
        try {
            double purchaseAmount = Double.parseDouble(purchaseAmountField.getText());
            if (customer.getBalance() >= purchaseAmount) {
                customer.onlinePurchase(purchaseAmount);
                balanceLabel.setText("Balance: $" + String.format("%.2f", customer.getBalance()));
                levelLabel.setText("Level: " + customer.getLevel());
                transaction[0] = String.format("Purchased item for $%.2f", purchaseAmount);
                transactionLabel.setText("Latest Transaction: " + transaction[0]);
            } else {
                showAlert("Error", "Insufficient balance for this purchase.");
            }
            
            purchaseAmountField.clear();
        } catch (NumberFormatException ex) {
            showAlert("Error", "Invalid amount.");
        } catch (IllegalArgumentException ex){
            showAlert("Error", ex.getMessage());
        }
        
        try{
            updateCustomerFile(customer);
        }catch(IOException ex){
            showAlert("Error", "Failed to upload customer data");
        }
    });

    Button backBtn = new Button("Logout");
    GridPane.setConstraints(backBtn, 0, 6);
    grid.getChildren().add(backBtn);

    backBtn.setOnAction(e -> {
        try{
            updateCustomerFile(customer);
            primaryStage.setScene(createLoginScene("Customer"));
        }catch(IOException ex){
            showAlert("Error", "Failed to update customer data.");
        }
    });

    return new Scene(grid, 300, 250);
    }




    private Scene createManagerScene() {
        // Create scene for manager interactions
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Label label = new Label("Manager View");
        vbox.getChildren().add(label);

        Button addCustomerBtn = new Button("Add Customer");
        addCustomerBtn.setOnAction(e -> primaryStage.setScene(createAddCustomerScene()));
        vbox.getChildren().add(addCustomerBtn);

        Button deleteCustomerBtn = new Button("Delete Customer");
        deleteCustomerBtn.setOnAction(e -> primaryStage.setScene(createDeleteCustomerScene()));
        vbox.getChildren().add(deleteCustomerBtn);

        Button backBtn = new Button("Logout");
        backBtn.setOnAction(e -> primaryStage.setScene(createInitialScene()));
        vbox.getChildren().add(backBtn);

        return new Scene(vbox, 300, 200);
    }


    
    private Scene createAddCustomerScene() {
        // Create scene for adding a new customer
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);
        grid.getChildren().add(usernameLabel);

        TextField usernameField = new TextField();
        GridPane.setConstraints(usernameField, 1, 0);
        grid.getChildren().add(usernameField);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        grid.getChildren().add(passwordLabel);

        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 1, 1);
        grid.getChildren().add(passwordField);

        Button addButton = new Button("Add Customer");
        GridPane.setConstraints(addButton, 1, 2);
        grid.getChildren().add(addButton);

        addButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Username and password cannot be empty.");
                return;
            }

            try{
                manager.addCustomer(username, password);
                primaryStage.setScene(createManagerScene());
            }catch(IOException ex){
                showAlert("Error", "Failed to add customer.");
            }catch(IllegalArgumentException ex){
                showAlert("Error", ex.getMessage());
            }
            primaryStage.setScene(createManagerScene()); // Go back to the manager scene after adding the customer
        });

        Button backButton = new Button("Back");
        GridPane.setConstraints(backButton, 1, 3);
        grid.getChildren().add(backButton);

        backButton.setOnAction(e -> primaryStage.setScene(createManagerScene()));

        return new Scene(grid, 300, 200);
}

    
private Scene createDeleteCustomerScene() {
    // Create scene for deleting an existing customer
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(8);

    Label label = new Label("Select a Customer to Delete:");
    vbox.getChildren().add(label);

    File[] files = new File(".").listFiles((dir,name) -> name.endsWith(".txt"));
    boolean customersExist = false;
    for(int i=0; i<files.length; i++){
        try{
            List<String> lines = Files.readAllLines(files[i].toPath());
            if(lines.size() == 4 && lines.get(3).equals("customer")){
                customersExist = true;
                String username = lines.get(0);
                Button deleteButton = new Button("Delete "+username);
                deleteButton.setOnAction(e -> {
                    manager.deleteCustomer(username);
                    primaryStage.setScene(createDeleteCustomerScene());
                });
                vbox.getChildren().add(deleteButton);
            }
        }catch(IOException e){
            showAlert("Error", "Failed to load customer data.");
        }
    }
    
    if(!customersExist){
        Label noCustomersLabel = new Label("No existing customers");
        vbox.getChildren().add(noCustomersLabel);
    }

    Button backButton = new Button("Back");
    backButton.setOnAction(e -> primaryStage.setScene(createManagerScene()));
    vbox.getChildren().add(backButton);

    return new Scene(vbox, 300, 200);
}

    /**
     * Checks if the representation invariant holds.
     * @effects returns true if the rep invariant holds, false otherwise
     */
     public boolean repOK(){
         return primaryStage != null && manager != null;
     }

     @Override
    public String toString() {
        // Implement the abstraction function
        return "BankApp{" +
                "primaryStage=" + primaryStage +
                ", manager=" + manager +
                '}';
    }

    public static void main(String[] args) {
        launch(args);
    }
}

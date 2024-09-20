## Project Overview
This project is a **Bank Application** that allows users to perform typical banking operations such as depositing, withdrawing, and managing transactions. The application offers two main user roles:

1. **Customer**: Customers can perform transactions, which affect their account balance and membership level. There are three membership levels: Silver, Gold, and Platinum. These levels are determined based on the customer's balance and affect the discounts that customers can receive on purchases made within the app.
2. **Manager**: Managers can add or remove customers, manage customer accounts, and oversee the general operation of the bank. The manager role provides full control over user data and the application itself.

## Features
### Customer Functionality
- **Deposit & Withdrawal**: Customers can add or withdraw money from their bank accounts.
- **Dynamic Membership Levels**:
  - **Silver**: Base level for balances below $10,000.
  - **Gold**: Granted to customers with a balance of $10,000 or more, but below $20,000.
  - **Platinum**: Top-tier level for customers with a balance of $20,000 or more.
  - Each level provides increasing benefits, such as discounts on purchases made through the app.
- **Discounts on Purchases**: Depending on the membership level, users receive various discounts when making purchases.

### Manager Functinoality
- **Customer Management**: Managers have the ability to add or remove customers from the system.
- **Account Monitoring**: Managers can review and update customer data.
- **Data Persistence**: Customer information is stored in .txt files for persistence and can be modified or deleted as needed.

## Architectrue Overview
The application follows an **MVC (Model-View-Controller)** pattern:
- **Model**: Defines the logic behind customer states (Silver, Gold, Platinum) and transaction management.
- **View**: Provides the interface for both customers and managers to interact with the application.
- **Controller**: Handles the logic for processing user inputs, managing transactions, and ensuring account state consistency.

### Code Structure
- **BankApp.java**: Serves as the entry point for the application and manages the main UI logic for customer and manager interactions.
- **Customer.java**: Defines the customer class, managing account balance, membership levels, and transaction functionalities.
- **Manager.java**: Defines the manager class, which is responsible for customer management and system oversight.

### Dynamic Membership Level Management
- The application implements a **State Design Pattern** for customer membership levels. The levels (Silver, Gold, and Platinum) are represented as separate states, and transitions occur based on the customer's account balance:
  - If a customer's balance crosses a threshold (e.g., $10,000 or $20,000), their membership level is automatically updated.
  - The customer's discounts on purchases are dynamically adjusted according to their level.

## Running the Application
To run this project:
1. Clone the repository.
2. Compile and run the BankApp.java file.
3. You can log in as either a customer or a manager to explore different functionalities.

## Future Enhancements
- **Transaction History**: Adding a feature to track the history of transactions for both customers and managers.
- **Enhanced Security**: Implementing encryption for customer data and secure authentication for logins.
- **Mobile Integration**: Expanding the application to mobile platforms for better accessibility.

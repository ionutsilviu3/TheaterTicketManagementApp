package com.ggc.theaterkarten;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UIManager {
    private TheaterTicketManager theaterTicketManager;
    private UserManager userManager;
    private Customer customer;
    private Employee employee;

    private BorderPane mainPane;

    private TextField logInUsernameField;
    private PasswordField logInPasswordField;
    private Button logInButton = new Button("Log in");

    private Label creditLabel;
    private TextField creditField;
    private Button topUpButton;
    private TableView ticketsTableView;
    private String userType = "";
    private Button customerButton;
    private Button employeeButton;

    Stage primaryStage;
    private Scene logInScene;
    private Scene customerScene;
    private Scene employeeScene;

    public UIManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.theaterTicketManager = new TheaterTicketManager();
        this.userManager = new UserManager();
    }

    private TableView createTicketsTable() {
        ticketsTableView = new TableView();
        ticketsTableView.setEditable(true);
        TableColumn<TheaterTicket, String> ticketColumn = new TableColumn<>("Play name");
        TableColumn<TheaterTicket, Integer> seatNumberColumn = new TableColumn<>("Seat Number");
        TableColumn<TheaterTicket, Double> priceColumn = new TableColumn<>("Price");
        TableColumn<TheaterTicket, Date> dateColumn = new TableColumn<>("Date");
        TableColumn<TheaterTicket, Time> timeColumn = new TableColumn<>("Time");

        ticketColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPlayName()));
        seatNumberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getSeatNumber()));
        priceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getPrice()));
        dateColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getDate().toString()));
        timeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getTime().toString()));

        ticketsTableView.getColumns().addAll(ticketColumn, seatNumberColumn, dateColumn, timeColumn, priceColumn);
        ticketsTableView.setItems(FXCollections.observableArrayList(
                theaterTicketManager.getNotSoldTheaterTickets()
        ));
        ticketsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return ticketsTableView;
    }

    public HBox createTopUpLayout() {
        // Create field for top-up amount
        TextField topUpField = new TextField();
        topUpField.setPrefSize(205, 25);
        topUpField.setPromptText("How much do you want to top up ?");

        // Create top-up button
        Button topUpButton = new Button("Top Up");
        topUpButton.setOnAction(event -> {
            String topUpAmountString = topUpField.getText();
            try {
                double topUpAmount = Double.parseDouble(topUpAmountString);
                if (topUpAmount > 10000)
                    throw new Exception();
                customer.topUpCredit(customer.getCredit() + topUpAmount);
                creditLabel.textProperty().bind(new ReadOnlyStringWrapper(new String("Credit: " + customer.getCredit())));
                showNotification("Bank notification", "Transaction: -" + topUpAmount + " bucks.");
                topUpField.clear();
            } catch (NumberFormatException e) {
                // Show an alert for invalid input
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Invalid Top Up value");
                alert.setContentText("Please enter a valid Top Up value.");
                topUpField.clear();
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Top Up value is too high");
                alert.setContentText("Please enter a Top Up value that is lower than 10000, your bank doesn't approve such big transactions.");
                topUpField.clear();
                alert.showAndWait();
            }
        });

        // Create container for top-up field and button
        HBox topUpContainer = new HBox(10);
        topUpContainer.setAlignment(Pos.CENTER);
        topUpContainer.setPadding(new Insets(10));
        topUpContainer.getChildren().addAll(topUpField, topUpButton);

        return topUpContainer;
    }

    private HBox createBuyLayout() {
        // Create buy button
        Button buyButton = new Button("Buy Ticket");
        buyButton.setOnAction(event -> {
            List<TheaterTicket> selectedTickets = ticketsTableView.getSelectionModel().getSelectedItems();
            double totalCost = calculateTotalCost(selectedTickets);

            if (totalCost > customer.getCredit()) {
                showNotification("Insufficient Credit", "Your credit balance is short " + (totalCost - customer.getCredit()) + " bucks, top up some credit and try again.");
            } else {
                for (TheaterTicket selectedTicket : selectedTickets) {
                    if (selectedTicket != null) {
                        selectedTicket.setSold("sold");
                        try {
                            theaterTicketManager.updateTicket(selectedTicket, customer.getName());
                        } catch (Exception e) {
                            showNotification("Error while buying ticket", "There was an error when you tried to buy the ticket.");
                        }
                    }
                }
                customer.topUpCredit(customer.getCredit() - totalCost);
                creditLabel.textProperty().bind(new SimpleStringProperty("Credit: " + customer.getCredit()));
                ticketsTableView.getItems().removeAll(selectedTickets);
            }
        });

        // Create bottom container for field and buy button
        HBox bottomContainer = new HBox(10);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setPadding(new Insets(10));
        bottomContainer.getChildren().addAll(buyButton);

        return bottomContainer;
    }

    private Scene createCustomerScene() {
        TableView ticketsTableView = createTicketsTable();

        HBox topUpContainer = createTopUpLayout();

        HBox topContainer = createTopLayout();

        HBox bottomContainer = createBuyLayout();

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(topContainer, ticketsTableView, topUpContainer, bottomContainer);
        customerScene = new Scene(root, 800, 600);
        return customerScene;

    }

    private VBox createTicketsTableEmployee() {
        ticketsTableView = new TableView();
        ticketsTableView.setEditable(true);
        TableColumn<TheaterTicket, String> ticketColumn = new TableColumn<>("Play name");
        TableColumn<TheaterTicket, Integer> seatNumberColumn = new TableColumn<>("Seat Number");
        TableColumn<TheaterTicket, String> customerColumn = new TableColumn<>("Customer name");
        TableColumn<TheaterTicket, Double> priceColumn = new TableColumn<>("Price");
        TableColumn<TheaterTicket, String> isSoldColumn = new TableColumn<>("Is sold");
        TableColumn<TheaterTicket, Date> dateColumn = new TableColumn<>("Date");
        TableColumn<TheaterTicket, Time> timeColumn = new TableColumn<>("Time");

        ticketColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPlayName()));
        seatNumberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getSeatNumber()));
        customerColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomerName()));
        priceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getPrice()));
        isSoldColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().isSold()));
        dateColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getDate().toString()));
        timeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getTime().toString()));

        TextField searchField = new TextField();
        FilteredList<TheaterTicket> filteredTickets = new FilteredList<>(FXCollections.observableArrayList(theaterTicketManager.getTheaterTickets()));
        ticketsTableView.getColumns().addAll(ticketColumn, seatNumberColumn, customerColumn, isSoldColumn, dateColumn, timeColumn, priceColumn);

        ticketsTableView.setItems(filteredTickets);

        searchField.setOnKeyReleased(event -> {
            String searchTerm = searchField.getText();
            filteredTickets.setPredicate(ticket -> ticket.getPlayName().contains(searchTerm));
        });
        searchField.setPromptText("Search here tickets by play name");
        VBox viewTicketsVBox = new VBox(10);
        viewTicketsVBox.setPadding(new Insets(10));
        viewTicketsVBox.getChildren().addAll(searchField, ticketsTableView);


        ticketsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return viewTicketsVBox;
    }

    private TableView createViewCustomersLayout() {
        TableView customersTableView = new TableView();
        customersTableView.setEditable(true);
        TableColumn<Customer, String> customerNameColumn = new TableColumn<>("Play name");
        TableColumn<Customer, Double> creditColumn = new TableColumn<>("Seat Number");


        customerNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
        creditColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getCredit()));

        customersTableView.getColumns().addAll(customerNameColumn, creditColumn);
        customersTableView.setItems(FXCollections.observableArrayList(
                userManager.getAllCustomers()
        ));
        customersTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return customersTableView;
    }

    private Scene createEmployeeScene() {
        TabPane tabPane = new TabPane();
        Tab viewCustomersTab = new Tab("View Customers");
        viewCustomersTab.setContent(createViewCustomersLayout());
        Tab viewTicketsTab = new Tab("View Tickets");
        viewTicketsTab.setContent(createTicketsTableEmployee());
        Tab addTicketsTab = new Tab("Add Tickets");
        addTicketsTab.setContent(createTicketAdder());
        Tab deleteTicketsTab = new Tab("Delete Tickets");
        deleteTicketsTab.setContent(createTicketDeleter());
        tabPane.getTabs().addAll(viewCustomersTab, viewTicketsTab, addTicketsTab, deleteTicketsTab);

        // Create a layout for the employee interface
        VBox employeeLayout = new VBox(10);
        employeeLayout.setAlignment(Pos.CENTER);
        employeeLayout.getChildren().addAll(tabPane);
        employeeScene = new Scene(tabPane, 800, 600);
        return employeeScene;
    }

    private VBox createTicketAdder() {
        Label playNameLabel = new Label("Play Name:");
        TextField playNameField = new TextField();
        Label priceLabel = new Label("Price:");
        TextField priceField = new TextField();
        Label seatNumberLabel = new Label("Seat Number:");
        TextField seatNumberField = new TextField();
        Label dateDayLabel = new Label("Day:");
        TextField dateDayField = new TextField();
        Label dateMonthLabel = new Label("Month:");
        TextField dateMonthField = new TextField();
        Label dateYearLabel = new Label("Year:");
        TextField dateYearField = new TextField();

        Label timeHourLabel = new Label("Hour:");
        TextField timeHourField = new TextField();
        Label timeMinuteLabel = new Label("Minute:");
        TextField timeMinuteField = new TextField();
        Button createButton = new Button("Create");
        createButton.setOnAction(event -> {
            try {
                theaterTicketManager.createTheaterTicket(playNameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(seatNumberField.getText()),
                        new Date(Integer.parseInt(dateDayField.getText()), Integer.parseInt(dateMonthField.getText()), Integer.parseInt(dateYearField.getText())),
                        new Time(Integer.parseInt(timeHourField.getText()), Integer.parseInt(timeMinuteField.getText())));
                showNotification("Ticket created", "Ticket was created succesfully!");
                playNameField.clear();
                priceField.clear();
                seatNumberField.clear();
                dateDayField.clear();
                dateMonthField.clear();
                dateYearField.clear();
                timeHourField.clear();
                timeMinuteField.clear();
            }
            catch (Exception e)
            {
                showNotification("Wrong inputs","Something is wrong here! Please re-enter the inputs.");

            }
        });

        VBox addTicketsVBox = new VBox(10);
        addTicketsVBox.setPadding(new Insets(10));
        addTicketsVBox.getChildren().addAll(
                playNameLabel, playNameField,
                priceLabel, priceField,
                seatNumberLabel, seatNumberField,
                dateDayLabel, dateDayField,
                dateMonthLabel, dateMonthField,
                dateYearLabel, dateYearField,
                timeHourLabel, timeHourField,
                timeMinuteLabel, timeMinuteField,
                createButton
        );
        return addTicketsVBox;
    }

    private VBox createTicketDeleter() {
        Label deletePlayNameLabel = new Label("Play Name:");
        TextField deletePlayNameField = new TextField();
        Label deleteSeatNumberLabel = new Label("Seat Number:");
        TextField deleteSeatNumberField = new TextField();
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            try {
                theaterTicketManager.deleteTheaterTicket(deletePlayNameField.getText(), Integer.parseInt(deleteSeatNumberField.getText()));
                showNotification("Ticket deleted", "The ticket has been deleted.");
                deletePlayNameField.clear();
                deleteSeatNumberField.clear();
            } catch (Exception e) {
                showNotification("Wrong inputs!", "Please enter correct inputs.");
                deletePlayNameField.clear();
                deleteSeatNumberField.clear();
            }
        });
        VBox deleteTicketsVBox = new VBox(10);
        deleteTicketsVBox.setPadding(new Insets(10));
        deleteTicketsVBox.getChildren().addAll(
                deletePlayNameLabel, deletePlayNameField,
                deleteSeatNumberLabel, deleteSeatNumberField,
                deleteButton
        );

        return deleteTicketsVBox;
    }

    public void manageScenes() {
        primaryStage.setScene(createIntro());
        employeeButton.setOnAction(event -> primaryStage.setScene(createLogIn("Employee")));
        customerButton.setOnAction(event -> primaryStage.setScene(createLogIn("Customer")));
        logInButton.setOnAction(event -> checkUserCredentials(userType));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Theater Ticket Management");
        primaryStage.show();
    }

    private void checkUserCredentials(String userType) {
        boolean succesful = false;
        if(logInUsernameField.getText().equals("") || logInPasswordField.getText().equals(""))
            showNotification("Please enter credentials.", "You have forgotten to enter something ;)");
        else {
            if (userType.equalsIgnoreCase("Customer")) {
                if (userManager.getAllCustomerNames().contains(logInUsernameField.getText())) {
                    for (Customer c : userManager.getAllCustomers()) {
                        if (c.getName().equalsIgnoreCase(logInUsernameField.getText()) && c.getPassword().equals(logInPasswordField.getText())) {
                            customer = c;
                            succesful = true;
                            primaryStage.setScene(createCustomerScene());
                        }
                    }
                    if(succesful == false)
                    showIncorrectCredentialsAlert();


                } else
                    showIncorrectUserAlert();
            } else if (userType.equalsIgnoreCase("Employee")) {
                if (userManager.getAllEmployeeNames().contains(logInUsernameField.getText())) {
                    for (Employee e : userManager.getAllEmployees()) {
                        if (e.getName().equalsIgnoreCase(logInUsernameField.getText()) && e.getPassword().equals(logInPasswordField.getText())) {
                            employee = e;
                            succesful = true;
                            primaryStage.setScene(createEmployeeScene());
                        }
                    }
                    if (succesful == false)
                        showIncorrectCredentialsAlert();
                } else
                    showIncorrectUserAlert();
            }
        }
    }
    private void showIncorrectCredentialsAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText("Incorrect username or password. Try again!");
        alert.showAndWait();
    }
    private void showIncorrectUserAlert() {
        // Create an alert with error message
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText("Username not found. Do you want to create a new account with these credentials or try again ?");

        // Add custom buttons
        ButtonType createAccountButton = new ButtonType("Create New Account");
        ButtonType tryAgainButton = new ButtonType("Try Again");
        alert.getButtonTypes().setAll(createAccountButton, tryAgainButton);

        // Handle button actions
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == createAccountButton) {
            // Create account button was clicked, implement account creation logic here
            if(userType.equalsIgnoreCase("Customer")) {
                customer = new Customer(logInUsernameField.getText(), logInPasswordField.getText(), 0.0);
                userManager.createCustomer(customer);
                primaryStage.setScene(createCustomerScene());
            } else if (userType.equalsIgnoreCase("Employee")) {
                employee = new Employee(logInUsernameField.getText(), logInPasswordField.getText());
                userManager.createEmployee(employee);
                primaryStage.setScene(createEmployeeScene());
            }
        } else {
            logInUsernameField.clear();
            logInPasswordField.clear();
        }
    }

    private Scene createIntro()
    {
        // Create buttons
        customerButton = createButton("Customer", "lightblue");
        employeeButton = createButton("Employee", "teal");

        // Create title label
        Label titleLabel = createLabel("Theater Tickets Management App", FontWeight.BOLD, 20);

        // Create description label
        Label descriptionLabel = createLabel("What are you ?", FontWeight.NORMAL, 14);

        // Create left and right containers for buttons
        VBox leftContainer = new VBox(20);
        leftContainer.setAlignment(Pos.CENTER);
        leftContainer.getChildren().add(customerButton);
        VBox.setVgrow(customerButton, Priority.ALWAYS);

        VBox rightContainer = new VBox(20);
        rightContainer.setAlignment(Pos.CENTER);
        rightContainer.getChildren().add(employeeButton);
        VBox.setVgrow(employeeButton, Priority.ALWAYS);

        // Create button container
        HBox buttonContainer = new HBox(2);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(leftContainer, rightContainer);
        HBox.setHgrow(leftContainer, Priority.ALWAYS);
        HBox.setHgrow(rightContainer, Priority.ALWAYS);

        // Create main layout
        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: dimgrey;");
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, descriptionLabel, buttonContainer);


        customerScene = new Scene(layout, 800, 600);
        return customerScene;
    }
    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(100);
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-pref-width: 800; -fx-pref-height: 700;");

        // Set button color on hover
        button.setOnMouseEntered(event -> button.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-background-color: " + color + "; -fx-pref-width: 800; -fx-pref-height: 700;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: grey;-fx-pref-width: 800; -fx-pref-height: 700;"));

        return button;
    }

    private Label createLabel(String text, FontWeight weight, int fontSize) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", weight, fontSize));

        return label;
    }

    private HBox createTopLayout() {
        // Create label for credit
        creditLabel = new Label();
        creditLabel.textProperty().bind(new ReadOnlyStringWrapper(new String("Credit: " + customer.getCredit())));

        // Create top container for back arrow and label
        HBox topContainer = new HBox(550);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(10));

        topContainer.getChildren().addAll(creditLabel);
        return topContainer;
    }

    private double calculateTotalCost(List<TheaterTicket> selectedTickets) {
        double totalCost = 0;
        for (TheaterTicket selectedTicket : selectedTickets) {
            totalCost += selectedTicket.getPrice();
        }
        return totalCost;
    }

    private Scene createLogIn(String userType) {
        this.userType = userType;
        GridPane logInLayout = new GridPane();
        logInLayout.setStyle("-fx-background-color: dimgrey;");
        logInLayout.setAlignment(Pos.CENTER);
        logInLayout.setHgap(10);
        logInLayout.setVgap(10);
        logInLayout.setPadding(new Insets(25, 25, 25, 25));
        logInUsernameField = new TextField();
        logInPasswordField = new PasswordField();
        Label logInUsernameLabel = new Label("Username");
        Label logInPasswordLabel = new Label("Password");
        logInLayout.add(logInUsernameLabel, 0, 0);
        logInLayout.add(logInUsernameField, 1, 0);
        logInLayout.add(logInPasswordLabel, 0, 1);
        logInLayout.add(logInPasswordField, 1, 1);
        logInLayout.add(logInButton, 1, 2);
        logInScene = new Scene(logInLayout, 800, 600);
        return logInScene;
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

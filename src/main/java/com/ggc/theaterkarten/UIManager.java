package com.ggc.theaterkarten;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class UIManager {
    private TheaterTicketManager theaterTicketManager;
    private UserManager userManager;
    private Customer customer;
    private AuthenticationManager authManager;

    private BorderPane mainPane;

    private TextField logInUsernameField;
    private PasswordField logInPasswordField;
    private Button logInButton;

    private Label creditLabel;
    private TextField creditField;
    private Button topUpButton;
    private TableView ticketsTableView;

    public UIManager() {
        this.customer = new Customer("user", "1234", 100);
        this.theaterTicketManager = new TheaterTicketManager();
        this.userManager = new UserManager();
        authManager = new AuthenticationManager();
    }
    private TableView createTicketsTable()
    {
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
                theaterTicketManager.getTheaterTickets()
        ));
        ticketsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return ticketsTableView;
    }

    public HBox createTopUpLayout()
    {
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
    
    private HBox createBuyLayout()
    {
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
                        selectedTicket.setSold(true);
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
        
        return  bottomContainer;
    }

    private VBox createCustomerLayout()
    {
        TableView ticketsTableView = createTicketsTable();

        HBox topUpContainer = createTopUpLayout();

        HBox topContainer = createTopLayout();

        HBox bottomContainer = createBuyLayout();

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(topContainer, ticketsTableView, topUpContainer, bottomContainer);
        return root;

    }

    private TableView createTicketsTableEmployee()
    {
        ticketsTableView = new TableView();
        ticketsTableView.setEditable(true);
        TableColumn<TheaterTicket, String> ticketColumn = new TableColumn<>("Play name");
        TableColumn<TheaterTicket, Integer> seatNumberColumn = new TableColumn<>("Seat Number");
        TableColumn<TheaterTicket, String> customerColumn = new TableColumn<>("Customer name");
        TableColumn<TheaterTicket, Double> priceColumn = new TableColumn<>("Price");
        TableColumn<TheaterTicket, Boolean> isSoldColumn = new TableColumn<>("Is sold");
        TableColumn<TheaterTicket, Date> dateColumn = new TableColumn<>("Date");
        TableColumn<TheaterTicket, Time> timeColumn = new TableColumn<>("Time");

        ticketColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPlayName()));
        seatNumberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getSeatNumber()));
        customerColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomerName()));
        priceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getPrice()));
        isSoldColumn.setCellValueFactory(cellData -> new ReadOnlyBooleanWrapper(cellData.getValue().isSold()));
        dateColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getDate().toString()));
        timeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getTime().toString()));

        ticketsTableView.getColumns().addAll(ticketColumn, seatNumberColumn, customerColumn,isSoldColumn, dateColumn, timeColumn, priceColumn);
        ticketsTableView.setItems(FXCollections.observableArrayList(
                theaterTicketManager.getTheaterTickets()
        ));
        ticketsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return ticketsTableView;
    }
    private TableView createCustomerTable()
    {
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

    private VBox createTicketAdder()
    {
        Button backButton3 = new Button("Back");
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
            } catch (IOException e) {
                throw new RuntimeException(e);
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

    private VBox createTicketDeleter()
    {
        Label deletePlayNameLabel = new Label("Play Name:");
        TextField deletePlayNameField = new TextField();
        Label deleteSeatNumberLabel = new Label("Seat Number:");
        TextField deleteSeatNumberField = new TextField();
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {theaterTicketManager.deleteTheaterTicket(deletePlayNameField.getText(), Integer.parseInt(deleteSeatNumberField.getText()));});
        VBox deleteTicketsVBox = new VBox(10);
        deleteTicketsVBox.setPadding(new Insets(10));
        deleteTicketsVBox.getChildren().addAll(
                deletePlayNameLabel, deletePlayNameField,
                deleteSeatNumberLabel, deleteSeatNumberField,
                deleteButton
        );

        return deleteTicketsVBox;
    }
    public BorderPane createMainLayout() {
        mainPane = new BorderPane();

        mainPane.setCenter(createTicketsTableEmployee());

        //createSignUpScene();
        //createLoginScene();
        //mainPane.setLeft(createOptionButton("Customer",mainPane));

        //mainPane.setRight(createOptionButton("Employee", mainPane));
        return mainPane;
    }

    private HBox createTopLayout() {
        Button backButton = new Button("Back to previous page");
        backButton.setOnAction(event -> {
            mainPane.setCenter(null);
        });

        // Create label for credit
        creditLabel = new Label();
        creditLabel.textProperty().bind(new ReadOnlyStringWrapper(new String("Credit: " + customer.getCredit())));

        // Create top container for back arrow and label
        HBox topContainer = new HBox(550);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(10));

        topContainer.getChildren().addAll(backButton, creditLabel);
        return topContainer;
    }

    private double calculateTotalCost(List<TheaterTicket> selectedTickets) {
        double totalCost = 0;
        for (TheaterTicket selectedTicket : selectedTickets) {
            totalCost += selectedTicket.getPrice();
        }
        return totalCost;
    }

    private Button createOptionButton(String option, BorderPane mainPane) {
        Button button = new Button(option);
        button.setPrefWidth(350);
        button.setPrefHeight(800);
        button.setOnAction(event -> {
            if (option.equals("Customer")) {
                button.setVisible(false);
            } else if (option.equals("Employee")) {
                button.setVisible(false);
            }
            mainPane.setLeft(null);
            mainPane.setRight(null);
            mainPane.setCenter(logIn());
        });
        return button;
    }

    private GridPane logIn() {
        GridPane logInLayout = new GridPane();
        logInLayout.setAlignment(Pos.CENTER);
        logInLayout.setHgap(10);
        logInLayout.setVgap(10);
        logInLayout.setPadding(new Insets(25, 25, 25, 25));
        logInUsernameField = new TextField();
        logInPasswordField = new PasswordField();
        logInButton = new Button("Log in");
        logInButton.setOnAction(event -> createCustomerLayout());
        Label logInUsernameLabel = new Label("Username");
        Label logInPasswordLabel = new Label("Password");
        logInLayout.add(logInUsernameLabel, 0, 0);
        logInLayout.add(logInUsernameField, 1, 0);
        logInLayout.add(logInPasswordLabel, 0, 1);
        logInLayout.add(logInPasswordField, 1, 1);
        logInLayout.add(logInButton, 1, 2);
        return logInLayout;
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

    /*private TheaterTicketManager theaterTicketManager;
    private CustomerManager customerManager;

    private ticketsTableView<TheaterTicket> theaterTicketsTable;
    private TextField playNameSearchField;
    private Button searchButton;
    private Button viewButton;
    private Button buyButton;
    private ticketsTableView<Customer> customersTable;
    private TextField customerNameField;
    private TextField creditField;
    private Button topUpButton;

    private TextField signUpUsernameField;
    private PasswordField signUpPasswordField;
    private Button signUpButton;

    private TextField loginUsernameField;
    private PasswordField loginPasswordField;
    private Button loginButton;

    private AuthenticationManager authManager;

    public UIManager(TheaterTicketManager theaterTicketManager, CustomerManager customerManager) {
        this.theaterTicketManager = theaterTicketManager;
        this.customerManager = customerManager;
        authManager = new AuthenticationManager();
    }

    public BorderPane createMainLayout() {
        BorderPane mainLayout = new BorderPane();

        theaterTicketsTable = new ticketsTableView<>();
        theaterTicketsTable.setItems(theaterTicketManager.getTheaterTicketsData());
        TableColumn<TheaterTicket, String> playNameColumn = new TableColumn<>("Play Name");
        playNameColumn.setCellValueFactory(cellData -> cellData.getValue().playNameProperty());
        TableColumn<TheaterTicket, String> customerNameColumn = new TableColumn<>("Customer Name");
        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        TableColumn<TheaterTicket, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        TableColumn<TheaterTicket, Integer> seatNumberColumn = new TableColumn<>("Seat Number");
        seatNumberColumn.setCellValueFactory(cellData -> cellData.getValue().seatNumberProperty().asObject());
        TableColumn<TheaterTicket, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty().asString());
        TableColumn<TheaterTicket, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty().asString());
        theaterTicketsTable.getColumns().addAll(playNameColumn, customerNameColumn, priceColumn, seatNumberColumn, dateColumn, timeColumn);

        playNameSearchField = new TextField();
        searchButton = new Button("Search");
        searchButton.setOnAction(event -> searchTheaterTickets());

        viewButton = new Button("View Details");
        viewButton.setOnAction(event -> viewTheaterTicketDetails());

        buyButton = new Button("Buy Ticket");
        buyButton.setOnAction(event -> buyTheaterTicket());

        customersTable = new ticketsTableView<>();
        customersTable.setItems(customerManager.getCustomersData());
        TableColumn<Customer, String> customerNameColumn2 = new TableColumn<>("Customer Name");
        customerNameColumn2.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        TableColumn<Customer, Double> creditColumn = new TableColumn<>("Credit");
        creditColumn.setCellValueFactory(cellData -> cellData.getValue().creditProperty().asObject());
        customersTable.getColumns().addAll(customerNameColumn2, creditColumn);

        customerNameField = new TextField();
        creditField = new TextField();
        topUpButton = new Button("Top-Up");
        topUpButton.setOnAction(event -> topUpCredit());

        VBox theaterTicketsVBox = new VBox(10);
        theaterTicketsVBox.getChildren().addAll(new Label("Theater Tickets"), theaterTicketsTable, viewButton, buyButton);

        VBox searchVBox = new VBox(10);
        searchVBox.getChildren().addAll(new Label("Search by Play Name"), playNameSearchField, searchButton);

        VBox customersVBox = new VBox(10);
        customersVBox.getChildren().addAll(new Label("Customers"), customersTable);

        HBox topUpHBox = new HBox(10);
        topUpHBox.getChildren().addAll(new Label("Customer Name:"), customerNameField, new Label("Credit:"), creditField, topUpButton);

        VBox customersActionVBox = new VBox(10);
        customersActionVBox.getChildren().addAll(new Label("Top-Up Credit"), topUpHBox);

        HBox mainHBox = new HBox(20);
        mainHBox.getChildren().addAll(theaterTicketsVBox, searchVBox, customersVBox, customersActionVBox);

        mainLayout.setCenter(mainHBox);

        signUpUsernameField = new TextField();
        signUpPasswordField = new PasswordField();
        signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(event -> signUp());

        loginUsernameField = new TextField();
        loginPasswordField = new PasswordField();
        loginButton = new Button("Login");
        loginButton.setOnAction(event -> login());

        VBox authVBox = new VBox(10);
        authVBox.getChildren().addAll(new Label("Sign Up"), signUpUsernameField, signUpPasswordField, signUpButton,
                new Label("Login"), loginUsernameField, loginPasswordField, loginButton);

        mainLayout.setRight(authVBox);
        return mainLayout;
    }
    }

}
*/

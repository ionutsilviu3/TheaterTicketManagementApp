package com.ggc.theaterkarten;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class TheaterTicketManager {


    private List<TheaterTicket> theaterTickets;
    private List<Customer> customers;

    // Constructor
    public TheaterTicketManager() {
        this.theaterTickets = new ArrayList<>();

        List<String> ticketNames = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get("src/main/resources/com/ggc/theaterkarten/Tickets"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    ticketNames.add(file.getFileName().toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (String ticketName : ticketNames)
            theaterTickets.add(findTicket((ticketName)));
    }

    public TheaterTicket findTicket(String ticketName) {
        TheaterTicket theaterTicket = null;
        ticketName = ticketName.substring(0, ticketName.length() - 4);

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/com/ggc/theaterkarten/Tickets/" + ticketName+".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String name = ticketName.replaceAll("[0-9]", "");
                int seatNumber = Integer.parseInt(ticketName.replaceAll("[a-zA-Z]", ""));
                String[] data = line.split(",");
                String customerName = data[0];
                double price = Double.parseDouble(data[1]);
                boolean isSold = false;
                if(data[2].equalsIgnoreCase("sold"))
                    isSold = true;
                String[] d = data[3].split("/");
                Date date = new Date(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]));
                String[] t = data[4].split(":");
                Time time = new Time(Integer.parseInt(t[0]), Integer.parseInt(t[1]));
                theaterTicket = new TheaterTicket(name, customerName, price, seatNumber, isSold, date, time);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return theaterTicket;
    }

    public List<TheaterTicket> getTheaterTickets() {
        return theaterTickets;
    }
    // Methods to manage theater tickets
    public boolean createTheaterTicket(String playName, double price, int seatNumber, Date date, Time time) throws IOException {
        TheaterTicket ticket = new TheaterTicket(playName, "", price, seatNumber,false, date, time);
        theaterTickets.add(ticket);
        createTheaterTicketFile(playName, price, seatNumber, date, time);
        return false;
    }

    private void createTheaterTicketFile(String playName, double price, int seatNumber, Date date, Time time) throws IOException {
        FileWriter theaterTicketFile = new FileWriter("src/main/resources/com/ggc/theaterkarten/Tickets/" + playName + seatNumber +".txt");
        theaterTicketFile.write(","+price+",free,"+date.toString() + "," + time.toString());
        theaterTicketFile.close();
    }

    public boolean deleteTheaterTicket(String playName, int seatNumber) {
        for (TheaterTicket ticket : theaterTickets) {
            if (ticket.getPlayName().equals(playName) && ticket.getSeatNumber() == seatNumber) {
                theaterTickets.remove(ticket);
                return deleteTheaterTicketFile(playName, seatNumber);
            }
        }
        return false;
    }

    private boolean deleteTheaterTicketFile(String playName, int seatNumber) {
        File theaterTicketFile = new File("src/main/resources/com/ggc/theaterkarten/Tickets/" + playName + seatNumber +".txt");
        if (theaterTicketFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public TheaterTicket searchTheaterTicketByPlayName(String playName) {
        for (TheaterTicket ticket : theaterTickets) {
            if (ticket.getPlayName().equals(playName)) {
                return ticket;
            }
        }
        return null;
    }

    // Methods to manage customers
    public void registerCustomer(String username, String password, double initialCredit) {
        Customer customer = new Customer(username, password, initialCredit);
        customers.add(customer);
    }

    public Customer customerLogin(String username, String password) {
        for (Customer customer : customers) {
            if (customer.getName().equals(username) && customer.getPassword().equals(password)) {
                return customer;
            }
        }
        return null;
    }
}


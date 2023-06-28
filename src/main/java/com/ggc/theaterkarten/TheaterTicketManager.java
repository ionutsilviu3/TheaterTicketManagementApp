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
                String isSold = "free";
                if(data[2].equalsIgnoreCase("sold"))
                    isSold = "sold";
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
    public boolean createTheaterTicket(String playName, double price, int seatNumber, Date date, Time time) throws Exception {
        TheaterTicket ticket = new TheaterTicket(playName, "", price, seatNumber,"free", date, time);
        theaterTickets.add(ticket);
        createTheaterTicketFile(playName, price, seatNumber, date, time);
        return false;
    }

    private void createTheaterTicketFile(String playName, double price, int seatNumber, Date date, Time time) throws Exception {
        if(playName.equalsIgnoreCase("")
                || (seatNumber < 1 || seatNumber > 100)
                || price < 1 || date.getDay() < 1 || date.getDay() > 31
                || date.getMonth() < 1 || date.getDay() > 12
                || date.getYear() < 2020 || date.getDay() > 2030
                || time.getHour() < 0 || time.getHour() > 24
                || time.getMinute() < 0 || time.getHour() > 59)
            throw new Exception();
        FileWriter theaterTicketFile = new FileWriter("src/main/resources/com/ggc/theaterkarten/Tickets/" + playName + seatNumber +".txt");
        theaterTicketFile.write(","+price+",free,"+date.toString() + "," + time.toString());
        theaterTicketFile.close();
    }

    public boolean deleteTheaterTicket(String playName, int seatNumber) throws Exception {
        for (TheaterTicket ticket : theaterTickets) {
            if (ticket.getPlayName().equals(playName) && ticket.getSeatNumber() == seatNumber) {
                theaterTickets.remove(ticket);
                return deleteTheaterTicketFile(playName, seatNumber);
            }
        }
        throw new Exception();
    }

    private boolean deleteTheaterTicketFile(String playName, int seatNumber) throws Exception {
        File theaterTicketFile = new File("src/main/resources/com/ggc/theaterkarten/Tickets/" + playName + seatNumber +".txt");
        System.out.println(playName + seatNumber);
        if (theaterTicketFile.delete()) {
            return true;
        } else {
            throw new Exception();
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
    public void updateTicket(TheaterTicket ticket, String customer) throws Exception {
        if(ticket.getPlayName().equalsIgnoreCase("")
                || (ticket.getSeatNumber() < 1 || ticket.getSeatNumber() > 99)
                || ticket.getPrice() < 1 || (ticket.getDate().getDay() < 1 || ticket.getDate().getDay() > 31)
                || (ticket.getDate().getMonth() < 1 || ticket.getDate().getDay() > 12)
                || (ticket.getDate().getYear() < 2020 || ticket.getDate().getDay() > 2030)
                || (ticket.getTime().getHour() < 0 || ticket.getTime().getHour() > 24)
                || (ticket.getTime().getMinute() < 0 || ticket.getTime().getHour() > 59))
            throw new Exception();
        deleteTheaterTicketFile(ticket.getPlayName(), ticket.getSeatNumber());
        FileWriter theaterTicketFile = new FileWriter("src/main/resources/com/ggc/theaterkarten/Tickets/" + ticket.getPlayName() + ticket.getSeatNumber() +".txt");
        theaterTicketFile.write(customer + "," + ticket.getPrice() +","+ticket.isSold()+","+ ticket.getDate().toString() + ","+ticket.getTime().toString());
        theaterTicketFile.close();
    }
    public List<TheaterTicket> getNotSoldTheaterTickets() {
        List<TheaterTicket> tickets = new ArrayList<>();
        for(TheaterTicket t : theaterTickets)
            if(t.isSold().equalsIgnoreCase("free"))
                tickets.add(t);
        return tickets;
    }
}


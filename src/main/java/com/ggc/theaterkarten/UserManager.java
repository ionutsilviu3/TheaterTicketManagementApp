package com.ggc.theaterkarten;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    List<Customer> customers;
    List<Employee> employees;

    public UserManager() {
        customers = new ArrayList<>();
        employees = new ArrayList<>();

        List<String> customerNames = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get("src/main/resources/com/ggc/theaterkarten/Customers"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    customerNames.add(file.getFileName().toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String customerName : customerNames)
            customers.add(findCustomer(customerName));


        List<String> employeeNames = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get("src/main/resources/com/ggc/theaterkarten/Employees"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    employeeNames.add(file.getFileName().toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (String employeeName : employeeNames)
            employees.add(findEmployee(employeeName));
    }
    public void createCustomer(String userName, String password, Double credit)
    {
        customers.add(new Customer(userName, password, credit));

        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/com/ggc/theaterkarten/Customers/" + userName+".txt");
            fileWriter.write(password +","+credit);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving the customer's information.");
            e.printStackTrace();
        }
    }
    public void createCustomer(Customer customer)
    {
        customers.add(customer);
        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/com/ggc/theaterkarten/Customers/" + customer.getName()+".txt");
            fileWriter.write(customer.getPassword() +","+customer.getCredit());
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving the customer's information.");
            e.printStackTrace();
        }
    }

    public void createEmployee(String userName, String password)
    {
        employees.add(new Employee(userName, password));
        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/com/ggc/theaterkarten/Employees/" + userName+".txt");
            fileWriter.write(password);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving the employee's information.");
            e.printStackTrace();
        }
    }
    public void createEmployee(Employee employee)
    {
        employees.add(employee);
        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/com/ggc/theaterkarten/Employees/" +employee.getName()+".txt");
            fileWriter.write(employee.getPassword());
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving the employee's information.");
            e.printStackTrace();
        }
    }

    public Customer findCustomer(String customerName)
    {
        Customer customer = null;
        customerName = customerName.substring(0, customerName.length() - 4);
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/com/ggc/theaterkarten/Customers/" + customerName+".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String password = data[0];
                double credit = Double.parseDouble(data[1]);
                customer = new Customer(customerName, password, credit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customer;
    }
    public List<String> getAllCustomerNames()
    {
        List<String> customerNames = new ArrayList<>();
        for(Customer c : customers)
            customerNames.add(c.getName());
        return customerNames;
    }
    public List<String> getAllEmployeeNames()
    {
        List<String> employeeNames = new ArrayList<>();
        for(Employee e : employees)
            employeeNames.add(e.getName());
        return employeeNames;
    }
    public Employee findEmployee(String employeeName)
    {
        Employee employee = null;
        employeeName = employeeName.substring(0, employeeName.length() - 4);
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/com/ggc/theaterkarten/Employees/" + employeeName+".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String password = data[0];
                employee = new Employee(employeeName, password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employee;
    }

    public List<Customer> getAllCustomers() {
        return customers;
    }

    public List<Employee> getAllEmployees() {
        return employees;
    }
}

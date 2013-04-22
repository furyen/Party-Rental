
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

import Datasource.DBFacade;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;

/**
 *
 * @author dekez
 */
public class Controller {

    private static Controller instance;
    private DBFacade dbFacade;
    private Resource currentResource;
    private Customer currentCustomer;
    private Order currentOrder;

    private Controller() {
        dbFacade = DBFacade.getIntance();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void getConnection() {
        dbFacade.getConnection();
    }

    public ArrayList getAvailableResources(Date startDate, Date endDate) {
        ArrayList<Resource> availableResources = new ArrayList();

        availableResources = dbFacade.getAvailableResources(startDate, endDate);



        return availableResources;
    }

    public boolean createNewResource(String resourceName, int quantity, double price, int unitSize) {
        boolean status = false;
        Resource resource = null;
        int resourceID = dbFacade.getUniqueResourceID();
        resource = new Resource(resourceID, resourceName, quantity, price, unitSize); //int resourceID, String resourceName,  int quantity,  double price, int unitSize

        try {
            status = dbFacade.createNewResource(resource);
        } catch (SQLException ex) {
            System.out.println("Error in createNewResource or methods involved");
        }

        return status;
    }

    public Resource getResource(String name) {
        try {
            currentResource = dbFacade.getResource(name);
        } catch (SQLException ex) {
            System.out.println("Error in getResource " + ex);
        }


        return currentResource;
    }

    public boolean editResource(String name, int quantitiy, double price) {
        boolean status = false;

        if (currentResource != null) {
            currentResource.setResourceName(name);
            currentResource.setQuantity(quantitiy);
            currentResource.setPrice(price);
        }

        try {
            status = dbFacade.editResource(currentResource);
        } catch (SQLException ex) {
            System.out.println("Error in the editResource - " + ex);
        }

        return status;
    }

    public ArrayList getTruckDeliveryForDate(Date date, char ch) {
        //char 0=delivery,1=return
        ArrayList<Truck> listRuns = new ArrayList();
        listRuns = dbFacade.getTruckDeliveryForDate(date, ch);
        return listRuns;
    }

    public ArrayList<Customer> getCustomerList(String firstName, String lastName) {
        ArrayList<Customer> customerList = new ArrayList();

        try {
            customerList = dbFacade.getCustomerList(firstName, lastName);
        } catch (SQLException ex) {
            System.out.println("Error in getCustomerList - " + ex);
        }

        return customerList;
    }

    public Customer getCustomer(int customerID) {
        Customer customer = null;

        customer = dbFacade.getCustomer(customerID);

        return customer;
    }

    public Customer createCustomer(String firstName, String lastName, String adress) {
        boolean status = false;
        Customer newCustomer = null;
        int customerID;


        try {
            dbFacade.startNewBusinessTransaction();
            customerID = dbFacade.getUniqueCustomerID();
            System.out.println(customerID);
            newCustomer = new Customer(customerID, firstName, lastName, adress);
            status = dbFacade.createCustomer(newCustomer);
            currentCustomer = newCustomer;
        } catch (SQLException ex) {
            System.out.println("Error in createCustomer - " + ex);
        }

        return newCustomer;
    }

    public double calculatePrice(LinkedHashMap<Resource, JComboBox> resourceList, int discount, LinkedHashMap<Truck, JToggleButton> truckDelivery, LinkedHashMap<Truck, JToggleButton> truckReturn) {
        double finalPrice = 0;

        for (Map.Entry<Resource, JComboBox> entry : resourceList.entrySet()) {
            finalPrice += entry.getKey().getQuantity() * entry.getKey().getPrice();
        }
        for (Map.Entry<Truck, JToggleButton> entry : truckDelivery.entrySet()) {
            if(!"Fill Truck".equals(entry.getValue().getText())&&!"0".equals(entry.getValue().getText())) {
                finalPrice += Integer.parseInt(entry.getValue().getText()) * entry.getKey().getUnitPrice();
            }
        }
          for (Map.Entry<Truck, JToggleButton> entry : truckReturn.entrySet()) {
            if(!"Fill Truck".equals(entry.getValue().getText())&&!"0".equals(entry.getValue().getText())) {
                finalPrice += Integer.parseInt(entry.getValue().getText()) * entry.getKey().getUnitPrice();
            }
        }
        
        finalPrice = finalPrice / 100 * (100 - discount);


        return finalPrice;
    }

    public boolean createNewInvoice(double discount, double finalPrice) {
        boolean status = false;
        currentOrder.setDiscount(discount);
        currentOrder.setFullPrice(finalPrice);

        if (currentOrder != null) {
            status = dbFacade.createNewInvoice(currentOrder);
            createDepositInvoiceFile(currentOrder);
        }



        return status;
    }

    public boolean finishOrder() {

        boolean status = false;
        try {
            status = dbFacade.finishOrder();
        } catch (Exception ex) {
            System.out.println("error in finishOrder - " + ex);
        }


        return status;
    }
    //int customerID, int orderID, int unitSize, String adress, Date startDate, Date endDate, boolean deposit

    public boolean createOrder(int customerID, int unitSize, String address, Date startDate, Date endDate) {
        boolean status = false;
        int orderID;
        Order newOrder = null;
        dbFacade.startNewBusinessTransaction();

        orderID = dbFacade.getUniqueOrderID();
        newOrder = new Order(customerID, orderID, unitSize, address, startDate, endDate, false, 0, 0, 0, 0);
        currentOrder = newOrder;
        status = dbFacade.createOrder(newOrder);


        return status;
    }

    public void createOrderDetail(int resourceID, int quantity, String resourceName) {
        OrderDetail orderDetail = new OrderDetail(currentOrder.getOrderID(), resourceID, quantity);
        orderDetail.setRessourceName(resourceName);
        currentOrder.insertOrderDetail(orderDetail);
        dbFacade.createOrderDetail(orderDetail);
    }

    public void truckBooking(int truckID, int truckRun, char ch, int orderPartSize) {
        TruckOrder tr = new TruckOrder(truckID, truckRun, currentOrder.getOrderID(), ch, orderPartSize);
        dbFacade.truckBooking(tr);
    }

    public ArrayList<Order> getOrders() {
        ArrayList<Order> list = dbFacade.getOrders();
        return list;
    }

    public ArrayList<Order> getCustomerOrderHistory(int customerID) {
        ArrayList<Order> orders = dbFacade.getCustomerOrders(customerID);
        return orders;

    }

    public boolean checkOrder() {
        boolean status = true;

        if (currentOrder != null) {
            ArrayList<Resource> resourceList = getAvailableResources(currentOrder.getStartDate(), currentOrder.getEndDate());
            ArrayList<OrderDetail> orderDetailList = currentOrder.getOrderDetails();

            for (OrderDetail orderDetail : orderDetailList) {
                if (orderDetail.getQuantity() != 0) {
                    for (Resource resource : resourceList) {
                        if (orderDetail.getResourceID() == resource.getResourceID()) {
                            status = status && (orderDetail.getQuantity() < resource.getQuantity());
                        }
                    }
                }
            }

            if (status == true) {
                finishOrder();
            }

        }

        return status;
    }

    public boolean savePayment(Order currentOrder, Double newPayment) {
        boolean status = false;
        currentOrder.setPaidAmount(newPayment);
        return status = dbFacade.savePayment(currentOrder);
    }
    
    public boolean cancelOrder(Order order){
        boolean status = false;

        order.setCancelled(true);

        status = dbFacade.cancelOrder(order);


        return status;
    }

    public Order editOrder(Order order) {
        boolean status = false;

        status = cancelOrder(order);

        return order;
    }

    public Order cancelEditOrder(Order order) {
        order.setCancelled(false);
        boolean status = false;

        status = dbFacade.cancelOrder(order);

        return order;
    }

    public boolean createDepositInvoiceFile(Order order) {
        boolean status = false;
        FileWriter fileWriter;
        Customer customer = getCustomer(order.getCustomerID());
        String invoiceString =
                "HellebÃ¦k Party Rental\t\t\t\t\t\tCVR: 32139429\n"
                + "\t\t\t\t\t\t\t\tOrder nr: " + order.getOrderID() + "\n"
                + "\n"
                + "\n"
                + "Dear " + customer.getFirstName() + " " + customer.getLastName()
                + "\n"
                + "You are receiving this invoice in accordance to your order\n"
                + "with to the following address: " + order.getAdress()
                + "\nYou have ordered the following things:\n"
                + "\n";

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Resource resource = getResource(orderDetail.getRessourceName());
            invoiceString += orderDetail.getRessourceName() + ": " + resource.getPrice() + "\n";
        }

        invoiceString +=
                "\n"
                + "The full price: " + order.getFullPrice() + "\n"
                + "\n"
                + "You are to pay the deposit of 25% which amounts to: " + (order.getFullPrice() * 0.25) + "\n"
                + "\n"
                + "We hope you will enjoy the event";

        System.out.println(invoiceString);

        try {
            fileWriter = new FileWriter(new File("Deposit Invoice - Order ID - " + order.getOrderID() + ".txt"));
            fileWriter.write(invoiceString);
            status = true;
            fileWriter.close();

        } catch (IOException ex) {
            System.out.println("Error in writing invoice file - " + ex);
        }
        return status;
    }

    public boolean createFinalInvoiceFile(Order order) {
        boolean status = false;
        FileWriter fileWriter;
        Customer customer = getCustomer(order.getCustomerID());
        String invoiceString =
                "HellebÃ¦k Party Rental\t\t\t\t\t\tCVR: 32139429\n"
                + "\t\t\t\t\t\t\t\tOrder nr: " + order.getOrderID() + "\n"
                + "\n"
                + "\n"
                + "Dear " + customer.getFirstName() + " " + customer.getLastName()
                + "\n"
                + "You are receiving this invoice in accordance to your order\n"
                + "with to the following address: " + order.getAdress()
                + "\nYou have ordered the following things:\n"
                + "\n";

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Resource resource = getResource(orderDetail.getRessourceName());
            invoiceString += orderDetail.getRessourceName() + ": " + resource.getPrice() + "\n";
        }

        invoiceString +=
                "\n"
                + "The full price: " + order.getFullPrice() + "\n"
                + "\n"
                + "You have payed the deposit of 25% which amounts to: " + ((order.getFullPrice() * (1 - order.getDiscount())) * 0.25) + "\n"
                + "The remaining amount to be payed amounts to: " + ((order.getFullPrice() * (1 - order.getDiscount())) - (order.getFullPrice() * 0.25)) + "\n"
                + "\n"
                + "We hope you enjoyed the event";

        System.out.println(invoiceString);

        try {
            fileWriter = new FileWriter(new File("Final Invoice - Order ID - " + order.getOrderID() + ".txt"));
            fileWriter.write(invoiceString);
            status = true;
            fileWriter.close();

        } catch (IOException ex) {
            System.out.println("Error in writing invoice file - " + ex);
        }
        return status;
    }

    public ArrayList<OrderDetail> getOrderDetail(Order o) {
        return o.getOrderDetails();
    }
    
    public String createDeliveryList(Date searchDate){
        String deliveryList = "";
        ArrayList<Order> orderList = getOrderDeliveryOnDate(searchDate);
        
        
        return deliveryList;
    }
    
    public ArrayList<Order> getOrderDeliveryOnDate(Date searchDate){
        ArrayList<Order> orderList = null;
        
        
        
        return orderList;
    }
    
    public boolean createTruck(int truckSize, int unitPrice){
        boolean bool  = dbFacade.createTruck(truckSize, unitPrice);
        return bool;
    } 
    
    public boolean editTruck(int truckID, double unitPrice){
        boolean bool = dbFacade.editTruck(truckID, unitPrice);
        return bool;
    }
    
    public ArrayList<Truck> getTrucks(){
        ArrayList<Truck> list = dbFacade.getTrucks();
        return list;
    }
    
    public boolean deactivateResource(int resourceID){
        boolean status = false;
        boolean cancelledStatus = true;
        ArrayList<Order> affectedOrders = getAffectedOrders(resourceID);
        
        if(affectedOrders.size() > 0){
            status = false;
        }
        else{
            status = dbFacade.deactiveResource(resourceID);
        }
        
        
        return status;
    }
    
    public boolean reactivateResource(String resourceName){
        boolean status = false;
        ArrayList<Resource> resourceList = getAvailableResources(null, null);
        
        for(Resource resource : resourceList){
            if(resource.equals(resourceName)){
                status = dbFacade.reactivateResource(resourceName);
            }
        }
        
        return status;
    }
    
}


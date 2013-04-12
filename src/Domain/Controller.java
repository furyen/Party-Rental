/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

import Datasource.DBFacade;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComboBox;

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
    
    
    public void getConnection(){
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
            System.out.println(status);
            currentCustomer = newCustomer;
        } catch (SQLException ex) {
            System.out.println("Error in createCustomer - " + ex);
        }

        return newCustomer;
    }

    public double calculatePrice(LinkedHashMap<Resource,JComboBox> resourceList,int discount) {
        double finalPrice = 0;

        for (Map.Entry<Resource,JComboBox> entry : resourceList.entrySet()) {
            finalPrice += entry.getKey().getQuantity() * entry.getKey().getPrice();
        }
        finalPrice=finalPrice/100*(100-discount);


        return finalPrice;
    }

    public boolean createNewInvoice(int orderID, double discount, double finalPrice) {
        boolean status = false;
        Invoice newInvoice = null;

        newInvoice = new Invoice(orderID, discount, finalPrice);
        System.out.println(newInvoice.getOrderID());
        status = dbFacade.createNewInvoice(newInvoice);

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
        newOrder = new Order(customerID, orderID, unitSize, address, startDate, endDate, false);
        currentOrder = newOrder;
        System.out.println("Inside create order");
        System.out.println(newOrder.getAdress());
        status = dbFacade.createOrder(newOrder);


        return status;
    }

    public void createOrderDetail(int resourceID, int quantity) {
        OrderDetail orderDetail = new OrderDetail(currentOrder.getOrderID(), resourceID, quantity);
        currentOrder.insertOrderDetail(orderDetail);
        dbFacade.createOrderDetail(orderDetail);
    }
     public void truckBooking(int truckID, int  truckRun, char ch){
         TruckOrder tr = new TruckOrder(truckID, truckRun,currentOrder.getOrderID(),ch);  
         dbFacade.truckBooking(tr);
    }
}

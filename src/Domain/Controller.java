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

    public boolean createNewInvoice(double discount, double finalPrice) {
        boolean status = false;
        currentOrder.setDiscount(discount);
        currentOrder.setFullPrice(finalPrice);
        
        if(currentOrder != null){
            status = dbFacade.createNewInvoice(currentOrder);
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
        newOrder = new Order(customerID, orderID, unitSize, address, startDate, endDate, false,0,0,0,0);
        currentOrder = newOrder;
        status = dbFacade.createOrder(newOrder);


        return status;
    }

    public void createOrderDetail(int resourceID, int quantity) {
        OrderDetail orderDetail = new OrderDetail(currentOrder.getOrderID(), resourceID, quantity);
        currentOrder.insertOrderDetail(orderDetail);
        dbFacade.createOrderDetail(orderDetail);
    }
    
    public void truckBooking(int truckID, int  truckRun, char ch, int orderPartSize){
         TruckOrder tr = new TruckOrder(truckID, truckRun,currentOrder.getOrderID(),ch, orderPartSize);  
         dbFacade.truckBooking(tr);
    }
    
    public ArrayList<Order> getOrders(){
        ArrayList<Order> list = dbFacade.getOrders();
        return list;
    }
    
    public boolean checkOrder(){
        boolean status = true;
        
        if(currentOrder != null){
            ArrayList<Resource> resourceList = getAvailableResources(currentOrder.getStartDate(), currentOrder.getEndDate());
            ArrayList<OrderDetail> orderDetailList = currentOrder.getOrderDetails();
            
            for(OrderDetail orderDetail : orderDetailList){
                if(orderDetail.getQuantity() != 0){
                    for(Resource resource : resourceList){
                        if(orderDetail.getResourceID() == resource.getResourceID()){
                            status = status && (orderDetail.getQuantity() < resource.getQuantity());
                        }
                    }
                }
            }
            
            if(status == true){
                finishOrder();
            }
            
        }
        
        return status;
    }
    
    public boolean cancelOrder(Order order){
        boolean status = false;
        
        order.setCancelled(true);
        
        status = dbFacade.cancelOrder(order);
        
        
        return status;
    }
    
    public Order editOrder(Order order){
        boolean status  = false;
        
        status = cancelOrder(order);
        
        return order;
    }
    
    public Order cancelEditOrder(Order order){
        order.setCancelled(false);
        boolean status = false;
        
        status = dbFacade.cancelOrder(order);
        
        return order;
    }
    
    public boolean createInvoiceFile(Order order){
        boolean status = false;
        FileWriter fileWriter;
        String invoiceString = 
                "Hellebæk Party Rental\t\t\t\t\t\tCVR: 32139429\n"
                + "\t\t\t\t\t\t\t\tOrder nr: " + "on" + "\n"
                + "\n"
                + "Dear " + "Customer Name"
                + "\n"
                + "You are receiving this invoice in accordance to your order\n"
                + "with to the following address: " + "Order address"
                + "You have ordered the following things:\n"
                + "\n";
        
        for(OrderDetail orderDetail : order.getOrderDetails()){
            
        }
        
        try{
            fileWriter = new FileWriter(new File("Order - " + order.getOrderID() + ".txt"));
            
        }
        catch(IOException ex){
            System.out.println("Error in writing invoice file - " + ex);
        }
        
        
        return status;
    }
    
}

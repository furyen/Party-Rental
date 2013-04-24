
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
    
    /*
     * Fetches the connection from the DBFacade.
     * Ends in the DBFacade.
     */
    public void getConnection() {
        dbFacade.getConnection();
    }
    
    /*
     * Gets all available resources from the start date
     * to the end date inputted.
     * Ends in the ResourceMapper
     */
    public ArrayList getAvailableResources(Date startDate, Date endDate) {
        ArrayList<Resource> availableResources = new ArrayList();

        availableResources = dbFacade.getAvailableResources(startDate, endDate);
        
        return availableResources;
    }
    
    /*
     * Creates a new resource 
     * Ends in the ResourceMapper
     */
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
    
    /*
     * Gets a resource based on its name.
     * Ends in ResourceMapper
     */
    public Resource getResource(String name) {
        try {
            currentResource = dbFacade.getResource(name);
        } catch (SQLException ex) {
            System.out.println("Error in getResource " + ex);
        }

        return currentResource;
    }

    /*
     * Takes in the values for a resource to edit the current
     * resource object. 
     * Ends in the ResourceMapper
     */
    public boolean editResource(String name, int quantitiy, double price, boolean active) {
        boolean status = false;

        if (currentResource != null) {
            currentResource.setResourceName(name);
            currentResource.setQuantity(quantitiy);
            currentResource.setPrice(price);
            currentResource.setActive(active);
            try {
                status = dbFacade.editResource(currentResource);
            } catch (SQLException ex) {
                System.out.println("Error in the editResource - " + ex);
            }
        }
        
        return status;
    }

    /*
     * Gets the trucks for a specific delivery date
     * Ends in the TruckMapper
     */
    public ArrayList getTruckDeliveryForDate(Date date, char ch) {
        //char 0=delivery,1=return
        ArrayList<Truck> listRuns = new ArrayList();
        listRuns = dbFacade.getTruckDeliveryForDate(date, ch);
        return listRuns;
    }
    
    /*
     * Get's a list of customers with the inputted
     * string in their names.
     * Ends in the CustomerMapper
     */
    public ArrayList<Customer> getCustomerList(String firstName, String lastName) {
        ArrayList<Customer> customerList = new ArrayList();

        try {
            customerList = dbFacade.getCustomerList(firstName, lastName);
        } catch (SQLException ex) {
            System.out.println("Error in getCustomerList - " + ex);
        }

        return customerList;
    }
    
    /*
     * Gets a customer based on his customerID
     * Ends in the CustomerMapper
     */
    public Customer getCustomer(int customerID) {
        Customer customer = null;

        customer = dbFacade.getCustomer(customerID);
        currentCustomer = customer;

        return customer;
    }
    
    /*
     * Starts a new business transaction
     * Creates a customer
     * Assigns a temporary customerID of 0
     * dbFacade.startNewBusinessTransaction() makes sure UnitOfWork object
     * is instantiated before using the UnitOfWorkProcessOrder
     * Ends in the UnitOfWordProcessOrder
     */
    public Customer createCustomer(String firstName, String lastName, String adress) {
        Customer newCustomer = null;
        int customerID;
        
        try{
            dbFacade.startNewBusinessTransaction();
            customerID = dbFacade.getUniqueCustomerID();
            newCustomer = new Customer(customerID, firstName, lastName, adress);
            dbFacade.createCustomer(newCustomer);
            currentCustomer = newCustomer;
        }
        catch(SQLException ex){
            System.out.println("Error in the createCustomer - " + ex);
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

    /*
     * Creates a new invoice and adds it to the 
     * newInvoiceList in UnitOfWorkProcessOrder
     * Ends in the UnitOfWorkProcessOrder
     */
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
    
    /*
     * Commits all business transactions to the database
     * Ends in commit() in UnitOfWorkProcessOrder
     */
    public boolean finishOrder() {
        boolean status = false;
        
        try {
            status = dbFacade.finishOrder();
        } catch (Exception ex) {
            System.out.println("error in finishOrder - " + ex);
        }

        return status;
    }
    
    /*
     * Creates a new Order object with a temporary
     * orderID and adds it to the 
     * newOrderList in UnitOfWorkProcessOrder
     * Ends in UnitOfWorkProcessOrder
     */
    public boolean createOrder(int customerID, int unitSize, String address, Date startDate, Date endDate) {
        boolean status = false;
        int orderID = dbFacade.getUniqueOrderID();
        Order newOrder = null;
        dbFacade.startNewBusinessTransaction();
        
        if(currentCustomer == null){
            getCustomer(customerID);
        }

        newOrder = new Order(customerID, orderID, unitSize, address, startDate, endDate, false, 0, 0, 0, 0);
        currentOrder = newOrder;
        System.out.println();
        status = dbFacade.createOrder(newOrder);


        return status;
    }
    
    /*
     * Creates a new OrderDetail and adds it to the currentOrder
     * It also adds it to the NewOrderDetailList in the UnitOfWorkProcessOrder
     * Ends in the UnitOfWorkProcessOrder
     */
    public void createOrderDetail(int resourceID, int quantity, String resourceName) {
        OrderDetail orderDetail = new OrderDetail(currentOrder.getOrderID(), resourceID, quantity);
        System.out.println(orderDetail.getOrderID());
        orderDetail.setRessourceName(resourceName);
        currentOrder.insertOrderDetail(orderDetail);
        dbFacade.createOrderDetail(orderDetail);
    }
    
    /*
     * Books a truck for an Order
     * It needs to be used for each truck which is booked for an order(if there are more trucks for an oreder)
     * The char is 0 for delivery and 1 for return
     * Ends in UnitOfWorkProcessOrder
     */
    public void truckBooking(int truckID, int truckRun, char ch, int orderPartSize) {
        TruckOrder tr = new TruckOrder(truckID, truckRun, currentOrder.getOrderID(), ch, orderPartSize);
        dbFacade.truckBooking(tr);
    }
    
    /*
     * Gets a list of all orders
     * Ends in OrderMapper
     */
    public ArrayList<Order> getOrders() {
        ArrayList<Order> list = dbFacade.getOrders();
        return list;
    }
    
    /*
     * Gets a list of all orders for a specific customer
     * Ends in OrderMapper
     */
    public ArrayList<Order> getCustomerOrderHistory(int customerID) {
        ArrayList<Order> orders = dbFacade.getCustomerOrders(customerID);
        return orders;
    }
    
    /*
     * Makes sure that all resources in the current order
     * are still available by checking against a new list of 
     * available resources
     * Runs finishOrder() to commit everything to database and returns
     * true if it succeeds and all resources in the order is still available.
     */
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
        status = dbFacade.savePayment(currentOrder);
        
        return status;
    }
    
    /*
     * Sets the boolean cancelled of an Order object to true
     * again making the order inactive.
     * Ends in OrderMapper
     */
    public boolean cancelOrder(Order order){
        boolean status = false;

        order.setCancelled(true);

        status = dbFacade.cancelOrder(order);

        return status;
    }

    /*
     * Sets the boolean cancelled of the current order to false
     * making it active again.
     * Ends in OrderMapper.
     */
    public Order cancelEditOrder(Order order) {
        order.setCancelled(false);

        dbFacade.cancelOrder(order);

        return order;
    }

    /*
     * Creates a deposit invoice file for a specific order
     * Returns true if the file was created successfully.
     * Ends in the Controller.
     */
    public boolean createDepositInvoiceFile(Order order) {
        boolean status = false;
        FileWriter fileWriter;
        Customer customer = currentCustomer;
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

    /*
     * Creates a final invoice file for an order
     * Returns true if it succeeds.
     * Ends in the controller.
     */
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

    /*
     * Gets the orderdetails of a certain order
     * Ends in the Order class.
     */
    public ArrayList<OrderDetail> getOrderDetail(Order o) {
        return o.getOrderDetails();
    }
    
    /*
     * Creates a new truck for use.
     * Ends in the TruckMapper.
     */
    public boolean createTruck(int truckSize, Double unitPrice){
        boolean bool  = dbFacade.createTruck(truckSize, unitPrice);
        return bool;
    } 
    
    /*
     * Edits the unitprice of a truck identified by a given truckID
     * Ends in TruckMapper.
     */
    public boolean editTruck(int truckID, double unitPrice){
        boolean bool = dbFacade.editTruck(truckID, unitPrice);
        return bool;
    }
    
    /*
     * Gets a list of all trucks
     * Ends in TruckMapper
     */
    public ArrayList<Truck> getTrucks(){
        ArrayList<Truck> list = dbFacade.getTrucks();
        return list;
    }
    
    /*
     * Sets the boolean active of a resource to false
     * Making the resource unavailable
     * Ends in ResourceMapper
     */
    public boolean deactivateResource(int resourceID){
        boolean status = false;
        ArrayList<Order> affectedOrders = getAffectedOrders(resourceID);
        
        if(affectedOrders.size() > 0){
            status = false;
        }
        else{
            status = dbFacade.deactiveResource(resourceID);
        }
        
        return status;
    }
    
    /*
     * Sets the active boolean of a resource to true
     * making the resource available again.
     * Ends in ResourceMapper.
     */
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
    
    /*
     * Gets all orders which include the resource, with the given resourceID
     * Ends in the OrderMapper
     */
    public ArrayList<Order> getAffectedOrders(int resourceID){
        ArrayList<Order> list = dbFacade.getAffectedOrders(resourceID);
        return list;
    }
}


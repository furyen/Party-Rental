
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
        dbFacade = DBFacade.getInstance();
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
     * Nicklas
     */
    public void getConnection() {
        dbFacade.getConnection();
    }
    
    /*
     * Gets all available resources from the start date
     * to the end date inputted.
     * Ends in the ResourceMapper
     * Daniel
     */
    public ArrayList getAvailableResources(Date startDate, Date endDate) {
        ArrayList<Resource> availableResources = new ArrayList();

        availableResources = dbFacade.getAvailableResources(startDate, endDate);
        
        return availableResources;
    }
    
    /*
     * Creates a new resource 
     * Ends in the ResourceMapper
     * Nicklas
     */
    public boolean createNewResource(String resourceName, int quantity, double price, int unitSize, boolean isTentPart) {
        boolean status = false;
        Resource resource = null;
        int resourceID = dbFacade.getUniqueResourceID();
        resource = new Resource(resourceID, resourceName, quantity, price, unitSize, isTentPart); //int resourceID, String resourceName,  int quantity,  double price, int unitSize

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
     * Nicklas
     */
    public Resource getResource(String name) {
        try {
            currentResource = dbFacade.getResource(name);
        } catch (SQLException ex) {
            System.out.println("Error in getResource " + ex);
            currentResource = null;
        }

        return currentResource;
    }

    /*
     * Takes in the values for a resource to edit the current
     * resource object. 
     * Ends in the ResourceMapper
     * Nicklas
     */
    public boolean editResource(String name, int quantitiy, double price, boolean active, boolean isTentPart) {
        boolean status = false;

        if (currentResource != null) {
            currentResource.setResourceName(name);
            currentResource.setQuantity(quantitiy);
            currentResource.setPrice(price);
            currentResource.setActive(active);
            currentResource.setTentPart(isTentPart);
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
     * Daniel
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
     * Nicklas
     */
    public ArrayList<Customer> getCustomerList(String firstName, String lastName) {
        ArrayList<Customer> customerList = new ArrayList();
        String firstNameUpper = firstName.toUpperCase();
        String lastNameUpper = lastName.toUpperCase();

        try {
            customerList = dbFacade.getCustomerList(firstNameUpper, lastNameUpper);
        } catch (SQLException ex) {
            System.out.println("Error in getCustomerList - " + ex);
        }

        return customerList;
    }
    
    /*
     * Gets a customer based on his customerID
     * Ends in the CustomerMapper
     * Nicklas
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
     * Nicklas
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

    /*
     * Petko
     */
    
    public double calculatePrice(LinkedHashMap<Resource, JComboBox> resourceList, double discount, LinkedHashMap<Truck, JToggleButton> truckDelivery, LinkedHashMap<Truck, JToggleButton> truckReturn) {
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
     * Nicklas
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
     * Nicklas
     */
    public boolean finishOrder() {
        boolean status = false;
        
        try {
            status = dbFacade.finishOrder();
            
            if (status == true){
                createDepositInvoiceFile(currentOrder);
            }
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
     * Nicklas
     */
    public boolean createOrder(int customerID, int unitSize, String address, Date startDate, Date endDate, double amountPaid) {
        boolean status = false;
        int orderID = dbFacade.getUniqueOrderID();
        Order newOrder = null;
        dbFacade.startNewBusinessTransaction();
        
        if(currentCustomer == null){
            getCustomer(customerID);
        }

        newOrder = new Order(customerID, orderID, unitSize, address, startDate, endDate, null, false, 0, 0, 0, 0);
        currentOrder = newOrder;
        status = dbFacade.createOrder(newOrder);


        return status;
    }
    
    /*
     * Creates a new OrderDetail and adds it to the currentOrder
     * It also adds it to the NewOrderDetailList in the UnitOfWorkProcessOrder
     * Ends in the UnitOfWorkProcessOrder
     * Nicklas
     */
    public void createOrderDetail(int resourceID, int quantity, String resourceName) {
        OrderDetail orderDetail = new OrderDetail(currentOrder.getOrderID(), resourceID, quantity);
        orderDetail.setRessourceName(resourceName);
        currentOrder.insertOrderDetail(orderDetail);
        dbFacade.createOrderDetail(orderDetail);
    }
    
    /*
     * Books a truck for an Order
     * It needs to be used for each truck which is booked for an order(if there are more trucks for an oreder)
     * The char is 0 for delivery and 1 for return
     * Ends in UnitOfWorkProcessOrder
     * Daniel
     */
    public void truckBooking(int truckID, int truckRun, char ch, int orderPartSize) {
        TruckOrder tr = new TruckOrder(truckID, truckRun, currentOrder.getOrderID(), ch, orderPartSize);
        
        dbFacade.truckBooking(tr);
    }
    
    /*
     * Gets a list of all orders
     * Ends in OrderMapper
     * Nicklas
     */
    public ArrayList<Order> getOrders() {
        ArrayList<Order> list = dbFacade.getOrders();
        return list;
    }
    
    /*
     * Gets a list of all orders for a specific customer
     * Ends in OrderMapper
     * Petko
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
     * Nicklas
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
                            status = status && (orderDetail.getQuantity() <= resource.getQuantity());
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
    
    /*
     * Timea
     */
    
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
     * Nicklas
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
     * Nicklas
     */
    public Order cancelEditOrder(Order order) {
        order.setCancelled(false);

        dbFacade.cancelOrder(order);

        return order;
    }
    
    /*
     * Deletes an order from the database
     * based on the orderID.
     * Ends in orderMapper.
     * Nicklas
     */
    
    public boolean deleteOrder(int orderID){
        boolean status = false;
        
        status = dbFacade.deleteOrder(orderID);
        
        return status;
    }

    /*
     * Creates a deposit invoice file for a specific order
     * Returns true if the file was created successfully.
     * Ends in the Controller.
     * Nicklas
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
     * Nicklas
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
     * Nicklas
     */
    public ArrayList<OrderDetail> getOrderDetail(Order o) {
        return o.getOrderDetails();
    }
    
    /*
     * Creates a new truck for use.
     * Ends in the TruckMapper.
     * Daniel
     */
    public boolean createTruck(int truckSize, Double unitPrice){
        boolean bool  = dbFacade.createTruck(truckSize, unitPrice);
        return bool;
    } 
    
    /*
     * Edits the unitprice of a truck identified by a given truckID
     * Ends in TruckMapper.
     * Daniel
     */
    public boolean editTruck(int truckID, double unitPrice){
        boolean bool = dbFacade.editTruck(truckID, unitPrice);
        return bool;
    }
    
    /*
     * Gets a list of all trucks
     * Ends in TruckMapper
     * Daniel
     */
    public ArrayList<Truck> getTrucks(){
        ArrayList<Truck> list = dbFacade.getTrucks();
        return list;
    }
    
    /*
     * Sets the boolean active of a resource to false
     * Making the resource unavailable
     * Ends in ResourceMapper
     * Nicklas
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
     * Nicklas
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
     * Daniel
     */
    public ArrayList<Order> getAffectedOrders(int resourceID){
        ArrayList<Order> list = dbFacade.getAffectedOrders(resourceID);
        return list;
    }
    
    /*
     * Deletes a truck from the database
     * Ends in TruckMapper
     * Daniel
     */
    
    public ArrayList<Order> deleteTruck(int truckID){
        ArrayList<Order> list = dbFacade.deleteTruck(truckID);
        return list;
    }
    
    /*
     * Gets all packages that can be used.
     * Ends in PackageMapper
     * Nicklas
     */
    
    public ArrayList<Package> getAllPackages(Date startD, Date endD){
        ArrayList<Package> packageList = new ArrayList();
        ArrayList<Resource> resourceList = getAvailableResources(startD, endD);
        ArrayList<Package> newPackageList = new ArrayList();
        boolean status = true;
        
        packageList = dbFacade.getAllPackages();
        
        for (Package packages : packageList){
            for(PackageDetail packageDetail : packages.getPackageDetailList()){
                for(Resource resource : resourceList){
                    if(packageDetail.getResourceID() == resource.getResourceID()){
                        if(packageDetail.getQuantity() <= resource.getQuantity()){
                            status = status && true;
                        }
                        else{
                            status = status && false;
                        }
                    }
                }
            }
            if(status == true){
                newPackageList.add(packages);
                System.out.println("added");
            }
            
        }
        
        return newPackageList;
    }
    
    /*
     * Creates a new event package
     * given the name and the discount for it.
     * Ends in the packageMapper
     * Nicklas
     */
    
    public boolean createNewPackage(String packageName, double discount){
        boolean status = false;
        String packageNameUpperCase = packageName.toUpperCase();
        
        if(getPackage(packageName) == null){
            status = dbFacade.createNewPackage(packageNameUpperCase, discount);
        }
        else{
            status = false;
        }
        
        
        return status;
    }
    
    /*
     * Getting a package based on the name
     * and returns the package object.
     * Nicklas
     */
    
    public Package getPackage(String name){
        Package newPackage = null;
        String nameUpperCase = name.toUpperCase();
        
        
        newPackage = dbFacade.getPackage(nameUpperCase);
        
        return newPackage;
    }
    
    
    /*
     * Deletes a package from the database.
     * Ends at the PackageMapper
     * Nicklas
     */
    
    public boolean deletePackage(String name){
        boolean status = false;
        String nameUpperCase = name.toUpperCase();
        
        status = dbFacade.deletePackage(nameUpperCase);
        
        return status;
    }
    
    /*
     * Creates a package detail to attach to a package
     * Ends in the PackageMapper
     * Nicklas
     */
    
    public boolean createPackageDetail(int resourceID, int quantity, String packageName){
        boolean status = false;
        Package currentPackage = getPackage(packageName);
        PackageDetail newPackageDetail = new PackageDetail(currentPackage.getPackageID(), resourceID, quantity);
        
        status = dbFacade.createPackageDetail(newPackageDetail);
        
        return status;
    }
    
    /*
     * This method gives all orders which
     * are unpaid or has less than the deposit paid 
     * based on the number of days since it was created.
     * Ends in the orderMapper
     * Daniel
     */
    
    public ArrayList<Order> getExpiringOrders(int days){
        ArrayList<Order> list = new ArrayList();
        list = dbFacade.getExpiringOrders(days);
        return list;
    } 
    
    /*
     * Cancels orders that have not been paid within
     * eight days of creation.
     * Ends in the orderMapper
     * Daniel
     */
    
    public boolean cancelUnpaidOrders(){
        boolean bool = dbFacade.cancelUnpaidOrders();
        return bool;
    } 
    
    /*
     * Gets a resource based on the name
     * and puts a pessimistic lock on it
     * Ends in resourceMapper.
     * Nicklas
     */
    
    public Resource getResourceWithLock(String name) {
        
        currentResource = dbFacade.getResourceWithLock(name);

        return currentResource;
    }
    
}


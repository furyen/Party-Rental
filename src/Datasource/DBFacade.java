/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author dekez
 */
public class DBFacade {

    private static DBFacade instance;
    private UnitOfWorkProcessOrder uow;
    private Connection connection;

    private DBFacade() {
    }

    public static DBFacade getInstance() {
        if (instance == null) {
            instance = new DBFacade();
        }

        return instance;
    }

    public int getUniqueResourceID() {
        ResourceMapper resourceMapper = new ResourceMapper();
        int uniqueID = 0;

        
            uniqueID = resourceMapper.getUniqueID(connection);
        

        return uniqueID;
    }

    public Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@delfi.lyngbyes.dk:1521:KNORD", "CLCOSV12E3", "CLCOSV12E3");
        } catch (Exception e) {
            System.out.println("Error in the DBFacade.getConnection()");
            System.out.println(e);
        }

        return connection;
    }

    private void releaseConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public boolean createNewResource(Resource resource) throws SQLException {
        ResourceMapper ressourceMapper = new ResourceMapper();
        boolean status = false;
        status = ressourceMapper.createNewResource(resource, connection);
        
        return status;
    }

    public Resource getResource(String name) throws SQLException {
        ResourceMapper resourceMapper = new ResourceMapper();
        Resource resource;
        resource = resourceMapper.getResource(name, connection);
        
        return resource;
    }

    public boolean editResource(Resource resource) throws SQLException {
        boolean status = false;
        ResourceMapper resourceMapper = new ResourceMapper();
        status = resourceMapper.editResource(resource, connection);
        
        return status;
    }

    public ArrayList<Resource> getAvailableResources(java.util.Date startDate, java.util.Date endDate) {
        ArrayList<Resource> list = null;
        list = new ResourceMapper().getAvailableResources(startDate, endDate, connection);

        return list;
    }

    public ArrayList getTruckDeliveryForDate(Date date, char ch) {
        ArrayList<Truck> listRuns = null;
        listRuns = new TruckMapper().getTruckDeliveryForDate(date, ch, connection);

        return listRuns;
    }

    public ArrayList<Customer> getCustomerList(String firstName, String lastName) throws SQLException {
        ArrayList<Customer> customerList;
        CustomerMapper customerMapper = new CustomerMapper();
        customerList = customerMapper.getCustomerList(firstName, lastName, connection);

        return customerList;

    }

    public boolean createCustomer(Customer newCustomer) {
        boolean status = false;
        if (uow != null) {
            uow.newCustomer(newCustomer);
            status = true;
        }

        return status;
    }

    public void startNewBusinessTransaction() {
        if (uow == null) {
            uow = new UnitOfWorkProcessOrder();
        }
        
    }

    public int getUniqueCustomerID() throws SQLException {
        CustomerMapper customerMapper = new CustomerMapper();
        int uniqueID = 0;
        uniqueID = customerMapper.getUniqueCustomerID(connection);

        return uniqueID;
    }

    public boolean finishOrder() throws Exception {
        boolean status = false;
        try {
            status = uow.commit(connection);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return status;
    }

    public boolean createNewInvoice(Order newInvoice) {
        boolean status = false;
        if (uow != null) {
            uow.createNewInvoice(newInvoice);
            status = true;
        }

        return status;
    }

    public int getUniqueOrderID() {
        int uniqueID;
        OrderMapper orderMapper = new OrderMapper();
        uniqueID = orderMapper.getUniqueOrderID(connection);

        return uniqueID;
    }

    public boolean createOrder(Order newOrder) {
        boolean status = false;
        if (uow != null) {
            uow.createNewOrder(newOrder);
            status = true;
        }

        return status;
    }

    public boolean createOrderDetail(OrderDetail orderDetail) {
        boolean status = false;
        if (uow != null) {
            uow.createOrderDetail(orderDetail);
            status = true;
        }

        return status;
    }

    public void truckBooking(TruckOrder tr) {
        if (uow != null) {
            uow.registerTruckBooking(tr);
        }

    }
     
    public ArrayList<Order> getOrders() {
        OrderMapper orderMapper = new OrderMapper();
        ArrayList<Order> list = orderMapper.getOrders(connection);
        return list;
    }


    public boolean cancelOrder(Order order) {
        boolean status = false;
        OrderMapper orderMapper = new OrderMapper();
        status = orderMapper.cancelOrder(order, connection);
        
        return status;
    }

    public Customer getCustomer(int customerID) {
        CustomerMapper customerMapper = new CustomerMapper();
        Customer customer = null;
        customer = customerMapper.getCustomer(customerID, connection);
        
        return customer;
    }

    public ArrayList<Order> getCustomerOrders(int customerID) {
        OrderMapper orderMapper = new OrderMapper();
        ArrayList<Order> orders = orderMapper.getCustomerOrders(connection,customerID);
        return orders;
    }


    public ArrayList<Order> deleteResource(int resourceID) {
        ArrayList<Order> list = null;
        ResourceMapper ressourceMapper = new ResourceMapper();
        list = ressourceMapper.deleteResource(resourceID, connection);
        return list;
    }

    public boolean savePayment(Order currentOrder) {
        boolean status = false;
        InvoiceMapper invoiceMapper = new InvoiceMapper();
        return status = invoiceMapper.savePayment(currentOrder, connection);
    }
    
    public boolean createTruck(int truckSize, double unitPrice){
        TruckMapper truckMapper = new TruckMapper();
        boolean bool = truckMapper.createTruck( truckSize, unitPrice, connection);
        return bool;
    } 

    public boolean editTruck(int truckID, double unitPrice) {
        TruckMapper truckMapper = new TruckMapper();
        boolean bool = truckMapper.editTruck(truckID, unitPrice ,connection);
        return bool;
    }

    public ArrayList<Truck> getTrucks() {
        TruckMapper truckMapper = new TruckMapper();
        ArrayList<Truck> list = truckMapper.getTrucks(connection);
        return list;
    }

    public boolean deactiveResource(int resourceID) {
        boolean status = false;
        ResourceMapper resourceMapper = new ResourceMapper();
        status = resourceMapper.deactivateOrder(resourceID, connection);
        
        return status;
    }

    public boolean reactivateResource(String resourceName) {
        boolean status = false;
        ResourceMapper resourceMapper = new ResourceMapper();
        status = resourceMapper.reactivateResource(resourceName, connection);
        
        return status;
    }

    public ArrayList<Order> getAffectedOrders(int resourceID) {
        OrderMapper orderMapper = new OrderMapper();
        ArrayList<Order> list = orderMapper.getAffectedOrders(resourceID,connection);
        return list;
    }

    public ArrayList<Order> deleteTruck(int truckID) {
        TruckMapper truckMapper = new TruckMapper();
        ArrayList<Order> list = truckMapper.deleteTruck(truckID , connection);
        return list;
    }

    public ArrayList<Domain.Package> getAllPackages() {
        ArrayList<Domain.Package> packageList = new ArrayList();
        PackageMapper packageMapper = new PackageMapper();
        
        packageList = packageMapper.getAllPackages(connection);
        
        return packageList;
    }

    public boolean createNewPackage(String packageName, double discount) {
        boolean status = false;
        PackageMapper packageMapper = new PackageMapper();
        
        status = packageMapper.createNewPackage(packageName, discount, connection);
        
        return status;
    }

    public Domain.Package getPackage(String name) {
        Domain.Package newPackage = null;
        PackageMapper packageMapper = new PackageMapper();
        
        newPackage = packageMapper.getPackage(name, connection);
        
        return newPackage;
    }

    public boolean deletePackage(String name) {
        boolean status = false;
        PackageMapper packageMapper = new PackageMapper();
        
        status = packageMapper.deletePackage(name, connection);
        
        return status;
    }

    public boolean createPackageDetail(PackageDetail newPackageDetail) {
        boolean status = false;
        PackageMapper packageMapper = new PackageMapper();
        
        status = packageMapper.createNewPackageDetail(newPackageDetail, connection);
        
        return status;        
    }
    
    public ArrayList<Order> getExpiringOrders(int days) {
        OrderMapper orderMapper = new OrderMapper();
        ArrayList<Order> list = new ArrayList();
        list = orderMapper.getExpiringOrders(days,connection);
        return list;
    }

    public boolean deleteOrder(int orderID) {
        boolean status = false;
        OrderMapper orderMapper = new OrderMapper();
        
        status = orderMapper.deleteOrder(orderID, connection);
        
        return status;
    }

    public boolean cancelUnpaidOrders() {
        OrderMapper orderMapper = new OrderMapper();
        boolean bool = orderMapper.cancelUnpaidOrders(connection);
        return bool;
    }

    public Resource getResourceWithLock(String name) {
        ResourceMapper resourceMapper = new ResourceMapper();
        Resource resource;
        resource = resourceMapper.getResourceWithLock(name, connection);
        
        return resource;
    }

}

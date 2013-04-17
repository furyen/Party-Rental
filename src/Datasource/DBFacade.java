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
    
    
    private DBFacade(){
        
    }
    
    public static DBFacade getIntance(){
        if(instance == null){
            instance = new DBFacade();
        }
        
        return instance;
    }
    
    public int getUniqueResourceID(){
        RessourceMapper resourceMapper = new RessourceMapper();
        int uniqueID = 0;
        
        if(resourceMapper.getUniqueID(connection) != 0){
            uniqueID = resourceMapper.getUniqueID(connection);
        }
        
        return uniqueID;
    }
    
    public Connection getConnection(){
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@delfi.lyngbyes.dk:1521:KNORD", "CLCOSV12E3", "CLCOSV12E3");
        }
        catch (Exception e){
            System.out.println("Error in the DBFacade.getConnection()");
            System.out.println(e);
        }
        
        return connection;
    }
    
    private void releaseConnection(){
        try{
            connection.close();
        }
        catch(Exception e){
            System.err.println(e);
        }
    }

    public boolean createNewResource(Resource resource) throws SQLException {
        RessourceMapper ressourceMapper = new RessourceMapper();    
        boolean status = false;
        
        status = ressourceMapper.createNewResource(resource, connection);
        
        return status;
    }
     public Resource getResource(String name) throws SQLException{
        RessourceMapper resourceMapper = new RessourceMapper();
        Resource resource;
        
        resource = resourceMapper.getResource(name, connection);
        
        return resource;
    }
       public boolean editResource(Resource resource) throws SQLException{
        boolean status = false;
        RessourceMapper resourceMapper = new RessourceMapper();
        
        status = resourceMapper.editResource(resource, connection);
        
        return status;
        
    }
    
    public ArrayList<Resource> getAvailableResources(java.util.Date startDate, java.util.Date endDate){
        ArrayList<Resource> list = null;
        
        list = new RessourceMapper().getAvailableResources(startDate,endDate,connection);
        
        return list;
    }
    
     public ArrayList getTruckDeliveryForDate (Date date,char ch){
        ArrayList<Truck> listRuns = null;
        listRuns = new TruckMapper().getTruckDeliveryForDate(date,ch,connection);
        
        return listRuns;
    }
      public ArrayList<Customer> getCustomerList(String firstName, String lastName) throws SQLException{
        ArrayList<Customer> customerList;
        CustomerMapper customerMapper = new CustomerMapper();
        
        customerList = customerMapper.getCustomerList(firstName, lastName, connection);
        
        return customerList;
        
    }

    public boolean createCustomer(Customer newCustomer) {
        boolean status = false;
        
        if(uow != null){
            uow.newCustomer(newCustomer);
            status = true;
        }
         
        return status;
    }
    
    public void startNewBusinessTransaction(){
        if(uow == null){
            uow = new UnitOfWorkProcessOrder();
        }
        
    }

    public int getUniqueCustomerID() throws SQLException{
        CustomerMapper customerMapper = new CustomerMapper();
        int uniqueID = 0;
        
        uniqueID = customerMapper.getUniqueCustomerID(connection);
        
        return uniqueID;
    }

    public boolean finishOrder() throws Exception{
        boolean status = false;
        try{
            status = uow.commit(connection);
        }
        catch(Exception ex){
            System.out.println(ex);
        }
        
        return status;
    }
    public boolean createNewInvoice(Order newInvoice) {
        boolean status = false;
        
        if(uow != null){
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
        
        if(uow != null){
            uow.createNewOrder(newOrder);
            status = true;
        }
        
        return status;
    }

    public boolean createOrderDetail(OrderDetail orderDetail) {
        boolean status = false;
        
        if(uow != null){
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
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.*;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author dekez
 */
public class UnitOfWorkProcessOrder {
    private ArrayList<Customer> newCustomerList = new ArrayList();
    private ArrayList<Order> newInvoiceList = new ArrayList();
    private ArrayList<Order> newOrderList = new ArrayList();
    private ArrayList<OrderDetail> newOrderDetailList = new ArrayList();
    private ArrayList<TruckOrder> newTruckBooking = new ArrayList();
    
    private ArrayList<Order> dirtyOrderList = new ArrayList();
    
    
    public void newCustomer(Customer newCustomer){
        if(!newCustomerList.contains(newCustomer)){
            newCustomerList.add(newCustomer);
        }
    }
    
    public boolean commit(Connection connection){
        CustomerMapper customerMapper = new CustomerMapper();
        InvoiceMapper invoiceMapper = new InvoiceMapper();
        OrderMapper orderMapper = new OrderMapper();
        TruckMapper truckMapper = new TruckMapper();
        boolean status = true;
        
        try{
            connection.setAutoCommit(false);
            if(newCustomerList.size() != 0){
                status = status && customerMapper.createNewCustomer(newCustomerList, connection);
            }
            status = status && orderMapper.createNewOrder(newOrderList, connection);
            status = status && invoiceMapper.createNewInvoice(newInvoiceList, connection);
            status = status && orderMapper.createNewOrderDetail(newOrderDetailList, connection);
            for (int i=0; i< newTruckBooking.size(); i++){
                status = status && truckMapper.truckBooking(newTruckBooking.get(i), connection);
            }
            
            if(!status){
                System.out.println("Business order failed");
            }
            
            connection.commit();
            connection.setAutoCommit(true);
        }
        catch(SQLException ex){
            try{
                connection.rollback();
            }
            catch(SQLException e){
                System.out.println("Error in the commit() - " + ex);
            }
            
            System.out.println("Error in the commit() - " + ex);
            
            status = false;
        }
        
        return status;
    }

    public void createNewInvoice(Order newInvoice) {
        if(!newInvoiceList.contains(newInvoice)){
            newInvoiceList.add(newInvoice);
        }
    }

    public void createNewOrder(Order newOrder) {
        if(!newOrderList.contains(newOrder)){
            newOrderList.add(newOrder);
            
        }
    }

    public void createOrderDetail(OrderDetail newOrderDetail) {
        if(!newOrderDetailList.contains(newOrderDetail)){
            newOrderDetailList.add(newOrderDetail);
        }
    }
    
    

    public void registerTruckBooking(TruckOrder tr) {
        newTruckBooking.add(tr);
    }

    public Customer getNewCustomer(int customerID) {
        Customer customer = newCustomerList.get(0);
        
        return customer;
    }
}

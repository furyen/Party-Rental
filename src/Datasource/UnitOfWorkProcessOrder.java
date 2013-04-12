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
    private ArrayList<Invoice> newInvoiceList = new ArrayList();
    private ArrayList<Order> newOrderList = new ArrayList();
    private ArrayList<OrderDetail> newOrderDetailList = new ArrayList();
    private ArrayList<TruckOrder> newTruckBooking = new ArrayList();
    
    
    public void newCustomer(Customer newCustomer){
        if(!newCustomerList.contains(newCustomer)){
            newCustomerList.add(newCustomer);
        }
    }
    
    public boolean commit(Connection connection){
        System.out.println("reached commit");
        CustomerMapper customerMapper = new CustomerMapper();
        InvoiceMapper invoiceMapper = new InvoiceMapper();
        OrderMapper orderMapper = new OrderMapper();
        TruckMapper truckMapper = new TruckMapper();
        boolean status = true;
        System.out.println("Reached the commit");
        
        try{
            connection.setAutoCommit(false);
            System.out.println("Set auto commit false");
            if(newCustomerList.size() != 0){
                status = status && customerMapper.createNewCustomer(newCustomerList, connection);
                System.out.println(newCustomerList.toString());
            }
            status = status && invoiceMapper.createNewInvoice(newInvoiceList, connection);
            System.out.println(newOrderList.toString());
            status = status && orderMapper.createNewOrder(newOrderList, connection);
            System.out.println("le");
            status = status && orderMapper.createNewOrderDetail(newOrderDetailList, connection);
            System.out.println("ldsadsa");
            for (int i=0; i< newTruckBooking.size(); i++){
                status = status && truckMapper.truckBooking(newTruckBooking.get(i), connection);
            }
            
            if(!status){
                System.out.println("Business order failed");
            }
            
            connection.commit();
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

    public void createNewInvoice(Invoice newInvoice) {
        if(!newInvoiceList.contains(newInvoice)){
            newInvoiceList.add(newInvoice);
        }
    }

    public void createNewOrder(Order newOrder) {
        if(!newOrderList.contains(newOrder)){
            newOrderList.add(newOrder);
            System.out.println(newOrderList.toString());
            
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
}

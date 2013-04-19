/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.Order;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author dekez
 */
public class InvoiceMapper {

    public boolean createNewInvoice(ArrayList<Order> newInvoiceList, Connection connection) {
        boolean status = false;
        int rowsInserted = 0;
        String SQLString = "insert into invoice values(?,?,?,?,?)";
        PreparedStatement statement = null;
        System.out.println(newInvoiceList.toString());
        System.out.println("Reached createNewInvoice - InvoiceMapper Level - " + newInvoiceList.toString());
        
        try{
            statement = connection.prepareStatement(SQLString);
            for(Order invoice : newInvoiceList){
                statement.setInt(1, invoice.getOrderID());
                statement.setDouble(2, invoice.getDiscount());
                statement.setDouble(3, invoice.getFullPrice());
                statement.setDouble(4, 0);
                statement.setDouble(5, 0);
                rowsInserted += statement.executeUpdate();
            }
            
            if(rowsInserted == newInvoiceList.size()){
                status = true;
            }
            
        }
        catch(SQLException ex){
            System.out.println("Error in InvoiceMapper - " + ex);
        }

        return status;
    }
    
        public boolean savePayment(Order currentOrder, Connection connection) {
        boolean status = false;
        int rowsInserted = 0;
        String SQLString = "update invoice set paid_amount = ? where order_id = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQLString);
            statement.setDouble(1, currentOrder.getPaidAmount());
            statement.setInt(2, currentOrder.getOrderID());
            rowsInserted += statement.executeUpdate();
            if (rowsInserted > 0)
                status = true;
            connection.commit();
        } catch (SQLException ex)
                { 
                    System.err.println("Error in InvoiceMapper - " + ex);
                }
        
        return status;
    }
    
}

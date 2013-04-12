/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.Invoice;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author dekez
 */
public class InvoiceMapper {

    public boolean createNewInvoice(ArrayList<Invoice> newInvoiceList, Connection connection) {
        boolean status = false;
        int rowsInserted = 0;
        String SQLString = "insert into invoice values(?,?,?)";
        PreparedStatement statement = null;
        
        try{
            statement = connection.prepareStatement(SQLString);
            for(Invoice invoice : newInvoiceList){
                statement.setInt(1, invoice.getOrderID());
                statement.setDouble(2, invoice.getDiscount());
                statement.setDouble(3, invoice.getFinalPrice());
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
    
}

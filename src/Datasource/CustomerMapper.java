/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.Customer;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author dekez
 */
public class CustomerMapper {

    public ArrayList<Customer> getCustomerList(String firstName, String lastName, Connection connection) throws SQLException {
        ArrayList<Customer> customerList = new ArrayList();;
        String SQLString = "select * from customer where first_name like ? and last_name like ?";
        PreparedStatement statement = null;
        
        statement = connection.prepareStatement(SQLString);
        statement.setString(1, firstName + "%");
        statement.setString(2, lastName + "%");
        ResultSet rs = statement.executeQuery();
        
        while(rs.next()){
            int customerID = rs.getInt(1);
            String custFirstName = rs.getString(2);
            String custLastName = rs.getString(3);
            String adress = rs.getString(4);
            customerList.add(new Customer(customerID, custFirstName, custLastName, adress));
        }
        
        return customerList;
    }

    public int getUniqueCustomerID(Connection connection){
        int uniqueID = 0;
        System.out.println("muie");
        String SQLString = "select SEQ_CUSTOMER.nextval from dual";
        PreparedStatement statement = null;
        try{
             statement = connection.prepareStatement(SQLString);
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                uniqueID = rs.getInt(1);
            }
        }
        catch(SQLException ex){
            System.out.println("Error in the getUniqueCustomerID() in CustomerMapper - " + ex);
        }
       
        
        return uniqueID;
    }

    public boolean createNewCustomer(ArrayList<Customer> newCustomerList, Connection connection) {
        boolean status = false;
        int rowsInserted = 0;
        String SQLString = "insert into customer values(?,?,?,?)";
        PreparedStatement statement = null;
        
        
        
        try{
            statement = connection.prepareStatement(SQLString);
            statement.setInt(1, newCustomerList.get(0).getCustomerID());
            statement.setString(2, newCustomerList.get(0).getFirstName());
            statement.setString(3, newCustomerList.get(0).getLastName());
            statement.setString(4, newCustomerList.get(0).getAdress());
            rowsInserted = statement.executeUpdate();
            
            if(rowsInserted == 1){
                status = true;
            }
            
        }
        catch(SQLException ex){
            System.out.println("Error in the CustomerMapper - " + ex);
        }
        
        return status;
        
    }

    public Customer getCustomer(int customerID, Connection connection) {
        Customer customer = null;
        String SQLString = "select * from customer where customer_id=?";
        PreparedStatement statement = null;
        
        try{
            statement = connection.prepareStatement(SQLString);
            statement.setInt(1, customerID);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                customer = new Customer(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
            
        }
        catch(SQLException ex){
            System.out.println("Error in the getCustomer in CustomerMapper - " + ex);
        }
        
        return customer;
        
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.Order;
import Domain.OrderDetail;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author dekez
 */
public class OrderMapper {

    public int getUniqueOrderID(Connection connection) {
        int uniqueID = 0;
        String SQLString = "select SEQ_ORDER.nextval from dual";
        PreparedStatement statement = null;
        
        try{
            statement = connection.prepareStatement(SQLString);
            ResultSet rs = statement.executeQuery();
            
            if(rs.next()){
                uniqueID = rs.getInt(1);
            }
        }
        catch(SQLException ex){
            System.out.println("Error in OrderMapper - " + ex);
        }
        
        return uniqueID;
    }

    public boolean createNewOrder(ArrayList<Order> newOrderList, Connection connection) {
        boolean status = false;
        int rowsInserted = 0;
        String SQLString = "insert into orders values(?,?,?,?,?,?,?)";
        PreparedStatement statement = null;
        java.sql.Date startSQL = new java.sql.Date(newOrderList.get(0).getStartDate().getTime());
        java.sql.Date endSQL = new java.sql.Date(newOrderList.get(0).getEndDate().getTime());
        
        
        try{
            statement = connection.prepareStatement(SQLString);
            statement.setInt(1, newOrderList.get(0).getOrderID());
            statement.setInt(2, newOrderList.get(0).getCustomerID());
            statement.setDate(3, startSQL);
            statement.setDate(4, endSQL);
            statement.setString(5, newOrderList.get(0).getAdress());
            statement.setString(6, "Y");
            statement.setInt(7, newOrderList.get(0).getUnitSize());
            rowsInserted = statement.executeUpdate();
            
            if(rowsInserted == 1){
                status = true;
            }
        }
        catch(SQLException ex){
            System.out.println("Problem in the orderMapper - " + ex);
        }
        
        return status;
    }

    boolean createNewOrderDetail(ArrayList<OrderDetail> newOrderDetailList, Connection connection) {
        boolean status = false;
        int rowsInserted = 0;
        String SQLString = "insert into order_detail values (?,?,?)";
        PreparedStatement statement = null;
        
        try{
            statement = connection.prepareStatement(SQLString);
            for(OrderDetail orderDetail : newOrderDetailList){
                statement.setInt(1, orderDetail.getOrderID());
                statement.setInt(2, orderDetail.getResourceID());
                statement.setInt(3, orderDetail.getQuantity());
                rowsInserted += statement.executeUpdate();
            }
            
            if(rowsInserted == newOrderDetailList.size()){
                status = true;
            }
        }
        catch(SQLException ex){
            System.out.println("Error in the OrderMapper - " + ex);
        }
        
        return status;
    }

    public ArrayList<Order> getOrders(Connection connection) {
        ArrayList<Order> orderList = new ArrayList();
        String SQLString1 = " select * "
                            + " FROM orders natural join invoice ";
        PreparedStatement statement = null;
        System.out.println("muita");
        try{
            System.out.println("muita");
            statement = connection.prepareStatement(SQLString1);
            ResultSet rs = statement.executeQuery();
            System.out.println("muita");
            while (rs.next()){
                System.out.println("muie");
                int orderID = rs.getInt(1);
                int customerID = rs.getInt(2);
                java.util.Date startDate = new java.util.Date(rs.getDate(3).getTime());
                java.util.Date endDate = new java.util.Date(rs.getDate(4).getTime());
                String eventAddress = rs.getString(5);
                byte paymantState = rs.getByte(6);
                int unitSize = rs.getInt(7);
                char can = rs.getString(8).charAt(0);
                boolean canceled;
                if (can == 'Y') {
                    canceled = true;
                }
                else{
                    canceled = false;
                }
                double discount = rs.getDouble(9);
                double fulllPrice = rs.getDouble(10);
                double additionalCosts = rs.getDouble(11);
                Order order = new Order(customerID, orderID, unitSize, eventAddress, startDate, endDate, canceled, fulllPrice, discount, additionalCosts, paymantState);
                
//                                        rs.getInt(3),
//                                        rs.getString(4),
//                                        rs.getDate(5),
//                                        rs.getDate(6),
//                                        rs.getInt(7),
//                                        rs.getDouble(8),
//                                        rs.getDouble(9)
//                                        );
                orderList.add(order);
            }
            
            
        }catch(Exception ex){
            System.out.println("Error in the OrderMapper - getOrders");
            System.out.println(ex);
        } 
        
        return orderList;
    }

    boolean cancelOrder(Order order, Connection connection) {
        boolean status = false;
        String SQLString = "UPDATE orders"
                         + "SET canceled=?"
                         + "where order_id=?";
        PreparedStatement statement = null;
        int rowsUpdated = 0;
        
        
        try{
            statement = connection.prepareStatement(SQLString);
            if(order.isCancelled() == true){
                statement.setString(1, "y");
            }
            else{
                return status;
            }
            statement.setInt(2, order.getOrderID());
            rowsUpdated = statement.executeUpdate();
            
            if(rowsUpdated == 1){
                status = true;
            }
        }
        catch(SQLException ex){
            System.out.println("Error in the OrderMapper(CancelOrder) - " + ex);
        }
        
        return status;
    }
    
}

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

        try {
            statement = connection.prepareStatement(SQLString);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                uniqueID = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderMapper (getUniqueOrderID) - " + ex);
        }

        return uniqueID;
    }

    public boolean createNewOrder(ArrayList<Order> newOrderList, Connection connection) {
        boolean status = false;
        int rowsInserted = 0;
        String SQLString = "insert into orders values(?,?,?,?,current_date-1 ,?,?,?)";
        PreparedStatement statement = null;
        java.sql.Date startSQL = new java.sql.Date(newOrderList.get(0).getStartDate().getTime());
        java.sql.Date endSQL = new java.sql.Date(newOrderList.get(0).getEndDate().getTime());

        try {
            statement = connection.prepareStatement(SQLString);
            System.out.println(newOrderList.get(0).getOrderID());
            statement.setInt(1, newOrderList.get(0).getOrderID());
            statement.setInt(2, newOrderList.get(0).getCustomerID());
            statement.setDate(3, startSQL);
            statement.setDate(4, endSQL);
            statement.setString(5, newOrderList.get(0).getAdress());
            statement.setInt(6, newOrderList.get(0).getUnitSize());
            statement.setString(7, "N");
            rowsInserted = statement.executeUpdate();
            if (rowsInserted == 1) {
                status = true;
            }
        } catch (SQLException ex) {
            System.out.println("Problem in the orderMapper (createNewOrder)- " + ex);
        }

        return status;
    }

    boolean createNewOrderDetail(ArrayList<OrderDetail> newOrderDetailList, Connection connection) {
        boolean status = false;
        int rowsInserted = 0;
        String SQLString = "insert into order_detail values (?,?,?)";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(SQLString);
            for (OrderDetail orderDetail : newOrderDetailList) {
                System.out.println(orderDetail.getOrderID());
                statement.setInt(1, orderDetail.getOrderID());
                statement.setInt(2, orderDetail.getResourceID());
                statement.setInt(3, orderDetail.getQuantity());
                rowsInserted += statement.executeUpdate();
            }
            if (rowsInserted == newOrderDetailList.size()) {
                status = true;
            }
        } catch (SQLException ex) {
            System.out.println("Error in the OrderMapper (createNewOrderDetail) - " + ex);
        }

        return status;
    }

    public ArrayList<Order> getOrders(Connection connection) {
        ArrayList<Order> orderList = new ArrayList();
        String SQLString1 = " select * "
                + " FROM orders natural join invoice "
                + " order by order_id";
        String SQLString2 = " select order_detail.order_id, order_detail.ressource_id, order_detail.quantity, ressource.ressource_name  "
                + " from order_detail,ressource  "
                + " where order_detail.ressource_id = ressource.ressource_id";
        String SQLString3 = " select unique order_id "
                          + " from truck_delivery"
                          + " order by order_id";
        String SQLString4 = " select unique order_id "
                          + " from truck_return"
                          + " order by order_id";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQLString1);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt(1);
                int customerID = rs.getInt(2);
                java.util.Date startDate = new java.util.Date(rs.getDate(3).getTime());
                java.util.Date endDate = new java.util.Date(rs.getDate(4).getTime());
                java.util.Date dateCreating = new java.util.Date(rs.getDate(5).getTime());
                String eventAddress = rs.getString(6);
                int unitSize = rs.getInt(7);
                char can = rs.getString(8).charAt(0);
                boolean canceled;
                if (can == 'Y') {
                    canceled = true;
                } else {
                    canceled = false;
                }
                double discount = rs.getDouble(9);
                double fulllPrice = rs.getDouble(10);
                double additionalCosts = rs.getDouble(11);
                double paidAmount = rs.getDouble(12);
                Order order = new Order(customerID, orderID, unitSize, eventAddress, startDate, endDate, dateCreating, canceled, fulllPrice, discount, additionalCosts, paidAmount);
                orderList.add(order);
            }
            statement = connection.prepareStatement(SQLString2);
            rs = statement.executeQuery();
            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3));
                orderDetail.setRessourceName(rs.getString(4));
                int orderID = orderDetail.getOrderID();
                int counter = 0;
                boolean found = false;
                while (!found && counter < orderList.size()) {
                    Order order = orderList.get(counter);
                    if (orderID == order.getOrderID()) {
                        order.insertOrderDetail(orderDetail);
                        found = true;
                    } else {
                        counter++;
                    }
                }
            }
            statement = connection.prepareStatement(SQLString3);
            rs = statement.executeQuery();
            ArrayList<Integer> list = new ArrayList();
            while (rs.next()) {
                 list.add(rs.getInt(1));
            }
            int k=0;
            for (int i = 0; i<list.size(); i++){
                while (orderList.get(k).getOrderID()< list.get(i)){
                    k++;
                }
                orderList.get(k).setTruckDelivery(true);
            }
            statement = connection.prepareStatement(SQLString4);
            rs = statement.executeQuery();
            list = new ArrayList();
            while (rs.next()) {
                 list.add(rs.getInt(1));
            }
            k=0;
            for (int i = 0; i<list.size(); i++){
                while (orderList.get(k).getOrderID()< list.get(i)){
                    k++;
                }
                orderList.get(k).setTruckReturn(true);
            }
        } catch (Exception ex) {
            System.out.println("Error in the OrderMapper - getOrders");
            System.out.println(ex);
        }

        return orderList;
    }

    boolean cancelOrder(Order order, Connection connection) {
        boolean status = false;
        String SQLString = "update orders "
                        + " set canceled = 'Y' "
                        + " where order_id = ? ";
        PreparedStatement statement = null;
        int rowsUpdated = 0;
        try {
            statement = connection.prepareStatement(SQLString);
            statement.setInt(1, order.getOrderID());
            rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 1) {
                status = true;
            }
            connection.commit();
        } catch (SQLException ex) {
            System.out.println("Error in the OrderMapper(CancelOrder) - " + ex);
        }

        return status;
    }

    public ArrayList<Order> getCustomerOrders(Connection connection, int customerID) {
        ArrayList<Order> orderList = new ArrayList();
        String SQLString1 = " select * "
                + " FROM orders natural join invoice "
                + " where customer_id=?";
        String SQLString2 = " select order_detail.order_id, order_detail.ressource_id, order_detail.quantity, ressource.ressource_name  "
                + " from order_detail,ressource  "
                + " where order_detail.ressource_id = ressource.ressource_id";
        PreparedStatement statement = null;
        
        try {
            statement = connection.prepareStatement(SQLString1);
            statement.setInt(1, customerID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt(1);
                rs.getInt(2);
                java.util.Date startDate = new java.util.Date(rs.getDate(3).getTime());
                java.util.Date endDate = new java.util.Date(rs.getDate(4).getTime());
                java.util.Date dateCreating = new java.util.Date(rs.getDate(5).getTime());
                String eventAddress = rs.getString(6);
                int unitSize = rs.getInt(7);
                char can = rs.getString(8).charAt(0);
                boolean canceled;
                if (can == 'Y') {
                    canceled = true;
                } else {
                    canceled = false;
                }
                double discount = rs.getDouble(9);
                double fulllPrice = rs.getDouble(10);
                double additionalCosts = rs.getDouble(11);
                double paidAmount = rs.getDouble(12);
                Order order = new Order(customerID, orderID, unitSize, eventAddress, startDate, endDate, dateCreating, canceled, fulllPrice, discount, additionalCosts, paidAmount);
                orderList.add(order);
            }
            
            statement = connection.prepareStatement(SQLString2);
            rs = statement.executeQuery();
            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3));
                orderDetail.setRessourceName(rs.getString(4));
                int orderID = orderDetail.getOrderID();
                int counter = 0;
                boolean found = false;
                while (!found && counter < orderList.size()) {
                    Order order = orderList.get(counter);
                    if (orderID == order.getOrderID()) {
                        order.insertOrderDetail(orderDetail);
                        found = true;
                    } else {
                        counter++;

                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error in the OrderMapper - getCustomerOrders");
            System.out.println(ex);
        }

        return orderList;
    }

    ArrayList<Order> getAffectedOrders(int resourceID, Connection connection) {
        ArrayList<Order> orderList = new ArrayList();
        String SQLString = " select * "
                + " FROM orders natural join invoice natural join order_detail"
                + " where ressource_id=?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQLString);
            statement.setInt(1, resourceID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt(1);
                int customerID = rs.getInt(2);
                java.util.Date startDate = new java.util.Date(rs.getDate(3).getTime());
                java.util.Date endDate = new java.util.Date(rs.getDate(4).getTime());
                java.util.Date dateCreating = new java.util.Date(rs.getDate(5).getTime());
                String eventAddress = rs.getString(6);
                int unitSize = rs.getInt(7);
                char can = rs.getString(8).charAt(0);
                boolean canceled;
                if (can == 'Y') {
                    canceled = true;
                } else {
                    canceled = false;
                }
                double discount = rs.getDouble(9);
                double fulllPrice = rs.getDouble(10);
                double additionalCosts = rs.getDouble(11);
                double paidAmount = rs.getDouble(12);
                Order order = new Order(customerID, orderID, unitSize, eventAddress, startDate, endDate, dateCreating, canceled, fulllPrice, discount, additionalCosts, paidAmount);
                if (!canceled){
                    orderList.add(order);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error in the OrderMapper - getAffectedOrders");
            System.out.println(ex);
        }
        return orderList;
    }

    ArrayList<Order> getExpiringOrders(int days,Connection connection) {
        ArrayList<Order> orderList = new ArrayList();
        String SQLString1 = " select * "
                + " FROM orders natural join invoice"
                + " where TO_CHAR(date_created + ?, 'DD-MON-YYYY') = TO_CHAR(SYSDATE, 'DD-MON-YYYY')";
        String SQLString2 = " select * "
                + " from order_detail "
                + " where order_id in (? ";
        PreparedStatement statement = null;
        try{
            statement = connection.prepareStatement(SQLString1);
            statement.setInt(1, days);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt(1);
                int customerID = rs.getInt(2);
                java.util.Date startDate = new java.util.Date(rs.getDate(3).getTime());
                java.util.Date endDate = new java.util.Date(rs.getDate(4).getTime());
                java.util.Date dateCreating = new java.util.Date(rs.getDate(5).getTime());
                String eventAddress = rs.getString(6);
                int unitSize = rs.getInt(7);
                char can = rs.getString(8).charAt(0);
                boolean canceled;
                if (can == 'Y') {
                    canceled = true;
                } else {
                    canceled = false;
                }
                double discount = rs.getDouble(9);
                double fulllPrice = rs.getDouble(10);
                double additionalCosts = rs.getDouble(11);
                double paidAmount = rs.getDouble(12);
                Order order = new Order(customerID, orderID, unitSize, eventAddress, startDate, endDate, dateCreating, canceled, fulllPrice, discount, additionalCosts, paidAmount);
                orderList.add(order);
            }
            if (orderList.size() != 0) {
                for(int i = 0; i<orderList.size()-1; i++){
                    SQLString2 = SQLString2.concat(",?");
                }
                SQLString2 = SQLString2.concat(") ");
                SQLString2 = SQLString2.concat(" order by order_id");
                statement = connection.prepareStatement(SQLString2);
                for(int i = 0; i<orderList.size(); i++){
                    statement.setInt(i+1, orderList.get(i).getOrderID());
                }
                rs = statement.executeQuery();
                int count = 0;
                while (rs.next()){
                    OrderDetail orderDetail = new OrderDetail(rs.getInt(1),
                                                              rs.getInt(2),
                                                              rs.getInt(3));
                    while (orderDetail.getOrderID()>orderList.get(count).getOrderID()){
                        count++;
                    }
                    orderList.get(count).insertOrderDetail(orderDetail);
                }
            }
        }catch (Exception ex) {
            System.out.println("Error in the OrderMapper - getExpiringOrders");
            System.out.println(ex);
        } 
        
        return orderList;
    }

    boolean deleteOrder(Order order, Connection connection) {
        boolean status = false;
        String SQLString = "delete from orders where order_id=?";
        PreparedStatement statement = null;
        int rowsDeleted = 0;
        
        try{
            statement = connection.prepareStatement(SQLString);
            statement.setInt(1, order.getOrderID());
            rowsDeleted = statement.executeUpdate();
            
            if (rowsDeleted == 1){
                status = true;
                connection.commit();
            }
        }
        catch(SQLException ex){
            System.out.println("Error in deleteOrder() in OrderMapper - " + ex);
        }
        
        return status;
    }
}

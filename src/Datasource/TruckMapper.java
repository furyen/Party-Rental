/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.Order;
import Domain.Resource;
import Domain.Truck;
import Domain.TruckOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author dekez
 */
public class TruckMapper {

    public ArrayList<Truck> getTruckDeliveryForDate(Date date,char ch,Connection con) {
        ArrayList<Truck> listRuns = new ArrayList();
        ArrayList<Truck> auxiliarList = new ArrayList();
        String SQLString1 = "select truck_id, truck_run, order_partial_size, order_id, canceled";
        if (ch == '0'){
            SQLString1 +=" from truck_delivery natural join orders";
            SQLString1 += " where orders.start_date = ?";
        }
        else {
            SQLString1 += " from truck_return natural join orders";
            SQLString1 += " where orders.end_date = ?";
        }                   
        String SQLString2 = "select *"
                            + " from truck";
        PreparedStatement statement = null;
        
        try{
            statement = con.prepareStatement(SQLString1);
            java.sql.Date dateSQL = new java.sql.Date(date.getTime());
            statement.setDate(1, dateSQL);
            ResultSet rs = statement.executeQuery();
            Truck truck = null;
            while (rs.next()) {
                truck = new Truck(rs.getInt(1),
                                  rs.getInt(2),
                                  rs.getInt(3),
                                  0,0);
                rs.getInt(4);
                char canceled = rs.getString(4).charAt(0);
                if (canceled == 'N'){
                    auxiliarList.add(truck);
                } 
            }
            
            statement = con.prepareStatement(SQLString2);
            rs = statement.executeQuery();
            int id;
            int size;
            double unitPrice;
            while (rs.next()){
                id = rs.getInt(1);
                size = rs.getInt(2);
                unitPrice = rs.getDouble(3);
                for(int i = 0; i<3; i++){
                    truck = new Truck(id,i+1,0,size,unitPrice);
                    listRuns.add(truck);
                }
            }
            for (int i=0; i<auxiliarList.size(); i++){
                truck = auxiliarList.get(i);
                for (int j=0; j<listRuns.size(); j++){
                    Truck truck2 = listRuns.get(j);
                    if (truck.getTruckID() == truck2.getTruckID() && truck.getTruckRun() == truck2.getTruckRun()){
                        truck2.setFilledSpace(truck.getFilledSpace()+truck2.getFilledSpace());
                    } 
                }
            }
        }catch (Exception e) {
            System.out.println("Fail in TruckMapper - getTruckDeliveryForDate");
            System.out.println(e.getMessage());
        }   
        return listRuns;
    }
    
    public boolean truckBooking(TruckOrder tr, Connection con) throws SQLException {
        String SQLString = "insert into ";
        if (tr.getCh()  =='0'){
            SQLString += "truck_delivery";
        }
        else {
            SQLString += "truck_return";
        }
        SQLString += " values (?,?,?,?)";
        PreparedStatement statement = null; 
        int rowsInserted = 0;
        
        statement = con.prepareStatement(SQLString);
        statement.setInt(1, tr.getTruckID());
        statement.setInt(2, tr.getOrderID());
        statement.setInt(3, tr.getTruckRun());
        statement.setInt(4, tr.getOrderPartSize());
        rowsInserted = statement.executeUpdate();   
        return (rowsInserted == 1);
    }

    public boolean createTruck(int truckSize, double unitPrice, Connection connection) {
        String SQLScript = "insert into truck values (seq_truck.nextval,?,?)";
        PreparedStatement statement = null;
        int updatedRows = 0;
        try{
            statement = connection.prepareStatement(SQLScript);
            statement.setInt(1, truckSize);
            statement.setDouble(2, unitPrice);
            updatedRows = statement.executeUpdate();
            connection.commit();
        }catch(Exception e){
            System.out.println("Fail in TruckMapper - creatTruck");
            System.out.println(e.getMessage());
        }
        return updatedRows == 1;
    }

    boolean editTruck(int truckID, double unitPrice, Connection connection) {
        String SQLScript = " update truck "
                          + " SET unit_price = ? "
                          + " where truck_id = ? ";
        int updatedRows = 0;
        PreparedStatement statement = null;
        try{
            statement = connection.prepareStatement(SQLScript);
            statement.setDouble(1, unitPrice);
            statement.setInt(2, truckID);
            updatedRows = statement.executeUpdate();
            connection.commit();
        }catch(Exception e){
            System.out.println("Fail in TruckMapper - editTruck");
            System.out.println(e.getMessage());
        }
        return updatedRows == 1;
    }

    ArrayList<Truck> getTrucks(Connection connection) {
        String SQLScript = " select * "
                          + " from truck ";
        PreparedStatement statement = null;
        ArrayList<Truck> list = new ArrayList();
        try{
            statement = connection.prepareCall(SQLScript);
            ResultSet rs = statement.executeQuery();
            Truck truck;
            while (rs.next()){
                truck = new Truck(rs.getInt(1),
                                  0,0,
                                  rs.getInt(2),
                                  rs.getDouble(3)
                                  );
                list.add(truck);
            }
        }catch(Exception e){
            System.out.println("Fail in TruckMapper - getTrucks");
            System.out.println(e.getMessage());
        }
        return list;
    }

    ArrayList<Order> deleteTruck(int truckID, Connection connection) {
        ArrayList<Order> list = new ArrayList();
        
        return list;
    }
    
    
}


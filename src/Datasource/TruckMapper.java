/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

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
        String SQLString1 = "select truck_id, truck_run, order_partial_size, order_id"
                           + " from truck_delivery natural join orders";
        if (ch == '0'){
            SQLString1 += " where orders.start_date = ?";
        }
        else {
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
                                  0);
                rs.getInt(4);
                auxiliarList.add(truck);
            }
            statement = con.prepareStatement(SQLString2);
            rs = statement.executeQuery();
            int id;
            int size;
            while (rs.next()){
                id = rs.getInt(1);
                size = rs.getInt(2);
                for(int i = 0; i<3; i++){
                    truck = new Truck(id,i+1,0,size);
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
        System.out.println("muie");
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
        System.out.println("muie");
        rowsInserted = statement.executeUpdate();   
        System.out.println("muie");
        return (rowsInserted == 1);
    }
    
    
}


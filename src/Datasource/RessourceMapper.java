/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.Order;
import Domain.Resource;
import Domain.ResourceDate;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 *
 * @author dekez
 */
public class RessourceMapper {
    

    public int getUniqueID(Connection connection){
        int uniqueID = 0;
        String SQLString = "select SEQ_RESOURCE.nextval from dual";
        PreparedStatement statement = null;
        
        try{
            statement = connection.prepareStatement(SQLString);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                uniqueID = rs.getInt(1);
            }
        }
        catch(SQLException e){
            System.out.println("Error in getting unique ID from RessourceMapper");
        }
        
        return uniqueID;
    }
    
    public boolean createNewResource(Resource resource, Connection connection) throws SQLException {
        boolean status = false;
        String SQLString = "INSERT INTO ressource values(?,?,?,?,?,?,?)";
        PreparedStatement statement = null;
        statement = connection.prepareStatement(SQLString);
        int rowsInserted = 0;
        char active = 'N';
        char isTentPart = 'N';
        int resourceID = getUniqueID(connection);
        
        if(resource.isActive() == true){
            active = 'Y';
        }
        
        if(resource.isTentPart() == true){
            isTentPart = 'Y';
        }
        
        statement.setInt(1, resourceID);
        statement.setString(2, resource.getResourceName());
        statement.setInt(3, resource.getQuantity());
        statement.setDouble(4, resource.getPrice());
        statement.setInt(5, resource.getUnitSize());
        statement.setString(6, "" + active);
        statement.setString(7, "" + isTentPart);
        rowsInserted = statement.executeUpdate();
        
        if(rowsInserted == 1){
            status = true;
        }
        
        return status;
        
    }
    
    public ArrayList<Resource> getAvailableResources(java.util.Date startD, java.util.Date endD, Connection con) {
        ArrayList<Resource> fullList = new ArrayList<Resource>();
        ArrayList<ResourceDate> auxiliarList = new ArrayList<ResourceDate>();
        ArrayList<Resource> finalList = new ArrayList<Resource>();
        ArrayList<Resource> auxiliarList2 = new ArrayList<Resource>();
        String SQLString1 = 
                "select *"
                + " from ressource"
                + " where active = 'Y' "
                + " order by ressource_id";
        String SQLString2 =
                "select order_detail.ressource_id, order_detail.quantity, orders.start_date, orders.end_date, orders.canceled"
                +" FROM order_detail natural join orders"
                +" where ((orders.start_date >= ?) and (orders.start_date <= ?))"
                +" or ((orders.end_date >= ?) and (orders.end_date <= ?))";
        PreparedStatement statement = null;
        
        try{
            statement = con.prepareStatement(SQLString1);
            ResultSet rs = statement.executeQuery();
            Resource resource,resource1,resource2;
            while (rs.next()) {
                boolean tentPart;
                if (rs.getString(7).charAt(0) == 'Y'){
                    tentPart = true;
                }
                else {
                    tentPart = false;
                }
                resource = new Resource(rs.getInt(1),
                                        rs.getString(2),
                                        rs.getInt(3),
                                        rs.getDouble(4),
                                        rs.getInt(5),
                                        tentPart
                                        );
                resource1 = new Resource(rs.getInt(1),
                                        rs.getString(2),
                                        rs.getInt(3),
                                        rs.getDouble(4),
                                        rs.getInt(5),
                                        tentPart
                                        );
                resource2 = new Resource(rs.getInt(1),
                                        rs.getString(2),
                                        rs.getInt(3),
                                        rs.getDouble(4),
                                        rs.getInt(5),
                                        tentPart
                                        );
                
                fullList.add(resource);
                finalList.add(resource1);
                auxiliarList2.add(resource2);
            }
            statement = con.prepareStatement(SQLString2);
            java.sql.Date startDate = new java.sql.Date(startD.getTime());
            java.sql.Date endDate = new java.sql.Date(endD.getTime());
            statement.setDate(1, startDate);
            statement.setDate (2, endDate);
            statement.setDate(3, startDate);
            statement.setDate(4, endDate);
            rs = statement.executeQuery();
            ResourceDate resourceDate;
            while (rs.next()){
                resourceDate = new ResourceDate(rs.getInt(1),
                                                rs.getInt(2),
                                                rs.getDate(3),
                                                rs.getDate(4));
                char canceled = rs.getString(5).charAt(0);
                if (canceled == 'N'){
                    auxiliarList.add(resourceDate); 
                }        
            }
       
            java.util.Date auxDate;
            int i;
            if ( ! auxiliarList.isEmpty()) 
            while (startD.compareTo(endD) <= 0 ) {
                for(i = 0; i<fullList.size()-1; i++){
                    auxiliarList2.get(i).setQuantity(fullList.get(i).getQuantity());
                }
                for(i = 0; i<auxiliarList.size(); i++){
                    resourceDate = auxiliarList.get(i);
                    if (!(startD.before(resourceDate.getStartDate())) && !(startD.after(resourceDate.getEndDate()))){
                        for (int j = 0; j<auxiliarList2.size(); j++){
                            if (auxiliarList2.get(j).getResourceID() == resourceDate.getResourceID()){
                                auxiliarList2.get(j).setQuantity(auxiliarList2.get(j).getQuantity() - resourceDate.getQuantity());
                            }
                        }
                    }
                
                }
                for(i = 0; i<finalList.size()-1; i++){
                    if (finalList.get(i).getQuantity() > auxiliarList2.get(i).getQuantity()){
                        finalList.get(i).setQuantity(auxiliarList2.get(i).getQuantity())  ;
                    }    
                }
                auxDate = new java.util.Date(startD.getTime() + (1000 * 60 * 60 * 24)); 
                startD = auxDate;
            }
            
        }catch (Exception e) {
            System.out.println("Fail in RessourceMapper - getAvailableResources");
            System.out.println(e.getMessage());
        }
    
        return finalList;
    }
    
     public boolean editResource(Resource resource, Connection connection) throws SQLException{
        boolean status = false;
        String SQLString = "update ressource "
                            + "set ressource_name=?, quantity=?, price=?, active=?, tent_part=?"
                            + "where ressource_id=?";
        PreparedStatement statement = null;
        int rowsInserted = 0;
        char active = 'N';    
        char isTentPart = 'N';
        
        if(resource.isActive() == true){
            active = 'Y';
        }
        
        if (resource.isTentPart() == true){
            isTentPart = 'Y';
        }
        
        statement = connection.prepareStatement(SQLString);
        statement.setString(1, resource.getResourceName());
        statement.setInt(2, resource.getQuantity());
        statement.setFloat(3, (float)resource.getPrice());
        statement.setInt(6, resource.getResourceID());
        statement.setString(4, "" + active);
        statement.setString(5, "" + isTentPart);
        rowsInserted = statement.executeUpdate();
        if(rowsInserted == 1){
            status = true;
            connection.commit();
        }
        
        return status;
                
     }
     
     public Resource getResource(String name, Connection connection) throws SQLException{
        Resource resource = null;
        String SQLString = "select * from ressource where ressource_name=? for update of ressource_name, price, quantity, active, tent_part nowait";
        PreparedStatement statement = null;
        boolean isTentPart = false;
        boolean isActive = true;
        
        statement = connection.prepareStatement(SQLString);       
        statement.setString(1, name);
        ResultSet rs = statement.executeQuery();
        
        if(rs.next()){
            if(rs.getString(7).equalsIgnoreCase("y")){
                isTentPart = true;
            }
            
            if(rs.getString(6).equalsIgnoreCase("n")){
                isActive = false;
            }
            resource = new Resource(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDouble(4), rs.getInt(5), isTentPart);
            resource.setActive(isActive);
        }
        
        return resource;
    }

    public ArrayList<Order> deleteResource(int resourceID, Connection connection) {
        ArrayList<Order> list = null;
        String SQLString1 = "delete from ressource "
                           + " where ressource_id = ? ";
        PreparedStatement statement = null;
        try{
            statement = connection.prepareStatement(SQLString1);
            statement.setInt(1, resourceID);
            int updatedRows = statement.executeUpdate();
        }catch(Exception e){
            System.out.println("Fail in RessourceMapper - deleteResource");
            System.out.println(e.getMessage());
        }
        return list;
    }

    public boolean deactivateOrder(int resourceID, Connection connection) {
        boolean status = false;
        String SQLString = "update resource"
                         + "set activated=false"
                         + "where resource_id=?";
        PreparedStatement statement = null;
        int rowsUpdated = 0;
        
        try{
            statement = connection.prepareStatement(SQLString);
            statement.setInt(1, resourceID);
            rowsUpdated = statement.executeUpdate();  
            if(rowsUpdated == 1){
                status = true;
            }
        }
        catch(SQLException ex){
            System.out.println("Error in the deactivateResource() in RessourceMapper - " + ex);
        }
        
        return status;
    }

    public boolean reactivateResource(String resourceName, Connection connection) {
        boolean status = false;
        String SQLString = "update resource"
                + "set activated=true"
                + "where resource_name=?";
        PreparedStatement statement = null;
        int rowsUpdated = 0;
        
        try{
            statement = connection.prepareStatement(SQLString);
            statement.setString(1, resourceName);
            rowsUpdated = statement.executeUpdate(); 
            if(rowsUpdated == 1){
                status = true;
            }       
        }
        catch(SQLException ex){
            System.out.println("Error in reactivateResource() in RessourceMapper - " + ex);
        }
        
        return status;
    }
    
}


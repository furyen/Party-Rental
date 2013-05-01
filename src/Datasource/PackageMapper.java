/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.Package;
import Domain.PackageDetail;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author dekez
 */
public class PackageMapper {

    public ArrayList<Package> getAllPackages(Connection connection) {
        ArrayList<Package> packageList = new ArrayList();
        String SQLString = "select * from event_package";
        String SQLString2 = "select * from event_package_detail where package_id=?";
        PreparedStatement statement = null;
        PreparedStatement statement2 = null;
        
        try{
            statement = connection.prepareStatement(SQLString);
            ResultSet rs = statement.executeQuery();
            
            while(rs.next()){
                statement2 = connection.prepareStatement(SQLString2);
                statement2.setInt(1, rs.getInt(1));
                ResultSet rs2 = statement2.executeQuery();
                System.out.println(" muie");
                Package newPackage = new Package(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                System.out.println("muie2");
                while(rs2.next()){
                    System.out.println("muie3");
                    newPackage.addPackageDetail(new PackageDetail(rs2.getInt(1), rs2.getInt(2), rs2.getInt(3)));
                    System.out.println("muie4");
                }               
                
                packageList.add(newPackage);
            }
            
        }
        catch(SQLException ex){
            System.out.println("Error in the PackageMapper, getAllPackages() - " + ex);
        }
        
        return packageList;
    }

    int getUniquePackageID(Connection connection) {
        int packageID = 0;
        String SQLString = "select SEQ_PACKAGE.nextval from dual";
        PreparedStatement statement = null;
        
        try{
            statement = connection.prepareStatement(SQLString);
            ResultSet rs = statement.executeQuery();
            
            if(rs.next()){
                packageID = rs.getInt(1);
            }
            
        }
        catch(SQLException ex){
            System.out.println("Error in the PackageMapper in getUniquePackageID() - " + ex);
        }
        
        return packageID;
    }

    boolean createNewPackage(String packageName, double discount, Connection connection) {
        boolean status = false;
        String SQLString = "insert into event_package values(?,?,?)";
        PreparedStatement statement = null;
        int packageID = getUniquePackageID(connection);
        int rowsInserted = 0;
        
        try{
            statement = connection.prepareStatement(SQLString);
            statement.setInt(1, packageID);
            statement.setString(2, packageName);
            statement.setDouble(3, discount);
            rowsInserted = statement.executeUpdate();
            
            if(rowsInserted == 1){
                status = true;
            }
        }
        catch(SQLException ex){
            System.out.println("Error in createNewPackage() in PackageMapper - " + ex);
        }
        
        return status;
        
    }

    public Package getPackage(String name, Connection connection) {
        Domain.Package newPackage = null;
        String SQLString = "select * from event_package where package_name=?";
        String SQLString2 = "select * from event_package_detail where package_id=?";
        PreparedStatement statement = null;
        PreparedStatement statement2 = null;
        
        try{
            statement = connection.prepareStatement(SQLString);
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            
            if(rs.next()){
                newPackage = new Package(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                statement2 = connection.prepareStatement(SQLString2);
                statement2.setInt(1, newPackage.getPackageID());
                ResultSet rs2 = statement2.executeQuery();
                
                while(rs2.next()){
                    newPackage.addPackageDetail(new PackageDetail(rs2.getInt(1), rs2.getInt(2), rs2.getInt(3)));
                    System.out.println("Inside RS2");
                }
            }
        }
        catch(SQLException ex){
            System.out.println("Error in getPackage() in PackageMapper - " + ex);
        }
        
        return newPackage;
        
    }

    public boolean deletePackage(String name, Connection connection) {
        boolean status = false;
        String SQLString = "delete from event_package where package_name=?";
        String SQLString2 = "select package_id from event_package where package_name=?";
        String SQLString3 = "delete from event_package_detail where package_id=?";
        PreparedStatement statement = null;
        PreparedStatement statement2 = null;
        PreparedStatement statement3 = null;
        int rowsDeleted = 0;
        int packageDeleted = 0;
        
        try{
            statement = connection.prepareStatement(SQLString2);
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            
            if(rs.next()){
                statement2 = connection.prepareStatement(SQLString3);
                statement2.setInt(1, rs.getInt(1));
                rowsDeleted = statement2.executeUpdate();

                statement3 = connection.prepareStatement(SQLString);
                statement3.setString(1, name);
                packageDeleted = statement3.executeUpdate();
            }
            
            
            if(rowsDeleted > 0 && packageDeleted == 1){
                status = true;
                connection.commit();
            }
            
        }
        catch(SQLException ex){
            System.out.println("Error in deletePackage() in PackageMapper - " + ex);
        }
        
        return status; 
    }

    public boolean createNewPackageDetail(PackageDetail newPackageDetail, Connection connection) {
        boolean status = false;
        String SQLString = "insert into event_package_detail values(?,?,?)";
        PreparedStatement statement;
        int rowsInserted = 0;
        
        try{
            statement = connection.prepareStatement(SQLString);
            statement.setInt(1, newPackageDetail.getPackageID());
            statement.setInt(2, newPackageDetail.getResourceID());
            statement.setInt(3, newPackageDetail.getQuantity());
            rowsInserted = statement.executeUpdate();
            
            if(rowsInserted == 1){
                status = true;
                connection.commit();
            }
        }
        catch(SQLException ex){
            System.out.println("Error in createNewPackageDetail() in PackageMapper - " + ex);
        }
        
        return status;
    }
    
}

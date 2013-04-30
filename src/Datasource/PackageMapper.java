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
                Package newPackage = new Package(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                
                while(rs2.next()){
                    newPackage.addPackageDetail(new PackageDetail(rs.getInt(1), rs.getInt(2), rs.getInt(3)));
                }               
                
                packageList.add(newPackage);
            }
            
        }
        catch(SQLException ex){
            System.out.println("Error in the PackageMapper, getAllPackages() - " + ex);
        }
        
        return packageList;
    }
    
}

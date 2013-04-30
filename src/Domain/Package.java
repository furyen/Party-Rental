/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

import java.util.ArrayList;

/**
 *
 * @author dekez
 */
public class Package {
    ArrayList<PackageDetail> packageDetailList;
    int packageID;
    String packageName;
    double discount;

    public Package(int packageID, String packageName, double discount) {
        this.packageID = packageID;
        this.packageName = packageName;
        packageDetailList = new ArrayList();
        this.discount = discount;
    }
    
    public void addPackageDetail(PackageDetail packageDetail){
        this.packageDetailList.add(packageDetail);
    }
    
    public double getDiscount(){
        return discount;
    }
    
    public void setDiscount(double discount){
        this.discount = discount;
    }

    public int getPackageID() {
        return packageID;
    }

    public void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    
    
}

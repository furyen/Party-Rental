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
    ArrayList<PackageDetail> packageDetail;
    int packageID;
    String packageName;

    public Package(int packageID, String packageName) {
        this.packageID = packageID;
        this.packageName = packageName;
        packageDetail = new ArrayList();
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

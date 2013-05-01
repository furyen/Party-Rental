/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

/**
 *
 * @author dekez
 */
public class PackageDetail {
    int packageID, resourceID, quantity;

    public PackageDetail(int packageID, int resourceID, int quantity) {
        this.packageID = packageID;
        this.resourceID = resourceID;
        this.quantity = quantity;
    }

    public int getPackageID() {
        return packageID;
    }

    public void setPackageID(int packageID) {
        this.packageID = packageID;
    }

    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String toString(){
        return packageID + " - " + resourceID + " - " + quantity;
    }
    
    
    
}

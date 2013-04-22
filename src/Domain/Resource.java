/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

/**
 *
 * @author dekez
 */
public class Resource {
    private int resourceID, unitSize, quantity;
    private String resourceName;
    private double price;
    private boolean active;
    
    public Resource(int resourceID, String resourceName,  int quantity,  double price, int unitSize){
        this.price = price;
        this.quantity = quantity;
        this.unitSize = unitSize;
        this.resourceName = resourceName;
        this.resourceID = resourceID;
        this.active = true;
    }
    
    public boolean isActive(){
        return active;
    }
    
    public void setActive(boolean active){
        this.active = active;
    }
    
    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(int unitSize) {
        this.unitSize = unitSize;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return  resourceName;
    }
    
    
    
}

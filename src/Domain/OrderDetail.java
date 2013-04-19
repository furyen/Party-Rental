/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

/**
 *
 * @author dekez
 */
public class OrderDetail {

    private int orderID, resourceID, quantity;
    private String ressourceName;

    public OrderDetail(int orderID, int resourceID, int quantity) {
        this.orderID = orderID;
        this.resourceID = resourceID;
        this.quantity = quantity;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
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

    public String getRessourceName() {
        return ressourceName;
    }

    public void setRessourceName(String ressourceName) {
        this.ressourceName = ressourceName;
    }

    @Override
    public String toString() {
        return ressourceName + " - " + quantity;
    }
}

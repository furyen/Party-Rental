/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

/**
 *
 * @author Daniel
 */
public class TruckOrder {
    private int truckID, truckRun, orderID, orderPartSize;
    private char ch;

    public TruckOrder(int truckID, int truckRun, int orderID, char ch, int orderPartSize) {
        this.truckID = truckID;
        this.truckRun = truckRun;
        this.orderID = orderID;
        this.ch = ch;
        this.orderPartSize = orderPartSize;
    }

    public int getOrderPartSize() {
        return orderPartSize;
    }

    public int getTruckID() {
        return truckID;
    }

    public int getTruckRun() {
        return truckRun;
    }

    public int getOrderID() {
        return orderID;
    }

    public char getCh() {
        return ch;
    }
    
    
}

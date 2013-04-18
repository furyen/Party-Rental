/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

/**
 *
 * @author dekez
 */
public class Truck {

    int truckID, size, truckRun, filledSpace;
    double unitPrice;

    public Truck(int truckID, int truckRun, int filledSpace, int size, double unitPrice) {
        this.truckID = truckID;
        this.size = size;
        this.truckRun = truckRun;
        this.filledSpace = filledSpace;
        this.unitPrice = unitPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setFilledSpace(int filledSpace) {
        this.filledSpace = filledSpace;
    }

    public int getFreeSpace() {
        return size - filledSpace;
    }

    public int getTruckID() {
        return truckID;
    }

    public int getSize() {
        return size;
    }

    public int getTruckRun() {
        return truckRun;
    }

    public int getFilledSpace() {
        return filledSpace;
    }
}

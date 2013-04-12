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

    public Truck(int truckID,  int truckRun,int filledSpace, int size ) {
        this.truckID = truckID;
        this.size = size;
        this.truckRun = truckRun;
        this.filledSpace = filledSpace;
    }

    public void setFilledSpace(int filledSpace) {
        this.filledSpace = filledSpace;
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

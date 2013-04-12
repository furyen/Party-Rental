/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

import java.sql.Date;

/**
 *
 * @author Daniel
 */
public class ResourceDate {
    private int resourceID;
    private int quantity;
    private Date startDate;
    private Date endDate;

    public ResourceDate(int resourceID, int quantity, Date startDate, Date endDate) {
        this.resourceID = resourceID;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getResourceID() {
        return resourceID;
    }

    public int getQuantity() {
        return quantity;
    }

   
   
    
}

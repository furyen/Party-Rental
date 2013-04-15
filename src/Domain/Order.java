/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author dekez
 */
public class Order {
    ArrayList<OrderDetail> orderDetails;
    int customerID, orderID, unitSize;
    String adress;
    Date startDate, endDate;
    boolean deposit;
    double finalPrice;
    double discount;

    public Order(int customerID, int orderID, int unitSize, String eventAddress, Date startDate, Date endDate, boolean deposit, double finalPrice, double discount) {
        this.customerID = customerID;
        this.orderID = orderID;
        this.unitSize = unitSize;
        this.adress = eventAddress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
        orderDetails = new ArrayList();
        this.finalPrice = finalPrice;
        this.discount = discount;
    }

    public void insertOrderDetail(OrderDetail orderDetail){
        orderDetails.add(orderDetail);
    }
    
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setUnitSize(int unitSize) {
        this.unitSize = unitSize;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isDeposit() {
        return deposit;
    }

    public void setDeposit(boolean deposit) {
        this.deposit = deposit;
    }
    
    
}

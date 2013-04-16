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
    private ArrayList<OrderDetail> orderDetails;
    private int customerID, orderID, unitSize;
    private String adress;
    private Date startDate, endDate;
    private boolean canceled;
    private double fullPrice, discount, aditionalCost;
    private byte paymantState;

    public Order( int customerID, int orderID, int unitSize, String adress, Date startDate, Date endDate, boolean canceled, double fullPrice, double discount, double aditionalCost, byte paymantState) {
        this.orderDetails = orderDetails;
        this.customerID = customerID;
        this.orderID = orderID;
        this.unitSize = unitSize;
        this.adress = adress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.canceled = canceled;
        this.fullPrice = fullPrice;
        this.discount = discount;
        this.aditionalCost = aditionalCost;
        this.paymantState = paymantState;
    }

    public ArrayList<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getUnitSize() {
        return unitSize;
    }

    public String getAdress() {
        return adress;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public double getFullPrice() {
        return fullPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public double getAditionalCost() {
        return aditionalCost;
    }

    public byte getPaymantState() {
        return paymantState;
    }

    public void setOrderDetails(ArrayList<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setUnitSize(int unitSize) {
        this.unitSize = unitSize;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void setFullPrice(double fullPrice) {
        this.fullPrice = fullPrice;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setAditionalCost(double aditionalCost) {
        this.aditionalCost = aditionalCost;
    }

    public void setPaymantState(byte paymantState) {
        this.paymantState = paymantState;
    }

    public void insertOrderDetail(OrderDetail orderDetail){
        orderDetails.add(orderDetail);
    }
}

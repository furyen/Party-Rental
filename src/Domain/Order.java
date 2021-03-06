/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private Date startDate, endDate, dateCreation;
    private boolean cancelled;
    private double fullPrice, discount, aditionalCost, paidAmount;
    private boolean truckDelivery, truckReturn;

    public boolean isTruckDelivery() {
        return truckDelivery;
    }

    public boolean isTruckReturn() {
        return truckReturn;
    }

    public void setTruckDelivery(boolean truckDelivery) {
        this.truckDelivery = truckDelivery;
    }

    public void setTruckReturn(boolean truckReturn) {
        this.truckReturn = truckReturn;
    }

    public Order(int customerID, int orderID, int unitSize, String adress, Date startDate, Date endDate, Date dateCreation, boolean canceled, double fullPrice, double discount, double additionalCost, double paidAmount) {
        this.orderDetails = new ArrayList();
        this.customerID = customerID;
        this.orderID = orderID;
        this.unitSize = unitSize;
        this.adress = adress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancelled = canceled;
        this.fullPrice = fullPrice;
        this.discount = discount;
        this.aditionalCost = additionalCost;
        this.paidAmount = paidAmount;
        this.dateCreation = dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateCreation() {
        return dateCreation;
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

    public boolean isCancelled() {
        return cancelled;
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

    public double getPaidAmount() {
        return paidAmount;
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

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void insertOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
    }

    @Override
    public String toString() {
         DateFormat formatter = new SimpleDateFormat ("dd MMMM YYYY");
        if (cancelled == true) {
            return "Delivery: " + formatter.format(startDate) + " - Return: " + formatter.format(endDate) + " [Cancelled]";
        } else {
            return "Delivery: " + formatter.format(startDate) + " - Return: " + formatter.format(endDate);
        }
    }
}

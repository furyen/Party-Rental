/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;
 
/**
 *
 * @author dekez
 */
public class Customer {
    private String firstName, lastName, adress;
    private int customerID;
    
    public Customer(int id, String first, String last, String adress){
        firstName = first;
        lastName = last;
        customerID = id;
        this.adress = adress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    @Override
    public String toString() {
        return  firstName + " " + lastName + " , " + adress + " , " + customerID;
    }
    
    
    
}

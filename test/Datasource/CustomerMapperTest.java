/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Datasource;

import Domain.Customer;
import java.sql.Connection;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dekez
 */
public class CustomerMapperTest {
    
    public CustomerMapperTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCustomerList method, of class CustomerMapper.
     * This method takes in two strings and a connection
     * The strings represent search keys from the user
     * and the connection is used for the database.
     * Nicklas
     */
    @Test
    public void testGetCustomerList() throws Exception {
        DBFacade dbFacade = DBFacade.getInstance();
        System.out.println("getCustomerList");
        String firstName = "DA";
        String lastName = "S";
        Connection connection = dbFacade.getConnection();
        CustomerMapper instance = new CustomerMapper();
        ArrayList expResult = new ArrayList();
        expResult.add(new Customer(1, "DANIEL", "S", "CPH"));
        ArrayList result = instance.getCustomerList(firstName, lastName, connection);
        
        assertEquals(expResult.size(), result.size());
    }

    /**
     * Test of getUniqueCustomerID method, of class CustomerMapper.
     * Here we are getting a unique number from the SEQ_CUSTOMER
     * and to test this we are checking if the received number
     * is already in the database.
     * Nicklas
     */
    @Test
    public void testGetUniqueCustomerID() throws Exception {
        System.out.println("getUniqueCustomerID");
        DBFacade dbFacade = DBFacade.getInstance();
        Connection connection = dbFacade.getConnection();
        CustomerMapper instance = new CustomerMapper();
        ArrayList<Customer> allCustomers = instance.getCustomerList("", "", connection);
        boolean expResult = false;
        int result = instance.getUniqueCustomerID(connection);
        
        for(Customer customer : allCustomers){
            if(customer.getCustomerID() == result){
                expResult = false;
            }
            else{
                expResult = true;
            }
        }
        
        assertTrue(expResult);
    }

    /**
     * Test of createNewCustomer method, of class CustomerMapper.
     * Takes in the attributes for a customer and inputs it into the database
     * after which it gives back a boolean. 
     * Test looks in the database to see if the customer
     * has been created.
     * Nicklas
     */
    @Test
    public void testCreateNewCustomer() throws Exception {
        System.out.println("createNewCustomer");
        DBFacade dbFacade = DBFacade.getInstance();
        ArrayList<Customer> newCustomerList = new ArrayList();
        Connection connection = dbFacade.getConnection();
        CustomerMapper instance = new CustomerMapper();
        newCustomerList.add(new Customer(instance.getUniqueCustomerID(connection), "TESTCASE", "TESTCASE", "TESTCASE"));
        instance.createNewCustomer(newCustomerList, connection);
        ArrayList<Customer> customerList = instance.getCustomerList("TESTCASE", "TESTCASE", connection);
        boolean expResult = true;
        boolean result = false;
        Customer expCustomer = newCustomerList.get(0);
        Customer resultCustomer = customerList.get(0);
        
        if(resultCustomer.getFirstName().equals(expCustomer.getFirstName()) && resultCustomer.getLastName().equals(expCustomer.getLastName())){
            result = true;
        }
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getCustomer method, of class CustomerMapper.
     * Gets a customer based on the expected customer ID
     * Returns a customer object after which the ID of the object
     * and the expected ID is compared to see if they match.
     * Nicklas
     */
    @Test
    public void testGetCustomer() {
        System.out.println("getCustomer");
        DBFacade dbFacade = DBFacade.getInstance();
        int expCustID = 1;
        Connection connection = dbFacade.getConnection();
        CustomerMapper instance = new CustomerMapper();
        Customer result = instance.getCustomer(expCustID, connection);
        int resultCustID = result.getCustomerID();
        assertEquals(expCustID, resultCustID);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Presentation;

import Domain.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author dekez
 */
public class Main {
    
    public static void main(String args[]) throws ParseException{
        Controller control = Controller.getInstance();
        ArrayList<Resource> availableResources = new ArrayList();
        ArrayList<Truck> trucks = new ArrayList();
        ArrayList<Order> orders = new ArrayList();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date startD = df.parse("31-12-1995");
        java.util.Date endD = df.parse("02-10-2001");
        Order order = new Order(1, 10, 150, "Dyringparken", startD, endD, false, 103230, 0.25, 0, 0);
        //int customerID, int orderID, int unitSize, String adress, Date startDate, Date endDate, boolean canceled, double fullPrice, double discount, double additionalCost, double paidAmount
        OrderDetail orderDetail1 = new OrderDetail(10, 5, 100);
        orderDetail1.setRessourceName("Crockery");
        order.insertOrderDetail(orderDetail1);
        OrderDetail orderDetail2 = new OrderDetail(10, 1, 100);
        orderDetail2.setRessourceName("Table");
        order.insertOrderDetail(orderDetail2);
                
        control.getConnection();
//        
//        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//            java.util.Date startD = df.parse("31-12-1995");
//            java.util.Date endD = df.parse("02-10-2001");
//       
//        availableResources = control.getAvailableResources(startD ,endD );
//        System.out.println(availableResources.toString());
////  "31-12-1995", "02-10-2001"      
//        for(int i=0; i<availableResources.size(); i++){
//            System.out.println(availableResources.get(i).getResourceName()+"  "+availableResources.get(i).getQuantity()
//                );
//        }
//       
//        trucks = control.getTruckDeliveryForDate(startD,'0');
//         for(int i=0; i<trucks.size(); i++){
//            System.out.println(trucks.get(i).getTruckID()+"  "+trucks.get(i).getTruckRun()+" "+ trucks.get(i).getFilledSpace()+ " "+ trucks.get(i).getSize()
//                );
//        }
////           control.getOrders();
////            
        
          control.createFinalInvoiceFile(order);
         
        
    }
    
}

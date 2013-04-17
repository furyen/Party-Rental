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
        
//        control.getConnection();
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
        
          System.out.println("Hellebæk Party Rental\t\t\t\t\t\tCVR: 32139429\n"
                + "\t\t\t\t\t\t\t\tOrder nr: " + "on" + "\n"
                + "\n"
                + "Dear " + "Customer Name"
                + "\n"
                + "You are receiving this invoice in accordance to your order\n"
                + "with to the following address: " + "Order address"
                + "You have ordered the following things:\n"
                + "\n"
                + "For loop appends to ");
         
        
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Presentation;

import Domain.Controller;
import Domain.Customer;
import Domain.Order;
import Domain.OrderDetail;
import Domain.Resource;
import Domain.Truck;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

/**
 *
 * @author dekez
 */
public class PartyRentalGUI extends javax.swing.JFrame {

    /**
     * Creates new form PartyRentalGUI
     */
    private Controller con = Controller.getInstance();
    private LinkedHashMap<Resource, JComboBox> resources = new LinkedHashMap();
    private LinkedHashMap<Truck, JToggleButton> truckDelivery = new LinkedHashMap();
    private LinkedHashMap<Truck, JToggleButton> truckReturn = new LinkedHashMap();
    private DefaultListModel searchCustomerModel = new DefaultListModel();
    private ArrayList<Resource> allResources = null;
    private DefaultListModel resourceModel = new DefaultListModel();
    private DefaultListModel customerOrdersModel = new DefaultListModel();
    private DefaultListModel orderDetailsModel = new DefaultListModel();
    private DefaultListModel truckListModel = new DefaultListModel();
    private ArrayList<Truck> allTrucks = null;
    private int unitsDeliver, unitsReturn;
    double totalPriceValue;

    public PartyRentalGUI() {
        initComponents();
        con.getConnection();
        allResources = con.getAvailableResources(null, null);
        for (Resource res : allResources) {
            resourceModel.addElement(res);
        }
        JList_resources.setModel(resourceModel);
        orderDetailsList.setModel(orderDetailsModel);
        customerOrders.setModel(customerOrdersModel);
        allTrucks = con.getTrucks();
        TruckList.setModel(truckListModel);
        for(Truck truck : allTrucks){
            truckListModel.addElement(truck);
        }

    }

    public void refreshAvailableResources(ArrayList<Resource> inventory) {

        int totalItems = inventory.size();
        int added = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridy = 0;
        gbc.gridx = 2;
        gbInventory.add(new JLabel("Available"), gbc);
        gbc.gridx = 6;
        gbInventory.add(new JLabel("Price"), gbc);


        gbc.gridy = 2;
        while (added < totalItems) {

            gbc.gridx = 0;
            gbInventory.add(new JLabel(inventory.get(added).getResourceName()), gbc);
            gbc.gridx = 2;
            gbInventory.add(new JLabel("" + inventory.get(added).getQuantity()), gbc);
            gbc.gridx = 4;

            //set the dropdown items in the Combo Box
            Integer[] quantity = new Integer[inventory.get(added).getQuantity() + 1];
            for (int i = 0; i < inventory.get(added).getQuantity() + 1; i++) {
                quantity[i] = i;
            }

            resources.put(inventory.get(added), new JComboBox(quantity));
            resources.get(inventory.get(added)).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int totalUnits = 0;

                    for (Map.Entry<Resource, JComboBox> entry : resources.entrySet()) {
                        entry.getKey().setQuantity(entry.getValue().getSelectedIndex());
                        totalUnits = totalUnits + entry.getKey().getUnitSize() * entry.getValue().getSelectedIndex();
                    }
                    for (Map.Entry<Truck, JToggleButton> entry : truckDelivery.entrySet()) {
                        if (entry.getValue().isSelected()) {
                            entry.getValue().doClick();
                        }
                    }
                    for (Map.Entry<Truck, JToggleButton> entry : truckReturn.entrySet()) {
                        if (entry.getValue().isSelected()) {
                            entry.getValue().doClick();
                        }
                    }

                    totalPriceValue = con.calculatePrice(resources, Integer.parseInt(discount.getText()), truckDelivery, truckReturn);
                    DecimalFormat f = new DecimalFormat("##.00");
                    totalPrice.setText("Total Price: " + f.format(totalPriceValue));
                    totalSize.setText("Total Unit Size: " + totalUnits);
                    unitsDeliver = totalUnits;
                    unitsReturn = totalUnits;
                    remainingDelivery.setText("Units left to deliver: " + unitsDeliver);
                    remainingReturn.setText("Units left to return: " + unitsReturn);

                }
            });



            gbInventory.add(resources.get(inventory.get(added)), gbc);

            gbc.gridx = 6;
            gbInventory.add(new JLabel("" + inventory.get(added).getPrice()), gbc);
            gbc.gridy = gbc.gridy + 2;
            added++;

        }
    }

    public void refreshTruckDelivery(ArrayList<Truck> truckRuns) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        deliveryPanel.add(new JLabel("Truck ID"), gbc);
        gbc.gridx = 2;
        deliveryPanel.add(new JLabel("Truck Run"), gbc);
        gbc.gridx = 4;
        deliveryPanel.add(new JLabel("Filled Space"), gbc);
        gbc.gridx = 6;
        deliveryPanel.add(new JLabel("Total Space"), gbc);


        for (Truck truck : truckRuns) {
            //set the dropdown items in the Combo Box

            gbc.gridy = gbc.gridy + 2;
            gbc.gridx = 0;
            deliveryPanel.add(new JLabel("" + truck.getTruckID()), gbc);
            gbc.gridx = 2;
            deliveryPanel.add(new JLabel("" + truck.getTruckRun()), gbc);
            gbc.gridx = 4;
            deliveryPanel.add(new JLabel("" + truck.getFilledSpace()), gbc);
            gbc.gridx = 6;
            deliveryPanel.add(new JLabel("" + truck.getSize()), gbc);
            gbc.gridx = 8;

            truckDelivery.put(truck, new JToggleButton("Fill Truck"));
            truckDelivery.get(truck).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JToggleButton button = (JToggleButton) e.getSource();
                    for (Map.Entry<Truck, JToggleButton> entry : truckDelivery.entrySet()) {
                        if (button == entry.getValue()) {
                            if (button.isSelected()) {
                                if (unitsDeliver >= entry.getKey().getFreeSpace()) {
                                    unitsDeliver = unitsDeliver - entry.getKey().getFreeSpace();
                                    button.setText("" + entry.getKey().getFreeSpace());
                                    remainingDelivery.setText("Units left to deliver: " + unitsDeliver);
                                } else {
                                    button.setText("" + unitsDeliver);
                                    unitsDeliver = 0;
                                    remainingDelivery.setText("Units left to deliver: " + unitsDeliver);
                                }
                            } else {
                                unitsDeliver = unitsDeliver + Integer.parseInt(button.getText());
                                button.setText("Fill Truck");
                                remainingDelivery.setText("Units left to deliver: " + unitsDeliver);


                            }
                        }
                    }
                    totalPriceValue = con.calculatePrice(resources, Integer.parseInt(discount.getText()), truckDelivery, truckReturn);
                    DecimalFormat f = new DecimalFormat("##.00");
                    totalPrice.setText("Total Price: " + f.format(totalPriceValue));
                }
            });
            deliveryPanel.add(truckDelivery.get(truck), gbc);

        }

    }

    public void refreshTruckReturn(ArrayList<Truck> truckRuns) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        returnPanel.add(new JLabel("Truck ID"), gbc);
        gbc.gridx = 2;
        returnPanel.add(new JLabel("Truck Run"), gbc);
        gbc.gridx = 4;
        returnPanel.add(new JLabel("Filled Space"), gbc);
        gbc.gridx = 6;
        returnPanel.add(new JLabel("Total Space"), gbc);

        for (Truck truck : truckRuns) {

            gbc.gridy = gbc.gridy + 2;
            gbc.gridx = 0;
            returnPanel.add(new JLabel("" + truck.getTruckID()), gbc);
            gbc.gridx = 2;
            returnPanel.add(new JLabel("" + truck.getTruckRun()), gbc);
            gbc.gridx = 4;
            returnPanel.add(new JLabel("" + truck.getFilledSpace()), gbc);
            gbc.gridx = 6;
            returnPanel.add(new JLabel("" + truck.getSize()), gbc);
            gbc.gridx = 8;
            truckReturn.put(truck, new JToggleButton("Fill Truck"));
            truckReturn.get(truck).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JToggleButton button = (JToggleButton) e.getSource();
                    for (Map.Entry<Truck, JToggleButton> entry : truckReturn.entrySet()) {
                        if (button == entry.getValue()) {
                            if (button.isSelected()) {
                                if (unitsReturn >= entry.getKey().getFreeSpace()) {
                                    unitsReturn = unitsReturn - entry.getKey().getFreeSpace();
                                    button.setText("" + entry.getKey().getFreeSpace());
                                    remainingReturn.setText("Units left to return: " + unitsReturn);
                                } else {
                                    button.setText("" + unitsReturn);
                                    unitsReturn = 0;
                                    remainingReturn.setText("Units left to return: " + unitsReturn);
                                }
                            } else {
                                unitsReturn = unitsReturn + Integer.parseInt(button.getText());
                                button.setText("Fill Truck");
                                remainingReturn.setText("Units left to return: " + unitsReturn);

                            }
                        }
                    }

                    totalPriceValue = con.calculatePrice(resources, Integer.parseInt(discount.getText()), truckDelivery, truckReturn);
                    DecimalFormat f = new DecimalFormat("##.00");
                    totalPrice.setText("Total Price: " + f.format(totalPriceValue));
                }
            });
            returnPanel.add(truckReturn.get(truck), gbc);

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchCustomer = new javax.swing.JDialog();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        fName = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        lName = new javax.swing.JTextField();
        searchCustomersButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        customerList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        customerOrders = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        orderDetailsList = new javax.swing.JList();
        saveOrderResult = new javax.swing.JDialog();
        saveOrderResultLabel = new javax.swing.JLabel();
        confirm = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        Menu = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        resourcesMenuButton = new javax.swing.JButton();
        Booking = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        Customer = new javax.swing.JPanel();
        firstName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lastName = new javax.swing.JTextField();
        customerID = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        customerAddress = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        Order = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        startDate = new com.toedter.calendar.JDateChooser();
        endDate = new com.toedter.calendar.JDateChooser();
        jButton4 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        eventAddress = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        gbInventory = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        deliveryPanel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        returnPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        discount = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        totalPrice = new javax.swing.JLabel();
        totalSize = new javax.swing.JLabel();
        remainingDelivery = new javax.swing.JLabel();
        remainingReturn = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        ResourceDone = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        createRes1 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        newResName = new javax.swing.JTextField();
        newResQuantity = new javax.swing.JTextField();
        newResPrice = new javax.swing.JTextField();
        newResUnitSize = new javax.swing.JTextField();
        createNewRes = new javax.swing.JButton();
        createdRes = new javax.swing.JLabel();
        editRes = new javax.swing.JPanel();
        UpdateResource = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        editResUnitSize = new javax.swing.JTextField();
        editResName = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        editResPrice = new javax.swing.JTextField();
        editResQuantity = new javax.swing.JTextField();
        getRes = new javax.swing.JPanel();
        getResource = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        resSelected = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        JList_resources = new javax.swing.JList();
        TruckHandlingPanel = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        TruckIDTextField = new javax.swing.JTextField();
        TruckSizeTextField = new javax.swing.JTextField();
        TruckUnitPriceTextField = new javax.swing.JTextField();
        EditTruckButton = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        GetExistingTrucksPanel = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        TruckList = new javax.swing.JList();
        GetAllTrucksButton = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        AddNewTruckPanel = new javax.swing.JPanel();
        AddNewTruckSizeTextField = new javax.swing.JTextField();
        AddNewTruckUnitPriceTextField = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        AddNewTruckButton = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();

        searchCustomer.setBounds(new java.awt.Rectangle(300, 300, 900, 300));
        searchCustomer.setMinimumSize(new java.awt.Dimension(470, 267));
        searchCustomer.setResizable(false);

        jButton10.setText("Choose");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Cancel");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Search for customer", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 2, 14))); // NOI18N

        jLabel12.setText("First Name");

        jLabel13.setText("Last Name");

        searchCustomersButton.setText("Search");
        searchCustomersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchCustomersButtonActionPerformed(evt);
            }
        });

        customerList.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Search Results", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 2, 14))); // NOI18N
        customerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customerListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(customerList);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(0, 0, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchCustomersButton))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(jLabel13)
                    .add(fName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(searchCustomersButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Customer Order History", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 2, 14))); // NOI18N

        customerOrders.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "No Customer Selected" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        customerOrders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customerOrdersMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(customerOrders);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Order Details", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 2, 14))); // NOI18N

        jLabel6.setText("Customer Name");

        jLabel7.setText("Event Address");

        jLabel11.setText("Total Price");

        jLabel15.setText("Paid");

        orderDetailsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "No order selected" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(orderDetailsList);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 337, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel6)
                            .add(jLabel7))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel15)
                            .add(jLabel11))
                        .add(150, 150, 150))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(169, 169, 169)
                                .add(jLabel15)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel7)))
                        .add(0, 11, Short.MAX_VALUE))
                    .add(jScrollPane5))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout searchCustomerLayout = new org.jdesktop.layout.GroupLayout(searchCustomer.getContentPane());
        searchCustomer.getContentPane().setLayout(searchCustomerLayout);
        searchCustomerLayout.setHorizontalGroup(
            searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(searchCustomerLayout.createSequentialGroup()
                .addContainerGap()
                .add(searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(searchCustomerLayout.createSequentialGroup()
                        .add(jButton11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 572, Short.MAX_VALUE)
                        .add(jButton10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(searchCustomerLayout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(27, 27, 27)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        searchCustomerLayout.setVerticalGroup(
            searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(searchCustomerLayout.createSequentialGroup()
                .addContainerGap()
                .add(searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton11)
                    .add(jButton10))
                .addContainerGap())
        );

        saveOrderResult.setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        saveOrderResult.setMinimumSize(new java.awt.Dimension(167, 71));
        saveOrderResult.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);

        confirm.setText("OK");
        confirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout saveOrderResultLayout = new org.jdesktop.layout.GroupLayout(saveOrderResult.getContentPane());
        saveOrderResult.getContentPane().setLayout(saveOrderResultLayout);
        saveOrderResultLayout.setHorizontalGroup(
            saveOrderResultLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(saveOrderResultLayout.createSequentialGroup()
                .addContainerGap()
                .add(saveOrderResultLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(confirm)
                    .add(saveOrderResultLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        saveOrderResultLayout.setVerticalGroup(
            saveOrderResultLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(saveOrderResultLayout.createSequentialGroup()
                .add(saveOrderResultLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(confirm)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(300, 150, 0, 0));
        setResizable(false);

        mainPanel.setLayout(new java.awt.CardLayout());

        jButton5.setText("Make a booking");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        resourcesMenuButton.setText("Create and Edit Resources\n");
        resourcesMenuButton.setMargin(null);
        resourcesMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resourcesMenuButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout MenuLayout = new org.jdesktop.layout.GroupLayout(Menu);
        Menu.setLayout(MenuLayout);
        MenuLayout.setHorizontalGroup(
            MenuLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MenuLayout.createSequentialGroup()
                .add(80, 80, 80)
                .add(jButton5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 161, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(resourcesMenuButton)
                .addContainerGap(457, Short.MAX_VALUE))
        );
        MenuLayout.setVerticalGroup(
            MenuLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MenuLayout.createSequentialGroup()
                .add(216, 216, 216)
                .add(MenuLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(resourcesMenuButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(504, Short.MAX_VALUE))
        );

        mainPanel.add(Menu, "menu");

        jButton1.setText("Back to Main Menu");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        Customer.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Customer", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N

        jLabel1.setText("First Name");

        jLabel3.setText("Last Name");

        jLabel4.setText("Customer ID");

        jLabel5.setText("Address");

        customerID.setEditable(false);

        jButton3.setText("Chose Existing Customer");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        customerAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerAddressActionPerformed(evt);
            }
        });

        jButton6.setText("Save New Customer");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout CustomerLayout = new org.jdesktop.layout.GroupLayout(Customer);
        Customer.setLayout(CustomerLayout);
        CustomerLayout.setHorizontalGroup(
            CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, CustomerLayout.createSequentialGroup()
                .addContainerGap()
                .add(CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(CustomerLayout.createSequentialGroup()
                        .add(CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(CustomerLayout.createSequentialGroup()
                                .add(jLabel1)
                                .add(18, 18, 18)
                                .add(firstName))
                            .add(CustomerLayout.createSequentialGroup()
                                .add(jLabel3)
                                .add(18, 18, 18)
                                .add(lastName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 151, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(18, 18, 18)
                        .add(CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(jLabel5)))
                    .add(CustomerLayout.createSequentialGroup()
                        .add(68, 68, 68)
                        .add(jButton3)))
                .add(18, 18, 18)
                .add(CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(CustomerLayout.createSequentialGroup()
                        .add(customerID)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(customerAddress))
                .addContainerGap())
        );
        CustomerLayout.setVerticalGroup(
            CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(CustomerLayout.createSequentialGroup()
                .add(CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(firstName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel5))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, customerAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lastName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(jLabel4)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, CustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(customerID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jButton6)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton3)
                .add(0, 0, Short.MAX_VALUE))
        );

        Order.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Order", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N

        jLabel8.setText("Start Date");

        jLabel9.setText("End Date");

        jButton4.setText("Get Available Resources");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel10.setText("Event Address");

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Available Resources", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N

        gbInventory.setLayout(new java.awt.GridBagLayout());
        jScrollPane1.setViewportView(gbInventory);

        deliveryPanel.setLayout(new java.awt.GridBagLayout());
        jScrollPane6.setViewportView(deliveryPanel);

        jTabbedPane1.addTab("Truck Delivery", jScrollPane6);

        returnPanel.setLayout(new java.awt.GridBagLayout());
        jScrollPane7.setViewportView(returnPanel);

        jTabbedPane1.addTab("Truck Return", jScrollPane7);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setText("Discount");

        discount.setText("0");
        discount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discountActionPerformed(evt);
            }
        });

        jLabel2.setText("%");

        totalPrice.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        totalPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalPrice.setText("Total Price : 0.0");

        remainingReturn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        org.jdesktop.layout.GroupLayout OrderLayout = new org.jdesktop.layout.GroupLayout(Order);
        Order.setLayout(OrderLayout);
        OrderLayout.setHorizontalGroup(
            OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(OrderLayout.createSequentialGroup()
                .addContainerGap()
                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(OrderLayout.createSequentialGroup()
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel8)
                            .add(jLabel10))
                        .add(20, 20, 20)
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(OrderLayout.createSequentialGroup()
                                .add(startDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(108, 108, 108)
                                .add(jLabel9)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(endDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 172, Short.MAX_VALUE)
                                .add(jButton4))
                            .add(eventAddress)))
                    .add(OrderLayout.createSequentialGroup()
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 298, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(OrderLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(totalSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(OrderLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 157, Short.MAX_VALUE)
                                .add(jLabel14)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(discount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel2)
                                .add(26, 26, 26)
                                .add(totalPrice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(11, 11, 11))
                            .add(OrderLayout.createSequentialGroup()
                                .add(18, 18, 18)
                                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jTabbedPane1)
                                    .add(OrderLayout.createSequentialGroup()
                                        .add(remainingDelivery, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(remainingReturn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 169, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(22, 22, 22)))))))
                .addContainerGap())
        );
        OrderLayout.setVerticalGroup(
            OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(OrderLayout.createSequentialGroup()
                .addContainerGap()
                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(OrderLayout.createSequentialGroup()
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(startDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(endDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(11, 11, 11))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, OrderLayout.createSequentialGroup()
                        .add(jButton4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)))
                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(eventAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(OrderLayout.createSequentialGroup()
                        .add(25, 25, 25)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 429, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(OrderLayout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 403, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(remainingDelivery, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(remainingReturn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(totalSize, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(discount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel2)
                        .add(totalPrice, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jButton7.setText("Save Order");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout BookingLayout = new org.jdesktop.layout.GroupLayout(Booking);
        Booking.setLayout(BookingLayout);
        BookingLayout.setHorizontalGroup(
            BookingLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(BookingLayout.createSequentialGroup()
                .addContainerGap()
                .add(BookingLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(Customer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(Order, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(BookingLayout.createSequentialGroup()
                        .add(jButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButton7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        BookingLayout.setVerticalGroup(
            BookingLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, BookingLayout.createSequentialGroup()
                .addContainerGap()
                .add(Order, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 24, Short.MAX_VALUE)
                .add(Customer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(BookingLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton7))
                .add(14, 14, 14))
        );

        mainPanel.add(Booking, "booking");

        ResourceDone.setMaximumSize(new java.awt.Dimension(720, 276));

        jButton9.setText("Back to Main Menu");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        createRes1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Create New Resource", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N

        jLabel16.setText("Name");

        jLabel17.setText("Quantity");

        jLabel18.setText("Price");

        jLabel19.setText("Unit Size");

        newResQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newResQuantityActionPerformed(evt);
            }
        });

        createNewRes.setText("Create");
        createNewRes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewResActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout createRes1Layout = new org.jdesktop.layout.GroupLayout(createRes1);
        createRes1.setLayout(createRes1Layout);
        createRes1Layout.setHorizontalGroup(
            createRes1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(createRes1Layout.createSequentialGroup()
                .addContainerGap()
                .add(createRes1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(createRes1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jLabel17)
                        .add(jLabel18)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel19))
                    .add(jLabel16))
                .add(18, 18, 18)
                .add(createRes1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(newResName)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, createNewRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .add(newResQuantity)
                    .add(newResPrice)
                    .add(newResUnitSize))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, createRes1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(createdRes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 158, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(25, 25, 25))
        );
        createRes1Layout.setVerticalGroup(
            createRes1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(createRes1Layout.createSequentialGroup()
                .addContainerGap()
                .add(createRes1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(createRes1Layout.createSequentialGroup()
                        .add(createRes1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel16)
                            .add(newResName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(createRes1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(newResQuantity, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel17))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(newResPrice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel18))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createRes1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(newResUnitSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel19))
                .add(18, 18, 18)
                .add(createNewRes)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createdRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                .add(18, 18, 18))
        );

        editRes.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Edit Existing Resource", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N

        UpdateResource.setText("Update");
        UpdateResource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateResourceActionPerformed(evt);
            }
        });

        jLabel20.setText("Name");

        jLabel21.setText("Quantity");

        jLabel22.setText("Price");

        editResUnitSize.setEditable(false);
        editResUnitSize.setEnabled(false);

        jLabel23.setText("Unit Size");

        editResPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editResPriceActionPerformed(evt);
            }
        });

        editResQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editResQuantityActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout editResLayout = new org.jdesktop.layout.GroupLayout(editRes);
        editRes.setLayout(editResLayout);
        editResLayout.setHorizontalGroup(
            editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editResLayout.createSequentialGroup()
                .addContainerGap()
                .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jLabel21)
                        .add(jLabel22)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel23))
                    .add(jLabel20))
                .add(18, 18, 18)
                .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(editResName)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, UpdateResource, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .add(editResQuantity)
                    .add(editResPrice)
                    .add(editResUnitSize))
                .addContainerGap())
        );
        editResLayout.setVerticalGroup(
            editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editResLayout.createSequentialGroup()
                .addContainerGap()
                .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(editResLayout.createSequentialGroup()
                        .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel20)
                            .add(editResName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(editResQuantity, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel21))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editResPrice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel22))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(editResUnitSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel23))
                .add(18, 18, 18)
                .add(UpdateResource)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getRes.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Get Existing Resource", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N
        getRes.setName(""); // NOI18N

        getResource.setText("Get");
        getResource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getResourceActionPerformed(evt);
            }
        });

        jLabel24.setText("Name");

        JList_resources.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(JList_resources);

        org.jdesktop.layout.GroupLayout getResLayout = new org.jdesktop.layout.GroupLayout(getRes);
        getRes.setLayout(getResLayout);
        getResLayout.setHorizontalGroup(
            getResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(getResLayout.createSequentialGroup()
                .addContainerGap()
                .add(getResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(getResLayout.createSequentialGroup()
                        .add(jLabel24)
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, getResLayout.createSequentialGroup()
                        .add(getResource, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(15, 15, 15))))
            .add(getResLayout.createSequentialGroup()
                .add(getResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(getResLayout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 159, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(getResLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(resSelected, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(0, 23, Short.MAX_VALUE))
        );
        getResLayout.setVerticalGroup(
            getResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(getResLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel24)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(getResource)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resSelected, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        TruckHandlingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Truck Handling", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14), new java.awt.Color(0, 0, 0))); // NOI18N

        jLabel25.setText("Truck ID");

        jLabel26.setText("Truck Size");

        jLabel27.setText("Unit Price");

        TruckIDTextField.setEditable(false);
        TruckIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TruckIDTextFieldActionPerformed(evt);
            }
        });

        TruckSizeTextField.setEditable(false);

        EditTruckButton.setText("Edit Truck");
        EditTruckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditTruckButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout TruckHandlingPanelLayout = new org.jdesktop.layout.GroupLayout(TruckHandlingPanel);
        TruckHandlingPanel.setLayout(TruckHandlingPanelLayout);
        TruckHandlingPanelLayout.setHorizontalGroup(
            TruckHandlingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TruckHandlingPanelLayout.createSequentialGroup()
                .add(TruckHandlingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(TruckHandlingPanelLayout.createSequentialGroup()
                        .add(16, 16, 16)
                        .add(TruckHandlingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel27)
                            .add(jLabel26)
                            .add(jLabel25))
                        .add(18, 18, 18)
                        .add(TruckHandlingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(TruckSizeTextField)
                            .add(TruckUnitPriceTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                            .add(TruckIDTextField)))
                    .add(TruckHandlingPanelLayout.createSequentialGroup()
                        .add(40, 40, 40)
                        .add(EditTruckButton))
                    .add(TruckHandlingPanelLayout.createSequentialGroup()
                        .add(60, 60, 60)
                        .add(jLabel32)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        TruckHandlingPanelLayout.setVerticalGroup(
            TruckHandlingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TruckHandlingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(TruckHandlingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(TruckIDTextField))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TruckHandlingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(TruckSizeTextField)
                    .add(jLabel26, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TruckHandlingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(TruckUnitPriceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                .add(jLabel32)
                .add(22, 22, 22)
                .add(EditTruckButton)
                .addContainerGap())
        );

        GetExistingTrucksPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Get Existing Trucks", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N

        TruckList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane8.setViewportView(TruckList);

        GetAllTrucksButton.setText("Get");
        GetAllTrucksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GetAllTrucksButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout GetExistingTrucksPanelLayout = new org.jdesktop.layout.GroupLayout(GetExistingTrucksPanel);
        GetExistingTrucksPanel.setLayout(GetExistingTrucksPanelLayout);
        GetExistingTrucksPanelLayout.setHorizontalGroup(
            GetExistingTrucksPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GetExistingTrucksPanelLayout.createSequentialGroup()
                .add(23, 23, 23)
                .add(jLabel28)
                .add(182, 182, 182)
                .add(GetAllTrucksButton)
                .addContainerGap(29, Short.MAX_VALUE))
            .add(GetExistingTrucksPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane8)
                .addContainerGap())
        );
        GetExistingTrucksPanelLayout.setVerticalGroup(
            GetExistingTrucksPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GetExistingTrucksPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(GetExistingTrucksPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(GetAllTrucksButton)
                    .add(jLabel28))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        AddNewTruckPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Add New Truck", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N

        jLabel30.setText("Truck Size");

        jLabel31.setText("Unit Prize");

        AddNewTruckButton.setText("Add Truck");
        AddNewTruckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddNewTruckButtonActionPerformed1(evt);
            }
        });

        org.jdesktop.layout.GroupLayout AddNewTruckPanelLayout = new org.jdesktop.layout.GroupLayout(AddNewTruckPanel);
        AddNewTruckPanel.setLayout(AddNewTruckPanelLayout);
        AddNewTruckPanelLayout.setHorizontalGroup(
            AddNewTruckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(AddNewTruckPanelLayout.createSequentialGroup()
                .add(AddNewTruckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(AddNewTruckPanelLayout.createSequentialGroup()
                        .add(56, 56, 56)
                        .add(AddNewTruckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel30)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel31))
                        .add(18, 18, 18)
                        .add(AddNewTruckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(AddNewTruckSizeTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                            .add(AddNewTruckUnitPriceTextField)))
                    .add(AddNewTruckPanelLayout.createSequentialGroup()
                        .add(83, 83, 83)
                        .add(AddNewTruckButton))
                    .add(AddNewTruckPanelLayout.createSequentialGroup()
                        .add(75, 75, 75)
                        .add(jLabel33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        AddNewTruckPanelLayout.setVerticalGroup(
            AddNewTruckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(AddNewTruckPanelLayout.createSequentialGroup()
                .add(14, 14, 14)
                .add(AddNewTruckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel30, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(AddNewTruckSizeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(AddNewTruckPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(AddNewTruckUnitPriceTextField)
                    .add(jLabel31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(AddNewTruckButton)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout ResourceDoneLayout = new org.jdesktop.layout.GroupLayout(ResourceDone);
        ResourceDone.setLayout(ResourceDoneLayout);
        ResourceDoneLayout.setHorizontalGroup(
            ResourceDoneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ResourceDoneLayout.createSequentialGroup()
                .addContainerGap()
                .add(ResourceDoneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, ResourceDoneLayout.createSequentialGroup()
                        .add(jButton9)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, ResourceDoneLayout.createSequentialGroup()
                        .add(0, 92, Short.MAX_VALUE)
                        .add(ResourceDoneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(ResourceDoneLayout.createSequentialGroup()
                                .add(getRes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(editRes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(ResourceDoneLayout.createSequentialGroup()
                                .add(GetExistingTrucksPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(TruckHandlingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ResourceDoneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(createRes1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(AddNewTruckPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        ResourceDoneLayout.setVerticalGroup(
            ResourceDoneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ResourceDoneLayout.createSequentialGroup()
                .addContainerGap()
                .add(ResourceDoneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(getRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, editRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, createRes1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ResourceDoneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(AddNewTruckPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(GetExistingTrucksPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(TruckHandlingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 307, Short.MAX_VALUE)
                .add(jButton9)
                .addContainerGap())
        );

        mainPanel.add(ResourceDone, "resources");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        CardLayout cl = (CardLayout) (mainPanel.getLayout());


        cl.show(mainPanel, "booking");

    }//GEN-LAST:event_jButton5ActionPerformed

    private void resourcesMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resourcesMenuButtonActionPerformed
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, "resources");

    }//GEN-LAST:event_resourcesMenuButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        CardLayout cl = (CardLayout) (mainPanel.getLayout());

        cl.show(mainPanel, "menu");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void customerAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_customerAddressActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        //calculate total unit size
        int size = 0;
        for (Map.Entry<Resource, JComboBox> entry : resources.entrySet()) {
            size = size + entry.getKey().getQuantity() * entry.getKey().getUnitSize();
        }

        //Create Order object      
        con.createOrder(Integer.parseInt(customerID.getText()), size, eventAddress.getText(), startDate.getDate(), endDate.getDate());
        //  Create order detail
        for (Map.Entry<Resource, JComboBox> entry : resources.entrySet()) {
            if (entry.getValue().getSelectedIndex() != 0) {
                con.createOrderDetail(entry.getKey().getResourceID(), entry.getValue().getSelectedIndex(), entry.getKey().getResourceName());
            }
        }
        //Book outgoing trucks
        for (Map.Entry<Truck, JToggleButton> entry : truckDelivery.entrySet()) {
            if (entry.getValue().isSelected() && Integer.parseInt(entry.getValue().getText()) != 0) {
                con.truckBooking(entry.getKey().getTruckID(), entry.getKey().getTruckRun(), '0', Integer.parseInt(entry.getValue().getText()));
            }
        }
        //Book ingoing trucks
        for (Map.Entry<Truck, JToggleButton> entry : truckReturn.entrySet()) {
            if (entry.getValue().isSelected() && Integer.parseInt(entry.getValue().getText()) != 0) {
                con.truckBooking(entry.getKey().getTruckID(), entry.getKey().getTruckRun(), '1', Integer.parseInt(entry.getValue().getText()));
            }
        }
        con.createNewInvoice(Double.parseDouble(discount.getText()), totalPriceValue);

        if (con.checkOrder() == false) {
            saveOrderResultLabel.setText("Failed to save order.");
            saveOrderResult.pack();
            saveOrderResult.setVisible(true);
        } else {
            saveOrderResult.pack();
            saveOrderResultLabel.setText("Order saved succesfully.");
            saveOrderResult.setVisible(true);
        }



    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        truckDelivery.clear();
        truckReturn.clear();
        resources.clear();
        gbInventory.removeAll();
        deliveryPanel.removeAll();
        returnPanel.removeAll();
        Order.repaint();
        refreshAvailableResources(con.getAvailableResources(startDate.getDate(), endDate.getDate()));
        // 0 is for delivery, 1 for return of truck

        refreshTruckDelivery(con.getTruckDeliveryForDate(startDate.getDate(), '0'));
        refreshTruckReturn(con.getTruckDeliveryForDate(endDate.getDate(), '1'));
        Order.repaint();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        Customer c = con.createCustomer(firstName.getText(), lastName.getText(), customerAddress.getText());
        customerID.setText("" + c.getCustomerID());
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        customerList.removeAll();
        fName.setText("");
        lName.setText("");

        //keeps focus on window until closed
        searchCustomer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        searchCustomer.pack();
        searchCustomer.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void searchCustomersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchCustomersButtonActionPerformed
        searchCustomerModel.clear();
        customerList.setModel(searchCustomerModel);
        ArrayList<Customer> list = con.getCustomerList(fName.getText(), lName.getText());
        for (Customer c : list) {
            searchCustomerModel.addElement(c);
        }
    }//GEN-LAST:event_searchCustomersButtonActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        Customer selected = (Customer) customerList.getSelectedValue();
        firstName.setText(selected.getFirstName());
        lastName.setText(selected.getLastName());
        customerAddress.setText(selected.getAdress());
        customerID.setText("" + selected.getCustomerID());
        searchCustomer.setVisible(false);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        CardLayout cl = (CardLayout) (mainPanel.getLayout());

        cl.show(mainPanel, "menu");
    }//GEN-LAST:event_jButton9ActionPerformed

    private void newResQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newResQuantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newResQuantityActionPerformed

    private void createNewResActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewResActionPerformed
        String newName = newResName.getText();
        int newQuantity = Integer.parseInt(newResQuantity.getText());
        double newPrice = Double.parseDouble(newResPrice.getText());
        int newUnitSize = Integer.parseInt(newResUnitSize.getText());
        createdRes.setText(newName + " is created ");
        con.createNewResource(newName, newQuantity, newPrice, newUnitSize);
        resourceModel.clear();
        allResources = con.getAvailableResources(null, null);
        for (Resource res : allResources) {
            resourceModel.addElement(res);
        }
        JList_resources.setModel(resourceModel);

    }//GEN-LAST:event_createNewResActionPerformed

    private void UpdateResourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateResourceActionPerformed
        String newName = editResName.getText();
        int newQuantity = Integer.parseInt(editResQuantity.getText());
        double newPrice = Double.parseDouble(editResPrice.getText());
        con.editResource(newName, newQuantity, newPrice);

    }//GEN-LAST:event_UpdateResourceActionPerformed

    private void editResPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editResPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editResPriceActionPerformed

    private void editResQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editResQuantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editResQuantityActionPerformed

    private void getResourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getResourceActionPerformed
        String selectedResource = ((Resource) JList_resources.getSelectedValue()).getResourceName();
        con.getResource(selectedResource);
        editResName.setText(con.getResource(selectedResource).getResourceName());
        editResQuantity.setText(con.getResource(selectedResource).getQuantity() + "");
        editResPrice.setText(con.getResource(selectedResource).getPrice() + "");
        editResUnitSize.setText(con.getResource(selectedResource).getUnitSize() + "");
    }//GEN-LAST:event_getResourceActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

        searchCustomer.setVisible(false);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void discountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discountActionPerformed


        for (Map.Entry<Resource, JComboBox> entry : resources.entrySet()) {
            entry.getKey().setQuantity(entry.getValue().getSelectedIndex());
        }
        totalPriceValue = con.calculatePrice(resources, Integer.parseInt(discount.getText()), truckDelivery, truckReturn);
        DecimalFormat f = new DecimalFormat("##.00");
        totalPrice.setText("Total Price: " + f.format(totalPriceValue));
    }//GEN-LAST:event_discountActionPerformed

    private void customerListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerListMouseClicked

        if (customerList.isSelectionEmpty()) {
            customerOrdersModel.clear();
            customerOrdersModel.add(0, "No Customer Selected");
        } else {
            customerOrdersModel.clear();
            Customer c = (Customer) customerList.getSelectedValue();

            ArrayList<Order> orders = con.getCustomerOrderHistory(c.getCustomerID());

            for (int i = 0; i < orders.size(); i++) {
                System.out.println(orders.get(i).getOrderID());
                customerOrdersModel.add(i, orders.get(i));
            }
        }
    }//GEN-LAST:event_customerListMouseClicked

    private void customerOrdersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerOrdersMouseClicked

        if (customerOrders.isSelectionEmpty()) {
            orderDetailsModel.clear();
            orderDetailsModel.add(0, "No order selected");
        } else {
            orderDetailsModel.clear();
            Order c = (Order) customerOrders.getSelectedValue();

            ArrayList<OrderDetail> details = c.getOrderDetails();

            for (int i = 0; i < details.size(); i++) {
                orderDetailsModel.add(i, details.get(i));
            }
        }

    }//GEN-LAST:event_customerOrdersMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        saveOrderResult.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void TruckIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TruckIDTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TruckIDTextFieldActionPerformed

    private void GetAllTrucksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GetAllTrucksButtonActionPerformed
        
        if(TruckList.isSelectionEmpty()){
            jLabel28.setText("No value selected");            
            jLabel28.setVisible(true);
        }
        else{
            Truck truck = allTrucks.get(allTrucks.indexOf(TruckList.getSelectedValue()));
            TruckIDTextField.setText("" + truck.getTruckID());
            TruckSizeTextField.setText("" + truck.getSize());
            TruckUnitPriceTextField.setText("" + truck.getUnitPrice());
        }
        
        
        
    }//GEN-LAST:event_GetAllTrucksButtonActionPerformed

    private void EditTruckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditTruckButtonActionPerformed
        int truckID = Integer.parseInt(TruckIDTextField.getText());
        int truckUnitPrice = Integer.parseInt(TruckUnitPriceTextField.getText());
        if(con.editTruck(truckID, truckUnitPrice)){
            jLabel32.setText("Truck edited!");
        }
        else{
            jLabel32.setText("There was an error editing the truck");
        }
    }//GEN-LAST:event_EditTruckButtonActionPerformed

    private void AddNewTruckButtonActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNewTruckButtonActionPerformed1
        int truckSize = Integer.parseInt(AddNewTruckSizeTextField.getText());
        int unitPrice = Integer.parseInt(AddNewTruckUnitPriceTextField.getText());
        
        if(con.createTruck(truckSize, unitPrice)){
            jLabel33.setText("Truck Created");
        }
        else{
            jLabel33.setText("There was an error creating the truck");
        }
    }//GEN-LAST:event_AddNewTruckButtonActionPerformed1

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PartyRentalGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PartyRentalGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PartyRentalGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PartyRentalGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PartyRentalGUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddNewTruckButton;
    private javax.swing.JPanel AddNewTruckPanel;
    private javax.swing.JTextField AddNewTruckSizeTextField;
    private javax.swing.JTextField AddNewTruckUnitPriceTextField;
    private javax.swing.JPanel Booking;
    private javax.swing.JPanel Customer;
    private javax.swing.JButton EditTruckButton;
    private javax.swing.JButton GetAllTrucksButton;
    private javax.swing.JPanel GetExistingTrucksPanel;
    private javax.swing.JList JList_resources;
    private javax.swing.JPanel Menu;
    private javax.swing.JPanel Order;
    private javax.swing.JPanel ResourceDone;
    private javax.swing.JPanel TruckHandlingPanel;
    private javax.swing.JTextField TruckIDTextField;
    private javax.swing.JList TruckList;
    private javax.swing.JTextField TruckSizeTextField;
    private javax.swing.JTextField TruckUnitPriceTextField;
    private javax.swing.JButton UpdateResource;
    private javax.swing.JButton confirm;
    private javax.swing.JButton createNewRes;
    private javax.swing.JPanel createRes1;
    private javax.swing.JLabel createdRes;
    private javax.swing.JTextField customerAddress;
    private javax.swing.JTextField customerID;
    private javax.swing.JList customerList;
    private javax.swing.JList customerOrders;
    private javax.swing.JPanel deliveryPanel;
    private javax.swing.JTextField discount;
    private javax.swing.JPanel editRes;
    private javax.swing.JTextField editResName;
    private javax.swing.JTextField editResPrice;
    private javax.swing.JTextField editResQuantity;
    private javax.swing.JTextField editResUnitSize;
    private com.toedter.calendar.JDateChooser endDate;
    private javax.swing.JTextField eventAddress;
    private javax.swing.JTextField fName;
    private javax.swing.JTextField firstName;
    private javax.swing.JPanel gbInventory;
    private javax.swing.JPanel getRes;
    private javax.swing.JButton getResource;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField lName;
    private javax.swing.JTextField lastName;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField newResName;
    private javax.swing.JTextField newResPrice;
    private javax.swing.JTextField newResQuantity;
    private javax.swing.JTextField newResUnitSize;
    private javax.swing.JList orderDetailsList;
    private javax.swing.JLabel remainingDelivery;
    private javax.swing.JLabel remainingReturn;
    private javax.swing.JLabel resSelected;
    private javax.swing.JButton resourcesMenuButton;
    private javax.swing.JPanel returnPanel;
    private javax.swing.JDialog saveOrderResult;
    private javax.swing.JLabel saveOrderResultLabel;
    private javax.swing.JDialog searchCustomer;
    private javax.swing.JButton searchCustomersButton;
    private com.toedter.calendar.JDateChooser startDate;
    private javax.swing.JLabel totalPrice;
    private javax.swing.JLabel totalSize;
    // End of variables declaration//GEN-END:variables
}

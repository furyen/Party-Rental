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
import java.awt.Toolkit;
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
    private double totalPriceValue;
    private ArrayList<Order> allOrders = null;
    private DefaultListModel ordersModel = new DefaultListModel();
    private DefaultListModel selectedOrderDetailModel = new DefaultListModel();
    private Order editOrder;
    private DefaultListModel searchCustomerModel2 = new DefaultListModel();
    private ArrayList<Order> filteredOrdersByName = new ArrayList();

    public PartyRentalGUI() {
        initComponents();
        //Centers the program in the middle of the screen
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        con.getConnection();
        TruckList.setModel(truckListModel);
        customerOrders.setModel(customerOrdersModel);
        orderDetailsList.setModel(orderDetailsModel);
        activeResButtonGroup.add(activeRes);
        activeResButtonGroup.add(inactiveRes);

        searchOrdersButtonGroup.add(depositPaidRadio);
        searchOrdersButtonGroup.add(nothingPaidRadio);
        searchOrdersButtonGroup.add(searchByName);
        searchOrdersButtonGroup.add(fullyPaidRadio);

        tentPartButtonGroup.add(tentPart);
        tentPartButtonGroup.add(notTentPart);

        tentPartButtonGroup2.add(tentPart2);
        tentPartButtonGroup2.add(notTentPart2);
    }
    //Shows all available resources in the "Make Booking" menu for a given period of time using a GridBagLayout
    //Since the resources are dynamic we need to specify Constraints manually and declare them when adding a new component instead of using the Layout Manager in NetBeans

    public void refreshAvailableResources(ArrayList<Resource> inventory) {

        int totalItems = inventory.size();
        int added = 0;
        //Used to control the layout of the Grid Bag
        GridBagConstraints gbc = new GridBagConstraints();
        //Fully fills the "cell" of the Grid Bag both horizontally and vertically (In this case it resizes all the JComboBoxes to the same size)
        gbc.fill = GridBagConstraints.BOTH;
        //Specify the cell you are currently working on 
        gbc.gridy = 0;
        gbc.gridx = 2;

        gbInventory.add(new JLabel("Available"), gbc);
        gbc.gridx = 6;
        gbInventory.add(new JLabel("Price"), gbc);


        gbc.gridy = 2;
        //A loop that makes sure all available resources are shown.
        while (added < totalItems) {

            gbc.gridx = 0;

            if (inventory.get(added).isTentPart()) {
                gbInventory.add(new JLabel(inventory.get(added).getResourceName() + "(T)"), gbc);
            } else {
                gbInventory.add(new JLabel(inventory.get(added).getResourceName()), gbc);
            }
            gbc.gridx = 2;
            gbInventory.add(new JLabel("" + inventory.get(added).getQuantity()), gbc);
            gbc.gridx = 4;

            //set the dropdown items in the Combo Box
            Integer[] quantity = new Integer[inventory.get(added).getQuantity() + 1];
            for (int i = 0; i < inventory.get(added).getQuantity() + 1; i++) {
                quantity[i] = i;
            }

            resources.put(inventory.get(added), new JComboBox(quantity));
            //Action listener to update total price ,total unit size etc. in the GUI
            resources.get(inventory.get(added)).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int totalUnits = 0;
                    //Iterate through all Combo Boxes to properly show total unit size
                    for (Map.Entry<Resource, JComboBox> entry : resources.entrySet()) {
                        entry.getKey().setQuantity(entry.getValue().getSelectedIndex());
                        totalUnits = totalUnits + entry.getKey().getUnitSize() * entry.getValue().getSelectedIndex();
                    }
                    //Resets filled trucks(if any) so that units can be allocated appropriately to delivering trucks
                    for (Map.Entry<Truck, JToggleButton> entry : truckDelivery.entrySet()) {
                        if (entry.getValue().isSelected()) {
                            entry.getValue().doClick();
                        }
                    }
                    //Resets filled trucks(if any) so that units can be allocated appropriately to returning trucks
                    for (Map.Entry<Truck, JToggleButton> entry : truckReturn.entrySet()) {
                        if (entry.getValue().isSelected()) {
                            entry.getValue().doClick();
                        }
                    }

                    totalPriceValue = con.calculatePrice(resources, Integer.parseInt(discount.getText()), truckDelivery, truckReturn);
                    //Limit the total price to have only 2 decimal places
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
    //Shows all delivery trucks in the "Make Booking" menu for a given date using a GridBagLayout
    //Since the trucks are dynamic we need to specify Constraints manually and declare them when adding a new component instead of using the Layout Manager in NetBeans

    public void refreshTruckDelivery(ArrayList<Truck> truckRuns) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        //Makes sure there is space between the cells in a row so they don't clump together in the center 
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
            //Move one row down with every added truck
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
            //Makes the neccessary changes in the GUI to show all information when filling a truck (units left to allocate,filled space etc.)
            truckDelivery.get(truck).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JToggleButton button = (JToggleButton) e.getSource();
                    //Goes through all "Fill Truck" buttons to find the one that has actually been clicked (This is because they are dynamic and not defined with names)
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

    //Works the same way as refreshTruckDelivery 
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
        spEventAddress = new javax.swing.JLabel();
        spTotalPrice = new javax.swing.JLabel();
        spPaid = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        orderDetailsList = new javax.swing.JList();
        saveOrderResult = new javax.swing.JDialog();
        saveOrderResultLabel = new javax.swing.JLabel();
        confirm = new javax.swing.JButton();
        paymentEditingDonePopup = new javax.swing.JDialog();
        paymentEditedLabel = new javax.swing.JLabel();
        OK = new javax.swing.JButton();
        paymentStatus = new javax.swing.JDialog();
        jLabel40 = new javax.swing.JLabel();
        oldPaymentTextField = new javax.swing.JTextField();
        saveNewPayment = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        newPaymentTextField = new javax.swing.JTextField();
        activeResButtonGroup = new javax.swing.ButtonGroup();
        searchCustomer2 = new javax.swing.JDialog();
        searchChoose = new javax.swing.JButton();
        searchCancel = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane13 = new javax.swing.JScrollPane();
        customerList2 = new javax.swing.JList();
        searchOrdersButtonGroup = new javax.swing.ButtonGroup();
        editConfirmation = new javax.swing.JDialog();
        jLabel7 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        tentPartButtonGroup = new javax.swing.ButtonGroup();
        tentPartButtonGroup2 = new javax.swing.ButtonGroup();
        resourceEdited = new javax.swing.JDialog();
        jLabel15 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        resourceCreated = new javax.swing.JDialog();
        jLabel29 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        trucksRequiredPopup = new javax.swing.JDialog();
        jLabel43 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        Menu = new javax.swing.JPanel();
        bookingMenuButton = new javax.swing.JButton();
        resourcesMenuButton = new javax.swing.JButton();
        eventManagementButton = new javax.swing.JButton();
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
        getAvailableResourcesButton = new javax.swing.JButton();
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
        tentPart2 = new javax.swing.JRadioButton();
        notTentPart2 = new javax.swing.JRadioButton();
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
        activeRes = new javax.swing.JRadioButton();
        inactiveRes = new javax.swing.JRadioButton();
        tentPart = new javax.swing.JRadioButton();
        notTentPart = new javax.swing.JRadioButton();
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
        EventManagement = new javax.swing.JPanel();
        orders = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        orderList = new javax.swing.JList();
        depositPaidRadio = new javax.swing.JRadioButton();
        nothingPaidRadio = new javax.swing.JRadioButton();
        searchByName = new javax.swing.JRadioButton();
        fullyPaidRadio = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        depositPaidButton = new javax.swing.JButton();
        editOrderButton = new javax.swing.JButton();
        cancelOrderButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        firstNameSearch = new javax.swing.JTextField();
        lastNameSearch = new javax.swing.JTextField();
        searchCustomerButton = new javax.swing.JButton();
        orderDetails = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        orderDetailList = new javax.swing.JList();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        discountLabel = new javax.swing.JLabel();
        truckDeliverLabel = new javax.swing.JLabel();
        truckReturnLabel = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        fullPriceLabel = new javax.swing.JLabel();
        paidAmount = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cancelledLabel = new javax.swing.JLabel();
        backToMenu = new javax.swing.JButton();

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
            .add(jPanel1Layout.createSequentialGroup()
                .add(0, 0, Short.MAX_VALUE)
                .add(jLabel12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchCustomersButton))
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3)
                .addContainerGap())
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
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
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

        spEventAddress.setText("Event Address: ");

        spTotalPrice.setText("Total Price: ");

        spPaid.setText("Paid: ");

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
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane5)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(spEventAddress)
                            .add(spPaid)
                            .add(spTotalPrice))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(spEventAddress)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spPaid)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spTotalPrice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout searchCustomerLayout = new org.jdesktop.layout.GroupLayout(searchCustomer.getContentPane());
        searchCustomer.getContentPane().setLayout(searchCustomerLayout);
        searchCustomerLayout.setHorizontalGroup(
            searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(searchCustomerLayout.createSequentialGroup()
                .addContainerGap()
                .add(searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(searchCustomerLayout.createSequentialGroup()
                        .add(jButton11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 572, Short.MAX_VALUE)
                        .add(jButton10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(searchCustomerLayout.createSequentialGroup()
                        .add(searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(27, 27, 27)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        searchCustomerLayout.setVerticalGroup(
            searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(searchCustomerLayout.createSequentialGroup()
                .addContainerGap()
                .add(searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(searchCustomerLayout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(searchCustomerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton11)
                    .add(jButton10))
                .addContainerGap())
        );

        saveOrderResult.setBounds(new java.awt.Rectangle(300, 300, 0, 0));
        saveOrderResult.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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

        paymentEditingDonePopup.setMinimumSize(new java.awt.Dimension(355, 155));
        paymentEditingDonePopup.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        paymentEditedLabel.setText("Payment is edited!");
        paymentEditingDonePopup.getContentPane().add(paymentEditedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, -1, -1));

        OK.setText("OK");
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKActionPerformed(evt);
            }
        });
        paymentEditingDonePopup.getContentPane().add(OK, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, -1, -1));

        paymentStatus.setBounds(new java.awt.Rectangle(300, 300, 0, 0));
        paymentStatus.setMinimumSize(new java.awt.Dimension(355, 155));
        paymentStatus.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel40.setText("Payment until now");
        paymentStatus.getContentPane().add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 20, 136, -1));

        oldPaymentTextField.setEnabled(false);
        oldPaymentTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oldPaymentTextFieldActionPerformed(evt);
            }
        });
        paymentStatus.getContentPane().add(oldPaymentTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(208, 12, 80, 30));

        saveNewPayment.setText("Save");
        saveNewPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNewPaymentActionPerformed(evt);
            }
        });
        paymentStatus.getContentPane().add(saveNewPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 94, -1, -1));

        jLabel41.setText("Payment new amount");
        paymentStatus.getContentPane().add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 54, 136, -1));

        newPaymentTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPaymentTextFieldActionPerformed(evt);
            }
        });
        paymentStatus.getContentPane().add(newPaymentTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(208, 48, 80, -1));

        searchCustomer2.setBounds(new java.awt.Rectangle(300, 300, 900, 300));
        searchCustomer2.setMinimumSize(new java.awt.Dimension(470, 267));
        searchCustomer2.setResizable(false);
        searchCustomer2.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        searchChoose.setText("Choose");
        searchChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchChooseActionPerformed(evt);
            }
        });
        searchCustomer2.getContentPane().add(searchChoose, new org.netbeans.lib.awtextra.AbsoluteConstraints(297, 223, 167, -1));

        searchCancel.setText("Cancel");
        searchCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchCancelActionPerformed(evt);
            }
        });
        searchCustomer2.getContentPane().add(searchCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 223, 130, -1));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Search for customer", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 2, 14))); // NOI18N
        jPanel8.setMaximumSize(new java.awt.Dimension(420, 267));
        jPanel8.setMinimumSize(new java.awt.Dimension(420, 267));

        customerList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customerList2MouseClicked(evt);
            }
        });
        jScrollPane13.setViewportView(customerList2);

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel8Layout.createSequentialGroup()
                .add(0, 0, Short.MAX_VALUE)
                .add(jScrollPane13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 445, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addContainerGap())
        );

        searchCustomer2.getContentPane().add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, -1));

        editConfirmation.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);

        jLabel7.setText("Are you sure you want to edit the selected order?");

        jButton4.setText("Yes");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton8.setText("No");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout editConfirmationLayout = new org.jdesktop.layout.GroupLayout(editConfirmation.getContentPane());
        editConfirmation.getContentPane().setLayout(editConfirmationLayout);
        editConfirmationLayout.setHorizontalGroup(
            editConfirmationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editConfirmationLayout.createSequentialGroup()
                .addContainerGap()
                .add(editConfirmationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel7)
                    .add(editConfirmationLayout.createSequentialGroup()
                        .add(jButton4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButton8)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        editConfirmationLayout.setVerticalGroup(
            editConfirmationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editConfirmationLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(editConfirmationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton4)
                    .add(jButton8))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jLabel15.setText("Resource is successfully edited!");

        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed1(evt);
            }
        });

        org.jdesktop.layout.GroupLayout resourceEditedLayout = new org.jdesktop.layout.GroupLayout(resourceEdited.getContentPane());
        resourceEdited.getContentPane().setLayout(resourceEditedLayout);
        resourceEditedLayout.setHorizontalGroup(
            resourceEditedLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(resourceEditedLayout.createSequentialGroup()
                .add(resourceEditedLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(resourceEditedLayout.createSequentialGroup()
                        .add(46, 46, 46)
                        .add(jLabel15))
                    .add(resourceEditedLayout.createSequentialGroup()
                        .add(108, 108, 108)
                        .add(jButton2)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        resourceEditedLayout.setVerticalGroup(
            resourceEditedLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(resourceEditedLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel15)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton2)
                .add(12, 12, 12))
        );

        jLabel29.setText("Resource is successfully created!");

        jButton5.setText("OK");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout resourceCreatedLayout = new org.jdesktop.layout.GroupLayout(resourceCreated.getContentPane());
        resourceCreated.getContentPane().setLayout(resourceCreatedLayout);
        resourceCreatedLayout.setHorizontalGroup(
            resourceCreatedLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, resourceCreatedLayout.createSequentialGroup()
                .addContainerGap(54, Short.MAX_VALUE)
                .add(jLabel29)
                .add(46, 46, 46))
            .add(resourceCreatedLayout.createSequentialGroup()
                .add(109, 109, 109)
                .add(jButton5)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        resourceCreatedLayout.setVerticalGroup(
            resourceCreatedLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(resourceCreatedLayout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .add(jLabel29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton5)
                .add(12, 12, 12))
        );

        trucksRequiredPopup.setPreferredSize(null);

        jLabel43.setText("You have tent in the order. You have to select trucks for delivery and return.");

        jButton12.setText("OK");

        org.jdesktop.layout.GroupLayout trucksRequiredPopupLayout = new org.jdesktop.layout.GroupLayout(trucksRequiredPopup.getContentPane());
        trucksRequiredPopup.getContentPane().setLayout(trucksRequiredPopupLayout);
        trucksRequiredPopupLayout.setHorizontalGroup(
            trucksRequiredPopupLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(trucksRequiredPopupLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(trucksRequiredPopupLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jButton12)
                    .add(jLabel43))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        trucksRequiredPopupLayout.setVerticalGroup(
            trucksRequiredPopupLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(trucksRequiredPopupLayout.createSequentialGroup()
                .add(19, 19, 19)
                .add(jLabel43)
                .add(18, 18, 18)
                .add(jButton12)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(300, 150, 0, 0));
        setResizable(false);

        mainPanel.setLayout(new java.awt.CardLayout());

        bookingMenuButton.setText("Make a booking");
        bookingMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookingMenuButtonActionPerformed(evt);
            }
        });

        resourcesMenuButton.setText("Create and Edit Resources\n");
        resourcesMenuButton.setMargin(null);
        resourcesMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resourcesMenuButtonActionPerformed(evt);
            }
        });

        eventManagementButton.setText("Event Management");
        eventManagementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventManagementButtonActionPerformed1(evt);
            }
        });

        org.jdesktop.layout.GroupLayout MenuLayout = new org.jdesktop.layout.GroupLayout(Menu);
        Menu.setLayout(MenuLayout);
        MenuLayout.setHorizontalGroup(
            MenuLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MenuLayout.createSequentialGroup()
                .add(80, 80, 80)
                .add(bookingMenuButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 161, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(resourcesMenuButton)
                .add(37, 37, 37)
                .add(eventManagementButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 196, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(257, Short.MAX_VALUE))
        );
        MenuLayout.setVerticalGroup(
            MenuLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MenuLayout.createSequentialGroup()
                .add(216, 216, 216)
                .add(MenuLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(eventManagementButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(MenuLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(bookingMenuButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(resourcesMenuButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(442, Short.MAX_VALUE))
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

        getAvailableResourcesButton.setText("Get Available Resources");
        getAvailableResourcesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getAvailableResourcesButtonActionPerformed(evt);
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
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 186, Short.MAX_VALUE)
                                .add(getAvailableResourcesButton))
                            .add(eventAddress)))
                    .add(OrderLayout.createSequentialGroup()
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 298, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(OrderLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(totalSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(OrderLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 190, Short.MAX_VALUE)
                                .add(jLabel14)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(discount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel2)
                                .add(26, 26, 26)
                                .add(totalPrice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(OrderLayout.createSequentialGroup()
                                .add(18, 18, 18)
                                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jTabbedPane1)
                                    .add(OrderLayout.createSequentialGroup()
                                        .add(remainingDelivery, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(remainingReturn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 169, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                        .add(11, 11, 11)))
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
                        .add(getAvailableResourcesButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)))
                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(eventAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(OrderLayout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 288, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(remainingDelivery, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(remainingReturn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, OrderLayout.createSequentialGroup()
                        .add(25, 25, 25)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 305, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(0, 0, 0)
                .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(OrderLayout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(OrderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(discount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel2)
                            .add(totalPrice, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(totalSize, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, BookingLayout.createSequentialGroup()
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(Customer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(BookingLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton7)
                    .add(jButton1))
                .addContainerGap(108, Short.MAX_VALUE))
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
        createRes1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setText("Name");
        createRes1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 31, -1, -1));

        jLabel17.setText("Quantity");
        createRes1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 65, -1, -1));

        jLabel18.setText("Price");
        createRes1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 105, -1, -1));

        jLabel19.setText("Unit Size");
        createRes1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 139, -1, -1));
        createRes1.add(newResName, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 25, 194, -1));

        newResQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newResQuantityActionPerformed(evt);
            }
        });
        createRes1.add(newResQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 59, 194, -1));
        createRes1.add(newResPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 93, 194, -1));
        createRes1.add(newResUnitSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 127, 194, -1));

        createNewRes.setText("Create");
        createNewRes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewResActionPerformed(evt);
            }
        });
        createRes1.add(createNewRes, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 200, 194, -1));

        tentPart2.setText("Tent Part");
        createRes1.add(tentPart2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 170, -1, -1));

        notTentPart2.setText("Not Tent");
        notTentPart2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notTentPart2ActionPerformed(evt);
            }
        });
        createRes1.add(notTentPart2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 170, -1, -1));

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

        activeRes.setText("Active");

        inactiveRes.setText("Inacative");
        inactiveRes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inactiveResActionPerformed(evt);
            }
        });

        tentPart.setText("Tent Part");

        notTentPart.setText("Not Tent");
        notTentPart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notTentPartActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout editResLayout = new org.jdesktop.layout.GroupLayout(editRes);
        editRes.setLayout(editResLayout);
        editResLayout.setHorizontalGroup(
            editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(editResLayout.createSequentialGroup()
                .addContainerGap()
                .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(editResLayout.createSequentialGroup()
                        .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel21)
                            .add(jLabel22)
                            .add(jLabel20))
                        .add(19, 19, 19)
                        .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(editResName)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, UpdateResource, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(editResQuantity)
                            .add(editResPrice)))
                    .add(editResLayout.createSequentialGroup()
                        .add(jLabel23)
                        .add(18, 18, 18)
                        .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(editResUnitSize)
                            .add(editResLayout.createSequentialGroup()
                                .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(editResLayout.createSequentialGroup()
                                        .add(tentPart)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(notTentPart))
                                    .add(editResLayout.createSequentialGroup()
                                        .add(activeRes)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(inactiveRes)))
                                .add(19, 19, 19)))))
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(editResUnitSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(inactiveRes)
                    .add(activeRes))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editResLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(notTentPart)
                    .add(tentPart))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(UpdateResource)
                .add(19, 19, 19))
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        TruckIDTextField.setEnabled(false);
        TruckIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TruckIDTextFieldActionPerformed(evt);
            }
        });

        TruckSizeTextField.setEditable(false);
        TruckSizeTextField.setEnabled(false);

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
                        .add(AddNewTruckButton)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, AddNewTruckPanelLayout.createSequentialGroup()
                .add(0, 0, Short.MAX_VALUE)
                .add(jLabel33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 217, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(28, 28, 28))
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
                        .add(0, 113, Short.MAX_VALUE)
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
                    .add(editRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(createRes1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(getRes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(30, 30, 30)
                .add(ResourceDoneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(AddNewTruckPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(GetExistingTrucksPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(TruckHandlingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 232, Short.MAX_VALUE)
                .add(jButton9)
                .addContainerGap())
        );

        mainPanel.add(ResourceDone, "resources");

        EventManagement.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Event Managenent", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N
        EventManagement.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        orders.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Orders", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14), java.awt.Color.black)); // NOI18N
        orders.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        orderList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        orderList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                orderListMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(orderList);

        orders.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 67, 740, 140));

        depositPaidRadio.setText("Deposit Paid");
        depositPaidRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depositPaidRadioActionPerformed(evt);
            }
        });
        orders.add(depositPaidRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 210, -1, -1));

        nothingPaidRadio.setText("Nothing Paid");
        nothingPaidRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nothingPaidRadioActionPerformed(evt);
            }
        });
        orders.add(nothingPaidRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 210, -1, -1));

        searchByName.setText("Only Name");
        orders.add(searchByName, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 240, -1, -1));

        fullyPaidRadio.setText("Fully Paid");
        fullyPaidRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullyPaidRadioActionPerformed(evt);
            }
        });
        orders.add(fullyPaidRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 240, 147, -1));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Edit", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14), java.awt.Color.black)); // NOI18N

        depositPaidButton.setText("Payment Status");
        depositPaidButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depositPaidButtonActionPerformed(evt);
            }
        });

        editOrderButton.setText("Edit Order");
        editOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editOrderButtonActionPerformed(evt);
            }
        });

        cancelOrderButton.setText("Cancel Order");
        cancelOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelOrderButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(79, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(depositPaidButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(editOrderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cancelOrderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(69, 69, 69))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(depositPaidButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(editOrderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(cancelOrderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 15, Short.MAX_VALUE))
        );

        orders.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 270, 330, 130));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Search", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel34.setText("First Name");
        jPanel5.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(98, 31, -1, -1));

        jLabel35.setText("Last Name");
        jPanel5.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 65, -1, -1));
        jPanel5.add(firstNameSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 25, 90, 30));
        jPanel5.add(lastNameSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 59, 90, 30));

        searchCustomerButton.setText("Search");
        searchCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchCustomerButtonActionPerformed(evt);
            }
        });
        jPanel5.add(searchCustomerButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, -1, -1));

        orders.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 350, 130));

        EventManagement.add(orders, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 800, 410));

        orderDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Order Details", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14)))); // NOI18N
        orderDetails.setPreferredSize(new java.awt.Dimension(394, 395));
        orderDetails.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        orderDetailList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "No order selected" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane10.setViewportView(orderDetailList);

        orderDetails.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 260, 150));

        jLabel36.setText("Address");
        orderDetails.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 130, -1, 20));

        jLabel37.setText("Discount");
        orderDetails.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 40, -1, 20));

        jLabel38.setText("Truck delivering");
        orderDetails.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 70, -1, 20));

        jLabel6.setText("Truck return");
        orderDetails.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 100, -1, 20));
        orderDetails.add(addressLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 130, 220, 20));
        orderDetails.add(discountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 40, 70, 20));
        orderDetails.add(truckDeliverLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 70, 70, 20));
        orderDetails.add(truckReturnLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 100, 70, 20));

        jLabel39.setText("Full Price");
        orderDetails.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 40, -1, 20));
        orderDetails.add(fullPriceLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 40, 70, 20));
        orderDetails.add(paidAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 70, 70, 20));

        jLabel42.setText("Paid Amount");
        orderDetails.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 70, -1, 20));

        jLabel11.setText("Cancelled");
        orderDetails.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 100, -1, 20));
        orderDetails.add(cancelledLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 100, 70, 20));

        EventManagement.add(orderDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, 800, 200));

        backToMenu.setText("Back to Main Menu");
        backToMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backToMenuActionPerformed(evt);
            }
        });
        EventManagement.add(backToMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 640, -1, -1));

        mainPanel.add(EventManagement, "eventManagement");

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

    private void bookingMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookingMenuButtonActionPerformed

        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        truckDelivery.clear();
        truckReturn.clear();
        resources.clear();
        gbInventory.removeAll();
        deliveryPanel.removeAll();
        returnPanel.removeAll();
        Order.repaint();

        cl.show(mainPanel, "booking");

    }//GEN-LAST:event_bookingMenuButtonActionPerformed

    private void resourcesMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resourcesMenuButtonActionPerformed
        CardLayout cl = (CardLayout) (mainPanel.getLayout());

        allTrucks = con.getTrucks();

        for (Truck truck : allTrucks) {
            truckListModel.addElement(truck);
        }
        allResources = con.getAvailableResources(null, null);
        for (Resource res : allResources) {
            resourceModel.addElement(res);
        }
        JList_resources.setModel(resourceModel);

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
        boolean truckRequired;



        //calculate total unit size
        int totalUnitSize = 0;
        for (Map.Entry<Resource, JComboBox> entry : resources.entrySet()) {
            totalUnitSize = totalUnitSize + entry.getKey().getQuantity() * entry.getKey().getUnitSize();
            if (entry.getKey().isTentPart() && entry.getValue().getSelectedIndex() != 0) {
                truckRequired = true;
            } else {
                truckRequired = false;
            }
        }

        if (truckRequired = true) {
            if (unitsDeliver == 0 && unitsReturn == 0) {
                saveOrder(totalUnitSize);

            } else {
                trucksRequiredPopup.pack();
                trucksRequiredPopup.setVisible(true);
            }
        } else{
            saveOrder(totalUnitSize);
        }
    }//GEN-LAST:event_jButton7ActionPerformed
    private void saveOrder(int totalUnitSize) {
        //Create Order object     
        if (editOrder != null) {
            if (editOrder.getPaidAmount() == 0) {
                con.createOrder(Integer.parseInt(customerID.getText()), totalUnitSize, eventAddress.getText(), startDate.getDate(), endDate.getDate(), 0);
            } else {
                con.createOrder(Integer.parseInt(customerID.getText()), totalUnitSize, eventAddress.getText(), startDate.getDate(), endDate.getDate(), editOrder.getPaidAmount());
            }
            editOrder = null;
        } else {
            con.createOrder(Integer.parseInt(customerID.getText()), totalUnitSize, eventAddress.getText(), startDate.getDate(), endDate.getDate(), 0);
        }

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
        //Create invoice file
        con.createNewInvoice(Double.parseDouble(discount.getText()), totalPriceValue);

        if (con.checkOrder() == false) {
            saveOrderResultLabel.setText("Failed to save order.");
            saveOrderResult.pack();
            saveOrderResult.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
            saveOrderResult.setVisible(true);
        } else {
            saveOrderResult.pack();
            saveOrderResult.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
            saveOrderResultLabel.setText("Order saved succesfully.");
            saveOrderResult.setVisible(true);
        }

    }
    //Clears the currently shown resources and trucks and shows them with the specified dates
    private void getAvailableResourcesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getAvailableResourcesButtonActionPerformed
        truckDelivery.clear();
        truckReturn.clear();
        resources.clear();
        gbInventory.removeAll();
        deliveryPanel.removeAll();
        returnPanel.removeAll();
        refreshAvailableResources(con.getAvailableResources(startDate.getDate(), endDate.getDate()));
        // 0 is for delivery, 1 for return of trucks
        refreshTruckDelivery(con.getTruckDeliveryForDate(startDate.getDate(), '0'));
        refreshTruckReturn(con.getTruckDeliveryForDate(endDate.getDate(), '1'));
        Order.repaint();
    }//GEN-LAST:event_getAvailableResourcesButtonActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        Customer c = con.createCustomer(firstName.getText().toUpperCase(), lastName.getText().toUpperCase(), customerAddress.getText());
        customerID.setText("" + c.getCustomerID());
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        customerList.removeAll();
        fName.setText("");
        lName.setText("");
        spTotalPrice.setText("Total Price: ");
        spPaid.setText("Paid: ");
        spEventAddress.setText("Event Address: ");
        searchCustomerModel.clear();
        customerOrdersModel.clear();
        orderDetailsModel.clear();
        //keeps focus on window until closed
        searchCustomer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        searchCustomer.pack();
        searchCustomer.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        searchCustomer.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void searchCustomersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchCustomersButtonActionPerformed
        searchCustomerModel.clear();

        customerList.setModel(searchCustomerModel);
        ArrayList<Customer> list = con.getCustomerList(fName.getText().toUpperCase(), lName.getText().toUpperCase());
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
        resourceModel.clear();
        truckListModel.clear();
        editResName.setText("");
        editResQuantity.setText("");
        editResPrice.setText("");
        editResUnitSize.setText("");
        newResName.setText("");
        newResQuantity.setText("");
        newResPrice.setText("");
        newResUnitSize.setText("");
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
        boolean isTent = tentPart2.isSelected();
        boolean changeMade = con.createNewResource(newName, newQuantity, newPrice, newUnitSize, isTent);
        resourceModel.clear();
        allResources = con.getAvailableResources(null, null);
        for (Resource res : allResources) {
            resourceModel.addElement(res);
        }
        JList_resources.setModel(resourceModel);
        newResName.setText("");
        newResQuantity.setText("");
        newResPrice.setText("");
        newResUnitSize.setText("");

        if (changeMade == true) {
            resourceCreated.pack();
            resourceCreated.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
            resourceCreated.setVisible(true);
        }

    }//GEN-LAST:event_createNewResActionPerformed

    private void UpdateResourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateResourceActionPerformed
        String newName = editResName.getText();
        int newQuantity = Integer.parseInt(editResQuantity.getText());
        double newPrice = Double.parseDouble(editResPrice.getText());
        boolean active = activeRes.isSelected();
        boolean tent = tentPart.isSelected();
        boolean changeMade = con.editResource(newName, newQuantity, newPrice, active, tent);
        editResName.setText("");
        editResQuantity.setText("");
        editResPrice.setText("");
        editResUnitSize.setText("");

        if (changeMade == true) {
            resourceEdited.pack();
            resourceEdited.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
            resourceEdited.setVisible(true);
        }


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
        System.out.println(selectedResource + "is tent part" + con.getResource(selectedResource).isTentPart());
        System.out.println(selectedResource + "is active" + con.getResource(selectedResource).isActive());
        if (con.getResource(selectedResource).isTentPart()) {

            tentPart.setSelected(true);
        } else {
            notTentPart.setSelected(true);
        }
        if (con.getResource(selectedResource).isActive()) {
            activeRes.setSelected(true);
        } else {
            inactiveRes.setSelected(true);
        }
    }//GEN-LAST:event_getResourceActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

        searchCustomer.setVisible(false);
    }//GEN-LAST:event_jButton11ActionPerformed
    //Takes the value from the discount field and updates the totalPrice
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

            ArrayList<Order> order = con.getCustomerOrderHistory(c.getCustomerID());

            for (int i = 0; i < order.size(); i++) {
                System.out.println(order.get(i).getOrderID());
                customerOrdersModel.add(i, order.get(i));
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
            spEventAddress.setText("Event Address: " + c.getAdress());
            spPaid.setText("Paid: " + c.getPaidAmount());
            spTotalPrice.setText("Total Price: " + c.getFullPrice());

            ArrayList<OrderDetail> details = c.getOrderDetails();

            for (int i = 0; i < details.size(); i++) {
                orderDetailsModel.add(i, details.get(i));
            }
        }

    }//GEN-LAST:event_customerOrdersMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        saveOrderResult.pack();
        saveOrderResult.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void TruckIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TruckIDTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TruckIDTextFieldActionPerformed

    private void GetAllTrucksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GetAllTrucksButtonActionPerformed

        if (TruckList.isSelectionEmpty()) {
            jLabel28.setText("No value selected");
            jLabel28.setVisible(true);
        } else {
            Truck truck = allTrucks.get(allTrucks.indexOf(TruckList.getSelectedValue()));
            TruckIDTextField.setText("" + truck.getTruckID());
            TruckSizeTextField.setText("" + truck.getSize());
            TruckUnitPriceTextField.setText("" + truck.getUnitPrice());
        }



    }//GEN-LAST:event_GetAllTrucksButtonActionPerformed

    private void EditTruckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditTruckButtonActionPerformed
        int truckID = Integer.parseInt(TruckIDTextField.getText());
        double truckUnitPrice = Double.parseDouble(TruckUnitPriceTextField.getText());
        if (con.editTruck(truckID, truckUnitPrice)) {
            jLabel32.setText("Truck edited!");
            TruckIDTextField.setText("");
            TruckSizeTextField.setText("");
            TruckUnitPriceTextField.setText("");
            allTrucks = con.getTrucks();
            truckListModel.clear();
            TruckList.setModel(truckListModel);
            for (Truck truck : allTrucks) {
                truckListModel.addElement(truck);
            }
        } else {
            jLabel32.setText("There was an error editing the truck");
        }
    }//GEN-LAST:event_EditTruckButtonActionPerformed

    private void AddNewTruckButtonActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNewTruckButtonActionPerformed1
        int truckSize = Integer.parseInt(AddNewTruckSizeTextField.getText());
        Double unitPrice = Double.parseDouble(AddNewTruckUnitPriceTextField.getText());

        if (con.createTruck(truckSize, unitPrice)) {
            jLabel33.setText("Truck Created");
            allTrucks = con.getTrucks();
            truckListModel.clear();
            TruckList.setModel(truckListModel);
            for (Truck truck : allTrucks) {
                truckListModel.addElement(truck);
            }
        } else {
            jLabel33.setText("There was an error creating the truck");
        }
    }//GEN-LAST:event_AddNewTruckButtonActionPerformed1

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        paymentEditingDonePopup.setVisible(false);
    }//GEN-LAST:event_OKActionPerformed

    private void oldPaymentTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oldPaymentTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_oldPaymentTextFieldActionPerformed

    private void saveNewPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNewPaymentActionPerformed
        Order currentOrder = (Order) orderList.getSelectedValue();
        boolean status;
        oldPaymentTextField.setText("" + currentOrder.getPaidAmount());
        Double newPayment = Double.parseDouble(newPaymentTextField.getText());
        status = con.savePayment(currentOrder, newPayment);
        paymentEditingDonePopup.pack();
        if (status == true) {
            paymentEditingDonePopup.setVisible(true);
        } else {
            paymentEditingDonePopup.setVisible(true);
            paymentEditedLabel.setText("Editing payment failed");
        }
        paymentStatus.setVisible(false);

    }//GEN-LAST:event_saveNewPaymentActionPerformed

    private void newPaymentTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPaymentTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newPaymentTextFieldActionPerformed

    private void eventManagementButtonActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventManagementButtonActionPerformed1
        orderDetailsList.setModel(orderDetailsModel);
        customerOrders.setModel(customerOrdersModel);
        allOrders = con.getOrders();
        ordersModel.clear();
        for (Order o : allOrders) {
            ordersModel.addElement(o);
        }
        orderList.setModel(ordersModel);


        CardLayout cl = (CardLayout) (mainPanel.getLayout());

        cl.show(mainPanel, "eventManagement");


    }//GEN-LAST:event_eventManagementButtonActionPerformed1

    private void inactiveResActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inactiveResActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inactiveResActionPerformed

    private void searchChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchChooseActionPerformed
        Customer c = (Customer) customerList2.getSelectedValue();
        int customerID = c.getCustomerID();
        ordersModel.clear();
        orderList.setModel(ordersModel);
        for (Order o : allOrders) {
            if (o.getCustomerID() == customerID) {
                filteredOrdersByName.add(o);
            }
        }
        for (Order o : filteredOrdersByName) {
            ordersModel.addElement(o);
        }
        searchCustomer2.setVisible(false);

    }//GEN-LAST:event_searchChooseActionPerformed

    private void searchCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchCancelActionPerformed
        searchCustomer2.setVisible(false);
    }//GEN-LAST:event_searchCancelActionPerformed

    private void customerList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerList2MouseClicked
    }//GEN-LAST:event_customerList2MouseClicked

    private void orderListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orderListMouseClicked
        if (orderList.isSelectionEmpty()) {
            selectedOrderDetailModel.clear();
            selectedOrderDetailModel.add(0, "No Order Selected");
        } else {
            selectedOrderDetailModel.clear();
            Order o = (Order) orderList.getSelectedValue();
            ArrayList<OrderDetail> selectedOrderDetails = con.getOrderDetail(o);
            for (OrderDetail od : selectedOrderDetails) {
                selectedOrderDetailModel.addElement(od);
                orderDetailList.setModel(selectedOrderDetailModel);
                discountLabel.setText(o.getDiscount() + "");
                fullPriceLabel.setText(o.getFullPrice() + "");
                paidAmount.setText(o.getPaidAmount() + "");
                if (o.isCancelled() == true) {
                    cancelledLabel.setText("yes");
                } else {
                    cancelledLabel.setText("no");
                }

                if (o.isTruckDelivery() == true) {
                    truckDeliverLabel.setText("yes");
                } else {
                    truckDeliverLabel.setText("no");
                }
                if (o.isTruckReturn() == true) {
                    truckReturnLabel.setText("yes");
                } else {
                    truckReturnLabel.setText("no");
                }
                addressLabel.setText(o.getAdress());

            }
        }
    }//GEN-LAST:event_orderListMouseClicked

    private void depositPaidRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depositPaidRadioActionPerformed
        ordersModel.clear();
        filteredOrdersByName.clear();
        for (Order o : allOrders) {
            if ((o.getFullPrice() * 0.25) <= o.getPaidAmount()) {
                filteredOrdersByName.add(o);
            }
        }
        for (Order o : filteredOrdersByName) {
            ordersModel.addElement(o);
        }
    }//GEN-LAST:event_depositPaidRadioActionPerformed

    private void nothingPaidRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nothingPaidRadioActionPerformed
        ordersModel.clear();
        filteredOrdersByName.clear();
        for (Order o : allOrders) {
            if (o.getPaidAmount() == 0) {
                filteredOrdersByName.add(o);
            }
        }
        for (Order o : filteredOrdersByName) {
            ordersModel.addElement(o);
        }
    }//GEN-LAST:event_nothingPaidRadioActionPerformed

    private void fullyPaidRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullyPaidRadioActionPerformed
        ordersModel.clear();
        filteredOrdersByName.clear();
        for (Order o : allOrders) {
            if (o.getPaidAmount() == o.getFullPrice()) {
                filteredOrdersByName.add(o);
            }
        }
        for (Order o : filteredOrdersByName) {
            ordersModel.addElement(o);
        }
    }//GEN-LAST:event_fullyPaidRadioActionPerformed

    private void depositPaidButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depositPaidButtonActionPerformed
        Order currentOrder = (Order) orderList.getSelectedValue();
        oldPaymentTextField.setText("" + currentOrder.getPaidAmount());
        paymentStatus.pack();
        paymentStatus.setVisible(true);


    }//GEN-LAST:event_depositPaidButtonActionPerformed

    private void editOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editOrderButtonActionPerformed

        editConfirmation.pack();
        editConfirmation.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
        editConfirmation.setVisible(true);
    }//GEN-LAST:event_editOrderButtonActionPerformed

    private void cancelOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelOrderButtonActionPerformed
        Order currentOrder = (Order) orderList.getSelectedValue();
        con.cancelOrder(currentOrder);
    }//GEN-LAST:event_cancelOrderButtonActionPerformed

    private void searchCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchCustomerButtonActionPerformed
        String first, last;
        first = firstNameSearch.getText();
        last = lastNameSearch.getText();
        searchCustomer2.setVisible(true);
        customerList2.setModel(searchCustomerModel2);
        ArrayList<Customer> list = con.getCustomerList(firstNameSearch.getText(), lastNameSearch.getText());
        for (Customer c : list) {
            searchCustomerModel2.addElement(c);
        }

    }//GEN-LAST:event_searchCustomerButtonActionPerformed

    private void backToMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backToMenuActionPerformed
        CardLayout cl = (CardLayout) (mainPanel.getLayout());

        cl.show(mainPanel, "menu");
    }//GEN-LAST:event_backToMenuActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        editConfirmation.setVisible(false);
    }//GEN-LAST:event_jButton8ActionPerformed
    //Cancels selected order and transfers it's data into "Make a Booking" to edit it
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if (!orderList.isSelectionEmpty()) {
            con.cancelOrder((Order) orderList.getSelectedValue());
            editOrder = (Order) orderList.getSelectedValue();
            resources.clear();
            truckDelivery.clear();
            truckReturn.clear();
            gbInventory.removeAll();
            deliveryPanel.removeAll();
            returnPanel.removeAll();
            startDate.setDate(editOrder.getStartDate());
            endDate.setDate(editOrder.getEndDate());
            getAvailableResourcesButton.doClick();
            ArrayList<OrderDetail> details = editOrder.getOrderDetails();
            //Get information about the booked resources and set the dropdowns in the booking menu
            for (Map.Entry<Resource, JComboBox> entry : resources.entrySet()) {
                for (int i = 0; i < details.size(); i++) {
                    if (details.get(i).getResourceID() == entry.getKey().getResourceID()) {
                        entry.getValue().setSelectedIndex(details.get(i).getQuantity());
                    }
                }
            }
            Customer c = con.getCustomer(editOrder.getCustomerID());
            eventAddress.setText(editOrder.getAdress());
            customerID.setText("" + c.getCustomerID());
            firstName.setText(c.getFirstName());
            lastName.setText(c.getLastName());
            customerAddress.setText(c.getAdress());


            CardLayout cl = (CardLayout) (mainPanel.getLayout());

            cl.show(mainPanel, "booking");
        }
        editConfirmation.setVisible(false);

    }//GEN-LAST:event_jButton4ActionPerformed

    private void notTentPartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notTentPartActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_notTentPartActionPerformed

    private void notTentPart2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notTentPart2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_notTentPart2ActionPerformed

    private void jButton2ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed1
        resourceEdited.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed1

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        resourceCreated.setVisible(false);
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(PartyRentalGUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PartyRentalGUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PartyRentalGUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PartyRentalGUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    private javax.swing.JPanel EventManagement;
    private javax.swing.JButton GetAllTrucksButton;
    private javax.swing.JPanel GetExistingTrucksPanel;
    private javax.swing.JList JList_resources;
    private javax.swing.JPanel Menu;
    private javax.swing.JButton OK;
    private javax.swing.JPanel Order;
    private javax.swing.JPanel ResourceDone;
    private javax.swing.JPanel TruckHandlingPanel;
    private javax.swing.JTextField TruckIDTextField;
    private javax.swing.JList TruckList;
    private javax.swing.JTextField TruckSizeTextField;
    private javax.swing.JTextField TruckUnitPriceTextField;
    private javax.swing.JButton UpdateResource;
    private javax.swing.JRadioButton activeRes;
    private javax.swing.ButtonGroup activeResButtonGroup;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JButton backToMenu;
    private javax.swing.JButton bookingMenuButton;
    private javax.swing.JButton cancelOrderButton;
    private javax.swing.JLabel cancelledLabel;
    private javax.swing.JButton confirm;
    private javax.swing.JButton createNewRes;
    private javax.swing.JPanel createRes1;
    private javax.swing.JTextField customerAddress;
    private javax.swing.JTextField customerID;
    private javax.swing.JList customerList;
    private javax.swing.JList customerList2;
    private javax.swing.JList customerOrders;
    private javax.swing.JPanel deliveryPanel;
    private javax.swing.JButton depositPaidButton;
    private javax.swing.JRadioButton depositPaidRadio;
    private javax.swing.JTextField discount;
    private javax.swing.JLabel discountLabel;
    private javax.swing.JDialog editConfirmation;
    private javax.swing.JButton editOrderButton;
    private javax.swing.JPanel editRes;
    private javax.swing.JTextField editResName;
    private javax.swing.JTextField editResPrice;
    private javax.swing.JTextField editResQuantity;
    private javax.swing.JTextField editResUnitSize;
    private com.toedter.calendar.JDateChooser endDate;
    private javax.swing.JTextField eventAddress;
    private javax.swing.JButton eventManagementButton;
    private javax.swing.JTextField fName;
    private javax.swing.JTextField firstName;
    private javax.swing.JTextField firstNameSearch;
    private javax.swing.JLabel fullPriceLabel;
    private javax.swing.JRadioButton fullyPaidRadio;
    private javax.swing.JPanel gbInventory;
    private javax.swing.JButton getAvailableResourcesButton;
    private javax.swing.JPanel getRes;
    private javax.swing.JButton getResource;
    private javax.swing.JRadioButton inactiveRes;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
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
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField lName;
    private javax.swing.JTextField lastName;
    private javax.swing.JTextField lastNameSearch;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField newPaymentTextField;
    private javax.swing.JTextField newResName;
    private javax.swing.JTextField newResPrice;
    private javax.swing.JTextField newResQuantity;
    private javax.swing.JTextField newResUnitSize;
    private javax.swing.JRadioButton notTentPart;
    private javax.swing.JRadioButton notTentPart2;
    private javax.swing.JRadioButton nothingPaidRadio;
    private javax.swing.JTextField oldPaymentTextField;
    private javax.swing.JList orderDetailList;
    private javax.swing.JPanel orderDetails;
    private javax.swing.JList orderDetailsList;
    private javax.swing.JList orderList;
    private javax.swing.JPanel orders;
    private javax.swing.JLabel paidAmount;
    private javax.swing.JLabel paymentEditedLabel;
    private javax.swing.JDialog paymentEditingDonePopup;
    private javax.swing.JDialog paymentStatus;
    private javax.swing.JLabel remainingDelivery;
    private javax.swing.JLabel remainingReturn;
    private javax.swing.JLabel resSelected;
    private javax.swing.JDialog resourceCreated;
    private javax.swing.JDialog resourceEdited;
    private javax.swing.JButton resourcesMenuButton;
    private javax.swing.JPanel returnPanel;
    private javax.swing.JButton saveNewPayment;
    private javax.swing.JDialog saveOrderResult;
    private javax.swing.JLabel saveOrderResultLabel;
    private javax.swing.JRadioButton searchByName;
    private javax.swing.JButton searchCancel;
    private javax.swing.JButton searchChoose;
    private javax.swing.JDialog searchCustomer;
    private javax.swing.JDialog searchCustomer2;
    private javax.swing.JButton searchCustomerButton;
    private javax.swing.JButton searchCustomersButton;
    private javax.swing.ButtonGroup searchOrdersButtonGroup;
    private javax.swing.JLabel spEventAddress;
    private javax.swing.JLabel spPaid;
    private javax.swing.JLabel spTotalPrice;
    private com.toedter.calendar.JDateChooser startDate;
    private javax.swing.JRadioButton tentPart;
    private javax.swing.JRadioButton tentPart2;
    private javax.swing.ButtonGroup tentPartButtonGroup;
    private javax.swing.ButtonGroup tentPartButtonGroup2;
    private javax.swing.JLabel totalPrice;
    private javax.swing.JLabel totalSize;
    private javax.swing.JLabel truckDeliverLabel;
    private javax.swing.JLabel truckReturnLabel;
    private javax.swing.JDialog trucksRequiredPopup;
    // End of variables declaration//GEN-END:variables
}

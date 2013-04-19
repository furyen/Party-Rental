/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Presentation;

import Domain.Controller;
import Domain.Order;
import Domain.OrderDetail;
import Domain.Resource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
/**
 *
 * @author Petko
 */
public class EventManagment extends javax.swing.JFrame {
    Controller con = Controller.getInstance();
    private ArrayList<Order> allOrders = null;
    DefaultListModel ordersModel = new DefaultListModel();
    DefaultListModel selectedOrderDetailModel = new DefaultListModel();
    private ArrayList<OrderDetail> selectedOrderDetail = null;
    
    /**
     * Creates new form NewJFrame
     */
    public EventManagment() {
        initComponents();
        con.getConnection();
        allOrders = con.getOrders();
        for (Order o : allOrders) {
            ordersModel.addElement(o);
        }
        orderList.setModel(ordersModel);
        
        
      
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paymentStatus = new javax.swing.JDialog();
        jLabel7 = new javax.swing.JLabel();
        oldPaymentTextField = new javax.swing.JTextField();
        saveNewPayment = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        newPaymentTextField = new javax.swing.JTextField();
        paymentEditingDonePopup = new javax.swing.JDialog();
        paymentEditedLabel = new javax.swing.JLabel();
        OK = new javax.swing.JButton();
        EventManagement = new javax.swing.JPanel();
        orders = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        orderList = new javax.swing.JList();
        fullyPaidRadio = new javax.swing.JRadioButton();
        depositPaidRadio = new javax.swing.JRadioButton();
        nothingPaidRadio = new javax.swing.JRadioButton();
        searchByName = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        depositPaidButton = new javax.swing.JButton();
        editOrderButton = new javax.swing.JButton();
        cancelOrderButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        firstNameSearch = new javax.swing.JTextField();
        lastNameSearch = new javax.swing.JTextField();
        searchCustomerButton = new javax.swing.JButton();
        orderDetails = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        orderDetailList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        addressTextF = new javax.swing.JTextField();
        discountTextF = new javax.swing.JTextField();
        truckDeliverTextF = new javax.swing.JTextField();
        truckReturnTextF = new javax.swing.JTextField();
        backToMenu = new javax.swing.JButton();

        paymentStatus.setMaximumSize(new java.awt.Dimension(355, 155));
        paymentStatus.setMinimumSize(new java.awt.Dimension(355, 155));
        paymentStatus.setPreferredSize(new java.awt.Dimension(355, 155));
        paymentStatus.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setText("Payment until now");
        paymentStatus.getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 20, 136, -1));

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

        jLabel8.setText("Payment new amount");
        paymentStatus.getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(54, 54, 136, -1));

        newPaymentTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPaymentTextFieldActionPerformed(evt);
            }
        });
        paymentStatus.getContentPane().add(newPaymentTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(208, 48, 80, -1));

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jScrollPane1.setViewportView(orderList);

        orders.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 67, 670, 140));

        fullyPaidRadio.setText("Fully Paid");
        orders.add(fullyPaidRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 240, 147, -1));

        depositPaidRadio.setText("Deposit Paid");
        orders.add(depositPaidRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 210, -1, -1));

        nothingPaidRadio.setText("Nothing Paid");
        orders.add(nothingPaidRadio, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 210, -1, -1));

        searchByName.setText("Only Name");
        orders.add(searchByName, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 240, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Edit", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14), java.awt.Color.black)); // NOI18N

        depositPaidButton.setText("Payment Status");
        depositPaidButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depositPaidButtonActionPerformed(evt);
            }
        });

        editOrderButton.setText("Edit Order");

        cancelOrderButton.setText("Cancel Order");
        cancelOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelOrderButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(79, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(depositPaidButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(69, 69, 69))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(depositPaidButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(editOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(cancelOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 15, Short.MAX_VALUE))
        );

        orders.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 270, 330, 130));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Search", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14))); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("First Name");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(98, 31, -1, -1));

        jLabel2.setText("Last Name");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 65, -1, -1));

        firstNameSearch.setText("jTextField1");
        jPanel3.add(firstNameSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 25, -1, -1));

        lastNameSearch.setText("jTextField1");
        jPanel3.add(lastNameSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 59, -1, -1));

        searchCustomerButton.setText("Search");
        searchCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchCustomerButtonActionPerformed(evt);
            }
        });
        jPanel3.add(searchCustomerButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, -1, -1));

        orders.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 350, 130));

        EventManagement.add(orders, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 720, 410));

        orderDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Order Details", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 14)))); // NOI18N
        orderDetails.setPreferredSize(new java.awt.Dimension(394, 395));
        orderDetails.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        orderDetailList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(orderDetailList);

        orderDetails.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 260, 150));

        jLabel3.setText("Address");
        orderDetails.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 150, -1, -1));

        jLabel4.setText("Discount");
        orderDetails.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 60, -1, -1));

        jLabel5.setText("Truck delivering");
        orderDetails.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 90, -1, -1));

        jLabel6.setText("Truck return");
        orderDetails.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 120, -1, -1));

        addressTextF.setText("jTextField3");
        orderDetails.add(addressTextF, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 140, -1, -1));

        discountTextF.setText("jTextField3");
        orderDetails.add(discountTextF, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 50, -1, -1));

        truckDeliverTextF.setText("jTextField3");
        orderDetails.add(truckDeliverTextF, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 80, -1, -1));

        truckReturnTextF.setText("jTextField3");
        orderDetails.add(truckReturnTextF, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 110, -1, -1));

        EventManagement.add(orderDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, 720, 200));

        backToMenu.setText("Back to Main Menu");
        backToMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backToMenuActionPerformed(evt);
            }
        });
        EventManagement.add(backToMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 640, -1, -1));

        getContentPane().add(EventManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 750, 680));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelOrderButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelOrderButtonActionPerformed

    private void searchCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchCustomerButtonActionPerformed
        String first, last;
        first = firstNameSearch.getText();
        last = lastNameSearch.getText();
        
        
    }//GEN-LAST:event_searchCustomerButtonActionPerformed

    private void backToMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backToMenuActionPerformed
//        CardLayout cl = (CardLayout) (mainPanel.getLayout());
//
//        cl.show(mainPanel, "menu");
    }//GEN-LAST:event_backToMenuActionPerformed

    private void depositPaidButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depositPaidButtonActionPerformed
        Order currentOrder = (Order)orderList.getSelectedValue();
        oldPaymentTextField.setText(""+ currentOrder.getPaidAmount()); 
        paymentStatus.setVisible(true);
        
    }//GEN-LAST:event_depositPaidButtonActionPerformed

    private void saveNewPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNewPaymentActionPerformed
        Order currentOrder = (Order)orderList.getSelectedValue();
        boolean status;
        oldPaymentTextField.setText(""+ currentOrder.getPaidAmount());  
        Double newPayment = Double.parseDouble(newPaymentTextField.getText());
        status = con.savePayment(currentOrder, newPayment);
        if (status == true){
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

    private void oldPaymentTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oldPaymentTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_oldPaymentTextFieldActionPerformed

    private void OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKActionPerformed
        paymentEditingDonePopup.setVisible(false);
    }//GEN-LAST:event_OKActionPerformed

    private void orderListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orderListMouseClicked
        if (orderList.isSelectionEmpty()) {
            selectedOrderDetailModel.clear();
            selectedOrderDetailModel.add(0, "No Customer Selected");
        } else {
            selectedOrderDetailModel.clear();
            Order o = (Order)orderList.getSelectedValue(); 
            ArrayList <OrderDetail> odList = con.getOrderDetail(o);
            for (OrderDetail od : selectedOrderDetail) {
            selectedOrderDetailModel.addElement(od);

        }
        }

    }//GEN-LAST:event_orderListMouseClicked

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
            java.util.logging.Logger.getLogger(EventManagment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EventManagment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EventManagment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EventManagment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EventManagment().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel EventManagement;
    private javax.swing.JButton OK;
    private javax.swing.JTextField addressTextF;
    private javax.swing.JButton backToMenu;
    private javax.swing.JButton cancelOrderButton;
    private javax.swing.JButton depositPaidButton;
    private javax.swing.JRadioButton depositPaidRadio;
    private javax.swing.JTextField discountTextF;
    private javax.swing.JButton editOrderButton;
    private javax.swing.JTextField firstNameSearch;
    private javax.swing.JRadioButton fullyPaidRadio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField lastNameSearch;
    private javax.swing.JTextField newPaymentTextField;
    private javax.swing.JRadioButton nothingPaidRadio;
    private javax.swing.JTextField oldPaymentTextField;
    private javax.swing.JList orderDetailList;
    private javax.swing.JPanel orderDetails;
    private javax.swing.JList orderList;
    private javax.swing.JPanel orders;
    private javax.swing.JLabel paymentEditedLabel;
    private javax.swing.JDialog paymentEditingDonePopup;
    private javax.swing.JDialog paymentStatus;
    private javax.swing.JButton saveNewPayment;
    private javax.swing.JRadioButton searchByName;
    private javax.swing.JButton searchCustomerButton;
    private javax.swing.JTextField truckDeliverTextF;
    private javax.swing.JTextField truckReturnTextF;
    // End of variables declaration//GEN-END:variables
}

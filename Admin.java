/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package brews104;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.Date;




/**
 *
 * @author Yuya
 */
public class Admin extends javax.swing.JFrame {

    /**
     * Creates new form Admin
     */
    public Admin() {
        initComponents();
        showDateTime();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setColumnIdentifiers(new String[]{"aColumn", "bColumn", "cColumn"});
        jTable1.setModel(model);
        loadOrdersIntoTable();
    }
    
   private void showDateTime() {
    Timer timer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Format for date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            lblDate.setText(dateFormat.format(new Date()));

            // Format for time
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            lblTime.setText(timeFormat.format(new Date()));
        }
    });
    timer.start(); // Start the clock
}
    
    private void searchOrderByCustomerNumber() {
    String customerId = txtSearchCustomer.getText().trim(); // get input
    if (customerId.isEmpty()) {
        loadOrdersIntoTable(); // If empty, reload all
        return;
    }

    DefaultTableModel model = new DefaultTableModel(
        new String[] { "SubTotal", "Tax", "Total", "Cash", "Change", "Order Time" }, 0
    );

    String url = "jdbc:mysql://localhost:3306/cbs";
    String user = "root";
    String password = "";

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, user, password);

        String sql = "SELECT SubTotal, Tax, Total, Cash, Change_amount, Order_time FROM orders WHERE Order_ID = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, customerId); // set user input to query

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Object[] row = {
                rs.getObject("SubTotal"),
                rs.getObject("Tax"),
                rs.getObject("Total"),
                rs.getObject("Cash"),
                rs.getObject("Change_amount"),
                rs.getObject("Order_time")
            };
            model.addRow(row);
        }

        jTable1.setModel(model);
        rs.close(); ps.close(); con.close();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error searching order: " + e.getMessage());
    }
}

    
    private void loadOrdersIntoTable() {
    DefaultTableModel model = new DefaultTableModel(
        new String[] { "SubTotal", "Tax", "Total", "Cash", "Change", "Order Time" }, 0
    );

    String url = "jdbc:mysql://localhost:3306/cbs";
    String user = "root";
    String password = "";

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, user, password);

        String sql = "SELECT SubTotal, Tax, Total, Cash, Change_amount, Order_time FROM orders ORDER BY Order_ID DESC";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Object[] row = {
                rs.getObject("SubTotal"),
                rs.getObject("Tax"),
                rs.getObject("Total"),
                rs.getObject("Cash"),
                rs.getObject("Change_amount"),
                rs.getObject("Order_time")
            };
            model.addRow(row);
        }

        jTable1.setModel(model);
        rs.close();
        stmt.close();
        con.close();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
    }
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txtSearchCustomer = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnSearch1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtSearchDate = new javax.swing.JTextField();
        btnResetDate = new javax.swing.JButton();
        btnSearchDate = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        lblDate = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtEarnings = new javax.swing.JTextField();
        comboRange = new javax.swing.JComboBox<>();
        btnSearchDate1 = new javax.swing.JButton();
        btnResetDate1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(244, 221, 176));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        txtSearchCustomer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Enter Day:");

        btnSearch.setBackground(new java.awt.Color(48, 186, 255));
        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("SEARCH");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnSearch1.setBackground(new java.awt.Color(255, 0, 0));
        btnSearch1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSearch1.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch1.setText("RESET");
        btnSearch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearch1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Enter Customer #:");

        txtSearchDate.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        btnResetDate.setBackground(new java.awt.Color(255, 0, 0));
        btnResetDate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnResetDate.setForeground(new java.awt.Color(255, 255, 255));
        btnResetDate.setText("RESET");
        btnResetDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetDateActionPerformed(evt);
            }
        });

        btnSearchDate.setBackground(new java.awt.Color(48, 186, 255));
        btnSearchDate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSearchDate.setForeground(new java.awt.Color(255, 255, 255));
        btnSearchDate.setText("SEARCH");
        btnSearchDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchDateActionPerformed(evt);
            }
        });

        btnLogout.setBackground(new java.awt.Color(204, 0, 0));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("LOGOUT");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        lblDate.setText("jLabel3");

        lblTime.setText("jLabel4");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Earnings:");

        txtEarnings.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        comboRange.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day", "Week", "Month", "Year" }));
        comboRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboRangeActionPerformed(evt);
            }
        });

        btnSearchDate1.setBackground(new java.awt.Color(48, 186, 255));
        btnSearchDate1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSearchDate1.setForeground(new java.awt.Color(255, 255, 255));
        btnSearchDate1.setText("CALCULATE");
        btnSearchDate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchDate1ActionPerformed(evt);
            }
        });

        btnResetDate1.setBackground(new java.awt.Color(255, 0, 0));
        btnResetDate1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnResetDate1.setForeground(new java.awt.Color(255, 255, 255));
        btnResetDate1.setText("RESET");
        btnResetDate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetDate1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTime)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtSearchCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(txtSearchDate))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnSearchDate, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                            .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(120, 120, 120)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnLogout)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtEarnings, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnSearchDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnResetDate1)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnSearch1)
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnResetDate)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(comboRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(234, 234, 234))))))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1021, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDate)
                    .addComponent(lblTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(txtEarnings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSearchDate1)
                            .addComponent(btnResetDate1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(txtSearchDate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSearchDate)
                                .addComponent(btnResetDate))
                            .addComponent(comboRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSearchCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSearch)
                            .addComponent(btnSearch1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        searchOrderByCustomerNumber();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSearch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearch1ActionPerformed
        // TODO add your handling code here:
        loadOrdersIntoTable();
        txtSearchCustomer.setText(null);
    }//GEN-LAST:event_btnSearch1ActionPerformed

    private void btnResetDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetDateActionPerformed
        // TODO add your handling code here:
        txtSearchDate.setText("");
        loadOrdersIntoTable();
    }//GEN-LAST:event_btnResetDateActionPerformed

    private void btnSearchDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchDateActionPerformed
        // TODO add your handling code here:
        String dateInput = txtSearchDate.getText().trim(); // yyyy-mm-dd format

    if (dateInput.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter a date (yyyy-mm-dd)");
        return;
    }

    DefaultTableModel model = new DefaultTableModel(
        new String[] { "SubTotal", "Tax", "Total", "Cash", "Change", "Order Time" }, 0
    );

    String url = "jdbc:mysql://localhost:3306/cbs";
    String user = "root";
    String password = "";

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, user, password);

        String sql = "SELECT SubTotal, Tax, Total, Cash, Change_amount, Order_time "
                   + "FROM orders WHERE DATE(Order_time) = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, dateInput);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Object[] row = {
                rs.getObject("SubTotal"),
                rs.getObject("Tax"),
                rs.getObject("Total"),
                rs.getObject("Cash"),
                rs.getObject("Change_amount"),
                rs.getObject("Order_time")
            };
            model.addRow(row);
        }

        jTable1.setModel(model);
        rs.close();
        ps.close();
        con.close();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error filtering by date: " + e.getMessage());
    }

    }//GEN-LAST:event_btnSearchDateActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        Login login = new Login();  
        login.setVisible(true);

    this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void comboRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboRangeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboRangeActionPerformed

    private void btnSearchDate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchDate1ActionPerformed
        // TODO add your handling code here:
        String range = (String) comboRange.getSelectedItem();
    String sql = "";

    switch (range) {
        case "Day":
            sql = "SELECT SUM(Total) AS earnings FROM orders WHERE DATE(Order_time) = CURDATE()";
            break;
        case "Week":
            sql = "SELECT SUM(Total) AS earnings FROM orders WHERE YEARWEEK(Order_time, 1) = YEARWEEK(CURDATE(), 1)";
            break;
        case "Month":
            sql = "SELECT SUM(Total) AS earnings FROM orders WHERE MONTH(Order_time) = MONTH(CURDATE()) AND YEAR(Order_time) = YEAR(CURDATE())";
            break;
        case "Year":
            sql = "SELECT SUM(Total) AS earnings FROM orders WHERE YEAR(Order_time) = YEAR(CURDATE())";
            break;
    }

    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cbs", "root", "");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            double total = rs.getDouble("earnings");
            txtEarnings.setText("₱" + total);
        } else {
            txtEarnings.setText("₱0.00");
        }

        rs.close();
        stmt.close();
        con.close();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error calculating earnings.");
    }

    }//GEN-LAST:event_btnSearchDate1ActionPerformed

    private void btnResetDate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetDate1ActionPerformed
        // TODO add your handling code here:
        comboRange.setSelectedIndex(-1);
        txtEarnings.setText(null);
        loadOrdersIntoTable();
    }//GEN-LAST:event_btnResetDate1ActionPerformed

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
            java.util.logging.Logger.getLogger(TheBillingSystem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TheBillingSystem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TheBillingSystem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TheBillingSystem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Admin().setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnResetDate;
    private javax.swing.JButton btnResetDate1;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearch1;
    private javax.swing.JButton btnSearchDate;
    private javax.swing.JButton btnSearchDate1;
    private javax.swing.JComboBox<String> comboRange;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblTime;
    private javax.swing.JTextField txtEarnings;
    private javax.swing.JTextField txtSearchCustomer;
    private javax.swing.JTextField txtSearchDate;
    // End of variables declaration//GEN-END:variables
}

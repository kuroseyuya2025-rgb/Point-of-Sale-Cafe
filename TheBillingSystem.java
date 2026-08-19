package brews104;


import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JFrame;


//Leader: Kurose 
//Group Members: Despabeladero, Luaton, Rito

/**
 *
 * @author Yuya
 */
public class TheBillingSystem extends javax.swing.JFrame {

    /**
     * Creates new form TheBillingSystem
     */
    private int subtotal = 0;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private void showDateTime() {
        Timer timer = new Timer(1000, (e) -> {
            
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        lblDate.setText(dateFormat.format(new Date()));
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        lblTime.setText(timeFormat.format(new Date()));
        });
        timer.start();

    
    }

    public TheBillingSystem() {
        initComponents();
        showDateTime();
        connect1();
        
    }
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
       
    
     public void connect1(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
           con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/cbs", "root", ""
            );
            System.out.println("✅ Connected to database!");

          
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("📦 Tables in the database:");
            while (rs.next()) {
                System.out.println(" - " + rs.getString(1));
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Failed to connect!");
        }
    }
    
private void updateReceipt() {
        int subtotal = 0;
    int tax = 10;  
    int total;
    double cash = 0;
    double change = 0;

    StringBuilder receipt = new StringBuilder();

    
    receipt.append("*****************************\n");
    receipt.append("   Café Restaurant Billing   \n");
    receipt.append("*****************************\n\n");
    
    java.time.LocalDateTime now = java.time.LocalDateTime.now();
    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDateTime = now.format(formatter);
    receipt.append("").append(formattedDateTime).append("\n\n");
    
    receipt.append("Item                     Price\n");
    receipt.append("------------------------------\n");

    
    for (String item : selectedItems) {
        
        String[] parts = item.split(" - ₱");
        if (parts.length == 2) {
            try {
                int itemPrice = Integer.parseInt(parts[1].trim());  
                subtotal += itemPrice; 
            } catch (NumberFormatException e) {
                System.out.println("Error parsing price: " + e.getMessage());
            }
        }

        receipt.append(item).append("\n");
    }

    
    receipt.append("\n------------------------------\n");
    receipt.append(String.format("Subtotal:               ₱%d\n", subtotal));
    receipt.append(String.format("Tax:                    ₱%d\n", tax));

    total = subtotal + tax;
    receipt.append(String.format("Total:                  ₱%d\n", total));

    
    try {
        cash = Double.parseDouble(CashField.getText().replaceAll("[^0-9.]", ""));
        change = cash - total;
    } catch (NumberFormatException e) {
        cash = 0;  
        change = 0;
    }

    receipt.append(String.format("Cash:                   ₱%.2f\n", cash));
    receipt.append(String.format("Change:                 ₱%.2f\n", change));

    receipt.append("------------------------------\n");
    receipt.append("  Thank you for dining with us!  \n");
    receipt.append("*****************************\n");

    
    ReceiptTextArea.setText(receipt.toString());

    
    SubTotalField.setText("₱" + subtotal);
    TaxField.setText("₱" + tax);
    TotalField.setText("₱" + total);
    ChangeField.setText("₱" + change);
}

public void saveOrder() {
    try {
        int subtotal = Integer.parseInt(SubTotalField.getText().replaceAll("[^0-9]", ""));
        int tax = Integer.parseInt(TaxField.getText().replaceAll("[^0-9]", ""));
        int total = Integer.parseInt(TotalField.getText().replaceAll("[^0-9]", ""));
        double cash = Double.parseDouble(CashField.getText().replaceAll("[^0-9.]", ""));
        double change = Double.parseDouble(ChangeField.getText().replaceAll("[^0-9.]", ""));

        Class.forName("com.mysql.cj.jdbc.Driver"); 
           con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/cbs", "root", ""
            );
            System.out.println("✅ Connected to database!");

        
        // Save order (receipt)
        String sql = "INSERT INTO orders (subtotal, tax, total, cash, change_amount) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, subtotal);
        ps.setInt(2, tax);
        ps.setInt(3, total);
        ps.setDouble(4, cash);
        ps.setDouble(5, change);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int orderId = rs.getInt(1);
        rs.close();
        ps.close();

        // Save items in order_items
        String itemSQL = "INSERT INTO order_items (Order_ID, MenuItem_ID, Item_Name, Quantity, Line_Subtotal, Tax, Total, Cash, Change_Amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ps = con.prepareStatement(itemSQL);

        for (String item : selectedItems) {
            // Format: Name xQTY - ₱TOTAL
            String[] parts = item.split(" x| - ₱");
            if (parts.length != 3) {
                System.out.println("Invalid item format: " + item);
                continue;
            }

            String name = parts[0].trim();
            int qty = Integer.parseInt(parts[1].trim());
            int lineTotal = Integer.parseInt(parts[2].trim());
            int lineTax = 10;
            int lineSubtotal = lineTotal - lineTax;

            // Get menu item ID
            PreparedStatement lookup = con.prepareStatement("SELECT id FROM menuitems WHERE name = ?");
            lookup.setString(1, name);
            ResultSet idRs = lookup.executeQuery();

            int menuItemId = 0;
            if (idRs.next()) {
                menuItemId = idRs.getInt("id");
            }
            idRs.close();
            lookup.close();

            // Insert item
            ps.setInt(1, orderId);
            ps.setInt(2, menuItemId);
            ps.setString(3, name);
            ps.setInt(4, qty);
            ps.setInt(5, lineSubtotal);
            ps.setInt(6, lineTax);
            ps.setInt(7, lineTotal);
            ps.setDouble(8, cash);
            ps.setDouble(9, change);
            ps.addBatch();
        }

        ps.executeBatch();
        ps.close();

        JOptionPane.showMessageDialog(this, "✅ Order saved to database!");

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "❌ Failed to save order:\n" + e.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        LogoLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnGrilledCheese = new javax.swing.JButton();
        btnTuna = new javax.swing.JButton();
        btnHamAndCheese = new javax.swing.JButton();
        btnCroissant = new javax.swing.JButton();
        btnCucumberAndEgg = new javax.swing.JButton();
        btnBaguette = new javax.swing.JButton();
        btnCaramelMacchiato = new javax.swing.JButton();
        btnHotChocolate = new javax.swing.JButton();
        btnSpanishLatte = new javax.swing.JButton();
        btnIcedLatte = new javax.swing.JButton();
        btnAppleJuice = new javax.swing.JButton();
        btnOrangeJuice = new javax.swing.JButton();
        btnMozzarellaSticks = new javax.swing.JButton();
        btnGreekSaladCups = new javax.swing.JButton();
        btnCapreseSkewers = new javax.swing.JButton();
        btnBruschetta = new javax.swing.JButton();
        btnDevilledEggs = new javax.swing.JButton();
        btnFrenchOnionSoup = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        btnQuantity = new javax.swing.JButton();
        btnTotal = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ReceiptTextArea = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        TaxField = new javax.swing.JTextField();
        SubTotalField = new javax.swing.JTextField();
        TotalField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        CashField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        ChangeField = new javax.swing.JTextField();
        btnCalcu = new javax.swing.JButton();
        lblDate = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        btnExit1 = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(253, 229, 155));

        LogoLabel.setFont(new java.awt.Font("Segoe Print", 1, 30)); // NOI18N
        LogoLabel.setText("104 Brews");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel4.setText("Appetizers");

        btnGrilledCheese.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\GrilledCheeese.jpg")); // NOI18N
        btnGrilledCheese.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnGrilledCheese.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGrilledCheeseActionPerformed(evt);
            }
        });

        btnTuna.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\tuna.jpg")); // NOI18N
        btnTuna.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnTuna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTunaActionPerformed(evt);
            }
        });

        btnHamAndCheese.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\HamAndCheese.jpg")); // NOI18N
        btnHamAndCheese.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnHamAndCheese.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHamAndCheeseActionPerformed(evt);
            }
        });

        btnCroissant.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\Croissant.jpg")); // NOI18N
        btnCroissant.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnCroissant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCroissantActionPerformed(evt);
            }
        });

        btnCucumberAndEgg.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\CucumberSalad.jpg")); // NOI18N
        btnCucumberAndEgg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnCucumberAndEgg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCucumberAndEggActionPerformed(evt);
            }
        });

        btnBaguette.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\baguette.jpg")); // NOI18N
        btnBaguette.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnBaguette.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaguetteActionPerformed(evt);
            }
        });

        btnCaramelMacchiato.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\Macchiato.jpg")); // NOI18N
        btnCaramelMacchiato.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnCaramelMacchiato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCaramelMacchiatoActionPerformed(evt);
            }
        });

        btnHotChocolate.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\HotChoco.jpg")); // NOI18N
        btnHotChocolate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnHotChocolate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHotChocolateActionPerformed(evt);
            }
        });

        btnSpanishLatte.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\SpanishLatte.jpg")); // NOI18N
        btnSpanishLatte.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnSpanishLatte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSpanishLatteActionPerformed(evt);
            }
        });

        btnIcedLatte.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\IcedLatte.jpg")); // NOI18N
        btnIcedLatte.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnIcedLatte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIcedLatteActionPerformed(evt);
            }
        });

        btnAppleJuice.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\AppleJuice.jpg")); // NOI18N
        btnAppleJuice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnAppleJuice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAppleJuiceActionPerformed(evt);
            }
        });

        btnOrangeJuice.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\OrangeJuice.jpg")); // NOI18N
        btnOrangeJuice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnOrangeJuice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrangeJuiceActionPerformed(evt);
            }
        });

        btnMozzarellaSticks.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\MozzarellaSticks.jpg")); // NOI18N
        btnMozzarellaSticks.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnMozzarellaSticks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMozzarellaSticksActionPerformed(evt);
            }
        });

        btnGreekSaladCups.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\Salad.jpg")); // NOI18N
        btnGreekSaladCups.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnGreekSaladCups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGreekSaladCupsActionPerformed(evt);
            }
        });

        btnCapreseSkewers.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\Skewers.jpg")); // NOI18N
        btnCapreseSkewers.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnCapreseSkewers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapreseSkewersActionPerformed(evt);
            }
        });

        btnBruschetta.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\Bruschetta.jpg")); // NOI18N
        btnBruschetta.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnBruschetta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBruschettaActionPerformed(evt);
            }
        });

        btnDevilledEggs.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\DevilledEggs.jpg")); // NOI18N
        btnDevilledEggs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnDevilledEggs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDevilledEggsActionPerformed(evt);
            }
        });

        btnFrenchOnionSoup.setIcon(new javax.swing.ImageIcon("C:\\Users\\Yuya\\Documents\\Downloads\\Brews104\\src\\images\\final-image (2).jpg")); // NOI18N
        btnFrenchOnionSoup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        btnFrenchOnionSoup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFrenchOnionSoupActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel2.setText("Pastry and Sandwiches ");

        jLabel1.setText("Tuna Sandwich - ₱ 50 ");

        jLabel10.setText("Grilled Cheese - ₱ 35");

        jLabel11.setText("Ham and Cheese - ₱45");

        jLabel12.setText("Cucumber and Egg  - ₱45");

        jLabel13.setText("Baguette - ₱75");

        jLabel14.setText("Croissant - ₱75");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel3.setText("Beverages");

        jLabel15.setText("Caramel Macchiato - ₱60");

        jLabel16.setText("Spanish Latte - ₱50");

        jLabel17.setText("Hot Chocolate - ₱35");

        jLabel18.setText("Iced Latte - ₱55");

        jLabel19.setText("Apple Juice - ₱20");

        jLabel20.setText("Orange Juice - ₱20");

        jLabel21.setText("\"Mozzarella Sticks - ₱35");

        jLabel22.setText("Greek Salad Cups - ₱50");

        jLabel23.setText("Caprese Skewers - ₱50");

        jLabel24.setText("Bruschetta - ₱55");

        jLabel25.setText("Devilled Eggs - ₱55");

        jLabel26.setText("French Onion Soup - ₱65");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel1)
                        .addGap(51, 51, 51)
                        .addComponent(jLabel10))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCaramelMacchiato, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel15)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnHotChocolate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(jLabel17)))))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnSpanishLatte, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(jLabel16)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnIcedLatte, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jLabel18)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAppleJuice, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(29, 29, 29)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnOrangeJuice, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(34, 34, 34)))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel11)
                        .addGap(236, 236, 236)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14)
                        .addGap(47, 47, 47))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnTuna, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGrilledCheese, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnHamAndCheese, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCucumberAndEgg, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel12)))
                        .addGap(18, 18, 18)
                        .addComponent(btnBaguette, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCroissant, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnMozzarellaSticks, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel21)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnGreekSaladCups, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCapreseSkewers, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBruschetta, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnDevilledEggs, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel23)
                                .addGap(87, 87, 87)
                                .addComponent(jLabel24)
                                .addGap(78, 78, 78)
                                .addComponent(jLabel25)
                                .addGap(43, 43, 43)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel26))
                            .addComponent(btnFrenchOnionSoup, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCroissant, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnGrilledCheese, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnTuna, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnHamAndCheese, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCucumberAndEgg, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBaguette, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(jLabel14)))
                .addGap(12, 12, 12)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnSpanishLatte, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnHotChocolate, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCaramelMacchiato, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnIcedLatte, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnAppleJuice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnOrangeJuice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel18)
                                        .addComponent(jLabel19)
                                        .addComponent(jLabel15))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)))))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnMozzarellaSticks, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnGreekSaladCups, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBruschetta, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCapreseSkewers, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDevilledEggs, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFrenchOnionSoup, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(jLabel25)
                            .addComponent(jLabel24)
                            .addComponent(jLabel23)
                            .addComponent(jLabel22)))
                    .addComponent(jLabel21))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnQuantity.setBackground(new java.awt.Color(51, 204, 0));
        btnQuantity.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnQuantity.setForeground(new java.awt.Color(255, 255, 255));
        btnQuantity.setText("QUANTITY");
        btnQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuantityActionPerformed(evt);
            }
        });

        btnTotal.setBackground(new java.awt.Color(0, 204, 0));
        btnTotal.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnTotal.setForeground(new java.awt.Color(255, 255, 255));
        btnTotal.setText("TOTAL");
        btnTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTotalActionPerformed(evt);
            }
        });

        btnLogout.setBackground(new java.awt.Color(0, 153, 255));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("LOGOUT");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnReset.setBackground(new java.awt.Color(48, 186, 255));
        btnReset.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnReset.setForeground(new java.awt.Color(255, 255, 255));
        btnReset.setText("RESET");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        ReceiptTextArea.setColumns(20);
        ReceiptTextArea.setRows(5);
        ReceiptTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane1.setViewportView(ReceiptTextArea);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel5.setText("Total (₱)");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel6.setText("Tax (₱)");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel7.setText("Sub-Total (₱)");

        TaxField.setFont(new java.awt.Font("Segoe UI Semibold", 0, 16)); // NOI18N
        TaxField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TaxFieldActionPerformed(evt);
            }
        });

        SubTotalField.setFont(new java.awt.Font("Segoe UI Semibold", 0, 16)); // NOI18N

        TotalField.setFont(new java.awt.Font("Segoe UI Semibold", 0, 16)); // NOI18N
        TotalField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TotalFieldActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel8.setText("Change (₱)");

        CashField.setFont(new java.awt.Font("Segoe UI Semibold", 0, 16)); // NOI18N
        CashField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CashFieldActionPerformed(evt);
            }
        });
        CashField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                CashFieldKeyReleased(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel9.setText("Cash (₱)");

        ChangeField.setFont(new java.awt.Font("Segoe UI Semibold", 0, 16)); // NOI18N
        ChangeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChangeFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SubTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TaxField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TotalField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CashField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ChangeField, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(TaxField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(SubTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(TotalField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CashField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(ChangeField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCalcu.setBackground(new java.awt.Color(255, 51, 51));
        btnCalcu.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnCalcu.setForeground(new java.awt.Color(255, 255, 255));
        btnCalcu.setText("CALCULATOR");
        btnCalcu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcuActionPerformed(evt);
            }
        });

        lblDate.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblDate.setText("DATE");

        lblTime.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblTime.setText("TIME");

        btnDelete.setBackground(new java.awt.Color(255, 51, 51));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("DELETE");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnExit1.setBackground(new java.awt.Color(153, 0, 0));
        btnExit1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnExit1.setForeground(new java.awt.Color(255, 255, 255));
        btnExit1.setText("EXIT");
        btnExit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExit1ActionPerformed(evt);
            }
        });

        btnPrint.setBackground(new java.awt.Color(204, 0, 204));
        btnPrint.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnPrint.setText("PRINT");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap(26, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(btnTotal)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReset)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnQuantity)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCalcu)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLogout)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExit1)))
                        .addGap(27, 27, 27))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(LogoLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 255, Short.MAX_VALUE)
                        .addComponent(lblTime)
                        .addGap(88, 88, 88))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LogoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDate)
                    .addComponent(lblTime))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnExit1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCalcu, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TaxFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TaxFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TaxFieldActionPerformed

    private void btnGrilledCheeseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGrilledCheeseActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 35;
selectedItems.add("Grilled Cheese x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnGrilledCheeseActionPerformed

    private void btnTunaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTunaActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 50;
selectedItems.add("Tuna Sandwich x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnTunaActionPerformed

    private void btnCaramelMacchiatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCaramelMacchiatoActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 60;
selectedItems.add("Caramel Macchiato x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
        
    }//GEN-LAST:event_btnCaramelMacchiatoActionPerformed

    private void btnHotChocolateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHotChocolateActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 35;
selectedItems.add("Hot Chocolate x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnHotChocolateActionPerformed

    private void btnMozzarellaSticksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMozzarellaSticksActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 35;
selectedItems.add("Mozzarella Sticks x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnMozzarellaSticksActionPerformed

    private void btnGreekSaladCupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGreekSaladCupsActionPerformed
        // TODO add your handling code here:
       int quantity = 1; // or use a quantity field or button value
int price = 50;
selectedItems.add("Greek Salad Cups x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnGreekSaladCupsActionPerformed

    private void btnQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuantityActionPerformed
        // TODO add your handling code here:
        
      String quantityInput = JOptionPane.showInputDialog(this, "Enter quantity:", "Quantity", JOptionPane.PLAIN_MESSAGE);

int quantity = 1;
try {
    quantity = Integer.parseInt(quantityInput);
    if (quantity < 1) quantity = 1;
} catch (NumberFormatException e) {
    quantity = 1;
}

if (!selectedItems.isEmpty()) {
    String lastItem = selectedItems.get(selectedItems.size() - 1);
    String[] parts = lastItem.split(" - ₱");
    
    if (parts.length == 2) {
        // ✅ Fix: Strip any previous " xN"
        String itemName = parts[0].split(" x")[0].trim();  
        
        int unitPrice = Integer.parseInt(parts[1].trim());
        int totalPrice = unitPrice * quantity;

        String formattedItem = String.format("%s x%d - ₱%d", itemName, quantity, totalPrice);
        selectedItems.set(selectedItems.size() - 1, formattedItem);

        updateReceipt();
    }
}
        
    }//GEN-LAST:event_btnQuantityActionPerformed

    private void btnTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTotalActionPerformed
        // TODO add your handling code here:
        updateReceipt();   // update fields
    saveOrder();       // log into DB

    }//GEN-LAST:event_btnTotalActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
      
        Login login = new Login();
    login.setVisible(true);
    login.setLocationRelativeTo(null); 

    
    this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnHamAndCheeseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHamAndCheeseActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 45;
selectedItems.add("Ham and Cheese x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnHamAndCheeseActionPerformed

    private void btnCucumberAndEggActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCucumberAndEggActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 45;
selectedItems.add("Cucumber and Egg x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnCucumberAndEggActionPerformed

    private void btnBaguetteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaguetteActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 75;
selectedItems.add("Baguette x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnBaguetteActionPerformed

    private void btnCroissantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCroissantActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 75;
selectedItems.add("Croissant x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnCroissantActionPerformed

    private void btnSpanishLatteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpanishLatteActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 50;
selectedItems.add("Spanish Latte x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnSpanishLatteActionPerformed

    private void btnIcedLatteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIcedLatteActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 55;
selectedItems.add("Iced Latte x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnIcedLatteActionPerformed

    private void btnAppleJuiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppleJuiceActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 20;
selectedItems.add("Apple Juice x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnAppleJuiceActionPerformed

    private void btnOrangeJuiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrangeJuiceActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 20;
selectedItems.add("Orange Juice x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnOrangeJuiceActionPerformed

    private void btnCapreseSkewersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapreseSkewersActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 50;
selectedItems.add("Capresse Skewers x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnCapreseSkewersActionPerformed

    private void btnBruschettaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBruschettaActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 55;
selectedItems.add("Bruschetta x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnBruschettaActionPerformed

    private void btnDevilledEggsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDevilledEggsActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 55;
selectedItems.add("Devilled Eggs x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnDevilledEggsActionPerformed

    private void btnFrenchOnionSoupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFrenchOnionSoupActionPerformed
        // TODO add your handling code here:
        int quantity = 1; // or use a quantity field or button value
int price = 65;
selectedItems.add("French Onion Soup x" + quantity + " - ₱" + (price * quantity));
updateReceipt(); // Refresh receipt display
    }//GEN-LAST:event_btnFrenchOnionSoupActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        ReceiptTextArea.setText(null);
        SubTotalField.setText(null);
        TaxField.setText(null);
        TotalField.setText(null);
        CashField.setText(null);
        ChangeField.setText(null);
        selectedItems.clear();
    }//GEN-LAST:event_btnResetActionPerformed

    private void CashFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CashFieldKeyReleased
        // TODO add your handling code here:
        try {
            int total = Integer.parseInt(TotalField.getText().replace("₱", "").trim());
            int cash = Integer.parseInt(CashField.getText().trim());
            
            if (cash >= total){
                int change = cash - total;
                ChangeField.setText("₱" + change);
            } else {
                ChangeField.setText("Insufficient");
            }
} catch (Exception e) {
    ChangeField.setText("");
                    
            }
            
        
    }//GEN-LAST:event_CashFieldKeyReleased

    private void btnCalcuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcuActionPerformed
        // TODO add your handling code here:
        Calculator Calculator = new Calculator();
        Calculator.setVisible(true);
        
    }//GEN-LAST:event_btnCalcuActionPerformed

    private void TotalFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TotalFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TotalFieldActionPerformed

    private void ChangeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChangeFieldActionPerformed
        // TODO add your handling code here:
        double total = Double.parseDouble(TotalField.getText());
double cash = Double.parseDouble(CashField.getText());
double change = cash - total;
ChangeField.setText(String.format("%.2f", change));
    }//GEN-LAST:event_ChangeFieldActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        selectedItems.remove(selectedItems.size()-1);
        updateReceipt();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnExit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExit1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btnExit1ActionPerformed

    private void CashFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CashFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CashFieldActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        try {
    // Create image from receiptTextArea
    BufferedImage image = new BufferedImage(
        ReceiptTextArea.getWidth(),
        ReceiptTextArea.getHeight(),
        BufferedImage.TYPE_INT_RGB
    );

    Graphics2D g2 = image.createGraphics();
    ReceiptTextArea.paint(g2);
    g2.dispose();

    // Save to file
    File file = new File("receipt_image.png");
    ImageIO.write(image, "png", file);

    // Show success message or preview
    JOptionPane.showMessageDialog(this, "Receipt saved as an image!");

    // Optional: Preview in a JLabel or new JFrame
    ImageIcon icon = new ImageIcon(file.getAbsolutePath());
    JLabel label = new JLabel(icon);
    JFrame preview = new JFrame("Receipt Preview");
    preview.add(label);
    preview.pack();
    preview.setVisible(true);

} catch (IOException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, "Failed to save image: " + e.getMessage());
}
    }//GEN-LAST:event_btnPrintActionPerformed

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
                new TheBillingSystem().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField CashField;
    private javax.swing.JTextField ChangeField;
    private javax.swing.JLabel LogoLabel;
    private javax.swing.JTextArea ReceiptTextArea;
    private javax.swing.JTextField SubTotalField;
    private javax.swing.JTextField TaxField;
    private javax.swing.JTextField TotalField;
    private javax.swing.JButton btnAppleJuice;
    private javax.swing.JButton btnBaguette;
    private javax.swing.JButton btnBruschetta;
    private javax.swing.JButton btnCalcu;
    private javax.swing.JButton btnCapreseSkewers;
    private javax.swing.JButton btnCaramelMacchiato;
    private javax.swing.JButton btnCroissant;
    private javax.swing.JButton btnCucumberAndEgg;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDevilledEggs;
    private javax.swing.JButton btnExit1;
    private javax.swing.JButton btnFrenchOnionSoup;
    private javax.swing.JButton btnGreekSaladCups;
    private javax.swing.JButton btnGrilledCheese;
    private javax.swing.JButton btnHamAndCheese;
    private javax.swing.JButton btnHotChocolate;
    private javax.swing.JButton btnIcedLatte;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMozzarellaSticks;
    private javax.swing.JButton btnOrangeJuice;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnQuantity;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSpanishLatte;
    private javax.swing.JButton btnTotal;
    private javax.swing.JButton btnTuna;
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
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblTime;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import static main.Product.currencyID;

/**
 *
 * @author Novandi Ramadhan
 */
public class Order extends javax.swing.JFrame {
    Connection con;
    Statement stat;
    PreparedStatement ps;
    ResultSet rs;
    String sql, fromUsername, orderDesc, idProduct, backupAddress, backupNoTelp;
    int countTotal, OVO_Saldo, Saldoku, orderQuantity, orderSize, cekSaldo;
    int countFinalTotal = 0;
    List<Integer> cartSize = new ArrayList<>();
    List<Integer> cartQuantity = new ArrayList<>();
    List<Integer> cartPrice = new ArrayList<>();
    List<String> cartDesc = new ArrayList<>();
    
    /**
     * Creates new form Order
     */
    public Order() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setTitle("Checkout");
        this.setIconImage(new ImageIcon(getClass().getResource("../assets/jordan.png")).getImage());
        jScrollPane2.getVerticalScrollBar().setUnitIncrement(16);
        listOrderPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 10));
        
        saveAddress.setVisible(false);
        cancelAddress.setVisible(false);
        saveNoTelp.setVisible(false);
        cancelNoTelp.setVisible(false);
        
        Config DB = new Config();
        DB.connect();
        con = DB.conn;
        stat = DB.stm;
    }
    
    public void DetailOrder(String username, String id_product, int quantity, int size, String description, String fromPage) throws MalformedURLException, IOException {
        fromUsername = username;
        idProduct = id_product;
        
        try {
            String checkOvo = "SELECT no_ovo FROM users WHERE username = '" + username + "'";
            rs = stat.executeQuery(checkOvo);
            boolean adaOvo = false;
            while(rs.next()) {
                adaOvo = !rs.getString("no_ovo").equals("");
            }
            
             sql = "SELECT "
                     + "p.name AS ProductName, "
                     + "p.model AS ProductModel, "
                     + "p.category AS ProductCategory, "
                     + "p.apparel AS ProductApparel, "
                     + "p.price AS ProductPrice, "
                     + "p.image AS ProductImage, "
                     + "u.address AS UsersAddress, "
                     + "u.no_telp AS UsersPhone, "
                     + "u.saldo AS UsersSaldo, "
                     + (fromPage.equals("cart") ? "c.id_product AS CIdProduct, c.username AS CUsername, c.quantity AS CQuantity, c.size AS CSize, c.price AS CPrize, c.description AS CDesc, " : "")
                     + (adaOvo == true ? "u.no_ovo AS UsersOVO, o.saldo_ovo AS OvoSaldo " : "u.no_ovo AS UsersOVO ")
                     + "FROM products p INNER JOIN users u ON u.username = '" + username + "' AND p.id_product IN (" + id_product + ")" + (adaOvo == true ? " INNER JOIN ovo o ON o.no_ovo = u.no_ovo" : "")
                     + (fromPage.equals("cart") ? " INNER JOIN cart c ON c.id_product = p.id_product AND c.username = u.username" : "");
             
            rs = stat.executeQuery(sql);
            int duplicateOvo = 0;
            while(rs.next()) {
                URL url = new URL("http://localhost/nikeshop/src/assets/img/" + rs.getString("ProductImage"));
                Image imgUrl = ImageIO.read(url);
                JLabel lbl = new JLabel(new ImageIcon(new ImageIcon(imgUrl).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
                
                String cN = rs.getString("ProductName");
                String cM = rs.getString("ProductModel");
                String cC = rs.getString("ProductCategory");
                String cQ = (fromPage.equals("cart") ? rs.getString("CQuantity") : String.valueOf(quantity));
                String cA = rs.getString("ProductApparel");
                String cS = (fromPage.equals("cart") ? rs.getString("CSize") : String.valueOf(size));
                String cD = (fromPage.equals("cart") ? rs.getString("CDesc") : description);
                
                addressL.setText(rs.getString("UsersAddress").equals("") ? "Alamat belum diatur" : rs.getString("UsersAddress"));
                noTelpL.setText(rs.getString("UsersPhone").equals("") ? "Nomor telpon belum diatur" : rs.getString("UsersPhone"));
                
                if(!rs.getString("UsersOVO").equals("")) {
                    OVO_Saldo = Integer.parseInt(rs.getString("OvoSaldo"));
                    if(duplicateOvo < 1) {
                        duplicateOvo += 1;
                        payment.addItem("OVO");
                    }
                }
                
                Saldoku = Integer.parseInt(rs.getString("UsersSaldo"));
                
                cekSaldo = payment.getSelectedItem().equals("Saldoku") ? Integer.parseInt(rs.getString("UsersSaldo")) : Integer.parseInt(rs.getString("OvoSaldo"));
                String showSaldo = currencyID(cekSaldo);
                String showTotal = (fromPage.equals("cart") ? currencyID(Integer.parseInt(rs.getString("ProductPrice")) * Integer.parseInt(rs.getString("CQuantity"))) : currencyID(Integer.parseInt(rs.getString("ProductPrice")) * quantity));

                countFinalTotal += (fromPage.equals("cart") ? Integer.parseInt(rs.getString("ProductPrice")) * Integer.parseInt(rs.getString("CQuantity")) : Integer.parseInt(rs.getString("ProductPrice")) * quantity);
                createListOrder(cN, cM, cC, cA, cQ, cS, cD, lbl, showTotal);
                
                cartSize.add(fromPage.equals("cart") ? Integer.parseInt(rs.getString("CSize")) : size);
                cartQuantity.add(fromPage.equals("cart") ? Integer.parseInt(rs.getString("CQuantity")) : quantity);
                cartPrice.add(countFinalTotal);
                cartDesc.add(fromPage.equals("cart") ? rs.getString("CDesc") : description);
                
                saldoku.setText("Rp. " + showSaldo);
            }
            
            total.setText("Rp. " + currencyID(countFinalTotal));
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public static String currencyID(int nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getNumberInstance(localeID);
        return formatRupiah.format((int) nominal);
    }
    
    private LocalDate nextDate(LocalDate localdate) {
        return localdate.plusDays(3);
    }
    
    public void createListOrder(String name, String model, String category, String apparel, String quantity, String size, String description, JLabel imageL, String price) {
        JPanel listOrder = orderPanel = new javax.swing.JPanel();
        JPanel imgPanel = new javax.swing.JPanel();
        imgPanel.add(imageL);
        JLabel nameL = new javax.swing.JLabel();
        JLabel productName = new javax.swing.JLabel();
        JLabel modelProduct = new javax.swing.JLabel();
        JLabel modelL = new javax.swing.JLabel();
        JLabel categoryL = new javax.swing.JLabel();
        JLabel categoryProduct = new javax.swing.JLabel();
        JLabel apparelProduct = new javax.swing.JLabel();
        JLabel apparelL = new javax.swing.JLabel();
        JLabel quantityL = new javax.swing.JLabel();
        JLabel quantityProduct = new javax.swing.JLabel();
        JScrollPane scrollCatatanPesanan = new javax.swing.JScrollPane();
        JLabel catatanL = new javax.swing.JLabel();
        JTextArea catatanPesanan = new javax.swing.JTextArea();
        
        listOrder.setBackground(new java.awt.Color(255, 255, 255));

        catatanL.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        catatanL.setForeground(new java.awt.Color(102, 102, 102));
        catatanL.setText("Catatan Pesanan");

        catatanPesanan.setEditable(false);
        catatanPesanan.setBackground(new java.awt.Color(255, 255, 255));
        catatanPesanan.setColumns(20);
        catatanPesanan.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        catatanPesanan.setForeground(new java.awt.Color(0, 0, 0));
        catatanPesanan.setLineWrap(true);
        catatanPesanan.setRows(5);
        catatanPesanan.setText(description.equals("") ? "Tidak ada deskripsi" : description);
        scrollCatatanPesanan.setViewportView(catatanPesanan);

        nameL.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        nameL.setForeground(new java.awt.Color(102, 102, 102));
        nameL.setText("Name");

        productName.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        productName.setForeground(new java.awt.Color(0, 0, 0));
        productName.setText(name);

        modelProduct.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        modelProduct.setForeground(new java.awt.Color(0, 0, 0));
        modelProduct.setText(model);

        modelL.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        modelL.setForeground(new java.awt.Color(102, 102, 102));
        modelL.setText("Model");

        categoryL.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        categoryL.setForeground(new java.awt.Color(102, 102, 102));
        categoryL.setText("Category");

        categoryProduct.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        categoryProduct.setForeground(new java.awt.Color(0, 0, 0));
        categoryProduct.setText(category);

        apparelProduct.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        apparelProduct.setForeground(new java.awt.Color(0, 0, 0));
        apparelProduct.setText(apparel);

        apparelL.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        apparelL.setForeground(new java.awt.Color(102, 102, 102));
        apparelL.setText("Apparel");

        quantityL.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        quantityL.setForeground(new java.awt.Color(102, 102, 102));
        quantityL.setText("Spesific");

        quantityProduct.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        quantityProduct.setForeground(new java.awt.Color(0, 0, 0));
        quantityProduct.setText("Quantity (" + quantity + "), Size (" + size + ") & Total harga Rp. " + price);

        imgPanel.setBackground(new java.awt.Color(255, 255, 255));
        imgPanel.setLayout(new java.awt.GridBagLayout());
        
        javax.swing.GroupLayout listOrderLayout = new javax.swing.GroupLayout(listOrder);
        listOrder.setLayout(listOrderLayout);
        listOrderLayout.setHorizontalGroup(
            listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listOrderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imgPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameL)
                    .addComponent(modelL)
                    .addComponent(categoryL)
                    .addComponent(apparelL)
                    .addComponent(quantityL))
                .addGap(18, 18, 18)
                .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(quantityProduct)
                    .addComponent(apparelProduct)
                    .addComponent(categoryProduct)
                    .addComponent(modelProduct)
                    .addComponent(productName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollCatatanPesanan, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(catatanL)))
        );
        listOrderLayout.setVerticalGroup(
            listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listOrderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imgPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, listOrderLayout.createSequentialGroup()
                            .addComponent(catatanL)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(scrollCatatanPesanan, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(listOrderLayout.createSequentialGroup()
                            .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(nameL)
                                .addComponent(productName))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(modelL)
                                .addComponent(modelProduct))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(categoryL)
                                .addComponent(categoryProduct))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(apparelL)
                                .addComponent(apparelProduct))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(listOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(quantityL)
                                .addComponent(quantityProduct))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        listOrderPanel.add(listOrder);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        orderPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listOrderPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        modelLabel4 = new javax.swing.JLabel();
        modelLabel6 = new javax.swing.JLabel();
        modelLabel7 = new javax.swing.JLabel();
        payment = new javax.swing.JComboBox<>();
        jSeparator4 = new javax.swing.JSeparator();
        modelLabel8 = new javax.swing.JLabel();
        modelLabel9 = new javax.swing.JLabel();
        saldoku = new javax.swing.JLabel();
        modelLabel13 = new javax.swing.JLabel();
        total = new javax.swing.JLabel();
        addressL = new javax.swing.JTextField();
        noTelpL = new javax.swing.JTextField();
        buy = new javax.swing.JButton();
        changeAddress = new javax.swing.JButton();
        saveAddress = new javax.swing.JButton();
        cancelAddress = new javax.swing.JButton();
        changeNoTelp = new javax.swing.JButton();
        saveNoTelp = new javax.swing.JButton();
        cancelNoTelp = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(832, 638));

        orderPanel.setBackground(new java.awt.Color(255, 255, 255));

        headerPanel.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\Nike\\src\\assets\\logo-white.png")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Google Sans", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Checkout");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(jLabel1)
                .addGap(148, 148, 148)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(354, 354, 354))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel2)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        listOrderPanel.setBackground(new java.awt.Color(255, 255, 255));
        listOrderPanel.setPreferredSize(new java.awt.Dimension(829, 365));
        listOrderPanel.setLayout(new java.awt.GridLayout(0, 1));
        jScrollPane2.setViewportView(listOrderPanel);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        modelLabel4.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        modelLabel4.setForeground(new java.awt.Color(102, 102, 102));
        modelLabel4.setText("Address");

        modelLabel6.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        modelLabel6.setForeground(new java.awt.Color(102, 102, 102));
        modelLabel6.setText("Phone Number");

        modelLabel7.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        modelLabel7.setForeground(new java.awt.Color(102, 102, 102));
        modelLabel7.setText("Payment");

        payment.setBackground(new java.awt.Color(0, 102, 204));
        payment.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        payment.setForeground(new java.awt.Color(255, 255, 255));
        payment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Saldoku" }));
        payment.setFocusable(false);
        payment.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                paymentItemStateChanged(evt);
            }
        });

        jSeparator4.setBackground(new java.awt.Color(153, 153, 153));
        jSeparator4.setForeground(new java.awt.Color(153, 153, 153));

        modelLabel8.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        modelLabel8.setForeground(new java.awt.Color(102, 102, 102));
        modelLabel8.setText("Total Payment");

        modelLabel9.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        modelLabel9.setForeground(new java.awt.Color(51, 51, 51));
        modelLabel9.setText("Saldo");

        saldoku.setFont(new java.awt.Font("Google Sans", 1, 12)); // NOI18N
        saldoku.setForeground(new java.awt.Color(51, 51, 51));
        saldoku.setText("0");

        modelLabel13.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        modelLabel13.setForeground(new java.awt.Color(51, 51, 51));
        modelLabel13.setText("Total");

        total.setFont(new java.awt.Font("Google Sans", 1, 12)); // NOI18N
        total.setForeground(new java.awt.Color(51, 51, 51));
        total.setText("0");

        addressL.setEditable(false);
        addressL.setBackground(new java.awt.Color(255, 255, 255));
        addressL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        noTelpL.setEditable(false);
        noTelpL.setBackground(new java.awt.Color(255, 255, 255));
        noTelpL.setForeground(new java.awt.Color(0, 0, 0));
        noTelpL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        buy.setBackground(new java.awt.Color(0, 0, 0));
        buy.setFont(new java.awt.Font("Google Sans", 1, 14)); // NOI18N
        buy.setForeground(new java.awt.Color(255, 255, 255));
        buy.setText("PAY NOW");
        buy.setBorder(null);
        buy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyActionPerformed(evt);
            }
        });

        changeAddress.setBackground(new java.awt.Color(0, 204, 0));
        changeAddress.setForeground(new java.awt.Color(0, 0, 0));
        changeAddress.setText("Change");
        changeAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeAddressActionPerformed(evt);
            }
        });

        saveAddress.setBackground(new java.awt.Color(0, 0, 0));
        saveAddress.setForeground(new java.awt.Color(255, 255, 255));
        saveAddress.setText("Save");
        saveAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAddressActionPerformed(evt);
            }
        });

        cancelAddress.setBackground(new java.awt.Color(153, 153, 153));
        cancelAddress.setForeground(new java.awt.Color(0, 0, 0));
        cancelAddress.setText("Cancel");
        cancelAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAddressActionPerformed(evt);
            }
        });

        changeNoTelp.setBackground(new java.awt.Color(0, 204, 0));
        changeNoTelp.setForeground(new java.awt.Color(0, 0, 0));
        changeNoTelp.setText("Change");
        changeNoTelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeNoTelpActionPerformed(evt);
            }
        });

        saveNoTelp.setBackground(new java.awt.Color(0, 0, 0));
        saveNoTelp.setForeground(new java.awt.Color(255, 255, 255));
        saveNoTelp.setText("Save");
        saveNoTelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNoTelpActionPerformed(evt);
            }
        });

        cancelNoTelp.setBackground(new java.awt.Color(153, 153, 153));
        cancelNoTelp.setForeground(new java.awt.Color(0, 0, 0));
        cancelNoTelp.setText("Cancel");
        cancelNoTelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNoTelpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(modelLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(total))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(modelLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(saldoku))
                    .addComponent(modelLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buy, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(modelLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(payment, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(noTelpL)
                    .addComponent(addressL)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(modelLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeAddress)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveAddress)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelAddress))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(modelLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeNoTelp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveNoTelp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelNoTelp)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(42, 42, 42))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modelLabel4)
                    .addComponent(changeAddress)
                    .addComponent(saveAddress)
                    .addComponent(cancelAddress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addressL, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(changeNoTelp)
                        .addComponent(saveNoTelp)
                        .addComponent(cancelNoTelp))
                    .addComponent(modelLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noTelpL, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(modelLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(payment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(modelLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(modelLabel9)
                            .addComponent(saldoku))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(modelLabel13)
                            .addComponent(total)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(buy, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 2, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout orderPanelLayout = new javax.swing.GroupLayout(orderPanel);
        orderPanel.setLayout(orderPanelLayout);
        orderPanelLayout.setHorizontalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
            .addGroup(orderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        orderPanelLayout.setVerticalGroup(
            orderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderPanelLayout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(orderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(orderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void paymentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_paymentItemStateChanged
        cekSaldo = payment.getSelectedItem().equals("Saldoku") ? Saldoku : OVO_Saldo;
        String showSaldo = currencyID(cekSaldo);
        saldoku.setText(showSaldo);
    }//GEN-LAST:event_paymentItemStateChanged

    private void buyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyActionPerformed
        if(cekSaldo < countFinalTotal) {
            JOptionPane.showMessageDialog(null, "Saldo " + (payment.getSelectedItem().equals("Saldoku") ? "Saldoku" : "OVO") + " anda kurang, mohon untuk mengisi ulang saldo anda");
        } else {
            if((noTelpL.getText()).matches("[0-9]+") && !(addressL.getText()).equals("Alamat belum diatur")) {
                List<String> idToArray = Arrays.asList(idProduct.split(","));
                List<Integer> idArrayToInt = new ArrayList<>();
                for(String x : idToArray) idArrayToInt.add(Integer.parseInt(x));
                
                Date dateNow = Date.valueOf(LocalDate.now());
                Date estimatedDate = Date.valueOf(nextDate(LocalDate.now()));
                
                try {
                    // Masukkan data ke myorder
                    ps = con.prepareStatement("INSERT INTO myorder(`username`, `id_product`, `quantity`, `size`, `desc`, `status`, `order_date`, `estimated_date`, `price`) VALUES(?,?,?,?,?,?,?,?,?)");
                    for(int i = 0; i < idArrayToInt.size(); i++) {
                        ps.setString(1, fromUsername);
                        ps.setInt(2, idArrayToInt.get(i));
                        ps.setInt(3, cartQuantity.get(i));
                        ps.setInt(4, cartSize.get(i));
                        ps.setString(5, cartDesc.get(i).equals("") ? "Tidak ada deskripsi" : cartDesc.get(i));
                        ps.setString(6, "Sedang diproses");
                        ps.setDate(7, dateNow);
                        ps.setDate(8, estimatedDate);
                        ps.setInt(9, cartPrice.get(i));
                        ps.addBatch();
                        
                        if(i + 1 % 1000 == 0 || i + 1 == idArrayToInt.size()) {
                            ps.executeBatch();
                        }
                    }

                    // Kurangi stok produk yang dibeli
                    sql = "UPDATE products SET stock = (CASE";
                    for (int i = 0; i < idArrayToInt.size(); i++) {
                        sql += " WHEN id_product = " + idArrayToInt.get(i) + " THEN stock - " + cartQuantity.get(i);
                    }
                    sql += " END) WHERE id_product IN (" + idProduct + ")";
                    stat = con.createStatement();
                    stat.executeUpdate(sql);
                    
                    // Hapus produk dari keranjang
                    sql = "DELETE FROM cart WHERE id_product IN (" + idProduct + ") AND username = '" + fromUsername + "'";
                    stat = con.createStatement();
                    stat.executeUpdate(sql);
                    
                    // Kurangi saldo pengguna
                    sql = "UPDATE " + (payment.getSelectedItem().equals("Saldoku") ? "users" : "ovo") 
                            + " SET " + (payment.getSelectedItem().equals("Saldoku") ? "saldo = saldo" : "saldo_ovo = saldo_ovo") 
                            + " - " + countFinalTotal 
                            + " WHERE "
                            + (payment.getSelectedItem().equals("Saldoku") ? "username = '" + fromUsername + "'" : "no_ovo = " + noTelpL.getText());
                    stat = con.createStatement();
                    stat.executeUpdate(sql);
                    
                    JOptionPane.showMessageDialog(null, "Sepatu berhasil dipesan! cek menu Status Pesanan untuk mengetahui status sepatu");
                    this.setVisible(false);
                    Cart c = new Cart();
                    c.setVisible(false);
                    
                    con.close();
                } catch(SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nomor telpon dan alamat harus disertakan!");
            }
        }
    }//GEN-LAST:event_buyActionPerformed

    private void changeAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeAddressActionPerformed
        changeAddress.setVisible(false);
        backupAddress = addressL.getText();
        addressL.setEditable(true);
        addressL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        saveAddress.setVisible(true);
        cancelAddress.setVisible(true);
    }//GEN-LAST:event_changeAddressActionPerformed

    private void cancelAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelAddressActionPerformed
        changeAddress.setVisible(true);
        addressL.setText(backupAddress);
        addressL.setEditable(false);
        addressL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        saveAddress.setVisible(false);
        cancelAddress.setVisible(false);
    }//GEN-LAST:event_cancelAddressActionPerformed

    private void saveAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAddressActionPerformed
        if(addressL.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Alamat harus diisi");
        } else {
            try {
                sql = "UPDATE users SET address = ? WHERE username = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, addressL.getText());
                ps.setString(2, fromUsername);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(null, "Alamat berhasil diubah");
                backupAddress = addressL.getText();
                changeAddress.setVisible(true);
                addressL.setEditable(false);
                addressL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
                saveAddress.setVisible(false);
                cancelAddress.setVisible(false);
            } catch(SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_saveAddressActionPerformed

    private void changeNoTelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeNoTelpActionPerformed
        changeNoTelp.setVisible(false);
        backupNoTelp = noTelpL.getText();
        noTelpL.setEditable(true);
        noTelpL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        saveNoTelp.setVisible(true);
        cancelNoTelp.setVisible(true);
    }//GEN-LAST:event_changeNoTelpActionPerformed

    private void saveNoTelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNoTelpActionPerformed
        if(noTelpL.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Nomor Telepon harus diisi");
        } else {
            if(noTelpL.getText().matches("[0-9]+")) {
                try {
                    sql = "UPDATE users SET no_telp = ? WHERE username = ?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, noTelpL.getText());
                    ps.setString(2, fromUsername);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Nomor Telepon berhasil diubah");
                    backupNoTelp = noTelpL.getText();
                    changeNoTelp.setVisible(true);
                    noTelpL.setEditable(false);
                    noTelpL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
                    saveNoTelp.setVisible(false);
                    cancelNoTelp.setVisible(false);
                } catch(SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nomor Telepon harus angka");
            }
        }
    }//GEN-LAST:event_saveNoTelpActionPerformed

    private void cancelNoTelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNoTelpActionPerformed
        changeNoTelp.setVisible(true);
        noTelpL.setText(backupNoTelp);
        noTelpL.setEditable(false);
        noTelpL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        saveNoTelp.setVisible(false);
        cancelNoTelp.setVisible(false);
    }//GEN-LAST:event_cancelNoTelpActionPerformed

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
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Order.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Order().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressL;
    private javax.swing.JButton buy;
    private javax.swing.JButton cancelAddress;
    private javax.swing.JButton cancelNoTelp;
    private javax.swing.JButton changeAddress;
    private javax.swing.JButton changeNoTelp;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPanel listOrderPanel;
    private javax.swing.JLabel modelLabel13;
    private javax.swing.JLabel modelLabel4;
    private javax.swing.JLabel modelLabel6;
    private javax.swing.JLabel modelLabel7;
    private javax.swing.JLabel modelLabel8;
    private javax.swing.JLabel modelLabel9;
    private javax.swing.JTextField noTelpL;
    private javax.swing.JPanel orderPanel;
    private javax.swing.JComboBox<String> payment;
    private javax.swing.JLabel saldoku;
    private javax.swing.JButton saveAddress;
    private javax.swing.JButton saveNoTelp;
    private javax.swing.JLabel total;
    // End of variables declaration//GEN-END:variables
}

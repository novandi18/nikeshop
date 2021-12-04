/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.sql.PreparedStatement;

/**
 *
 * @author Novandi Ramadhan
 */
public class Home extends javax.swing.JFrame {
    Connection con;
    Statement stat;
    PreparedStatement ps;
    ResultSet rs;
    String sql, username_login;
    
    /**
     * Creates new form Home
     */
    public Home() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("../assets/jordan.png")).getImage());
        this.setTitle("Home");
        contentPanel.getViewport().setBackground(Color.WHITE);
        contentPanel.getVerticalScrollBar().setUnitIncrement(16);
        
        Config DB = new Config();
        DB.connect();
        con = DB.conn;
        stat = DB.stm;
        
        listApparel();
        listModel();
    }
    
    private void listApparel() {
        try {
            sql = "SELECT name FROM apparel";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                filterApparel.addItem(rs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    private void listModel() {
        try {
            sql = "SELECT name FROM model";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                filterModel.addItem(rs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public void UserSession(String name, String username) {
        userWelcome.setText(name);
        username_login = username;
        showCart();
        showAll.setVisible(false);
    }
    
    public void showCart() {
        try {
            System.out.println(username_login);
            sql = "SELECT p.name AS ProductName, p.id_product AS IdProduct, c.id_product AS IdProductC, c.username AS Username, c.quantity AS Quantity, c.size AS Size FROM cart c JOIN products p ON c.username = ? AND c.id_product = p.id_product";
            ps = con.prepareStatement(sql);
            ps.setString(1, username_login);
            rs = ps.executeQuery();
            openCart.setVisible(false);
            while(rs.next()) {
                cart.addItem(rs.getString("ProductName") + " (P. " + rs.getString("Quantity") + ") (U. " + rs.getString("Size") + ")");
                openCart.setVisible(true);
            }
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    void showAllProduct() {
        try {
            sql = "SELECT * FROM products";
            rs = stat.executeQuery(sql);
            Home home = new Home();
            
            while(rs.next()) {
                URL url = new URL("http://localhost/nikeshop/src/assets/img/" + rs.getString("image"));
                Image image = ImageIO.read(url);
                
                JLabel l = new JLabel(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
                l.setText("<html>" + rs.getString("name") + "<br>Rp. " + rs.getString("price") + "</html>");
                l.setVerticalTextPosition(JLabel.BOTTOM);
                l.setHorizontalTextPosition(JLabel.CENTER);
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                l.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));
                
                String id = rs.getString("id_product");
                String name = rs.getString("name");
                String apparel = rs.getString("apparel");
                String model = rs.getString("model");
                String category = rs.getString("category");
                String img = rs.getString("image");
                String price = rs.getString("price");
                String stock = rs.getString("stock");
                
                l.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Product productUI = new Product();
                            home.setVisible(false);
                            productUI.setVisible(true);
                            productUI.getProductSelected(Integer.parseInt(id), name, apparel, model, category, img, price, stock, username_login);
                        } catch (IOException ex) {
//                            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,255,255), 2));
                    }
                });
                
                subContentPanel.add(l);
            }
        } catch(SQLException | IOException e) {
            JOptionPane.showMessageDialog(null, e);
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

        mainPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        userWelcome = new javax.swing.JLabel();
        user = new javax.swing.JButton();
        history = new javax.swing.JButton();
        logout1 = new javax.swing.JButton();
        showAll = new javax.swing.JButton();
        cart = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        refresh = new javax.swing.JButton();
        openCart = new javax.swing.JButton();
        contentPanel = new javax.swing.JScrollPane();
        subContentPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        cariProduct = new javax.swing.JTextField();
        submitCariProduct = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        filterApparel = new javax.swing.JComboBox<>();
        filterModel = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        filterCategory = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1180, 648));

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setForeground(new java.awt.Color(255, 255, 255));

        headerPanel.setBackground(new java.awt.Color(0, 0, 0));
        headerPanel.setPreferredSize(new java.awt.Dimension(1024, 210));

        jLabel1.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Welcome,");

        logo.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\Nike\\src\\assets\\logo-white.png")); // NOI18N

        userWelcome.setFont(new java.awt.Font("Google Sans", 1, 14)); // NOI18N
        userWelcome.setForeground(new java.awt.Color(255, 255, 255));

        user.setBackground(new java.awt.Color(0, 0, 0));
        user.setForeground(new java.awt.Color(255, 255, 255));
        user.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\Nike\\src\\assets\\user.png")); // NOI18N
        user.setBorder(null);
        user.setContentAreaFilled(false);
        user.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        user.setOpaque(true);
        user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userActionPerformed(evt);
            }
        });

        history.setBackground(new java.awt.Color(0, 0, 0));
        history.setForeground(new java.awt.Color(255, 255, 255));
        history.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\Nike\\src\\assets\\history.png")); // NOI18N
        history.setBorder(null);
        history.setContentAreaFilled(false);
        history.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        history.setOpaque(true);
        history.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                historyMouseClicked(evt);
            }
        });
        history.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historyActionPerformed(evt);
            }
        });

        logout1.setBackground(new java.awt.Color(0, 0, 0));
        logout1.setForeground(new java.awt.Color(255, 255, 255));
        logout1.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\Nike\\src\\assets\\logout.png")); // NOI18N
        logout1.setBorder(null);
        logout1.setContentAreaFilled(false);
        logout1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logout1.setOpaque(true);
        logout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout1ActionPerformed(evt);
            }
        });

        showAll.setBackground(new java.awt.Color(0, 153, 255));
        showAll.setForeground(new java.awt.Color(0, 0, 0));
        showAll.setText("Tampilkan Semua");
        showAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Google Sans", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("CART");

        refresh.setBackground(new java.awt.Color(255, 102, 0));
        refresh.setForeground(new java.awt.Color(0, 0, 0));
        refresh.setText("Refresh");
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });

        openCart.setBackground(new java.awt.Color(255, 255, 255));
        openCart.setForeground(new java.awt.Color(0, 0, 0));
        openCart.setText("OPEN CART");
        openCart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(logo)
                .addGap(18, 18, 18)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userWelcome)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 436, Short.MAX_VALUE)
                .addComponent(refresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showAll)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(openCart)
                .addGap(18, 18, 18)
                .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(history, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(logout1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(history, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logout1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(headerPanelLayout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(userWelcome))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                                    .addGap(3, 3, 3)
                                    .addComponent(logo)))))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(showAll)
                            .addComponent(cart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(refresh)
                            .addComponent(openCart))))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setBorder(null);
        contentPanel.setForeground(new java.awt.Color(255, 255, 255));
        contentPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPanel.setMinimumSize(new java.awt.Dimension(800, 400));

        subContentPanel.setBackground(new java.awt.Color(255, 255, 255));
        subContentPanel.setForeground(new java.awt.Color(255, 255, 255));
        subContentPanel.setLayout(new java.awt.GridLayout(0, 4));
        contentPanel.setViewportView(subContentPanel);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jSeparator1.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel2.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Cari Produk");

        cariProduct.setBackground(new java.awt.Color(240, 240, 240));
        cariProduct.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        cariProduct.setForeground(new java.awt.Color(0, 0, 0));
        cariProduct.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240)));
        cariProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariProductActionPerformed(evt);
            }
        });

        submitCariProduct.setBackground(new java.awt.Color(0, 0, 0));
        submitCariProduct.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        submitCariProduct.setForeground(new java.awt.Color(255, 255, 255));
        submitCariProduct.setText("CARI");
        submitCariProduct.setBorder(null);
        submitCariProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitCariProductActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Apparel");

        filterApparel.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        filterApparel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua" }));
        filterApparel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterApparelItemStateChanged(evt);
            }
        });

        filterModel.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        filterModel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua" }));
        filterModel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterModelItemStateChanged(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Model");

        filterCategory.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        filterCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Pria", "Wanita" }));
        filterCategory.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterCategoryItemStateChanged(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Kategori");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterModel, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cariProduct, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submitCariProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(filterApparel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filterCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cariProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitCariProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterApparel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterModel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1180, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userActionPerformed
        Profile profile = new Profile();
        profile.getUsers(username_login);
        profile.setVisible(true);
    }//GEN-LAST:event_userActionPerformed

    private void historyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyActionPerformed
        History h = new History();
        h.loadHistory(username_login);
        h.setVisible(true);
    }//GEN-LAST:event_historyActionPerformed

    private void logout1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logout1ActionPerformed
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Yakin ingin keluar akun?", "Warning", dialogButton);
        if(dialogResult == JOptionPane.YES_OPTION){
            this.setVisible(false);
            Login login = new Login();
            Product productui = new Product();
            Order orderui = new Order();
            Address addressui = new Address();
            PhoneNumber pnui = new PhoneNumber();
            
            productui.setVisible(false);
            orderui.setVisible(false);
            addressui.setVisible(false);
            pnui.setVisible(false);
            
            login.setVisible(true);
        }
    }//GEN-LAST:event_logout1ActionPerformed

    private void cariProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariProductActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cariProductActionPerformed

    private void submitCariProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitCariProductActionPerformed
        showAll.setVisible(true);
        try {
            sql = "SELECT * FROM products WHERE name LIKE ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, '%' + cariProduct.getText() + '%');
            rs = ps.executeQuery();
            Home home = new Home();
            
            Component[] componentList = subContentPanel.getComponents();
            for(Component c : componentList) {
                if(c instanceof JLabel) {
                    subContentPanel.remove(c);
                }
            }
            subContentPanel.revalidate();
            subContentPanel.repaint();
            
            while(rs.next()) {
                URL url = new URL("http://localhost/nikeshop/src/assets/img/" + rs.getString("image"));
                Image image = ImageIO.read(url);
                
                JLabel l = new JLabel(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
                l.setText("<html>" + rs.getString("name") + "<br>Rp. " + rs.getString("price") + "</html>");
                l.setVerticalTextPosition(JLabel.BOTTOM);
                l.setHorizontalTextPosition(JLabel.CENTER);
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                l.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));
                
                String id = rs.getString("id_product");
                String name = rs.getString("name");
                String apparel = rs.getString("apparel");
                String model = rs.getString("model");
                String category = rs.getString("category");
                String img = rs.getString("image");
                String price = rs.getString("price");
                String stock = rs.getString("stock");
                
                l.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Product productUI = new Product();
                            home.setVisible(false);
                            productUI.setVisible(true);
                            productUI.getProductSelected(Integer.parseInt(id), name, apparel, model, category, img, price, stock, username_login);
                        } catch (IOException ex) {
//                            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,255,255), 2));
                    }
                });
                
                subContentPanel.add(l);
            }
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } catch (MalformedURLException ex) {
//            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_submitCariProductActionPerformed

    private void showAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllActionPerformed
        filterApparel.setSelectedIndex(0);
        filterModel.setSelectedIndex(0);
        filterCategory.setSelectedIndex(0);
        
        Component[] componentList = subContentPanel.getComponents();
         for(Component c : componentList) {
            if(c instanceof JLabel) {
               subContentPanel.remove(c);
            }
        }
        
        subContentPanel.revalidate();
        subContentPanel.repaint();
        cariProduct.setText("");
        showAllProduct();
        showAll.setVisible(false);
    }//GEN-LAST:event_showAllActionPerformed

    private void historyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historyMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_historyMouseClicked

    private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
        cart.removeAllItems();
        showCart();
    }//GEN-LAST:event_refreshActionPerformed

    private void openCartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCartActionPerformed
        Cart cartUI = new Cart();
        try {
            cartUI.listCart(username_login);
        } catch (IOException ex) {
//            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_openCartActionPerformed

    private void filterModelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filterModelItemStateChanged
        showAll.setVisible(true);
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            String item = (String) evt.getItem();
            
            try {
                sql = "SELECT * FROM products" + 
                        (cariProduct.getText().equals("") && filterApparel.getSelectedItem().equals("Semua") && item.equals("Semua") && filterCategory.getSelectedItem().equals("Semua")
                            ? ""
                            : " WHERE") +
                        (cariProduct.getText().equals("") 
                            ? filterApparel.getSelectedItem().equals("Semua") 
                                ? item.equals("Semua")
                                    ? ""
                                    : filterApparel.getSelectedItem().equals("Semua")
                                        ? ""
                                        : " apparel = '" + filterApparel.getSelectedItem() + "'"
                                : " apparel = '" + filterApparel.getSelectedItem() + "'" 
                            : filterApparel.getSelectedItem().equals("Semua")
                                ? " name LIKE '%" + cariProduct.getText() + "%'"
                                : " name LIKE '%" + cariProduct.getText() + "%' AND apparel = '" + filterApparel.getSelectedItem() + "'") +
                        (cariProduct.getText().equals("")
                            ? filterApparel.getSelectedItem().equals("Semua")
                                ? item.equals("Semua")
                                    ? ""
                                    : " model = '" + item + "'"
                                : item.equals("Semua")
                                    ? ""
                                    : " AND model = '" + item + "'"
                            : item.equals("Semua")
                                ? ""
                                : " AND model = '" + filterModel.getSelectedItem() + "'") +
                        (cariProduct.getText().equals("")
                            ? filterApparel.getSelectedItem().equals("Semua")
                                ? item.equals("Semua")
                                    ? filterCategory.getSelectedItem().equals("Semua")
                                        ? ""
                                        : " category = '" + filterCategory.getSelectedItem() + "'"
                                    : filterCategory.getSelectedItem().equals("Semua")
                                        ? ""
                                        : " AND category = '" + filterCategory.getSelectedItem() + "'"
                                : filterCategory.getSelectedItem().equals("Semua")
                                    ? ""
                                    : " AND category = '" + filterCategory.getSelectedItem() + "'"
                            : filterCategory.getSelectedItem().equals("Semua")
                                ? ""
                                : " AND category = '" + filterCategory.getSelectedItem() + "'");

                stat = con.createStatement();
                rs = stat.executeQuery(sql);
                Home home = new Home();
            
                Component[] componentList = subContentPanel.getComponents();
                for(Component c : componentList) {
                    if(c instanceof JLabel) {
                        subContentPanel.remove(c);
                    }
                }
                subContentPanel.revalidate();
                subContentPanel.repaint();

                while(rs.next()) {
                    URL url = new URL("http://localhost/nikeshop/src/assets/img/" + rs.getString("image"));
                    Image image = ImageIO.read(url);

                    JLabel l = new JLabel(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
                    l.setText("<html>" + rs.getString("name") + "<br>Rp. " + rs.getString("price") + "</html>");
                    l.setVerticalTextPosition(JLabel.BOTTOM);
                    l.setHorizontalTextPosition(JLabel.CENTER);
                    l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    l.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));

                    String id = rs.getString("id_product");
                    String name = rs.getString("name");
                    String apparel = rs.getString("apparel");
                    String model = rs.getString("model");
                    String category = rs.getString("category");
                    String img = rs.getString("image");
                    String price = rs.getString("price");
                    String stock = rs.getString("stock");

                    l.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                Product productUI = new Product();
                                home.setVisible(false);
                                productUI.setVisible(true);
                                productUI.getProductSelected(Integer.parseInt(id), name, apparel, model, category, img, price, stock, username_login);
                            } catch (IOException ex) {
    //                            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,255,255), 2));
                        }
                    });

                    subContentPanel.add(l);
                }
            } catch (SQLException | IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }//GEN-LAST:event_filterModelItemStateChanged

    private void filterApparelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filterApparelItemStateChanged
        showAll.setVisible(true);
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            String item = (String) evt.getItem();
            
            try {
                sql = "SELECT * FROM products" + 
                        (cariProduct.getText().equals("") && item.equals("Semua") && filterModel.getSelectedItem().equals("Semua") && filterCategory.getSelectedItem().equals("Semua")
                            ? ""
                            : " WHERE") +
                        (cariProduct.getText().equals("") 
                            ? item.equals("Semua") 
                                ? filterModel.getSelectedItem().equals("Semua")
                                    ? ""
                                    : item.equals("Semua")
                                        ? ""
                                        : " apparel = '" + item + "'"
                                : " apparel = '" + item + "'" 
                            : item.equals("Semua")
                                ? " name LIKE '%" + cariProduct.getText() + "%'"
                                : " name LIKE '%" + cariProduct.getText() + "%' AND apparel = '" + item + "'") +
                        (cariProduct.getText().equals("")
                            ? item.equals("Semua")
                                ? filterModel.getSelectedItem().equals("Semua")
                                    ? ""
                                    : " model = '" + filterModel.getSelectedItem() + "'"
                                : filterModel.getSelectedItem().equals("Semua")
                                    ? ""
                                    : " AND model = '" + filterModel.getSelectedItem() + "'"
                            : filterModel.getSelectedItem().equals("Semua")
                                ? ""
                                : " AND model = '" + filterModel.getSelectedItem() + "'") +
                        (cariProduct.getText().equals("")
                            ? item.equals("Semua")
                                ? filterModel.getSelectedItem().equals("Semua")
                                    ? filterCategory.getSelectedItem().equals("Semua")
                                        ? ""
                                        : filterCategory.getSelectedItem().equals("Semua")
                                            ? ""
                                            : " AND category = '" + filterCategory.getSelectedItem() + "'"
                                    : " AND category = '" + filterCategory.getSelectedItem() + "'"
                                : filterCategory.getSelectedItem().equals("Semua")
                                    ? ""
                                    : " AND category = '" + filterCategory.getSelectedItem() + "'"
                            : filterCategory.getSelectedItem().equals("Semua")
                                ? ""
                                : " AND category = '" + filterCategory.getSelectedItem() + "'");
                
                stat = con.createStatement();
                rs = stat.executeQuery(sql);
                Home home = new Home();
            
                Component[] componentList = subContentPanel.getComponents();
                for(Component c : componentList) {
                    if(c instanceof JLabel) {
                        subContentPanel.remove(c);
                    }
                }
                subContentPanel.revalidate();
                subContentPanel.repaint();

                while(rs.next()) {
                    URL url = new URL("http://localhost/nikeshop/src/assets/img/" + rs.getString("image"));
                    Image image = ImageIO.read(url);

                    JLabel l = new JLabel(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
                    l.setText("<html>" + rs.getString("name") + "<br>Rp. " + rs.getString("price") + "</html>");
                    l.setVerticalTextPosition(JLabel.BOTTOM);
                    l.setHorizontalTextPosition(JLabel.CENTER);
                    l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    l.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));

                    String id = rs.getString("id_product");
                    String name = rs.getString("name");
                    String apparel = rs.getString("apparel");
                    String model = rs.getString("model");
                    String category = rs.getString("category");
                    String img = rs.getString("image");
                    String price = rs.getString("price");
                    String stock = rs.getString("stock");

                    l.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                Product productUI = new Product();
                                home.setVisible(false);
                                productUI.setVisible(true);
                                productUI.getProductSelected(Integer.parseInt(id), name, apparel, model, category, img, price, stock, username_login);
                            } catch (IOException ex) {
    //                            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,255,255), 2));
                        }
                    });

                    subContentPanel.add(l);
                }
            } catch (SQLException | IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }//GEN-LAST:event_filterApparelItemStateChanged

    private void filterCategoryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filterCategoryItemStateChanged
        showAll.setVisible(true);
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            String item = (String) evt.getItem();
            
            try {
                sql = "SELECT * FROM products" + 
                        (cariProduct.getText().equals("") && filterApparel.getSelectedItem().equals("Semua") && filterModel.getSelectedItem().equals("Semua") && item.equals("Semua")
                            ? ""
                            : " WHERE") +
                        (cariProduct.getText().equals("") 
                            ? filterApparel.getSelectedItem().equals("Semua") 
                                ? filterModel.getSelectedItem().equals("Semua")
                                    ? ""
                                    : filterApparel.getSelectedItem().equals("Semua")
                                        ? ""
                                        : " apparel = '" + filterApparel.getSelectedItem() + "'"
                                : " apparel = '" + filterApparel.getSelectedItem() + "'" 
                            : filterApparel.getSelectedItem().equals("Semua")
                                ? " name LIKE '%" + cariProduct.getText() + "%'"
                                : " name LIKE '%" + cariProduct.getText() + "%' AND apparel = '" + filterApparel.getSelectedItem() + "'") +
                        (cariProduct.getText().equals("")
                            ? filterApparel.getSelectedItem().equals("Semua")
                                ? filterModel.getSelectedItem().equals("Semua")
                                    ? ""
                                    : " model = '" + filterModel.getSelectedItem() + "'"
                                : filterModel.getSelectedItem().equals("Semua")
                                    ? ""
                                    : " AND model = '" + filterModel.getSelectedItem() + "'"
                            : filterModel.getSelectedItem().equals("Semua")
                                ? ""
                                : " AND model = '" + filterModel.getSelectedItem() + "'") +
                        (cariProduct.getText().equals("")
                            ? filterApparel.getSelectedItem().equals("Semua")
                                ? filterModel.getSelectedItem().equals("Semua")
                                    ? item.equals("Semua")
                                        ? ""
                                        : " category = '" + item + "'"
                                    : " AND category = '" + item + "'"
                                : item.equals("Semua")
                                    ? ""
                                    : " AND category = '" + item + "'"
                            : item.equals("Semua")
                                ? ""
                                : " AND category = '" + item + "'");
                
                stat = con.createStatement();
                rs = stat.executeQuery(sql);
                Home home = new Home();
            
                Component[] componentList = subContentPanel.getComponents();
                for(Component c : componentList) {
                    if(c instanceof JLabel) {
                        subContentPanel.remove(c);
                    }
                }
                subContentPanel.revalidate();
                subContentPanel.repaint();

                while(rs.next()) {
                    URL url = new URL("http://localhost/nikeshop/src/assets/img/" + rs.getString("image"));
                    Image image = ImageIO.read(url);

                    JLabel l = new JLabel(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
                    l.setText("<html>" + rs.getString("name") + "<br>Rp. " + rs.getString("price") + "</html>");
                    l.setVerticalTextPosition(JLabel.BOTTOM);
                    l.setHorizontalTextPosition(JLabel.CENTER);
                    l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    l.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));

                    String id = rs.getString("id_product");
                    String name = rs.getString("name");
                    String apparel = rs.getString("apparel");
                    String model = rs.getString("model");
                    String category = rs.getString("category");
                    String img = rs.getString("image");
                    String price = rs.getString("price");
                    String stock = rs.getString("stock");

                    l.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                Product productUI = new Product();
                                home.setVisible(false);
                                productUI.setVisible(true);
                                productUI.getProductSelected(Integer.parseInt(id), name, apparel, model, category, img, price, stock, username_login);
                            } catch (IOException ex) {
    //                            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,255,255), 2));
                        }
                    });

                    subContentPanel.add(l);
                }
            } catch (SQLException | IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }//GEN-LAST:event_filterCategoryItemStateChanged

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
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Home().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField cariProduct;
    private javax.swing.JComboBox<String> cart;
    private javax.swing.JScrollPane contentPanel;
    private javax.swing.JComboBox<String> filterApparel;
    private javax.swing.JComboBox<String> filterCategory;
    private javax.swing.JComboBox<String> filterModel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JButton history;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel logo;
    private javax.swing.JButton logout1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton openCart;
    private javax.swing.JButton refresh;
    private javax.swing.JButton showAll;
    private javax.swing.JPanel subContentPanel;
    private javax.swing.JButton submitCariProduct;
    private javax.swing.JButton user;
    private javax.swing.JLabel userWelcome;
    // End of variables declaration//GEN-END:variables
}

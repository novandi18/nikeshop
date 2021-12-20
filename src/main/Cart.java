/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

/**
 *
 * @author Novandi Ramadhan
 */
public class Cart extends javax.swing.JFrame {
    Connection con;
    Statement stat;
    PreparedStatement ps;
    ResultSet rs;
    String sql, usernameSession, cP, d, st, gSt;
    int q, s, p;
    ArrayList<String> cartSelected = new ArrayList<>();
    ArrayList<Integer> stockProduct = new ArrayList<>();
    
    /**
     * Creates new form Cart
     */
    public Cart() {
        initComponents();this.setIconImage(new ImageIcon(getClass().getResource("../assets/jordan.png")).getImage());
        this.setTitle("Cart");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        mainPanel.getViewport().setBackground(Color.WHITE);
        mainPanel.getVerticalScrollBar().setUnitIncrement(16);
        
        Config DB = new Config();
        DB.connect();
        con = DB.conn;
        stat = DB.stm;
    }
    
    public void deleteProductFromCart() throws IOException {
        try {
            sql = "DELETE FROM cart WHERE id_product IN (" + String.join(",", cartSelected) + ")";
            stat = con.createStatement();
            stat.executeUpdate(sql);
            
            JOptionPane.showMessageDialog(null, "Product berhasil dihapus dari keranjang");
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public void listCart(String username) throws MalformedURLException, IOException {
        this.setVisible(true);
        deleteFromCart.setVisible(false);
        usernameSession = username;
        try {
            sql = "SELECT "
                    + "p.name AS ProductName, "
                    + "p.image AS ProductImage, "
                    + "p.id_product AS IdProduct, "
                    + "c.id_product AS IdProductC, "
                    + "c.username AS Username, "
                    + "c.quantity AS Quantity, "
                    + "c.size AS Size, "
                    + "c.price AS Price, "
                    + "p.stock AS Stock, "
                    + "c.description AS Description "
                    + "FROM cart c JOIN products p ON c.username = ? AND c.id_product = p.id_product";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            int idx = 0;
            while(rs.next()) {
                if(idx > 4) {
                    contentPanel.setLayout(new java.awt.GridLayout(0, 1));
                } else {
                    contentPanel.setLayout(new java.awt.GridLayout(5, 1));
                }
                
                q = Integer.parseInt(rs.getString("Quantity"));
                s = Integer.parseInt(rs.getString("Size"));
                p = Integer.parseInt(rs.getString("Price"));
                d = rs.getString("Description");
                st = rs.getString("Stock");
                stockProduct.add(Integer.parseInt(rs.getString("Stock")));
                int gIdx = stockProduct.get(idx);
                idx++;
                
                URL url = new URL("http://localhost/nikeshop/src/assets/img/" + rs.getString("ProductImage"));
                Image image = ImageIO.read(url);
                
                JToggleButton tb = new JToggleButton(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
                tb.setText("<html>" + rs.getString("ProductName") + "<br>" + rs.getString("Quantity") + " Pasang" + "<br>" + "Ukuran " + rs.getString("Size") + "<br>" + "Stok : " + rs.getString("Stock") + "</html>");
                tb.setHorizontalAlignment(JToggleButton.LEFT);
                tb.setCursor(new Cursor(Cursor.HAND_CURSOR));
                tb.setPreferredSize(new Dimension(962, 120));
                
                String idPC = rs.getString("IdProductC");
                
                tb.addItemListener((ItemEvent ev) -> {
                    if(ev.getStateChange() == ItemEvent.SELECTED) {
                        if(gIdx >= 1) {
                            cartSelected.add(idPC);
                            txtSelected.setText("Terpilih (" + cartSelected.size() + ")");
                        } else {
                            JOptionPane.showMessageDialog(null, "Stok barang ini sudah habis");
                            tb.setSelected(false);
                        }
                        
                        if(cartSelected.size() > 0) {
                            deleteFromCart.setVisible(true);
                        } else {
                            deleteFromCart.setVisible(false);
                        }
                    } else if(ev.getStateChange() == ItemEvent.DESELECTED) {
                        cartSelected.remove(idPC);
                        txtSelected.setText("Terpilih (" + cartSelected.size() + ")");
                        if(cartSelected.size() > 0) {
                            deleteFromCart.setVisible(true);
                        } else {
                            deleteFromCart.setVisible(false);
                        }
                    }
                });
                
                contentPanel.add(tb);
            }
        } catch(SQLException e) {
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

        jPanel1 = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cartCheckout = new javax.swing.JButton();
        txtSelected = new javax.swing.JLabel();
        deleteFromCart = new javax.swing.JButton();
        mainPanel = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(962, 547));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        headerPanel.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\Nike\\src\\assets\\logo-white.png")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Google Sans", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Cart");

        cartCheckout.setText("Checkout");
        cartCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cartCheckoutActionPerformed(evt);
            }
        });

        txtSelected.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        txtSelected.setForeground(new java.awt.Color(255, 255, 255));
        txtSelected.setText("Terpilih (0)");

        deleteFromCart.setBackground(new java.awt.Color(255, 0, 51));
        deleteFromCart.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        deleteFromCart.setForeground(new java.awt.Color(255, 255, 255));
        deleteFromCart.setText("Hapus");
        deleteFromCart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFromCartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                .addGap(68, 68, 68)
                .addComponent(deleteFromCart)
                .addGap(12, 12, 12)
                .addComponent(txtSelected)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cartCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cartCheckout)
                    .addComponent(txtSelected)
                    .addComponent(deleteFromCart))
                .addGap(22, 22, 22))
        );

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setForeground(new java.awt.Color(0, 0, 0));
        mainPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setLayout(new java.awt.GridLayout(5, 1));
        mainPanel.setViewportView(contentPanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainPanel)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE))
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

    private void cartCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cartCheckoutActionPerformed
        if(cartSelected.size() < 1) {
            JOptionPane.showMessageDialog(null, "Belum ada produk yang dipilih untuk di checkout");
        } else {
            try {
                Order orderUI = new Order();
                orderUI.setVisible(true);
                orderUI.DetailOrder(usernameSession, String.join(",", cartSelected), q, s, d, "cart");
            } catch (IOException ex) {
//                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_cartCheckoutActionPerformed

    private void deleteFromCartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFromCartActionPerformed
        if(cartSelected.size() < 1) {
            JOptionPane.showMessageDialog(null, "Tidak ada produk yang dipilih untuk di hapus dari keranjang");
        } else {
            try {
                deleteProductFromCart();
            } catch (IOException ex) {
//                Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_deleteFromCartActionPerformed

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
            java.util.logging.Logger.getLogger(Cart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Cart().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cartCheckout;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton deleteFromCart;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane mainPanel;
    private javax.swing.JLabel txtSelected;
    // End of variables declaration//GEN-END:variables
}

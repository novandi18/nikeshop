/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package admin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 *
 * @author Novandi Ramadhan
 */
public class Dashboard extends javax.swing.JFrame {
    List<String> listData = new ArrayList<>();
    int pageSelected = 1;
    Connection con;
    Statement stat;
    PreparedStatement ps;
    ResultSet rs;
    String sql, dataClicked, editFormMode, imageChoose, filePath, userLogin, mode;
    List<String> listId = new ArrayList<>();
    JTable uTb = new JTable();
    JTable pTb = new JTable();
    JTable aTb = new JTable();
    JTable apTb = new JTable();
    JTable mTb = new JTable();
    DefaultTableModel uModel = new DefaultTableModel();
    DefaultTableModel pModel = new DefaultTableModel();
    DefaultTableModel aModel = new DefaultTableModel();
    DefaultTableModel apModel = new DefaultTableModel();
    DefaultTableModel mModel = new DefaultTableModel();
    String tgl = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    private UsersWorker uWorker;
    private ProductWorker pWorker;
    private AdminWorker aWorker;
    private ApparelWorker apWorker;
    private ModelWorker mWorker;
    
    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("../assets/jordan.png")).getImage());
        this.setTitle("Dashboard");
        welcomeName.setText(userLogin);
        dashboardBtn.setBackground(new java.awt.Color(0,51,204));
        dashboardBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,204), 15));
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);
        contentScroll.getHorizontalScrollBar().setUnitIncrement(16);
        searchField.setVisible(false);
        searchBtn.setVisible(false);
        editForm.setVisible(false);
        deleteBtn.setVisible(false);
        adminForm.setVisible(false);
        apparelModelP.setVisible(false);
        showAll.setVisible(false);
        
        Connect DB = new Connect();
        DB.connect();
        con = DB.conn;
        stat = DB.stm;
        
        headerInformation();
        showFilterTahun();
    }
    
    public void userSession(String username) {
        userLogin = username;
        welcomeName.setText(username);
    }
    
    private void showLoading() {
        ImageIcon icon = new ImageIcon(this.getClass().getResource("../assets/loading.gif"));
        Image img = icon.getImage();
        Image imgScale = img.getScaledInstance(22, 22, Image.SCALE_DEFAULT);
        icon = new ImageIcon(imgScale);
        loading.setIcon(icon);
    }
    
    private class UsersWorker extends SwingWorker<Boolean, List> {
        @Override
        protected void done() {
            mode = "";
            try {
                if (get() != null) {
                    loading.setIcon(null);
                    loading.revalidate();
                }
            } catch (InterruptedException | ExecutionException ex) {
                // nothing
            }
        }
        
        @Override
        protected void process(List chunks) {
            uModel.addRow(new Object[] {
                listData.get(0),
                listData.get(1),
                listData.get(2),
                listData.get(3)
            });
        }
        
        @Override
        protected Boolean doInBackground() throws Exception {
            showLoading();
            
            try {
                if(mode.equals("all-users")) {
                    sql = "SELECT u.id as id, u.username as user, p.name AS produk, mo.order_date as orderdate FROM users u "
                            + "JOIN myorder mo ON u.username = mo.username JOIN products p ON mo.id_product = p.id_product "
                            + "WHERE mo.order_date = ( SELECT MAX(order_date) FROM myorder WHERE username = u.username ) GROUP BY u.username "
                            + "UNION SELECT u.id, u.username, ?, ? FROM users u WHERE u.username NOT IN (SELECT mo.username FROM myorder mo)";
                } else if(mode.equals("search-users")) {
                    sql = "SELECT u.id as id, u.username as user, p.name AS produk, mo.order_date as orderdate FROM users u "
                            + "JOIN myorder mo ON u.username = mo.username JOIN products p ON mo.id_product = p.id_product "
                            + "WHERE mo.order_date = ( SELECT MAX(order_date) FROM myorder WHERE username LIKE ? ) GROUP BY u.username "
                            + "UNION SELECT u.id, u.username, ?, ? FROM users u WHERE u.username NOT IN (SELECT mo.username FROM myorder mo) "
                            + "AND u.username LIKE ?";
                }
                ps = con.prepareStatement(sql);
                if(mode.equals("search-users")) {
                    ps.setString(1, "%" + searchField.getText() + "%");
                    ps.setString(2, "Tidak ada produk");
                    ps.setString(3, "Tidak ada tanggal pembelian");
                    ps.setString(4, "%" + searchField.getText() + "%");
                } else {
                    ps.setString(1, "Tidak ada produk");
                    ps.setString(2, "Tidak ada tanggal pembelian");
                }
                rs = ps.executeQuery();
                while(rs.next()) {
                    listData.add(rs.getString("id"));
                    listData.add(rs.getString("user"));
                    listData.add(rs.getString("produk"));
                    listData.add(rs.getString("orderdate"));
                    publish(listData);
                    Thread.sleep(100);
                    listData.clear();
                }
            } catch(SQLException e) {
                return false;
            }
            
            return true;
        }
    }
    
    private class ProductWorker extends SwingWorker<Boolean, List> {
        @Override
        protected void done() {
            mode = "";
            try {
                if (get() != null) {
                    loading.setIcon(null);
                    loading.revalidate();
                }
            } catch (InterruptedException | ExecutionException ex) {
                // nothing
            }
        }
        
        @Override
        protected void process(List chunks) {
            pModel.addRow(new Object[] {
                "",
                listData.get(0),
                listData.get(1),
                listData.get(2),
                listData.get(3),
                listData.get(4),
                listData.get(5),
                listData.get(6),
            });
        }
        
        @Override
        protected Boolean doInBackground() throws Exception {
            showLoading();
            
            try {
                if(mode.equals("all-product")) {
                    sql = "SELECT * FROM products";
                } else if(mode.equals("search-product")) {
                    sql = "SELECT * FROM products WHERE name LIKE ? OR apparel LIKE ? OR model LIKE ?";
                }
                
                ps = con.prepareStatement(sql);
                if(mode.equals("search-product")) {
                    ps.setString(1, "%" + searchField.getText() + "%");
                    ps.setString(2, "%" + searchField.getText() + "%");
                    ps.setString(3, "%" + searchField.getText() + "%");
                }
                
                rs = ps.executeQuery();
                while(rs.next()) {
                    listData.add(rs.getString(1));
                    listData.add(rs.getString(2));
                    listData.add(rs.getString(3));
                    listData.add(rs.getString(4));
                    listData.add(rs.getString(5));
                    listData.add(rs.getString(6));
                    listData.add(rs.getString(8));
                    publish(listData);
                    Thread.sleep(100);
                    listData.clear();
                }
            } catch(SQLException e) {
                return false;
            }
            
            pTb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = pTb.getSelectedRow();
                    String check = pTb.getModel().getValueAt(row, 0).toString();
                    String rowData = pTb.getModel().getValueAt(row, 1).toString();

                    if(check.equals("")) {
                        pTb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        pTb.setValueAt("", row, 0);
                        listId.remove(rowData);
                        if(listId.size() < 1) {
                            deleteBtn.setText("DELETE ALL");
                        } else {
                            deleteBtn.setText("DELETE (" + listId.size() + ")");
                        }
                    }

                    dataClicked = rowData;
                    editFormMode = "update";
                    insertBtn.setVisible(true);
                    titleForm.setForeground(new java.awt.Color(0, 153, 0));
                    submitProducts.setBackground(new java.awt.Color(0, 153, 0));
                    if(editForm.isShowing()) {
                        showEditProducts(rowData);
                        titleForm.setText("Edit Product");
                        editBtn.setVisible(false);
                    } else {
                        editBtn.setVisible(true);
                    }
                }
            });
            
            return true;
        }
    }
    
    private class AdminWorker extends SwingWorker<Boolean, List> {
        @Override
        protected void done() {
            mode = "";
            try {
                if (get() != null) {
                    loading.setIcon(null);
                    loading.revalidate();
                }
            } catch (InterruptedException | ExecutionException ex) {
                // nothing
            }
        }
        
        @Override
        protected void process(List chunks) {
            aModel.addRow(new Object[] {
                "",
                listData.get(0),
                listData.get(1),
                listData.get(2),
            });
        }
        
        @Override
        protected Boolean doInBackground() throws Exception {
            showLoading();
            
            try {
                if(mode.equals("all-admin")) {
                    sql = "SELECT * FROM admin WHERE username != ?";
                } else if(mode.equals("search-admin")) {
                    sql = "SELECT * FROM admin WHERE username LIKE ? AND username != ?";
                }
                
                ps = con.prepareStatement(sql);
                if(mode.equals("search-admin")) {
                    ps.setString(1, "%" + searchField.getText() + "%");
                    ps.setString(2, userLogin);
                } else if(mode.equals("all-admin")) {
                    ps.setString(1, userLogin);
                }
                
                rs = ps.executeQuery();
                while(rs.next()) {
                    listData.add(rs.getString(1));
                    listData.add(rs.getString(2));
                    listData.add(rs.getString(4));
                    publish(listData);
                    Thread.sleep(100);
                    listData.clear();
                }
            } catch(SQLException e) {
                return false;
            }
            
            aTb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = aTb.getSelectedRow();
                    String check = aTb.getModel().getValueAt(row, 0).toString();
                    String rowData = aTb.getModel().getValueAt(row, 2).toString();

                    if(check.equals("")) {
                        aTb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        aTb.setValueAt("", row, 0);
                        listId.remove(rowData);
                        if(listId.size() < 1) {
                            deleteBtn.setText("DELETE ALL");
                        } else {
                            deleteBtn.setText("DELETE (" + listId.size() + ")");
                        }
                    }

                    dataClicked = rowData;
                    editFormMode = "update";
                    alertPass.setVisible(true);
                    insertBtn.setVisible(true);
                    titleForm1.setForeground(new java.awt.Color(0, 153, 0));
                    submitAdminForm.setBackground(new java.awt.Color(0, 153, 0));
                    if(adminForm.isShowing()) {
                        showEditAdmin(rowData);
                        titleForm1.setText("Edit Admin");
                        editBtn.setVisible(false);
                    } else {
                        editBtn.setVisible(true);
                    }
                }
            });
            
            return true;
        }
    }
    
    private class ApparelWorker extends SwingWorker<Boolean, List> {
        @Override
        protected void done() {
            mode = "";
            try {
                if (get() != null) {
                    loading.setIcon(null);
                    loading.revalidate();
                }
            } catch (InterruptedException | ExecutionException ex) {
                // nothing
            }
        }
        
        @Override
        protected void process(List chunks) {
            apModel.addRow(new Object[] {
                "",
                listData.get(0),
                listData.get(1),
                listData.get(2),
            });
        }
        
        @Override
        protected Boolean doInBackground() throws Exception {
            showLoading();
            
            try {
                if(mode.equals("all-apparel")) {
                    sql = "SELECT a.id_apparel AS id, a.name AS name, (SELECT COUNT(p.apparel) "
                            + "FROM products p WHERE p.apparel = a.name) AS jumlah FROM apparel a";
                } else if(mode.equals("search-apparel")) {
                    sql = "SELECT a.id_apparel AS id, a.name AS name, "
                            + "(SELECT COUNT(p.apparel) FROM products p WHERE p.apparel = a.name) AS jumlah FROM apparel a WHERE a.name LIKE ?";
                }
                
                ps = con.prepareStatement(sql);
                if(mode.equals("search-apparel")) ps.setString(1, "%" + searchField.getText() + "%");
                
                rs = ps.executeQuery();
                while(rs.next()) {
                    listData.add(rs.getString(1));
                    listData.add(rs.getString(2));
                    listData.add(rs.getString(3));
                    publish(listData);
                    Thread.sleep(100);
                    listData.clear();
                }
            } catch(SQLException e) {
                return false;
            }
            
            apTb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = apTb.getSelectedRow();
                    String check = apTb.getModel().getValueAt(row, 0).toString();
                    String rowData = apTb.getModel().getValueAt(row, 1).toString();

                    if(check.equals("")) {
                        apTb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        apTb.setValueAt("", row, 0);
                        listId.remove(rowData);
                        if(listId.size() < 1) {
                            deleteBtn.setText("DELETE ALL");
                        } else {
                            deleteBtn.setText("DELETE (" + listId.size() + ")");
                        }
                    }

                    dataClicked = rowData;
                    apparelModelT.setText("Edit Apparel");
                    editFormMode = "update";
                    alertPass.setVisible(false);
                    insertBtn.setVisible(true);
                    apparelModelT.setForeground(new java.awt.Color(0, 153, 0));
                    apparelModelB.setBackground(new java.awt.Color(0, 153, 0));
                    if(apparelModelP.isShowing()) {
                        showEditApparel(rowData);
                        apparelModelT.setText("Edit Apparel");
                        editBtn.setVisible(false);
                    } else {
                        editBtn.setVisible(true);
                    }
                }
            });
            
            return true;
        }
    }
    
    private class ModelWorker extends SwingWorker<Boolean, List> {
        @Override
        protected void done() {
            mode = "";
            try {
                if (get() != null) {
                    loading.setIcon(null);
                    loading.revalidate();
                }
            } catch (InterruptedException | ExecutionException ex) {
                // nothing
            }
        }
        
        @Override
        protected void process(List chunks) {
            mModel.addRow(new Object[] {
                "",
                listData.get(0),
                listData.get(1),
                listData.get(2),
            });
        }
        
        @Override
        protected Boolean doInBackground() throws Exception {
            showLoading();
            
            try {
                if(mode.equals("all-model")) {
                    sql = "SELECT m.id_model AS id, m.name AS name, (SELECT COUNT(p.model) FROM products p WHERE p.model = m.name) AS jumlah FROM model m";
                } else if(mode.equals("search-model")) {
                    sql = "SELECT m.id_model AS id, m.name AS name, (SELECT COUNT(p.model) FROM products p WHERE p.model = m.name) AS jumlah FROM model m WHERE m.name LIKE ?";
                }
                
                ps = con.prepareStatement(sql);
                if(mode.equals("search-model")) ps.setString(1, "%" + searchField.getText() + "%");
                
                rs = ps.executeQuery();
                while(rs.next()) {
                    listData.add(rs.getString(1));
                    listData.add(rs.getString(2));
                    listData.add(rs.getString(3));
                    publish(listData);
                    Thread.sleep(100);
                    listData.clear();
                }
            } catch(SQLException e) {
                return false;
            }
            
            mTb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = mTb.getSelectedRow();
                    String check = mTb.getModel().getValueAt(row, 0).toString();
                    String rowData = mTb.getModel().getValueAt(row, 1).toString();

                    if(check.equals("")) {
                        mTb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        mTb.setValueAt("", row, 0);
                        listId.remove(rowData);
                        if(listId.size() < 1) {
                            deleteBtn.setText("DELETE ALL");
                        } else {
                            deleteBtn.setText("DELETE (" + listId.size() + ")");
                        }
                    }

                    apparelModelT.setText("Edit Model");
                    dataClicked = rowData;
                    editFormMode = "update";
                    alertPass.setVisible(true);
                    insertBtn.setVisible(true);
                    apparelModelT.setForeground(new java.awt.Color(0, 153, 0));
                    apparelModelB.setBackground(new java.awt.Color(0, 153, 0));
                    if(apparelModelP.isShowing()) {
                        showEditModel(rowData);
                        editBtn.setVisible(false);
                    } else {
                        editBtn.setVisible(true);
                    }
                }
            });
            
            return true;
        }
    }
    
    private void showDataAdmin() {
        if(mode.equals("all-admin-show")) {
            aModel = (DefaultTableModel) aTb.getModel();
            aModel.setRowCount(0);
            mode = "all-admin";
        } else if(mode.equals("add-admin") || mode.equals("edit-admin")) {
            aModel = (DefaultTableModel) aTb.getModel();
            aModel.setRowCount(0);
            mode = "all-admin";
        } else {
            mode = "all-admin";
            this.setTitle("Data Admin");
            exportExcel.setVisible(false);
            searchField.setVisible(true);
            searchBtn.setVisible(true);
            deleteBtn.setText("DELETE ALL");
            listId.clear();
            showAll.setVisible(false);
            exportExcel.setVisible(false);
            
            JPanel tableBox = new JPanel();
            tableBox.setBackground(Color.white);
            tableBox.setLayout(new GridLayout(1, 0));
            aTb.setModel(new DefaultTableModel(
                new Object [][] {},
                new String [] {
                    "#", "Id", "Username", "Role"
                }
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

            JScrollPane scrollPane = new JScrollPane(aTb);
            tableBox.add(scrollPane);
            contentPanel.add(tableBox);
            aModel = (DefaultTableModel) aTb.getModel();
        }
        
        aWorker = new AdminWorker();
        aWorker.execute();
    }
    
    private void showEditAdmin(String username) {
        try {
            sql = "SELECT username, role FROM admin WHERE username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            while(rs.next()) {
                usernameField.setText(rs.getString("username"));
                roleField.setSelectedItem(rs.getString("role"));
            }
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void showEditApparel(String id) {
        try {
            sql = "SELECT name FROM apparel WHERE id_apparel = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            while(rs.next()) {
                apparelModelF.setText(rs.getString("name"));
            }
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void showEditModel(String id) {
        try {
            sql = "SELECT name FROM model WHERE id_model = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            while(rs.next()) {
                apparelModelF.setText(rs.getString("name"));
            }
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void showDataProducts() {
        if(mode.equals("all-product-show")) {
            pModel = (DefaultTableModel) pTb.getModel();
            pModel.setRowCount(0);
            mode = "all-product";
        } else if(mode.equals("add-product") || mode.equals("edit-product")) {
            pModel = (DefaultTableModel) pTb.getModel();
            pModel.setRowCount(0);
            mode = "all-product";
        } else if(mode.equals("all-product")) {
            this.setTitle("Data Products");
            searchField.setVisible(true);
            searchBtn.setVisible(true);
            insertBtn.setVisible(true);
            editBtn.setVisible(false);
            deleteBtn.setText("DELETE ALL");
            listId.clear();
            showAll.setVisible(false);
            exportExcel.setVisible(true);
            
            JPanel tableBox = new JPanel();
            tableBox.setBackground(Color.white);
            tableBox.setLayout(new GridLayout(1, 0));
            pTb.setModel(new DefaultTableModel(
                new Object [][] {
                },
                new String [] {
                    "#", "Id", "Nama", "Model", "Kategori", "Apparel", "Stok", "Harga"
                }
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

            JScrollPane scrollPane = new JScrollPane(pTb);
            tableBox.add(scrollPane);
            contentPanel.add(tableBox);
            pModel = (DefaultTableModel) pTb.getModel();
        }
        
        pWorker = new ProductWorker();
        pWorker.execute();
    }
    
    private void showEditProducts(String id) {
        try {
            sql = "SELECT * FROM products WHERE id_product = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            while(rs.next()) {
                nameField.setText(rs.getString("name"));
                modelField.setSelectedItem(rs.getString("model"));
                kategoriField.setSelectedItem(rs.getString("category"));
                apparelField.setSelectedItem(rs.getString("apparel"));
                hargaField.setText(rs.getString("price"));
                stokField.setText(rs.getString("stock"));
                
                imageChoose = rs.getString("image");
                filePath = null;
                URL url = new URL("http://localhost/nikeshop/src/assets/img/" + rs.getString("image"));
                Image image = ImageIO.read(url);
                JLabel l = new JLabel(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
                imageThumb.removeAll();
                imageThumb.revalidate();
                imageThumb.repaint();
                imageThumb.add(l);
                imageThumb.setVisible(true);
                
                imageBtn.setText("Ubah Gambar");
            }
        } catch(SQLException | IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void showDataUsers() {
        if(mode.equals("all-users-show")) {
            uModel = (DefaultTableModel) uTb.getModel();
            uModel.setRowCount(0);
            mode = "all-users";
        } else if(mode.equals("all-users")) {
            this.setTitle("Data Users");
            searchField.setVisible(true);
            searchBtn.setVisible(true);
            insertBtn.setVisible(false);
            editBtn.setVisible(false);
            showAll.setVisible(false);
            exportExcel.setVisible(false);

            JPanel tableBox = new JPanel();
            tableBox.setBackground(Color.white);
            tableBox.setLayout(new GridLayout(1, 0));
            uTb.setModel(new DefaultTableModel(
                new Object [][] {},
                new String [] {
                    "Id", "Username", "Produk Terakhir Dibeli", "Tanggal Terakhir Order"
                }
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

            JScrollPane scrollPane = new JScrollPane(uTb);
            tableBox.add(scrollPane);
            contentPanel.add(tableBox);
            uModel = (DefaultTableModel) uTb.getModel();
        }
        
        uWorker = new UsersWorker();
        uWorker.execute();
    }
    
    private void checkPage(int page) {
        switch (page) {
            case 1 -> {
                dashboardBtn.setBackground(new java.awt.Color(0,51,102));
                dashboardBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
            }
            case 2 -> {
                usersBtn.setBackground(new java.awt.Color(0,51,102));
                usersBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
            }
            case 3 -> {
                productBtn.setBackground(new java.awt.Color(0,51,102));
                productBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
            }
            case 4 -> {
                adminBtn.setBackground(new java.awt.Color(0,51,102));
                adminBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
            }
            case 5 -> {
                apparelBtn.setBackground(new java.awt.Color(0,51,102));
                apparelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
            }
            case 6 -> {
                modelBtn.setBackground(new java.awt.Color(0,51,102));
                modelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
            }
            case 7 -> {
                logoutBtn.setBackground(new java.awt.Color(0,51,102));
                logoutBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
            }
            default -> {
            }
        }
    }
    
    private void showFilterTahun() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar years = Calendar.getInstance();
        years.add(Calendar.YEAR, -1);
        filterTahun.addItem(String.valueOf(currentYear));
        filterTahun.addItem("" + years.get(Calendar.YEAR));
        years.add(Calendar.YEAR, -1);
        filterTahun.addItem("" + years.get(Calendar.YEAR));
        editBtn.setVisible(false);
        insertBtn.setVisible(false);
    }
    
    private void checkEstimated() {
        List<String> listIdOrder = new ArrayList<>();
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        sql = "SELECT id_order, estimated_date FROM myorder WHERE estimated_date <= ? AND `status` = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, dateNow);
            ps.setString(2, "Sedang diproses");
            rs = ps.executeQuery();
            boolean est = false;
            while(rs.next()) {
                listIdOrder.add(rs.getString("id_order"));
                est = true;
            }
            
            if(est == true) {
                sql = "UPDATE myorder SET `status` = ? WHERE id_order IN (" + String.join(",", listIdOrder) + ")";
                ps = con.prepareStatement(sql);
                ps.setString(1, "Berhasil dikirim");
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
    
    private DefaultCategoryDataset createLineDataset(String tahun) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            sql = "SELECT DISTINCT" +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 1 AND YEAR(order_date) = ? ) AS Januari," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 2 AND YEAR(order_date) = ? ) AS Februari," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 3 AND YEAR(order_date) = ? ) AS Maret," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 4 AND YEAR(order_date) = ? ) AS April," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 5 AND YEAR(order_date) = ? ) AS Mei," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 6 AND YEAR(order_date) = ? ) AS Juni," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 7 AND YEAR(order_date) = ? ) AS Juli," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 8 AND YEAR(order_date) = ? ) AS Agustus," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 9 AND YEAR(order_date) = ? ) AS September," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 10 AND YEAR(order_date) = ? ) AS Oktober," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 11 AND YEAR(order_date) = ? ) AS November," +
                    " ( SELECT SUM(price) FROM myorder WHERE MONTH(order_date) = 12 AND YEAR(order_date) = ? ) AS Desember " +
                    "FROM myorder";
            ps = con.prepareStatement(sql);
            for(int i = 0; i < 12; i++) {
                ps.setString(i + 1, tahun);
            }
            rs = ps.executeQuery();
            while(rs.next()) {
                dataset.addValue(rs.getString("Januari") == null ? 0 : Integer.parseInt(rs.getString("Januari")) , "Pendapatan" , "Januari");
                dataset.addValue(rs.getString("Februari") == null ? 0 : Integer.parseInt(rs.getString("Februari")) , "Pendapatan" , "Februari");
                dataset.addValue(rs.getString("Maret") == null ? 0 : Integer.parseInt(rs.getString("Maret")) , "Pendapatan" , "Maret");
                dataset.addValue(rs.getString("April") == null ? 0 : Integer.parseInt(rs.getString("April")) , "Pendapatan" , "April");
                dataset.addValue(rs.getString("Mei") == null ? 0 : Integer.parseInt(rs.getString("Mei")) , "Pendapatan" , "Mei");
                dataset.addValue(rs.getString("Juni") == null ? 0 : Integer.parseInt(rs.getString("Juni")) , "Pendapatan" , "Juni");
                dataset.addValue(rs.getString("Juli") == null ? 0 : Integer.parseInt(rs.getString("Juli")) , "Pendapatan" , "Juli");
                dataset.addValue(rs.getString("Agustus") == null ? 0 : Integer.parseInt(rs.getString("Agustus")) , "Pendapatan" , "Agustus");
                dataset.addValue(rs.getString("September") == null ? 0 : Integer.parseInt(rs.getString("September")) , "Pendapatan" , "September");
                dataset.addValue(rs.getString("Oktober") == null ? 0 : Integer.parseInt(rs.getString("Oktober")) , "Pendapatan" , "Oktober");
                dataset.addValue(rs.getString("November") == null ? 0 : Integer.parseInt(rs.getString("November")) , "Pendapatan" , "November");
                dataset.addValue(rs.getString("Desember") == null ? 0 : Integer.parseInt(rs.getString("Desember")) , "Pendapatan" , "Desember");
            }
            
            checkEstimated();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return dataset;
   }
    
    private void lineChart(String tahun) {
        JFreeChart lineChart = ChartFactory.createLineChart(
            "Rata-Rata Pendapatan Pertahun",
            "Bulan", "Total Pendapatan",
            createLineDataset(tahun),
            PlotOrientation.VERTICAL,
            true, true, false
        );
         
        lineChart.setBackgroundPaint(new Color(255,255,255));
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(500, 367));
        contentPanel.add(chartPanel);
    }
    
    private DefaultCategoryDataset createTotalPenjualan() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar years = Calendar.getInstance();
        years.add(Calendar.YEAR, -1);
        int prevYear1 = years.get(Calendar.YEAR);
        years.add(Calendar.YEAR, -1);
        int prevYear2 = years.get(Calendar.YEAR);
        try {
            sql = "SELECT DISTINCT "
                    + "(SELECT COALESCE(SUM(mo.price), 0) FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE YEAR(mo.order_date) = ?) AS f, "
                    + "(SELECT COALESCE(SUM(mo.price), 0) FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE YEAR(mo.order_date) = ?) AS s, "
                    + "(SELECT COALESCE(SUM(mo.price), 0) FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE YEAR(mo.order_date) = ?) AS t "
                    + "FROM myorder";
            ps = con.prepareStatement(sql);
            ps.setString(1, String.valueOf(prevYear2));
            ps.setString(2, String.valueOf(prevYear1));
            ps.setString(3, String.valueOf(currentYear));
            rs = ps.executeQuery();
            while(rs.next()) {
                dataset.addValue(Integer.parseInt(rs.getString("f")), "Total", String.valueOf(prevYear2));
                dataset.addValue(Integer.parseInt(rs.getString("s")), "Total", String.valueOf(prevYear1));
                dataset.addValue(Integer.parseInt(rs.getString("t")), "Total", String.valueOf(currentYear));
            }
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        return dataset;
    }
    
    private void totalPenjualanChart() {
        JFreeChart lineChart = ChartFactory.createLineChart(
            "Total Penjualan Produk",
            "Tahun", "Pendapatan",
            createTotalPenjualan(),
            PlotOrientation.VERTICAL,
            true, true, false
        );
         
        lineChart.setBackgroundPaint(new Color(255,255,255));
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(500, 367));
        contentPanel.add(chartPanel);
    }
    
    private void headerInformation() {
        sql = "SELECT "
                + "( SELECT COUNT(status) FROM myorder WHERE status = 'Berhasil dikirim' ) AS Berhasil, "
                + "( SELECT COUNT(status) FROM myorder WHERE status = 'Sedang diproses' ) AS Proses, "
                + "( SELECT COUNT(username) FROM users ) AS Pengguna, "
                + "( SELECT COUNT(id_product) FROM products ) AS Stock";
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                totalUsers.setText(rs.getString("Pengguna"));
                totalDikirim.setText(rs.getString("Berhasil"));
                totalDiproses.setText(rs.getString("Proses"));
                totalBarang.setText(rs.getString("Stock"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public static JPanel pieChart() {
        JFreeChart chart = createPieChart(datasetPie());
        chart.setBackgroundPaint(new Color(255,255,255));
        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 367);
            }
        };
   }
    
    private static PieDataset datasetPie() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        Connect DB = new Connect();
        DB.connect();
        Connection xcon = DB.conn;
        PreparedStatement xps;
        ResultSet xrs;
        List<String> listModel = new ArrayList<>();
        List<Integer> listCount = new ArrayList<>();
            
        try {
            String getModel = "SELECT name FROM model";
            xps = xcon.prepareStatement(getModel);
            xrs = xps.executeQuery();
            while(xrs.next()) {
                listModel.add(xrs.getString("name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        
        for(String lm : listModel) {
            try {
                String count = "SELECT COUNT(model) AS Jumlah FROM products WHERE model = ?";
                xps = xcon.prepareStatement(count);
                xps.setString(1, lm);
                xrs = xps.executeQuery();
                while(xrs.next()) {
                    listCount.add(Integer.parseInt(xrs.getString("Jumlah")));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        
        for(int i = 0; i < listModel.size(); i++) {
            dataset.setValue(listModel.get(i), Double.valueOf(listCount.get(i)));
        }
        
        return dataset;
   }
    
    private static JFreeChart createPieChart(PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(      
            "Jumlah Model Sepatu", 
            dataset,
            false,
            true, 
            false
        );

        return chart;
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
        sidebar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        welcomeName = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        dashboardBtn = new javax.swing.JLabel();
        usersBtn = new javax.swing.JLabel();
        productBtn = new javax.swing.JLabel();
        adminBtn = new javax.swing.JLabel();
        apparelBtn = new javax.swing.JLabel();
        modelBtn = new javax.swing.JLabel();
        logoutBtn = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        statusPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        totalBarang = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        totalDiproses = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        totalDikirim = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        totalUsers = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        contentScroll = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        filterTahun = new javax.swing.JComboBox<>();
        searchField = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        editBtn = new javax.swing.JButton();
        insertBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        showAll = new javax.swing.JButton();
        exportExcel = new javax.swing.JButton();
        loading = new javax.swing.JLabel();
        editForm = new javax.swing.JPanel();
        titleForm = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        modelField = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        kategoriField = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        apparelField = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        hargaField = new javax.swing.JTextField();
        closeEditForm = new javax.swing.JLabel();
        stokField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        imageLabel = new javax.swing.JLabel();
        imageBtn = new javax.swing.JButton();
        imageThumb = new javax.swing.JPanel();
        submitProducts = new javax.swing.JButton();
        adminForm = new javax.swing.JPanel();
        closeFormAdmin = new javax.swing.JLabel();
        titleForm1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        submitAdminForm = new javax.swing.JButton();
        alertPass = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        roleField = new javax.swing.JComboBox<>();
        apparelModelP = new javax.swing.JPanel();
        closeFormAdmin1 = new javax.swing.JLabel();
        apparelModelT = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        apparelModelF = new javax.swing.JTextField();
        apparelModelB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1084, 677));

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));

        sidebar.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Selamat Datang,");

        welcomeName.setFont(new java.awt.Font("Google Sans", 0, 18)); // NOI18N
        welcomeName.setForeground(new java.awt.Color(255, 255, 255));
        welcomeName.setText("username");

        jLabel10.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\jordan-45-white.png")); // NOI18N

        jPanel3.setBackground(new java.awt.Color(0, 51, 102));
        jPanel3.setLayout(new java.awt.GridLayout(7, 1));

        dashboardBtn.setBackground(new java.awt.Color(0, 51, 102));
        dashboardBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        dashboardBtn.setForeground(new java.awt.Color(255, 255, 255));
        dashboardBtn.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\dashboard.png")); // NOI18N
        dashboardBtn.setText("  Dashboard");
        dashboardBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 102), 15));
        dashboardBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dashboardBtn.setOpaque(true);
        dashboardBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashboardBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashboardBtnMouseExited(evt);
            }
        });
        jPanel3.add(dashboardBtn);

        usersBtn.setBackground(new java.awt.Color(0, 51, 102));
        usersBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        usersBtn.setForeground(new java.awt.Color(255, 255, 255));
        usersBtn.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\user.png")); // NOI18N
        usersBtn.setText("  Data Users");
        usersBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 102), 15));
        usersBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        usersBtn.setOpaque(true);
        usersBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usersBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                usersBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                usersBtnMouseExited(evt);
            }
        });
        jPanel3.add(usersBtn);

        productBtn.setBackground(new java.awt.Color(0, 51, 102));
        productBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        productBtn.setForeground(new java.awt.Color(255, 255, 255));
        productBtn.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\package.png")); // NOI18N
        productBtn.setText("  Data Products");
        productBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 102), 15));
        productBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        productBtn.setOpaque(true);
        productBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                productBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                productBtnMouseExited(evt);
            }
        });
        jPanel3.add(productBtn);

        adminBtn.setBackground(new java.awt.Color(0, 51, 102));
        adminBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        adminBtn.setForeground(new java.awt.Color(255, 255, 255));
        adminBtn.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\admin.png")); // NOI18N
        adminBtn.setText("  Data Admin");
        adminBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 102), 15));
        adminBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        adminBtn.setOpaque(true);
        adminBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adminBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                adminBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                adminBtnMouseExited(evt);
            }
        });
        jPanel3.add(adminBtn);

        apparelBtn.setBackground(new java.awt.Color(0, 51, 102));
        apparelBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        apparelBtn.setForeground(new java.awt.Color(255, 255, 255));
        apparelBtn.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\apparel.png")); // NOI18N
        apparelBtn.setText("  Data Apparel");
        apparelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 102), 15));
        apparelBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        apparelBtn.setOpaque(true);
        apparelBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                apparelBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                apparelBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                apparelBtnMouseExited(evt);
            }
        });
        jPanel3.add(apparelBtn);

        modelBtn.setBackground(new java.awt.Color(0, 51, 102));
        modelBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        modelBtn.setForeground(new java.awt.Color(255, 255, 255));
        modelBtn.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\model.png")); // NOI18N
        modelBtn.setText("  Data Model");
        modelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 102), 15));
        modelBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modelBtn.setOpaque(true);
        modelBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                modelBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                modelBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                modelBtnMouseExited(evt);
            }
        });
        jPanel3.add(modelBtn);

        logoutBtn.setBackground(new java.awt.Color(0, 51, 102));
        logoutBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        logoutBtn.setForeground(new java.awt.Color(255, 255, 255));
        logoutBtn.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\sign-out.png")); // NOI18N
        logoutBtn.setText("  Logout");
        logoutBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 102), 15));
        logoutBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logoutBtn.setOpaque(true);
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtnMouseExited(evt);
            }
        });
        jPanel3.add(logoutBtn);

        jLabel3.setForeground(new java.awt.Color(0, 102, 255));
        jLabel3.setText("Made by RiNov with ❤️");

        javax.swing.GroupLayout sidebarLayout = new javax.swing.GroupLayout(sidebar);
        sidebar.setLayout(sidebarLayout);
        sidebarLayout.setHorizontalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGroup(sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sidebarLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(welcomeName)
                            .addComponent(jLabel1)))
                    .addGroup(sidebarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel3)))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        sidebarLayout.setVerticalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sidebarLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(welcomeName))
                    .addComponent(jLabel10))
                .addGap(35, 35, 35)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(14, 14, 14))
        );

        statusPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 102, 204));

        jLabel2.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Jumlah Produk");

        totalBarang.setFont(new java.awt.Font("Google Sans", 1, 36)); // NOI18N
        totalBarang.setForeground(new java.awt.Color(255, 255, 255));
        totalBarang.setText("100");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(totalBarang))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalBarang)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(0, 204, 0));

        totalDiproses.setFont(new java.awt.Font("Google Sans", 1, 36)); // NOI18N
        totalDiproses.setForeground(new java.awt.Color(255, 255, 255));
        totalDiproses.setText("100");

        jLabel5.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Barang Diproses");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(totalDiproses))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalDiproses)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 153, 0));

        totalDikirim.setFont(new java.awt.Font("Google Sans", 1, 36)); // NOI18N
        totalDikirim.setForeground(new java.awt.Color(255, 255, 255));
        totalDikirim.setText("100");

        jLabel7.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Barang Dikirim");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(totalDikirim))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalDikirim)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 0, 51));

        totalUsers.setFont(new java.awt.Font("Google Sans", 1, 36)); // NOI18N
        totalUsers.setForeground(new java.awt.Color(255, 255, 255));
        totalUsers.setText("100");

        jLabel9.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Jumlah Pengguna");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(totalUsers))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalUsers)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        contentScroll.setBackground(new java.awt.Color(255, 255, 255));
        contentScroll.setBorder(null);

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setLayout(new java.awt.GridLayout(0, 2));
        contentScroll.setViewportView(contentPanel);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(204, 204, 204));

        title.setFont(new java.awt.Font("Google Sans", 0, 18)); // NOI18N
        title.setForeground(new java.awt.Color(0, 0, 0));
        title.setText("Dashboard");

        filterTahun.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        filterTahun.setForeground(new java.awt.Color(255, 255, 255));
        filterTahun.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterTahunItemStateChanged(evt);
            }
        });

        searchField.setBackground(new java.awt.Color(204, 204, 204));
        searchField.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        searchField.setForeground(new java.awt.Color(0, 0, 0));
        searchField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));
        searchField.setCaretColor(new java.awt.Color(51, 51, 51));

        searchBtn.setBackground(new java.awt.Color(0, 0, 0));
        searchBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        searchBtn.setForeground(new java.awt.Color(255, 255, 255));
        searchBtn.setText("CARI");
        searchBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchBtn.setContentAreaFilled(false);
        searchBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        searchBtn.setOpaque(true);
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        editBtn.setBackground(new java.awt.Color(0, 153, 0));
        editBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        editBtn.setForeground(new java.awt.Color(255, 255, 255));
        editBtn.setText("EDIT DATA");
        editBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 0)));
        editBtn.setContentAreaFilled(false);
        editBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editBtn.setOpaque(true);
        editBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editBtnMouseExited(evt);
            }
        });
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });

        insertBtn.setBackground(new java.awt.Color(0, 51, 204));
        insertBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        insertBtn.setForeground(new java.awt.Color(255, 255, 255));
        insertBtn.setText("ADD DATA");
        insertBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 204)));
        insertBtn.setContentAreaFilled(false);
        insertBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        insertBtn.setOpaque(true);
        insertBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                insertBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                insertBtnMouseExited(evt);
            }
        });
        insertBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertBtnActionPerformed(evt);
            }
        });

        deleteBtn.setBackground(new java.awt.Color(255, 0, 51));
        deleteBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        deleteBtn.setForeground(new java.awt.Color(255, 255, 255));
        deleteBtn.setText("DELETE ALL");
        deleteBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51)));
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteBtn.setOpaque(true);
        deleteBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteBtnMouseExited(evt);
            }
        });
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        showAll.setBackground(new java.awt.Color(153, 204, 255));
        showAll.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        showAll.setForeground(new java.awt.Color(0, 51, 255));
        showAll.setText("SHOW ALL");
        showAll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 255)));
        showAll.setContentAreaFilled(false);
        showAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        showAll.setOpaque(true);
        showAll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                showAllMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                showAllMouseExited(evt);
            }
        });
        showAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllActionPerformed(evt);
            }
        });

        exportExcel.setBackground(new java.awt.Color(255, 255, 153));
        exportExcel.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        exportExcel.setForeground(new java.awt.Color(153, 153, 0));
        exportExcel.setText("EXPORT EXCEL");
        exportExcel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 0)));
        exportExcel.setContentAreaFilled(false);
        exportExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exportExcel.setOpaque(true);
        exportExcel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exportExcelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exportExcelMouseExited(evt);
            }
        });
        exportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(title)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loading)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(exportExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showAll, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(insertBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterTahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(filterTahun)
                        .addComponent(title))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(searchField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(editBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(insertBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(showAll, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(exportExcel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(loading, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(2, 2, 2)))
                .addGap(3, 3, 3))
        );

        editForm.setBackground(new java.awt.Color(255, 255, 255));

        titleForm.setBackground(new java.awt.Color(0, 51, 102));
        titleForm.setFont(new java.awt.Font("Google Sans", 1, 14)); // NOI18N
        titleForm.setForeground(new java.awt.Color(0, 153, 0));
        titleForm.setText("Edit Data");

        jLabel4.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Nama Produk");

        nameField.setBackground(new java.awt.Color(255, 255, 255));
        nameField.setForeground(new java.awt.Color(0, 0, 0));
        nameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Model");

        modelField.setBackground(new java.awt.Color(255, 255, 255));
        modelField.setForeground(new java.awt.Color(0, 0, 0));

        jLabel8.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Kategori");

        kategoriField.setBackground(new java.awt.Color(255, 255, 255));
        kategoriField.setForeground(new java.awt.Color(0, 0, 0));
        kategoriField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pria", "Wanita" }));

        jLabel11.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Apparel");

        apparelField.setBackground(new java.awt.Color(255, 255, 255));
        apparelField.setForeground(new java.awt.Color(0, 0, 0));

        jLabel12.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Harga");

        hargaField.setBackground(new java.awt.Color(255, 255, 255));
        hargaField.setForeground(new java.awt.Color(0, 0, 0));
        hargaField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        closeEditForm.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        closeEditForm.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\cancel.png")); // NOI18N
        closeEditForm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        closeEditForm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeEditFormMouseClicked(evt);
            }
        });

        stokField.setBackground(new java.awt.Color(255, 255, 255));
        stokField.setForeground(new java.awt.Color(0, 0, 0));
        stokField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("Stok");

        imageLabel.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        imageLabel.setForeground(new java.awt.Color(0, 0, 0));
        imageLabel.setText("Gambar");

        imageBtn.setBackground(new java.awt.Color(255, 153, 0));
        imageBtn.setForeground(new java.awt.Color(255, 255, 255));
        imageBtn.setText("Pilih Gambar");
        imageBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 0)));
        imageBtn.setContentAreaFilled(false);
        imageBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        imageBtn.setOpaque(true);
        imageBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                imageBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                imageBtnMouseExited(evt);
            }
        });
        imageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageBtnActionPerformed(evt);
            }
        });

        imageThumb.setBackground(new java.awt.Color(255, 255, 255));
        imageThumb.setLayout(new java.awt.GridBagLayout());

        submitProducts.setBackground(new java.awt.Color(0, 153, 0));
        submitProducts.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        submitProducts.setForeground(new java.awt.Color(255, 255, 255));
        submitProducts.setText("Submit");
        submitProducts.setBorder(null);
        submitProducts.setContentAreaFilled(false);
        submitProducts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        submitProducts.setOpaque(true);
        submitProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitProductsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitProductsMouseExited(evt);
            }
        });
        submitProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitProductsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout editFormLayout = new javax.swing.GroupLayout(editForm);
        editForm.setLayout(editFormLayout);
        editFormLayout.setHorizontalGroup(
            editFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editFormLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(editFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hargaField)
                    .addComponent(nameField)
                    .addComponent(modelField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(kategoriField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(apparelField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(editFormLayout.createSequentialGroup()
                        .addComponent(titleForm)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                        .addComponent(closeEditForm, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(stokField)
                    .addGroup(editFormLayout.createSequentialGroup()
                        .addGroup(editFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(imageBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(editFormLayout.createSequentialGroup()
                                .addGroup(editFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel13)
                                    .addComponent(imageLabel))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imageThumb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(submitProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        editFormLayout.setVerticalGroup(
            editFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editFormLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(editFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(closeEditForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(titleForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kategoriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(apparelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hargaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stokField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imageThumb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(editFormLayout.createSequentialGroup()
                        .addComponent(imageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imageBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(submitProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        adminForm.setBackground(new java.awt.Color(255, 255, 255));

        closeFormAdmin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        closeFormAdmin.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\cancel.png")); // NOI18N
        closeFormAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        closeFormAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeFormAdminMouseClicked(evt);
            }
        });

        titleForm1.setBackground(new java.awt.Color(0, 51, 102));
        titleForm1.setFont(new java.awt.Font("Google Sans", 1, 14)); // NOI18N
        titleForm1.setForeground(new java.awt.Color(0, 51, 204));
        titleForm1.setText("Add Admin");

        jLabel14.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("Username");

        usernameField.setBackground(new java.awt.Color(255, 255, 255));
        usernameField.setForeground(new java.awt.Color(0, 0, 0));
        usernameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel15.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Password");

        passwordField.setBackground(new java.awt.Color(255, 255, 255));
        passwordField.setForeground(new java.awt.Color(0, 0, 0));
        passwordField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        submitAdminForm.setBackground(new java.awt.Color(0, 51, 204));
        submitAdminForm.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        submitAdminForm.setForeground(new java.awt.Color(255, 255, 255));
        submitAdminForm.setText("Submit");
        submitAdminForm.setBorder(null);
        submitAdminForm.setContentAreaFilled(false);
        submitAdminForm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        submitAdminForm.setOpaque(true);
        submitAdminForm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitAdminFormMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitAdminFormMouseExited(evt);
            }
        });
        submitAdminForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitAdminFormActionPerformed(evt);
            }
        });

        alertPass.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        alertPass.setForeground(new java.awt.Color(51, 51, 51));
        alertPass.setText("Kosongkan jika password tidak ingin diubah");

        jLabel17.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Role");

        roleField.setBackground(new java.awt.Color(255, 255, 255));
        roleField.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        roleField.setForeground(new java.awt.Color(0, 0, 0));
        roleField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "super", "admin" }));

        javax.swing.GroupLayout adminFormLayout = new javax.swing.GroupLayout(adminForm);
        adminForm.setLayout(adminFormLayout);
        adminFormLayout.setHorizontalGroup(
            adminFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminFormLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(adminFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roleField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usernameField)
                    .addComponent(passwordField)
                    .addGroup(adminFormLayout.createSequentialGroup()
                        .addGroup(adminFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(adminFormLayout.createSequentialGroup()
                                .addComponent(titleForm1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                                .addComponent(closeFormAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(adminFormLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addComponent(submitAdminForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(adminFormLayout.createSequentialGroup()
                        .addGroup(adminFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(alertPass)
                            .addComponent(jLabel15))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        adminFormLayout.setVerticalGroup(
            adminFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminFormLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(adminFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(closeFormAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(titleForm1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alertPass)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roleField, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(submitAdminForm, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        apparelModelP.setBackground(new java.awt.Color(255, 255, 255));

        closeFormAdmin1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        closeFormAdmin1.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\cancel.png")); // NOI18N
        closeFormAdmin1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        closeFormAdmin1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeFormAdmin1MouseClicked(evt);
            }
        });

        apparelModelT.setBackground(new java.awt.Color(0, 51, 102));
        apparelModelT.setFont(new java.awt.Font("Google Sans", 1, 14)); // NOI18N
        apparelModelT.setForeground(new java.awt.Color(0, 51, 204));
        apparelModelT.setText("Add Apparel");

        jLabel16.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("Nama");

        apparelModelF.setBackground(new java.awt.Color(255, 255, 255));
        apparelModelF.setForeground(new java.awt.Color(0, 0, 0));
        apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        apparelModelB.setBackground(new java.awt.Color(0, 51, 204));
        apparelModelB.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        apparelModelB.setForeground(new java.awt.Color(255, 255, 255));
        apparelModelB.setText("Submit");
        apparelModelB.setBorder(null);
        apparelModelB.setContentAreaFilled(false);
        apparelModelB.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        apparelModelB.setOpaque(true);
        apparelModelB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                apparelModelBMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                apparelModelBMouseExited(evt);
            }
        });
        apparelModelB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apparelModelBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout apparelModelPLayout = new javax.swing.GroupLayout(apparelModelP);
        apparelModelP.setLayout(apparelModelPLayout);
        apparelModelPLayout.setHorizontalGroup(
            apparelModelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(apparelModelPLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(apparelModelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(apparelModelF)
                    .addGroup(apparelModelPLayout.createSequentialGroup()
                        .addGroup(apparelModelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(apparelModelPLayout.createSequentialGroup()
                                .addComponent(apparelModelT)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
                                .addComponent(closeFormAdmin1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(apparelModelPLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addComponent(apparelModelB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        apparelModelPLayout.setVerticalGroup(
            apparelModelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(apparelModelPLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(apparelModelPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(closeFormAdmin1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(apparelModelT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(apparelModelF, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(apparelModelB, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(sidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(contentScroll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(apparelModelP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(adminForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contentScroll)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(editForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addComponent(adminForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(apparelModelP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

    private void usersBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersBtnMouseEntered
       if(pageSelected != 2) {
            usersBtn.setBackground(new java.awt.Color(0, 51, 153));
            usersBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 153), 15));
       }
    }//GEN-LAST:event_usersBtnMouseEntered

    private void usersBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersBtnMouseExited
        if(pageSelected != 2) {
            usersBtn.setBackground(new java.awt.Color(0,51,102));
            usersBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
        }
    }//GEN-LAST:event_usersBtnMouseExited

    private void productBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productBtnMouseEntered
        if(pageSelected != 3) {
            productBtn.setBackground(new java.awt.Color(0, 51, 153));
            productBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 153), 15));
        }
    }//GEN-LAST:event_productBtnMouseEntered

    private void productBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productBtnMouseExited
        if(pageSelected != 3) {
            productBtn.setBackground(new java.awt.Color(0,51,102));
            productBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
        }
    }//GEN-LAST:event_productBtnMouseExited

    private void adminBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminBtnMouseEntered
        if(pageSelected != 4) {
            adminBtn.setBackground(new java.awt.Color(0, 51, 153));
            adminBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 153), 15));
        }
    }//GEN-LAST:event_adminBtnMouseEntered

    private void adminBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminBtnMouseExited
        if(pageSelected != 4) {
            adminBtn.setBackground(new java.awt.Color(0,51,102));
            adminBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
        }
    }//GEN-LAST:event_adminBtnMouseExited

    private void logoutBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtnMouseEntered
        if(pageSelected != 7) {
            logoutBtn.setBackground(new java.awt.Color(0, 51, 153));
            logoutBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 153), 15));
        }
    }//GEN-LAST:event_logoutBtnMouseEntered

    private void logoutBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtnMouseExited
        if(pageSelected != 7) {
            logoutBtn.setBackground(new java.awt.Color(0,51,102));
            logoutBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
        }
    }//GEN-LAST:event_logoutBtnMouseExited

    private void dashboardBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardBtnMouseEntered
        if(pageSelected != 1) {
            dashboardBtn.setBackground(new java.awt.Color(0, 51, 153));
            dashboardBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 153), 15));
        }
    }//GEN-LAST:event_dashboardBtnMouseEntered

    private void dashboardBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardBtnMouseExited
        if(pageSelected != 1) {
            dashboardBtn.setBackground(new java.awt.Color(0,51,102));
            dashboardBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
        }
    }//GEN-LAST:event_dashboardBtnMouseExited

    private void filterTahunItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filterTahunItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            contentPanel.removeAll();
            contentPanel.revalidate();
            contentPanel.repaint();
            String x = (String) evt.getItem();
            exportExcel.setVisible(true);
            
            contentPanel.add(pieChart());
            lineChart(x);
            totalPenjualanChart();
            totalPenjualanProduk();
        }
    }//GEN-LAST:event_filterTahunItemStateChanged

    private void totalPenjualanProduk() {
        CategoryDataset dataset = datasetTotalPenjualanProduk();
        JFreeChart chart = ChartFactory.createBarChart(
            "Total Jumlah Produk Terjual",
            "Tahun",
            "Jumlah produk",
            dataset,
            PlotOrientation.VERTICAL,  
            true, true, false
        );

        chart.setBackgroundPaint(new Color(255,255,255));
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 367));
        contentPanel.add(chartPanel);
    }
    
     private CategoryDataset datasetTotalPenjualanProduk() {  
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar years = Calendar.getInstance();
        years.add(Calendar.YEAR, -1);
        int prevYear1 = years.get(Calendar.YEAR);
        years.add(Calendar.YEAR, -1);
        int prevYear2 = years.get(Calendar.YEAR);
        try {
            sql = "SELECT DISTINCT" +
                    " (SELECT COUNT(COALESCE(p.apparel, 0)) FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE p.apparel = 'Nike' AND YEAR(mo.order_date) = ?) AS firstNike," +
                    " (SELECT COUNT(COALESCE(p.apparel, 0)) FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE p.apparel = 'Jordan' AND YEAR(mo.order_date) = ?) AS firstJordan," +
                    " (SELECT COUNT(COALESCE(p.apparel, 0)) FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE p.apparel = 'Nike' AND YEAR(mo.order_date) = ?) AS secondNike," +
                    " (SELECT COUNT(COALESCE(p.apparel, 0)) FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE p.apparel = 'Jordan' AND YEAR(mo.order_date) = ?) AS secondJordan," +
                    " (SELECT COUNT(COALESCE(p.apparel, 0)) FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE p.apparel = 'Nike' AND YEAR(mo.order_date) = ?) AS thirdNike," +
                    " (SELECT COUNT(COALESCE(p.apparel, 0)) FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE p.apparel = 'Jordan' AND YEAR(mo.order_date) = ?) AS thirdJordan" +
                    " FROM myorder";
            ps = con.prepareStatement(sql);
            ps.setString(1, String.valueOf(prevYear2));
            ps.setString(2, String.valueOf(prevYear2));
            ps.setString(3, String.valueOf(prevYear1));
            ps.setString(4, String.valueOf(prevYear1));
            ps.setString(5, String.valueOf(currentYear));
            ps.setString(6, String.valueOf(currentYear));
            rs = ps.executeQuery();
            while(rs.next()) {
                dataset.addValue(Integer.parseInt(rs.getString("firstNike")), "Nike", String.valueOf(prevYear2));
                dataset.addValue(Integer.parseInt(rs.getString("firstJordan")), "Jordan", String.valueOf(prevYear2));
                dataset.addValue(Integer.parseInt(rs.getString("secondNike")), "Nike", String.valueOf(prevYear1));
                dataset.addValue(Integer.parseInt(rs.getString("secondJordan")), "Jordan", String.valueOf(prevYear1));
                dataset.addValue(Integer.parseInt(rs.getString("thirdNike")), "Nike", String.valueOf(currentYear));
                dataset.addValue(Integer.parseInt(rs.getString("thirdJordan")), "Jordan", String.valueOf(currentYear));
            }
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        return dataset;  
      }
    
    private void dashboardBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardBtnMouseClicked
        if(pageSelected != 1) {
            title.setText("Dashboard");
            this.setTitle("Dashboard");
            checkPage(pageSelected);
            pageSelected = 1;
            dashboardBtn.setBackground(new java.awt.Color(0,51,204));
            dashboardBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,204), 15));
            filterTahun.setVisible(true);
            exportExcel.setVisible(true);
            
            contentPanel.removeAll();
            contentPanel.revalidate();
            contentPanel.repaint();
            contentPanel.setLayout(new GridLayout(2, 0));
            
            editForm.setVisible(false);
            searchField.setVisible(false);
            searchBtn.setVisible(false);
            editBtn.setVisible(false);
            insertBtn.setVisible(false);
            deleteBtn.setVisible(false);
            apparelModelP.setVisible(false);
            contentPanel.add(pieChart());
            dataClicked = null;
            showAll.setVisible(false);
            lineChart((String) filterTahun.getSelectedItem());
            totalPenjualanChart();
            totalPenjualanProduk();
        }
    }//GEN-LAST:event_dashboardBtnMouseClicked

    private void usersBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersBtnMouseClicked
        if(pageSelected != 2) {
            listId.clear();
            listData.clear();
            title.setText("Data Users");
            checkPage(pageSelected);
            pageSelected = 2;
            usersBtn.setBackground(new java.awt.Color(0,51,204));
            usersBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,204), 15));
            filterTahun.setVisible(false);
            deleteBtn.setVisible(true);
            
            contentPanel.removeAll();
            contentPanel.revalidate();
            contentPanel.repaint();
            contentPanel.setLayout(new GridLayout(1, 0));
            
            apparelModelP.setVisible(false);
            editForm.setVisible(false);
            deleteBtn.setVisible(false);
            searchField.setText("");
            mode = "all-users";
            showDataUsers();
        }
    }//GEN-LAST:event_usersBtnMouseClicked

    private void productBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productBtnMouseClicked
        if(pageSelected != 3) {
            listId.clear();
            title.setText("Data Products");
            checkPage(pageSelected);
            pageSelected = 3;
            productBtn.setBackground(new java.awt.Color(0,51,204));
            productBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,204), 15));
            filterTahun.setVisible(false);
            deleteBtn.setVisible(true);
            listId.clear();
            deleteBtn.setText("DELETE ALL");
            searchField.setText("");
            
            contentPanel.removeAll();
            contentPanel.revalidate();
            contentPanel.repaint();
            contentPanel.setLayout(new GridLayout(1, 0));
            
            imageThumb.setVisible(false);
            checkFieldEditForm();
            editForm.setVisible(false);
            apparelModelP.setVisible(false);
            mode = "all-product";
            showDataProducts();
            showAllComboBox();
        }
    }//GEN-LAST:event_productBtnMouseClicked

    private void adminBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminBtnMouseClicked
        if(pageSelected != 4) {
            listData.clear();
            title.setText("Data Admin");
            checkPage(pageSelected);
            pageSelected = 4;
            listId.clear();
            deleteBtn.setVisible(true);
            deleteBtn.setText("DELETE ALL");
            dataClicked = null;
            adminBtn.setBackground(new java.awt.Color(0,51,204));
            adminBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,204), 15));
            filterTahun.setVisible(false);
            searchField.setText("");
            
            contentPanel.removeAll();
            contentPanel.revalidate();
            contentPanel.repaint();
            contentPanel.setLayout(new GridLayout(1, 0));
            
            editForm.setVisible(false);
            apparelModelP.setVisible(false);
            checkFieldAdminForm();
            mode = "all-admin";
            showDataAdmin();
        }
    }//GEN-LAST:event_adminBtnMouseClicked

    private void logoutBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtnMouseClicked
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog (null, "Yakin ingin keluar akun?", "Warning", dialogButton);
        if(dialogResult == JOptionPane.YES_OPTION) {
            this.setVisible(false);
            Login login = new Login();
            login.setVisible(true);
        }
    }//GEN-LAST:event_logoutBtnMouseClicked

    private void showAllComboBox() {
        try {
            sql = "SELECT name FROM model";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                modelField.addItem(rs.getString("name"));
            }
            
            sql = "SELECT name FROM apparel";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                apparelField.addItem(rs.getString("name"));
            }
            
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
        switch (pageSelected) {
            case 3 -> {
                titleForm.setForeground(new java.awt.Color(0, 153, 0));
                submitProducts.setBackground(new java.awt.Color(0, 153, 0));
                editForm.setVisible(true);
                editFormMode = "update";
                titleForm.setText("Edit Product");
                editBtn.setVisible(false);
                showEditProducts(dataClicked);
            }
            case 4 -> {
                titleForm1.setText("Edit Admin");
                titleForm1.setForeground(new java.awt.Color(0, 153, 0));
                submitAdminForm.setBackground(new java.awt.Color(0, 153, 0));
                adminForm.setVisible(true);
                editFormMode = "update";
                editBtn.setVisible(false);
                showEditAdmin(dataClicked);
            }
            case 5 -> {
                apparelModelT.setText("Edit Apparel");
                apparelModelT.setForeground(new java.awt.Color(0, 153, 0));
                submitAdminForm.setBackground(new java.awt.Color(0, 153, 0));
                apparelModelP.setVisible(true);
                editFormMode = "update";
                editBtn.setVisible(false);
                showEditApparel(dataClicked);
            }
            case 6 -> {
                apparelModelT.setText("Edit Model");
                apparelModelT.setForeground(new java.awt.Color(0, 153, 0));
                submitAdminForm.setBackground(new java.awt.Color(0, 153, 0));
                apparelModelP.setVisible(true);
                editFormMode = "update";
                editBtn.setVisible(false);
                showEditModel(dataClicked);
            }
            default -> {
            }
        }
    }//GEN-LAST:event_editBtnActionPerformed

    private void closeEditFormMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeEditFormMouseClicked
        editForm.setVisible(false);
        if(editFormMode.equals("insert")) {
            insertBtn.setVisible(true);
        } else if(editFormMode.equals("update")) {
            editBtn.setVisible(true);
        }
        nameField.setText("");
        modelField.setSelectedIndex(0);
        kategoriField.setSelectedIndex(0);
        apparelField.setSelectedIndex(0);
        hargaField.setText("");
        stokField.setText("");
        imageChoose = null;
        filePath = null;
        imageThumb.removeAll();
        imageThumb.revalidate();
        imageThumb.repaint();
    }//GEN-LAST:event_closeEditFormMouseClicked

    private void checkFieldAdminForm() {
        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if(usernameField.getText().equals("")) {
                    usernameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
                } else {
                    usernameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                }
             }
        });
        
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if(editFormMode.equals("insert")) {
                    if(String.valueOf(passwordField.getPassword()).equals("")) {
                        passwordField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
                    } else {
                        passwordField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                    }
                }
             }
        });
    }
    
    private void checkFieldEditForm() {
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if(nameField.getText().equals("")) {
                    nameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
                } else {
                    nameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                }
             }
        });
        
        hargaField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if(hargaField.getText().equals("")) {
                    hargaField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
                } else if(!hargaField.getText().matches("[0-9]+")) {
                    hargaField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
                } else {
                    hargaField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                }
             }
        });
        
        stokField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if(stokField.getText().equals("")) {
                    stokField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
                } else if(!stokField.getText().matches("[0-9]+")) {
                    stokField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
                } else {
                    stokField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                }
             }
        });
    }
    
    private void insertBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertBtnActionPerformed
        switch (pageSelected) {
            case 3 -> {
                imageBtn.setText("Pilih Gambar");
                insertBtn.setVisible(false);
                editFormMode = "insert";
                if(!editForm.isShowing()) {
                    editForm.setVisible(true);
                }
                imageChoose = null;
                filePath = null;
                titleForm.setText("Add Product");
                titleForm.setForeground(new java.awt.Color(0,51,204));
                submitProducts.setBackground(new java.awt.Color(0,51,204));
                nameField.setText("");
                modelField.setSelectedIndex(0);
                kategoriField.setSelectedIndex(0);
                apparelField.setSelectedIndex(0);
                hargaField.setText("");
                stokField.setText("");
                imageThumb.removeAll();
                imageThumb.revalidate();
                imageThumb.repaint();
            }
            case 4 -> {
                if(!adminForm.isShowing()) {
                    adminForm.setVisible(true);
                    insertBtn.setVisible(false);
                } else {
                    insertBtn.setVisible(false);
                }   alertPass.setVisible(false);
                usernameField.setText("");
                passwordField.setText("");
                titleForm1.setText("Add Admin");
                titleForm1.setForeground(new java.awt.Color(0,51,204));
                submitAdminForm.setBackground(new java.awt.Color(0,51,204));
                editFormMode = "insert";
            }
            case 5 -> {
                if(!apparelModelP.isShowing()) {
                    apparelModelP.setVisible(true);
                    insertBtn.setVisible(false);
                } else {
                    insertBtn.setVisible(false);
                }   alertPass.setVisible(false);
                apparelModelF.setText("");
                apparelModelT.setText("Add Apparel");
                apparelModelT.setForeground(new java.awt.Color(0,51,204));
                apparelModelB.setBackground(new java.awt.Color(0,51,204));
                editFormMode = "insert";
            }
            case 6 -> {
                if(!apparelModelP.isShowing()) {
                    apparelModelP.setVisible(true);
                    insertBtn.setVisible(false);
                } else {
                    insertBtn.setVisible(false);
                }   alertPass.setVisible(false);
                apparelModelF.setText("");
                apparelModelT.setText("Add Model");
                apparelModelT.setForeground(new java.awt.Color(0,51,204));
                apparelModelB.setBackground(new java.awt.Color(0,51,204));
                editFormMode = "insert";
            }
            default -> {
            }
        }
    }//GEN-LAST:event_insertBtnActionPerformed

    private void insertProduct() {
        if((nameField.getText().equals("") || hargaField.getText().equals("")) || (stokField.getText().equals("") || imageChoose == null)) {
            if(nameField.getText().equals("")) {
                nameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
            } else {
                nameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            }
            
            if(hargaField.getText().equals("")) {
                hargaField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
            } else {
                hargaField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            }
            
            if(stokField.getText().equals("")) {
                stokField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
            } else {
                stokField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            }
            
            if(imageChoose == null) {
                imageBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
            } else {
                imageBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 0)));
            }
        } else {
            try {
                Files.copy(Paths.get(filePath), Paths.get("D:\\xampp\\htdocs\\nikeshop\\src\\assets\\img\\" + imageChoose), StandardCopyOption.REPLACE_EXISTING);
                sql = "INSERT INTO products(name, model, category, apparel, stock, image, price) VALUES(?, ?, ?, ?, ?, ?, ?)";
                ps = con.prepareStatement(sql);
                ps.setString(1, nameField.getText());
                ps.setString(2, (String) modelField.getSelectedItem());
                ps.setString(3, (String) kategoriField.getSelectedItem());
                ps.setString(4, (String) apparelField.getSelectedItem());
                ps.setString(5, stokField.getText());
                ps.setString(6, imageChoose);
                ps.setString(7, hargaField.getText());
                ps.executeUpdate();
                
                nameField.setText("");
                modelField.setSelectedIndex(0);
                kategoriField.setSelectedIndex(0);
                apparelField.setSelectedIndex(0);
                hargaField.setText("");
                stokField.setText("");
                imageChoose = null;
                filePath = null;
                imageThumb.removeAll();
                imageThumb.revalidate();
                imageThumb.repaint();
                modelField.removeAllItems();
                apparelField.removeAllItems();
                listId.clear();
                deleteBtn.setText("DELETE ALL");
                
                headerInformation();
                showAllComboBox();
                mode = "add-products";
                showDataProducts();
                
                JOptionPane.showMessageDialog(null, "Berhasil menambahkan data produk");
            } catch (IOException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }
    
    private void updateProduct() {
        if((nameField.getText().equals("") && hargaField.getText().equals("")) && stokField.getText().equals("")) {
            if(nameField.getText().equals("")) {
                nameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
            } else {
                nameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            }
            
            if(hargaField.getText().equals("")) {
                hargaField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
            } else {
                hargaField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            }
            
            if(stokField.getText().equals("")) {
                stokField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
            } else {
                stokField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            }
        } else {
            try {
                if(filePath != null) {
                    Files.copy(Paths.get(filePath), Paths.get("D:\\xampp\\htdocs\\nikeshop\\src\\assets\\img\\" + imageChoose), StandardCopyOption.REPLACE_EXISTING);
                }
                sql = "UPDATE products SET name = ?, model = ?, category = ?, apparel = ?, stock = ?, image = ?, price = ? WHERE id_product = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, nameField.getText());
                ps.setString(2, (String) modelField.getSelectedItem());
                ps.setString(3, (String) kategoriField.getSelectedItem());
                ps.setString(4, (String) apparelField.getSelectedItem());
                ps.setString(5, stokField.getText());
                ps.setString(6, imageChoose);
                ps.setString(7, hargaField.getText());
                ps.setString(8, dataClicked);
                ps.executeUpdate();
                
                nameField.setText("");
                modelField.setSelectedIndex(0);
                kategoriField.setSelectedIndex(0);
                apparelField.setSelectedIndex(0);
                hargaField.setText("");
                stokField.setText("");
                imageChoose = null;
                filePath = null;
                imageThumb.removeAll();
                imageThumb.revalidate();
                imageThumb.repaint();
                modelField.removeAllItems();
                apparelField.removeAllItems();
                listId.clear();
                deleteBtn.setText("DELETE ALL");
                
                showAllComboBox();
                mode = "edit-product";
                showDataProducts();
                editForm.setVisible(false);
                
                JOptionPane.showMessageDialog(null, "Berhasil mengubah data produk");
            } catch (IOException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }
    
    private void submitProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitProductsActionPerformed
        if(editFormMode.equals("insert")) {
            insertProduct();
        } else if(editFormMode.equals("update")) {
            updateProduct();
        }
    }//GEN-LAST:event_submitProductsActionPerformed

    private void imageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageBtnActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        int returnVal = fileChooser.showOpenDialog((Component) evt.getSource());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String path = file.toString();
                String fileName = fileChooser.getSelectedFile().getName();
                if(path.endsWith(".png")) {
                    imageBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 0)));
                    imageChoose = fileName;
                    filePath = path;
                    JLabel l = new JLabel(new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
                    imageThumb.removeAll();
                    imageThumb.revalidate();
                    imageThumb.repaint();
                    imageThumb.add(l);
                    imageThumb.setVisible(true);
                } else {
                    imageBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
                    JOptionPane.showMessageDialog(null, "File harus format PNG");
                }
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(null, "Problem accessing file " + file.getAbsolutePath());
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_imageBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        int dialogButton = JOptionPane.YES_NO_OPTION;
        switch(pageSelected) {
            case 3 -> {
                int dialogResult = JOptionPane.showConfirmDialog (null, (listId.size() < 1 ? "Yakin ingin menghapus semua data produk?" : "Yakin ingin menghapus data produk ini?"), "Warning", dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION) {
                    deleteProduct();
                }
            }
            case 4 -> {
                int dialogResult = JOptionPane.showConfirmDialog (null, (listId.size() < 1 ? "Yakin ingin menghapus semua data admin?" : "Yakin ingin menghapus data admin ini?"), "Warning", dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION) {
                    deleteAdmin();
                }
            }
            case 5 -> {
                int dialogResult = JOptionPane.showConfirmDialog (null, (listId.size() < 1 ? "Yakin ingin menghapus semua data apparel?" : "Yakin ingin menghapus data apparel ini?"), "Warning", dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION) {
                    deleteApparel();
                }
            }
            case 6 -> {
                int dialogResult = JOptionPane.showConfirmDialog (null, (listId.size() < 1 ? "Yakin ingin menghapus semua data model?" : "Yakin ingin menghapus data model ini?"), "Warning", dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION) {
                    deleteModel();
                }
            }
            default -> {
            }
        }
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void deleteProduct() {
        try {
             sql = "DELETE FROM products" + (listId.size() < 1 ? "" : " WHERE id_product IN (" + String.join(",", listId) + ")");
             stat = con.createStatement();
             stat.executeUpdate(sql);

             listId.clear();
             headerInformation();
             contentPanel.removeAll();
             contentPanel.revalidate();
             contentPanel.repaint();
             showDataProducts();
             deleteBtn.setText("DELETE ALL");
             JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void deleteAdmin() {
        try {
             sql = "DELETE FROM admin" + (listId.size() < 1 ? "" : " WHERE id IN (" + String.join(",", listId) + ")");
             stat = con.createStatement();
             stat.executeUpdate(sql);

             listId.clear();
             headerInformation();
             contentPanel.removeAll();
             contentPanel.revalidate();
             contentPanel.repaint();
             showDataAdmin();
             deleteBtn.setText("DELETE ALL");
             JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void deleteApparel() {
        try {
             sql = "DELETE FROM apparel" + (listId.size() < 1 ? "" : " WHERE id_apparel IN (" + String.join(",", listId) + ")");
             stat = con.createStatement();
             stat.executeUpdate(sql);

             listId.clear();
             headerInformation();
             contentPanel.removeAll();
             contentPanel.revalidate();
             contentPanel.repaint();
             showDataApparel();
             deleteBtn.setText("DELETE ALL");
             JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void deleteModel() {
        try {
             sql = "DELETE FROM model" + (listId.size() < 1 ? "" : " WHERE id_model IN (" + String.join(",", listId) + ")");
             stat = con.createStatement();
             stat.executeUpdate(sql);

             listId.clear();
             headerInformation();
             contentPanel.removeAll();
             contentPanel.revalidate();
             contentPanel.repaint();
             showDataModel();
             deleteBtn.setText("DELETE ALL");
             JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void closeFormAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeFormAdminMouseClicked
        adminForm.setVisible(false);
        if(listId.size() < 1) {
            editBtn.setVisible(false);
        } else {
            editBtn.setVisible(true);
        }
        insertBtn.setVisible(true);
    }//GEN-LAST:event_closeFormAdminMouseClicked

    private String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }
    
    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
    
    private String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);

        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }
    
    private void insertAdmin() {
        if(usernameField.getText().equals("") || String.valueOf(passwordField.getPassword()).equals("")) {
            if(usernameField.getText().equals("")) {
                usernameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
            } else {
                usernameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            }
            
            if(String.valueOf(passwordField.getPassword()).equals("")) {
                passwordField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
            } else {
                passwordField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            }
        } else {
            try {
                sql = "SELECT username FROM admin WHERE username = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, usernameField.getText());
                rs = ps.executeQuery();
                boolean userExist = false;
                while(rs.next()) {
                    userExist = true;
                }
                
                if(userExist) {
                    JOptionPane.showMessageDialog(null, "Username sudah digunakan oleh admin lain, mohon gunakan username lain");
                } else {
                    String passHashed = hashPassword(String.valueOf(passwordField.getPassword()));
                    sql = "INSERT INTO admin(username, password, role) VALUES(?, ?, ?)";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, usernameField.getText());
                    ps.setString(2, passHashed);
                    ps.setString(3, (String) roleField.getSelectedItem());
                    ps.executeUpdate();
                    
                    usernameField.setText("");
                    passwordField.setText("");
                    roleField.setSelectedIndex(0);
                    mode = "add-admin";
                    showDataAdmin();
                    
                    JOptionPane.showMessageDialog(null, "Berhasil menambahkan admin baru");
                }
            } catch(SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void updateAdmin(String username) {
        if(usernameField.getText().equals("")) {
            if(usernameField.getText().equals("")) {
                usernameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
            } else {
                usernameField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            }
        } else {
            try {
                sql = "SELECT username, password FROM admin";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                boolean userExist = false;
                String oldPass = "";
                while(rs.next()) {
                    if(usernameField.getText().equals(rs.getString("username"))) {
                        if(rs.getString("username").equals(username)) {
                            oldPass = rs.getString("password");
                        }
                    } else {
                        if(rs.getString("username").equals(usernameField.getText())) {
                            userExist = true;
                        }
                    }
                }
                
                if(userExist) {
                    JOptionPane.showMessageDialog(null, "Username sudah digunakan oleh admin lain, mohon gunakan username lain");
                } else {
                    String passHashed = String.valueOf(passwordField.getPassword()).equals("") ? "" : hashPassword(String.valueOf(passwordField.getPassword()));
                    sql = "UPDATE admin SET username = ?, password = ? WHERE username = ?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, usernameField.getText());
                    ps.setString(2, String.valueOf(passwordField.getPassword()).equals("") ? oldPass : passHashed);
                    ps.setString(3, username);
                    ps.executeUpdate();
                    
                    usernameField.setText("");
                    passwordField.setText("");
                    mode = "edit-admin";
                    showDataAdmin();
                    
                    JOptionPane.showMessageDialog(null, "Berhasil mengubah data admin");
                }
            } catch(SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void closeFormAdmin1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeFormAdmin1MouseClicked
        apparelModelP.setVisible(false);
        insertBtn.setVisible(true);
    }//GEN-LAST:event_closeFormAdmin1MouseClicked

    private void apparelModelBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apparelModelBActionPerformed
        switch(pageSelected) {
            case 5 -> {
                if(editFormMode.equals("insert")) {
                    addApparel();
                } else if(editFormMode.equals("update")) {
                    editApparel(dataClicked);
                }
            }
            case 6 -> {
                if(editFormMode.equals("insert")) {
                    addModel();
                } else if(editFormMode.equals("update")) {
                    editModel(dataClicked);
                }
            }
            default -> {
            }
        }
    }//GEN-LAST:event_apparelModelBActionPerformed

    private void addApparel() {
        if(apparelModelF.getText().equals("")) {
            apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
        } else {
            apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,0,0)));
            try {
                sql = "SELECT name FROM apparel WHERE name = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, apparelModelF.getText());
                rs = ps.executeQuery();
                boolean exist = false;
                while(rs.next()) {
                    exist = true;
                }
                
                if(exist == true) {
                    JOptionPane.showMessageDialog(null, "Nama Apparel ini sudah ada");
                } else {
                    sql = "INSERT INTO apparel(name) VALUES(?)";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, apparelModelF.getText());
                    ps.executeUpdate();
                    
                    apparelModelF.setText("");
                    mode = "add-apparel";
                    showDataApparel();
                    insertBtn.setVisible(false);
                    
                    JOptionPane.showMessageDialog(null, "Berhasil menambahkan data apparel");
                }
            } catch(SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void editApparel(String id) {
        if(apparelModelF.getText().equals("")) {
            apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
        } else {
            apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,0,0)));
            try {
                sql = "SELECT name FROM apparel WHERE name = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, apparelModelF.getText());
                rs = ps.executeQuery();
                boolean exist = false;
                while(rs.next()) {
                    exist = true;
                }
                
                if(exist == true) {
                    JOptionPane.showMessageDialog(null, "Nama Apparel ini sudah ada");
                } else {
                    sql = "UPDATE apparel SET name = ? WHERE id_apparel = ?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, apparelModelF.getText());
                    ps.setString(2, id);
                    ps.executeUpdate();
                    
                    apparelModelF.setText("");
                    mode = "edit-apparel";
                    showDataApparel();
                    
                    JOptionPane.showMessageDialog(null, "Berhasil mengubah data apparel");
                }
            } catch(HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void addModel() {
        if(apparelModelF.getText().equals("")) {
            apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
        } else {
            apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,0,0)));
            try {
                sql = "SELECT name FROM model WHERE name = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, apparelModelF.getText());
                rs = ps.executeQuery();
                boolean exist = false;
                while(rs.next()) {
                    exist = true;
                }
                
                if(exist == true) {
                    JOptionPane.showMessageDialog(null, "Nama Model ini sudah ada");
                } else {
                    sql = "INSERT INTO model(name) VALUES(?)";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, apparelModelF.getText());
                    ps.executeUpdate();
                    
                    apparelModelF.setText("");
                    mode = "add-model";
                    showDataModel();
                    
                    JOptionPane.showMessageDialog(null, "Berhasil menambahkan data model");
                }
            } catch(SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void editModel(String id) {
        if(apparelModelF.getText().equals("")) {
            apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
        } else {
            apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,0,0)));
            try {
                sql = "SELECT name FROM model WHERE name = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, apparelModelF.getText());
                rs = ps.executeQuery();
                boolean exist = false;
                while(rs.next()) {
                    exist = true;
                }
                
                if(exist == true) {
                    JOptionPane.showMessageDialog(null, "Nama Model ini sudah ada");
                } else {
                    sql = "UPDATE model SET name = ? WHERE id_model = ?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, apparelModelF.getText());
                    ps.setString(2, id);
                    ps.executeUpdate();
                    
                    apparelModelF.setText("");
                    mode = "edit-model";
                    showDataModel();
                    
                    JOptionPane.showMessageDialog(null, "Berhasil mengubah data model");
                }
            } catch(HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void apparelBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_apparelBtnMouseEntered
        if(pageSelected != 5) {
            apparelBtn.setBackground(new java.awt.Color(0, 51, 153));
            apparelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 153), 15));
        }
    }//GEN-LAST:event_apparelBtnMouseEntered

    private void apparelBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_apparelBtnMouseExited
        if(pageSelected != 5) {
            apparelBtn.setBackground(new java.awt.Color(0,51,102));
            apparelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
        }
    }//GEN-LAST:event_apparelBtnMouseExited

    private void modelBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_modelBtnMouseClicked
        if(pageSelected != 6) {
            listData.clear();
            title.setText("Data Model");
            checkPage(pageSelected);
            pageSelected = 6;
            listId.clear();
            deleteBtn.setVisible(true);
            deleteBtn.setText("DELETE ALL");
            dataClicked = null;
            modelBtn.setBackground(new java.awt.Color(0,51,204));
            modelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,204), 15));
            filterTahun.setVisible(false);
            searchField.setText("");
            
            contentPanel.removeAll();
            contentPanel.revalidate();
            contentPanel.repaint();
            contentPanel.setLayout(new GridLayout(1, 0));
            
            editForm.setVisible(false);
            adminForm.setVisible(false);
            apparelModelP.setVisible(false);
            checkFieldApparelModel();
            mode = "all-model";
            showDataModel();
        }
    }//GEN-LAST:event_modelBtnMouseClicked

    private void modelBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_modelBtnMouseEntered
        if(pageSelected != 6) {
            modelBtn.setBackground(new java.awt.Color(0, 51, 153));
            modelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 153), 15));
        }
    }//GEN-LAST:event_modelBtnMouseEntered

    private void modelBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_modelBtnMouseExited
        if(pageSelected != 6) {
            modelBtn.setBackground(new java.awt.Color(0,51,102));
            modelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,102), 15));
        }
    }//GEN-LAST:event_modelBtnMouseExited

    private void apparelBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_apparelBtnMouseClicked
        if(pageSelected != 5) {
            listData.clear();
            title.setText("Data Apparel");
            checkPage(pageSelected);
            pageSelected = 5;
            listId.clear();
            deleteBtn.setVisible(true);
            deleteBtn.setText("DELETE ALL");
            dataClicked = null;
            apparelBtn.setBackground(new java.awt.Color(0,51,204));
            apparelBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,204), 15));
            filterTahun.setVisible(false);
            searchField.setText("");
            
            contentPanel.removeAll();
            contentPanel.revalidate();
            contentPanel.repaint();
            contentPanel.setLayout(new GridLayout(1, 0));
            
            editForm.setVisible(false);
            apparelModelP.setVisible(false);
            adminForm.setVisible(false);
            checkFieldApparelModel();
            mode = "all-apparel";
            showDataApparel();
        }
    }//GEN-LAST:event_apparelBtnMouseClicked

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        listId.clear();
        deleteBtn.setText("DELETE ALL");
        showAll.setVisible(true);
        switch(pageSelected) {
            case 2 -> {
                mode = "search-users";
                searchUser();
            }
            case 3 -> {
                if(editForm.isShowing()) {
                    editForm.setVisible(false);
                    editBtn.setVisible(false);
                }
                mode = "search-product";
                searchProduct();
            }
            case 4 -> {
                if(adminForm.isShowing()) {
                    editForm.setVisible(false);
                    editBtn.setVisible(false);
                }
                mode = "search-admin";
                searchAdmin();
            }
            case 5 -> {
                if(apparelModelP.isShowing()) {
                    editForm.setVisible(false);
                    editBtn.setVisible(false);
                }
                mode = "search-apparel";
                searchApparel();
            }
            case 6 -> {
                if(apparelModelP.isShowing()) {
                    editForm.setVisible(false);
                    editBtn.setVisible(false);
                }
                mode = "search-model";
                searchModel();
            }
            default -> {
            }
        }
    }//GEN-LAST:event_searchBtnActionPerformed

    private void showAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllActionPerformed
        showAll.setVisible(false);
        searchField.setText("");
        switch(pageSelected) {
            case 2 -> {
                mode = "all-users-show";
                showDataUsers();
            }
            case 3 -> {
                mode = "all-product-show";
                showDataProducts();
            }
            case 4 -> {
                mode = "all-admin-show";
                showDataAdmin();
            }
            case 5 -> {
                mode = "all-apparel-show";
                showDataApparel();
            }
            case 6 -> {
                mode = "all-model-show";
                showDataModel();
            }
            default -> {
            }
        }
    }//GEN-LAST:event_showAllActionPerformed

    private void exportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportExcelActionPerformed
        switch(pageSelected) {
            case 1 -> {
                exportPenghasilan((String) filterTahun.getSelectedItem());
            }
            case 3 -> {
                exportProduct();
            }
            case 5 -> {
                exportApparel();
            }
            case 6 -> {
                exportModel();
            }
            default -> {
            }
        }
    }//GEN-LAST:event_exportExcelActionPerformed

    private void imageBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageBtnMouseEntered
        imageBtn.setBackground(new java.awt.Color(212, 129, 4));
        imageBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(212, 129, 4)));
    }//GEN-LAST:event_imageBtnMouseEntered

    private void imageBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageBtnMouseExited
        imageBtn.setBackground(new java.awt.Color(255, 153, 0));
        imageBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 0)));
    }//GEN-LAST:event_imageBtnMouseExited

    private void submitProductsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitProductsMouseEntered
        submitProducts.setBackground(new java.awt.Color(0, 102, 0));
    }//GEN-LAST:event_submitProductsMouseEntered

    private void submitProductsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitProductsMouseExited
        submitProducts.setBackground(new java.awt.Color(0, 153, 0));
    }//GEN-LAST:event_submitProductsMouseExited

    private void submitAdminFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitAdminFormActionPerformed
        if(editFormMode.equals("insert")) {
            insertAdmin();
        } else if(editFormMode.equals("update")) {
            updateAdmin(dataClicked);
        }
    }//GEN-LAST:event_submitAdminFormActionPerformed

    private void submitAdminFormMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitAdminFormMouseExited
        submitAdminForm.setBackground(new java.awt.Color(0, 51, 204));
    }//GEN-LAST:event_submitAdminFormMouseExited

    private void submitAdminFormMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitAdminFormMouseEntered
        submitAdminForm.setBackground(new java.awt.Color(0, 41, 166));
    }//GEN-LAST:event_submitAdminFormMouseEntered

    private void apparelModelBMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_apparelModelBMouseEntered
        apparelModelB.setBackground(new java.awt.Color(0, 41, 166));
    }//GEN-LAST:event_apparelModelBMouseEntered

    private void apparelModelBMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_apparelModelBMouseExited
        apparelModelB.setBackground(new java.awt.Color(0, 51, 204));
    }//GEN-LAST:event_apparelModelBMouseExited

    private void editBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editBtnMouseEntered
        editBtn.setBackground(new java.awt.Color(0, 102, 0));
        editBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 0)));
    }//GEN-LAST:event_editBtnMouseEntered

    private void editBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editBtnMouseExited
        editBtn.setBackground(new java.awt.Color(0, 153, 0));
        editBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 0)));
    }//GEN-LAST:event_editBtnMouseExited

    private void insertBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_insertBtnMouseEntered
        insertBtn.setBackground(new java.awt.Color(0, 41, 166));
        insertBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 41, 166)));
    }//GEN-LAST:event_insertBtnMouseEntered

    private void insertBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_insertBtnMouseExited
        insertBtn.setBackground(new java.awt.Color(0, 51, 204));
        insertBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 204)));
    }//GEN-LAST:event_insertBtnMouseExited

    private void deleteBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteBtnMouseEntered
        deleteBtn.setBackground(new java.awt.Color(201, 2, 42));
        deleteBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(201, 2, 42)));
    }//GEN-LAST:event_deleteBtnMouseEntered

    private void deleteBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteBtnMouseExited
        deleteBtn.setBackground(new java.awt.Color(255, 0, 51));
        deleteBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51)));
    }//GEN-LAST:event_deleteBtnMouseExited

    private void showAllMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showAllMouseEntered
        showAll.setBackground(new java.awt.Color(89, 141, 194));
    }//GEN-LAST:event_showAllMouseEntered

    private void showAllMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showAllMouseExited
        showAll.setBackground(new java.awt.Color(153, 204, 255));
    }//GEN-LAST:event_showAllMouseExited

    private void exportExcelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exportExcelMouseEntered
        exportExcel.setBackground(new java.awt.Color(196, 196, 102));
    }//GEN-LAST:event_exportExcelMouseEntered

    private void exportExcelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exportExcelMouseExited
        exportExcel.setBackground(new java.awt.Color(255, 255, 153));
    }//GEN-LAST:event_exportExcelMouseExited

    private void exportPenghasilan(String tahun) {
        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("D:/penghasilan-report-" + tgl + ".xls"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xls");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Tentukan Lokasi File Excel Yang Akan Disimpan");
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String file = fileToSave.toString();
            if(!file.endsWith(".xls"))
               file = file + ".xls";
            
            try {
                FileOutputStream fileOut;
                fileOut = new FileOutputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet worksheet = workbook.createSheet("Sheet 0");
                Row row1 = worksheet.createRow((short) 0);
                row1.createCell(0).setCellValue("No");
                row1.createCell(1).setCellValue("Bulan");
                row1.createCell(2).setCellValue("Tahun");
                row1.createCell(3).setCellValue("Penghasilan");
                
                Row row2;
                sql = "SELECT DISTINCT" +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 1 AND YEAR(order_date) = ? ) AS Bulan " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT" +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 2 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT" +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 3 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT" +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 4 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT" +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 5 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT" +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 6 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT" +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 7 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT" +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 8 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT" +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 9 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT " +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 10 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL" +
                    " SELECT DISTINCT " +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 11 AND YEAR(order_date) = ? ) " +
                    "FROM myorder UNION ALL " +
                    " SELECT DISTINCT " +
                    " ( SELECT COALESCE(SUM(price), 0) FROM myorder WHERE MONTH(order_date) = 12 AND YEAR(order_date) = ? ) " +
                    "FROM myorder";
                ps = con.prepareStatement(sql);
                for(int i = 1; i <= 12; i++) {
                    ps.setString(i, tahun);
                }
                rs = ps.executeQuery();
                int num = 1;
                String[] bulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                while(rs.next()) {
                    int a = rs.getRow();
                    row2 = worksheet.createRow((short) a);
                    row2.createCell(0).setCellValue(num);
                    row2.createCell(1).setCellValue(bulan[num - 1]);
                    row2.createCell(2).setCellValue(tahun);
                    row2.createCell(3).setCellValue(rs.getString(1));
                    num++;
                }
                workbook.write(fileOut);
                fileOut.flush();
                fileOut.close();
                rs.close();
                ps.close();
                JOptionPane.showMessageDialog(null, "Data Penghasilan berhasil di export");
            } catch(FileNotFoundException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void exportProduct() {
        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("D:/products-report-" + tgl + ".xls"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xls");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Tentukan Lokasi File Excel Yang Akan Disimpan");
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String file = fileToSave.toString();
            if(!file.endsWith(".xls"))
               file = file + ".xls";
            
            try {
                FileOutputStream fileOut;
                fileOut = new FileOutputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet worksheet = workbook.createSheet("Sheet 0");
                Row row1 = worksheet.createRow((short) 0);
                row1.createCell(0).setCellValue("Id");
                row1.createCell(1).setCellValue("Nama Produk");
                Row row2;
                sql = "SELECT * FROM products";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                while(rs.next()) {
                    int a = rs.getRow();
                    row2 = worksheet.createRow((short) a);
                    row2.createCell(0).setCellValue(rs.getString(1));
                }
                workbook.write(fileOut);
                fileOut.flush();
                fileOut.close();
                rs.close();
                ps.close();
                JOptionPane.showMessageDialog(null, "Data Produk berhasil di export");
            } catch(FileNotFoundException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void exportApparel() {
        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("D:/apparel-report-" + tgl + ".xls"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xls");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Tentukan Lokasi File Excel Yang Akan Disimpan");
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String file = fileToSave.toString();
            if(!file.endsWith(".xls"))
               file = file + ".xls";
        
            try {
                FileOutputStream fileOut;
                fileOut = new FileOutputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet worksheet = workbook.createSheet("Sheet 0");
                Row row1 = worksheet.createRow((short) 0);
                row1.createCell(0).setCellValue("Id");
                row1.createCell(1).setCellValue("Nama Apparel");
                row1.createCell(2).setCellValue("Jumlah Produk");
                Row row2;
                sql = "SELECT a.id_apparel AS id, a.name AS name, (SELECT COUNT(p.apparel) FROM products p WHERE p.apparel = a.name) AS jumlah FROM apparel a";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                while(rs.next()) {
                    int a = rs.getRow();
                    row2 = worksheet.createRow((short) a);
                    row2.createCell(0).setCellValue(rs.getString(1));
                    row2.createCell(1).setCellValue(rs.getString(2));
                    row2.createCell(2).setCellValue(rs.getString(3));
                }
                workbook.write(fileOut);
                fileOut.flush();
                fileOut.close();
                rs.close();
                ps.close();
                JOptionPane.showMessageDialog(null, "Data Apparel berhasil di export ke D:/");
            } catch(IOException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void exportModel() {
        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("D:/model-report-" + tgl + ".xls"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xls");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Tentukan Lokasi File Excel Yang Akan Disimpan");
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String file = fileToSave.toString();
            if(!file.endsWith(".xls"))
               file = file + ".xls";
        
            try {
                FileOutputStream fileOut;
                fileOut = new FileOutputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet worksheet = workbook.createSheet("Sheet 0");
                Row row1 = worksheet.createRow((short) 0);
                row1.createCell(0).setCellValue("Id");
                row1.createCell(1).setCellValue("Nama Model");
                row1.createCell(2).setCellValue("Jumlah Produk");
                Row row2;
                sql = "SELECT m.id_model AS id, m.name AS name, (SELECT COUNT(p.model) FROM products p WHERE p.model = m.name) AS jumlah FROM model m";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                while(rs.next()) {
                    int a = rs.getRow();
                    row2 = worksheet.createRow((short) a);
                    row2.createCell(0).setCellValue(rs.getString(1));
                    row2.createCell(1).setCellValue(rs.getString(2));
                    row2.createCell(2).setCellValue(rs.getString(3));
                }
                workbook.write(fileOut);
                fileOut.flush();
                fileOut.close();
                rs.close();
                ps.close();
                JOptionPane.showMessageDialog(null, "Data Model berhasil di export ke D:/");
            } catch(IOException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void searchUser() {
        uModel = (DefaultTableModel) uTb.getModel();
        uModel.setRowCount(0);
        
        showDataUsers();
    }
    
    private void searchProduct() {
        pModel = (DefaultTableModel) pTb.getModel();
        pModel.setRowCount(0);
        
        showDataProducts();
    }
    
    private void searchAdmin() {
        aModel = (DefaultTableModel) aTb.getModel();
        aModel.setRowCount(0);
        
        showDataAdmin();
    }
    
    private void searchApparel() {
        apModel = (DefaultTableModel) apTb.getModel();
        apModel.setRowCount(0);
        
        showDataApparel();
    }
    
    private void searchModel() {
        mModel = (DefaultTableModel) mTb.getModel();
        mModel.setRowCount(0);
        
        showDataModel();
    }
    
    private void checkFieldApparelModel() {
        apparelModelF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if(apparelModelF.getText().equals("")) {
                    apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204,0,0)));
                } else {
                    apparelModelF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                }
             }
        });
    }
    
    private void showDataApparel() {
        if(mode.equals("all-apparel-show")) {
            apModel = (DefaultTableModel) apTb.getModel();
            apModel.setRowCount(0);
            mode = "all-apparel";
        } else if(mode.equals("add-apparel") || mode.equals("edit-apparel")) {
            apModel = (DefaultTableModel) apTb.getModel();
            apModel.setRowCount(0);
            mode = "all-apparel";
        } else if(mode.equals("all-apparel")) {
            this.setTitle("Data Apparel");
            exportExcel.setVisible(false);
            searchField.setVisible(true);
            searchBtn.setVisible(true);
            insertBtn.setVisible(true);
            editBtn.setVisible(false);
            deleteBtn.setText("DELETE ALL");
            listId.clear();
            showAll.setVisible(false);
            exportExcel.setVisible(true);

            JPanel tableBox = new JPanel();
            tableBox.setBackground(Color.white);
            tableBox.setLayout(new GridLayout(1, 0));
            apTb.setModel(new DefaultTableModel(
                new Object [][] {},
                new String [] {
                    "#", "Id", "Nama", "Jumlah Produk"
                }
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

            JScrollPane scrollPane = new JScrollPane(apTb);
            tableBox.add(scrollPane);
            contentPanel.add(tableBox);
            apModel = (DefaultTableModel) apTb.getModel();
        }
        
        apWorker = new ApparelWorker();
        apWorker.execute();
    }
    
    private void showDataModel() {
        if(mode.equals("all-model-show")) {
            mModel = (DefaultTableModel) mTb.getModel();
            mModel.setRowCount(0);
            mode = "all-model";
        } else if(mode.equals("add-model") || mode.equals("edit-model")) {
            mModel = (DefaultTableModel) mTb.getModel();
            mModel.setRowCount(0);
            mode = "all-model";
        } else if(mode.equals("all-model")) {
            this.setTitle("Data Model");
            exportExcel.setVisible(false);
            searchField.setVisible(true);
            searchBtn.setVisible(true);
            insertBtn.setVisible(true);
            editBtn.setVisible(false);
            deleteBtn.setText("DELETE ALL");
            listId.clear();
            showAll.setVisible(false);
            exportExcel.setVisible(true);

            JPanel tableBox = new JPanel();
            tableBox.setBackground(Color.white);
            tableBox.setLayout(new GridLayout(1, 0));
            mTb.setModel(new DefaultTableModel(
                new Object [][] {},
                new String [] {
                    "#", "Id", "Nama", "Jumlah Produk"
                }
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

            JScrollPane scrollPane = new JScrollPane(mTb);
            tableBox.add(scrollPane);
            contentPanel.add(tableBox);
            mModel = (DefaultTableModel) mTb.getModel();
        }
        
        mWorker = new ModelWorker();
        mWorker.execute();
    }
    
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
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adminBtn;
    private javax.swing.JPanel adminForm;
    private javax.swing.JLabel alertPass;
    private javax.swing.JLabel apparelBtn;
    private javax.swing.JComboBox<String> apparelField;
    private javax.swing.JButton apparelModelB;
    private javax.swing.JTextField apparelModelF;
    private javax.swing.JPanel apparelModelP;
    private javax.swing.JLabel apparelModelT;
    private javax.swing.JLabel closeEditForm;
    private javax.swing.JLabel closeFormAdmin;
    private javax.swing.JLabel closeFormAdmin1;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JScrollPane contentScroll;
    private javax.swing.JLabel dashboardBtn;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JButton editBtn;
    private javax.swing.JPanel editForm;
    private javax.swing.JButton exportExcel;
    private javax.swing.JComboBox<String> filterTahun;
    private javax.swing.JTextField hargaField;
    private javax.swing.JButton imageBtn;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JPanel imageThumb;
    private javax.swing.JButton insertBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JComboBox<String> kategoriField;
    private javax.swing.JLabel loading;
    private javax.swing.JLabel logoutBtn;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel modelBtn;
    private javax.swing.JComboBox<String> modelField;
    private javax.swing.JTextField nameField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel productBtn;
    private javax.swing.JComboBox<String> roleField;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchField;
    private javax.swing.JButton showAll;
    private javax.swing.JPanel sidebar;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextField stokField;
    private javax.swing.JButton submitAdminForm;
    private javax.swing.JButton submitProducts;
    private javax.swing.JLabel title;
    private javax.swing.JLabel titleForm;
    private javax.swing.JLabel titleForm1;
    private javax.swing.JLabel totalBarang;
    private javax.swing.JLabel totalDikirim;
    private javax.swing.JLabel totalDiproses;
    private javax.swing.JLabel totalUsers;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usersBtn;
    private javax.swing.JLabel welcomeName;
    // End of variables declaration//GEN-END:variables
}

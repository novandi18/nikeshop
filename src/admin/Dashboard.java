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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 *
 * @author Novandi Ramadhan
 */
public class Dashboard extends javax.swing.JFrame {
    int pageSelected = 1;
    Connection con;
    Statement stat;
    PreparedStatement ps;
    ResultSet rs;
    String sql, dataClicked, editFormMode, imageChoose, filePath;
    List<String> listId = new ArrayList<>();
    JTable tb = new JTable();
    
    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("../assets/jordan.png")).getImage());
        this.setTitle("Dashboard");
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
    
    private void showDataAdmin() {
        this.setTitle("Data Admin");
        searchField.setVisible(true);
        searchBtn.setVisible(true);
        insertBtn.setVisible(true);
        editBtn.setVisible(false);
        deleteBtn.setText("DELETE ALL");
        listId.clear();
        showAll.setVisible(false);
        
        JPanel tableBox = new JPanel();
        tableBox.setBackground(Color.white);
        tableBox.setLayout(new GridLayout(1, 0));
        tb = new JTable();
        DefaultTableModel table = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.addColumn("#");
        table.addColumn("Id");
        table.addColumn("Username");
        try {
            sql = "SELECT * FROM admin";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                table.addRow(new Object[] {
                    "", rs.getString(1), rs.getString(2)
                });
            }
            tb.setModel(table);
            tb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = tb.getSelectedRow();
                    String check = tb.getModel().getValueAt(row, 0).toString();
                    String rowData = tb.getModel().getValueAt(row, 2).toString();

                    if(check.equals("")) {
                        tb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        tb.setValueAt("", row, 0);
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
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        JScrollPane scrollPane = new JScrollPane(tb);
        tableBox.add(scrollPane);
        contentPanel.add(tableBox);
    }
    
    private void showEditAdmin(String username) {
        try {
            sql = "SELECT username FROM admin WHERE username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            while(rs.next()) {
                usernameField.setText(rs.getString("username"));
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
        this.setTitle("Data Products");
        searchField.setVisible(true);
        searchBtn.setVisible(true);
        insertBtn.setVisible(true);
        editBtn.setVisible(false);
        deleteBtn.setText("DELETE ALL");
        listId.clear();
        showAll.setVisible(false);
        
        JPanel tableBox = new JPanel();
        tableBox.setBackground(Color.white);
        tableBox.setLayout(new GridLayout(1, 0));
        tb = new JTable();
        DefaultTableModel table = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.addColumn("#");
        table.addColumn("Id");
        table.addColumn("Nama");
        table.addColumn("Model");
        table.addColumn("Kategori");
        table.addColumn("Apparel");
        table.addColumn("Stok");
        table.addColumn("Harga");
        try {
            sql = "SELECT * FROM products";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                table.addRow(new Object[] {
                    "", rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6),
                    rs.getString(8)
                });
            }
            tb.setModel(table);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        tb.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tb.getSelectedRow();
                String check = tb.getModel().getValueAt(row, 0).toString();
                String rowData = tb.getModel().getValueAt(row, 1).toString();
                
                if(check.equals("")) {
                    tb.setValueAt("Dipilih", row, 0);
                    listId.add(rowData);
                    deleteBtn.setText("DELETE (" + listId.size() + ")");
                } else {
                    tb.setValueAt("", row, 0);
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
        JScrollPane scrollPane = new JScrollPane(tb);
        tableBox.add(scrollPane);
        contentPanel.add(tableBox);
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
        this.setTitle("Data Users");
        searchField.setVisible(true);
        searchBtn.setVisible(true);
        insertBtn.setVisible(false);
        editBtn.setVisible(false);
        showAll.setVisible(false);
        
        JPanel tableBox = new JPanel();
        tableBox.setBackground(Color.white);
        tableBox.setLayout(new GridLayout(1, 0));
        tb = new JTable();
        DefaultTableModel table = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.addColumn("Id");
        table.addColumn("Username");
        table.addColumn("Produk Terakhir Dibeli");
        table.addColumn("Tanggal Terakhir Order");
        try {
            sql = "SELECT u.id as id, u.username as user, p.name AS produk, mo.order_date as orderdate FROM users u "
                    + "JOIN myorder mo ON u.username = mo.username JOIN products p ON mo.id_product = p.id_product "
                    + "WHERE mo.order_date = ( SELECT MAX(order_date) FROM myorder WHERE username = u.username )";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                table.addRow(new Object[] {
                    rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4)
                });
            }
            tb.setModel(table);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        JScrollPane scrollPane = new JScrollPane(tb);
        tableBox.add(scrollPane);
        contentPanel.add(tableBox);
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
        chartPanel.setPreferredSize(new Dimension(560 , 367));
        contentPanel.add(chartPanel);
    }
    
    private void tableBarangDiproses() {
        JPanel tableBox = new JPanel();
        tableBox.setBackground(Color.white);
        tableBox.setLayout(new GridLayout(1, 0));
        tb = new JTable();
        DefaultTableModel table = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.addColumn("Id Order");
        table.addColumn("Produk");
        table.addColumn("Tanggal Order");
        table.addColumn("Tanggal Estimasi");
        try {
            sql = "SELECT mo.id_order, p.name, mo.order_date, mo.estimated_date FROM myorder mo JOIN products p ON p.id_product = mo.id_product WHERE status = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, "Sedang diproses");
            rs = ps.executeQuery();
            while(rs.next()) {
                table.addRow(new Object[] {
                    rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4)
                });
            }
            tb.setModel(table);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        JScrollPane scrollPane = new JScrollPane(tb);
        tableBox.add(scrollPane);
        contentPanel.add(tableBox);
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
                return new Dimension(560 , 367);
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
        welcomeName.setText("adminganteng");

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

        javax.swing.GroupLayout sidebarLayout = new javax.swing.GroupLayout(sidebar);
        sidebar.setLayout(sidebarLayout);
        sidebarLayout.setHorizontalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(welcomeName)
                    .addComponent(jLabel1))
                .addContainerGap(71, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        showAll.setBackground(new java.awt.Color(153, 204, 255));
        showAll.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        showAll.setForeground(new java.awt.Color(0, 51, 255));
        showAll.setText("SHOW ALL");
        showAll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 255)));
        showAll.setContentAreaFilled(false);
        showAll.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        showAll.setOpaque(true);
        showAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(title)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(filterTahun)
                        .addComponent(title))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(showAll, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(editBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(insertBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        submitAdminForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitAdminFormActionPerformed(evt);
            }
        });

        alertPass.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        alertPass.setForeground(new java.awt.Color(51, 51, 51));
        alertPass.setText("Kosongkan jika password tidak ingin diubah");

        javax.swing.GroupLayout adminFormLayout = new javax.swing.GroupLayout(adminForm);
        adminForm.setLayout(adminFormLayout);
        adminFormLayout.setHorizontalGroup(
            adminFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminFormLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(adminFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addGap(63, 63, 63)
                .addComponent(submitAdminForm, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(214, Short.MAX_VALUE))
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
            
            contentPanel.add(pieChart());
            lineChart(x);
            tableBarangDiproses();
        }
    }//GEN-LAST:event_filterTahunItemStateChanged

    private void dashboardBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardBtnMouseClicked
        if(pageSelected != 1) {
            title.setText("Dashboard");
            this.setTitle("Dashboard");
            checkPage(pageSelected);
            pageSelected = 1;
            dashboardBtn.setBackground(new java.awt.Color(0,51,204));
            dashboardBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,204), 15));
            filterTahun.setVisible(true);
            
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
            tableBarangDiproses();
        }
    }//GEN-LAST:event_dashboardBtnMouseClicked

    private void usersBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersBtnMouseClicked
        if(pageSelected != 2) {
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
            showDataUsers();
        }
    }//GEN-LAST:event_usersBtnMouseClicked

    private void productBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productBtnMouseClicked
        if(pageSelected != 3) {
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
            showDataProducts();
            showAllComboBox();
        }
    }//GEN-LAST:event_productBtnMouseClicked

    private void adminBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adminBtnMouseClicked
        if(pageSelected != 4) {
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
            rs.close();
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
                titleForm.setText("Insert Data");
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
                titleForm1.setText("Insert Data");
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
                
                headerInformation();
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();
                showAllComboBox();
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
                
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();
                showAllComboBox();
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
        editBtn.setVisible(true);
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
                    sql = "INSERT INTO admin(username, password) VALUES(?, ?)";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, usernameField.getText());
                    ps.setString(2, passHashed);
                    ps.executeUpdate();
                    
                    usernameField.setText("");
                    passwordField.setText("");
                    contentPanel.removeAll();
                    contentPanel.revalidate();
                    contentPanel.repaint();
                    contentPanel.setLayout(new GridLayout(1, 0));
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
                    contentPanel.removeAll();
                    contentPanel.revalidate();
                    contentPanel.repaint();
                    contentPanel.setLayout(new GridLayout(1, 0));
                    showDataAdmin();
                    
                    JOptionPane.showMessageDialog(null, "Berhasil mengubah data admin");
                }
            } catch(SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    private void submitAdminFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitAdminFormActionPerformed
        if(editFormMode.equals("insert")) {
            insertAdmin();
        } else if(editFormMode.equals("update")) {
            updateAdmin(dataClicked);
        }
    }//GEN-LAST:event_submitAdminFormActionPerformed

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
                    contentPanel.removeAll();
                    contentPanel.revalidate();
                    contentPanel.repaint();
                    contentPanel.setLayout(new GridLayout(1, 0));
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
                    contentPanel.removeAll();
                    contentPanel.revalidate();
                    contentPanel.repaint();
                    contentPanel.setLayout(new GridLayout(1, 0));
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
                    contentPanel.removeAll();
                    contentPanel.revalidate();
                    contentPanel.repaint();
                    contentPanel.setLayout(new GridLayout(1, 0));
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
                    contentPanel.removeAll();
                    contentPanel.revalidate();
                    contentPanel.repaint();
                    contentPanel.setLayout(new GridLayout(1, 0));
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
            showDataApparel();
        }
    }//GEN-LAST:event_apparelBtnMouseClicked

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        listId.clear();
        deleteBtn.setText("DELETE ALL");
        showAll.setVisible(true);
        switch(pageSelected) {
            case 2 -> {
                searchUser();
            }
            case 3 -> {
                if(editForm.isShowing()) {
                    editForm.setVisible(false);
                    editBtn.setVisible(false);
                }
                searchProduct();
            }
            case 4 -> {
                if(adminForm.isShowing()) {
                    editForm.setVisible(false);
                    editBtn.setVisible(false);
                }
                searchAdmin();
            }
            case 5 -> {
                if(apparelModelP.isShowing()) {
                    editForm.setVisible(false);
                    editBtn.setVisible(false);
                }
                searchApparel();
            }
            case 6 -> {
                if(apparelModelP.isShowing()) {
                    editForm.setVisible(false);
                    editBtn.setVisible(false);
                }
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
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();
                contentPanel.setLayout(new GridLayout(1, 0));
                showDataUsers();
            }
            case 3 -> {
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();
                contentPanel.setLayout(new GridLayout(1, 0));
                showDataProducts();
            }
            case 4 -> {
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();
                contentPanel.setLayout(new GridLayout(1, 0));
                showDataAdmin();
            }
            case 5 -> {
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();
                contentPanel.setLayout(new GridLayout(1, 0));
                showDataApparel();
            }
            case 6 -> {
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();
                contentPanel.setLayout(new GridLayout(1, 0));
                showDataModel();
            }
            default -> {
            }
        }
    }//GEN-LAST:event_showAllActionPerformed

    private void searchUser() {
        try {
            sql = "SELECT u.id as id, u.username as user, p.name AS produk, mo.order_date as orderdate "
                    + "FROM users u JOIN myorder mo ON u.username = mo.username "
                    + "JOIN products p ON mo.id_product = p.id_product "
                    + "WHERE mo.order_date = ( SELECT MAX(order_date) FROM myorder WHERE username LIKE ? )";
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + searchField.getText() + "%");
            DefaultTableModel tbModel = (DefaultTableModel) tb.getModel();
            tbModel.setRowCount(0);
            rs = ps.executeQuery();
            while(rs.next()) {
                tbModel.addRow(new Object[] {
                    "", rs.getString(1), rs.getString(2), rs.getString(3)
                });
            }
            tb.setModel(tbModel);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void searchProduct() {
        try {
            sql = "SELECT * FROM products WHERE name LIKE ? OR apparel LIKE ? OR model LIKE ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + searchField.getText() + "%");
            ps.setString(2, "%" + searchField.getText() + "%");
            ps.setString(3, "%" + searchField.getText() + "%");
            DefaultTableModel tbModel = (DefaultTableModel) tb.getModel();
            tbModel.setRowCount(0);
            rs = ps.executeQuery();
            while(rs.next()) {
                tbModel.addRow(new Object[] {
                    "", rs.getString(1), rs.getString(2), 
                    rs.getString(3), rs.getString(4), rs.getString(5),
                    rs.getString(6), rs.getString(8)
                });
            }
            tb.setModel(tbModel);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void searchAdmin() {
        try {
            sql = "SELECT * FROM admin WHERE username LIKE ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + searchField.getText() + "%");
            DefaultTableModel tbModel = (DefaultTableModel) tb.getModel();
            tbModel.setRowCount(0);
            rs = ps.executeQuery();
            while(rs.next()) {
                tbModel.addRow(new Object[] {
                    "", rs.getString(1), rs.getString(2)
                });
            }
            tb.setModel(tbModel);
            tb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = tb.getSelectedRow();
                    String check = tb.getModel().getValueAt(row, 0).toString();
                    String rowData = tb.getModel().getValueAt(row, 2).toString();

                    if(check.equals("")) {
                        tb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        tb.setValueAt("", row, 0);
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
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void searchApparel() {
        try {
            sql = "SELECT a.id_apparel AS id, a.name AS name, (SELECT COUNT(p.apparel) FROM products p WHERE p.apparel = a.name) AS jumlah FROM apparel a WHERE a.name LIKE ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + searchField.getText() + "%");
            DefaultTableModel tbModel = (DefaultTableModel) tb.getModel();
            tbModel.setRowCount(0);
            rs = ps.executeQuery();
            while(rs.next()) {
                tbModel.addRow(new Object[] {
                    "", rs.getString(1), rs.getString(2), 
                    rs.getString(3)
                });
            }
            tb.setModel(tbModel);
            tb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = tb.getSelectedRow();
                    String check = tb.getModel().getValueAt(row, 0).toString();
                    String rowData = tb.getModel().getValueAt(row, 1).toString();

                    if(check.equals("")) {
                        tb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        tb.setValueAt("", row, 0);
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
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void searchModel() {
        try {
            sql = "SELECT m.id_model AS id, m.name AS name, (SELECT COUNT(p.model) FROM products p WHERE p.model = m.name) AS jumlah FROM model m WHERE m.name LIKE ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + searchField.getText() + "%");
            DefaultTableModel tbModel = (DefaultTableModel) tb.getModel();
            tbModel.setRowCount(0);
            rs = ps.executeQuery();
            while(rs.next()) {
                tbModel.addRow(new Object[] {
                    "", rs.getString(1), rs.getString(2), 
                    rs.getString(3)
                });
            }
            tb.setModel(tbModel);
            tb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = tb.getSelectedRow();
                    String check = tb.getModel().getValueAt(row, 0).toString();
                    String rowData = tb.getModel().getValueAt(row, 1).toString();

                    if(check.equals("")) {
                        tb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        tb.setValueAt("", row, 0);
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
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
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
        this.setTitle("Data Apparel");
        searchField.setVisible(true);
        searchBtn.setVisible(true);
        insertBtn.setVisible(true);
        editBtn.setVisible(false);
        deleteBtn.setText("DELETE ALL");
        listId.clear();
        showAll.setVisible(false);
        
        JPanel tableBox = new JPanel();
        tableBox.setBackground(Color.white);
        tableBox.setLayout(new GridLayout(1, 0));
        tb = new JTable();
        DefaultTableModel table = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.addColumn("#");
        table.addColumn("Id");
        table.addColumn("Nama");
        table.addColumn("Jumlah Produk");
        try {
            sql = "SELECT a.id_apparel AS id, a.name AS name, (SELECT COUNT(p.apparel) FROM products p WHERE p.apparel = a.name) AS jumlah FROM apparel a";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                table.addRow(new Object[] {
                    "", rs.getString(1), rs.getString(2), rs.getString(3)
                });
            }
            tb.setModel(table);
            tb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = tb.getSelectedRow();
                    String check = tb.getModel().getValueAt(row, 0).toString();
                    String rowData = tb.getModel().getValueAt(row, 1).toString();

                    if(check.equals("")) {
                        tb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        tb.setValueAt("", row, 0);
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
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        JScrollPane scrollPane = new JScrollPane(tb);
        tableBox.add(scrollPane);
        contentPanel.add(tableBox);
    }
    
    private void showDataModel() {
        this.setTitle("Data Model");
        searchField.setVisible(true);
        searchBtn.setVisible(true);
        insertBtn.setVisible(true);
        editBtn.setVisible(false);
        deleteBtn.setText("DELETE ALL");
        listId.clear();
        showAll.setVisible(false);
        
        JPanel tableBox = new JPanel();
        tableBox.setBackground(Color.white);
        tableBox.setLayout(new GridLayout(1, 0));
        tb = new JTable();
        DefaultTableModel table = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.addColumn("#");
        table.addColumn("Id");
        table.addColumn("Nama");
        table.addColumn("Jumlah Produk");
        try {
            sql = "SELECT m.id_model AS id, m.name AS name, (SELECT COUNT(p.model) FROM products p WHERE p.model = m.name) AS jumlah FROM model m";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                table.addRow(new Object[] {
                    "", rs.getString(1), rs.getString(2), rs.getString(3)
                });
            }
            tb.setModel(table);
            tb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = tb.getSelectedRow();
                    String check = tb.getModel().getValueAt(row, 0).toString();
                    String rowData = tb.getModel().getValueAt(row, 1).toString();

                    if(check.equals("")) {
                        tb.setValueAt("Dipilih", row, 0);
                        listId.add(rowData);
                        deleteBtn.setText("DELETE (" + listId.size() + ")");
                    } else {
                        tb.setValueAt("", row, 0);
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
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        JScrollPane scrollPane = new JScrollPane(tb);
        tableBox.add(scrollPane);
        contentPanel.add(tableBox);
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
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel logoutBtn;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel modelBtn;
    private javax.swing.JComboBox<String> modelField;
    private javax.swing.JTextField nameField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel productBtn;
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

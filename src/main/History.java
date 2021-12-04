/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author Novandi Ramadhan
 */
public class History extends javax.swing.JFrame {
    Connection con;
    Statement stat;
    PreparedStatement ps;
    ResultSet rs;
    String sql, userLogin;
    List<String> listToDelete = new ArrayList<>();
    List<String> estId = new ArrayList<>();
    
    /**
     * Creates new form History
     */
    public History() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("../assets/jordan.png")).getImage());
        this.setTitle("Riwayat Pembelian");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        Config DB = new Config();
        DB.connect();
        con = DB.conn;
        stat = DB.stm;
    }
    
    public void loadHistory(String username) {
        userLogin = username;
        DefaultTableModel tb = new DefaultTableModel();
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        tb.addColumn("#");
        tb.addColumn("No");
        tb.addColumn("ID Order");
        tb.addColumn("Nama Produk");
        tb.addColumn("Jumlah");
        tb.addColumn("Ukuran");
        tb.addColumn("Deskripsi");
        tb.addColumn("Total Harga");
        tb.addColumn("Tanggal Order");
        tb.addColumn("Tanggal Estimasi");
        tb.addColumn("Status");

        try {
            sql = "SELECT mo.id_order AS MId, "
                    + "p.name AS PName, "
                    + "mo.quantity AS MQuantity, "
                    + "mo.size AS MSize, "
                    + "mo.desc AS MDesc, "
                    + "mo.price AS MPrice, "
                    + "mo.order_date AS MODate, "
                    + "mo.estimated_date AS MEDate, "
                    + "mo.status AS MStatus "
                    + "FROM myorder mo "
                    + "JOIN products p "
                    + "ON mo.id_product = p.id_product AND mo.username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            
            String checkStatus = "UPDATE myorder SET status = (CASE";
            int no = 1;
            boolean estimated = false;
            while(rs.next()) {
                if(dateNow.equals(rs.getString(8))) {
                    estId.add(rs.getString(1));
                    checkStatus += " WHEN estimated_date = '" + rs.getString(8) + "' THEN 'Berhasil dikirim'";
                    estimated = true;
                }
                
                tb.addRow(new Object[] {
                    "", no++, rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4),
                    rs.getString(5), "Rp. " + currencyID(Integer.parseInt(rs.getString(6))),
                    rs.getString(7), rs.getString(8),
                    (dateNow.equals(rs.getString(8)) ? "Berhasil dikirim" : rs.getString(9))
                });
            }
            
            tbHistory.setModel(tb);
            
            if(estimated == true) {
                checkStatus += " END) WHERE username = '" + username + "' AND id_order IN (" + String.join(",", estId) + ")";
                stat = con.createStatement();
                stat.executeUpdate(checkStatus);
            }
            
            ps.close();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public static String currencyID(int nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getNumberInstance(localeID);
        return formatRupiah.format((int) nominal);
    }
    
    public void exportToExcel() throws FileNotFoundException, IOException{
        try {
            stat = con.createStatement();
            FileOutputStream fileOut;
            
            // Hasil Export
            String tgl = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            fileOut = new FileOutputStream("D:/" + userLogin + "-" + tgl + ".xls");
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet("Sheet 0");
            
            // Nama Field
            Row row1 = worksheet.createRow((short) 0);
            row1.createCell(0).setCellValue("No");
            row1.createCell(1).setCellValue("ID Order");
            row1.createCell(2).setCellValue("Nama Produk");
            row1.createCell(3).setCellValue("Jumlah");
            row1.createCell(4).setCellValue("Ukuran");
            row1.createCell(5).setCellValue("Deskripsi");
            row1.createCell(6).setCellValue("Total Harga");
            row1.createCell(7).setCellValue("Tanggal Order");
            row1.createCell(8).setCellValue("Tanggal Estimasi");
            row1.createCell(9).setCellValue("Status");
            
            Row row2;
            sql = "SELECT mo.id_order AS MId, "
                    + "p.name AS PName, "
                    + "mo.quantity AS MQuantity, "
                    + "mo.size AS MSize, "
                    + "mo.desc AS MDesc, "
                    + "mo.price AS MPrice, "
                    + "mo.order_date AS MODate, "
                    + "mo.estimated_date AS MEDate, "
                    + "mo.status AS MStatus "
                    + "FROM myorder mo "
                    + "JOIN products p "
                    + "ON mo.id_product = p.id_product AND mo.username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, userLogin);
            rs = ps.executeQuery();
            int no = 1;
            while(rs.next()) {
                int a = rs.getRow();
                row2 = worksheet.createRow((short) a);
                // Sesuaikan dengan Jumlah Field
                row2.createCell(0).setCellValue(no++);
                row2.createCell(1).setCellValue(rs.getString(1));
                row2.createCell(2).setCellValue(rs.getString(2));
                row2.createCell(3).setCellValue(rs.getString(3));
                row2.createCell(4).setCellValue(rs.getString(4));
                row2.createCell(5).setCellValue(rs.getString(5));
                row2.createCell(6).setCellValue(rs.getString(6));
                row2.createCell(7).setCellValue(rs.getString(7));
                row2.createCell(8).setCellValue(rs.getString(8));
                row2.createCell(9).setCellValue(rs.getString(9));
            }
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
            rs.close();
            ps.close();
            JOptionPane.showMessageDialog(this, "Berhasil export!, hasil export berada di D:/");
        } catch(SQLException | IOException ex) {
            System.out.println(ex);
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
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        exportExcel = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbHistory = new javax.swing.JTable();
        alertBox = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        headerPanel.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\Nike\\src\\assets\\logo-white.png")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Google Sans", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Riwayat Pembelian");

        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(jLabel1)
                .addGap(148, 148, 148)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addGap(178, 178, 178)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(96, 96, 96))
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
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jButton1))))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        searchField.setBackground(new java.awt.Color(255, 255, 255));
        searchField.setForeground(new java.awt.Color(0, 0, 0));
        searchField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        searchBtn.setBackground(new java.awt.Color(0, 0, 0));
        searchBtn.setForeground(new java.awt.Color(255, 255, 255));
        searchBtn.setText("CARI");
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        deleteBtn.setBackground(new java.awt.Color(255, 0, 51));
        deleteBtn.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        deleteBtn.setForeground(new java.awt.Color(255, 255, 255));
        deleteBtn.setText("HAPUS SEMUA");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        exportExcel.setBackground(new java.awt.Color(0, 153, 51));
        exportExcel.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        exportExcel.setForeground(new java.awt.Color(255, 255, 255));
        exportExcel.setText("EXPORT TO EXCEL");
        exportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportExcelActionPerformed(evt);
            }
        });

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setForeground(new java.awt.Color(0, 0, 0));

        tbHistory.setBackground(new java.awt.Color(255, 255, 255));
        tbHistory.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        tbHistory.setForeground(new java.awt.Color(0, 0, 0));
        tbHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Order", "Nama Produk", "Quantity", "Size", "Tanggal Order", "Tanggal Estimasi", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbHistoryMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbHistory);

        alertBox.setBackground(new java.awt.Color(255, 255, 153));
        alertBox.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        alertBox.setForeground(new java.awt.Color(0, 0, 0));
        alertBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alertBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 0));
        alertBox.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(alertBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exportExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(34, 34, 34))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alertBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                .addGap(31, 31, 31))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        exportExcel.setVisible(true);
        searchField.setText("");
        alertBox.setText("");
        alertBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 153), 0));
        loadHistory(userLogin);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tbHistoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHistoryMouseClicked
        int row = tbHistory.getSelectedRow();
        String selectedData = tbHistory.getModel().getValueAt(row, 2).toString();
        String checkSelected = tbHistory.getModel().getValueAt(row, 0).toString();
        String checkStatus = tbHistory.getModel().getValueAt(row, 10).toString();
        
        if(checkStatus.equals("Sedang diproses")) {
            JOptionPane.showMessageDialog(null, "Data order ini tidak dapat dipilih, karena produk sedang diproses");
        } else {
            if(checkSelected.equals("")) {
                tbHistory.setValueAt("Dipilih", row, 0);
                listToDelete.add(selectedData);
            } else {
                tbHistory.setValueAt("", row, 0);
                listToDelete.remove(selectedData);
            }
        }
        
        if(listToDelete.size() > 0) {
            deleteBtn.setText("Hapus (" + listToDelete.size() + ")");
        } else {
            deleteBtn.setText("Hapus Semua");
        }
    }//GEN-LAST:event_tbHistoryMouseClicked

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        DefaultTableModel tb = new DefaultTableModel();
        tb.addColumn("No");
        tb.addColumn("ID Order");
        tb.addColumn("Nama Produk");
        tb.addColumn("Jumlah");
        tb.addColumn("Ukuran");
        tb.addColumn("Deskripsi");
        tb.addColumn("Total Harga");
        tb.addColumn("Tanggal Order");
        tb.addColumn("Tanggal Estimasi");
        tb.addColumn("Status");
        
        exportExcel.setVisible(false);
        
        try {
            sql = "SELECT mo.id_order AS MId, p.name AS PName, mo.quantity AS MQuantity, mo.size AS MSize, mo.desc AS MDesc, mo.price AS MPrice, mo.order_date AS MODate, mo.estimated_date AS MEDate, mo.status AS MStatus "
                    + "FROM myorder mo JOIN products p ON mo.id_product = p.id_product AND mo.username = ? WHERE p.name LIKE ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, userLogin);
            ps.setString(2, '%' + searchField.getText() + '%');
            rs = ps.executeQuery();
            
            alertBox.setText("Hasil pencarian untuk : " + searchField.getText());
            alertBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 153), 10));
            
            int no = 1;
            while(rs.next()) {
                tb.addRow(new Object[] {
                    no++, rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4),
                    rs.getString(5), "Rp. " + currencyID(Integer.parseInt(rs.getString(6))),
                    rs.getString(7), rs.getString(8),
                    rs.getString(9)
                });
            }
            
            tbHistory.setModel(tb);
            ps.close();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_searchBtnActionPerformed

    private void exportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportExcelActionPerformed
        try {
            exportToExcel();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_exportExcelActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        try {
            sql = "DELETE FROM myorder" + (listToDelete.size() > 0 ? " WHERE id_order IN (" + String.join(",", listToDelete) + ")" : " WHERE status != 'Sedang diproses' AND username = '" + userLogin + "'");
            stat = con.createStatement();
            stat.executeUpdate(sql);
            JOptionPane.showMessageDialog(null, "Data Riwayat Pesanan berhasil dihapus");
            loadHistory(userLogin);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Tidak dapat menghapus, coba lagi nanti");
//            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_deleteBtnActionPerformed

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
            java.util.logging.Logger.getLogger(History.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(History.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(History.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(History.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new History().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel alertBox;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JButton exportExcel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchField;
    private javax.swing.JTable tbHistory;
    // End of variables declaration//GEN-END:variables
}

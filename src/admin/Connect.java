/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class Connect {
    public Connection conn;
    public Statement stm;
    
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/dbnike", "root", "");
            stm = conn.createStatement();
        } catch(ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Koneksi gagal " + e.getMessage());
        }
    }
}

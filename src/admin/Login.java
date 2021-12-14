/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package admin;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Novandi Ramadhan
 */
public class Login extends javax.swing.JFrame {
    int xx = 0;
    int yy = 0;
    Connection con;
    Statement stat;
    ResultSet rs;
    String sql;
    /**
     * Creates new form Login
     */
    public Login() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("../assets/jordan.png")).getImage());
        checkUsername();
        checkPassword();
        
        Connect DB = new Connect();
        DB.connect();
        con = DB.conn;
        stat = DB.stm;
    }
    
    private void checkUsername() {
        username.getDocument().addDocumentListener(new DocumentListener() {
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
                if(username.getText().equals("")) {
                     username.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,153,51), 2));
                     alertUsername.setText("Username harus diisi");
                 } else {
                     username.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240,240,240), 2));
                     alertUsername.setText("");
                 }
             }
        });
    }
    
    private void checkPassword() {
        password.getDocument().addDocumentListener(new DocumentListener() {
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
                if(String.valueOf(password.getPassword()).equals("")) {
                     password.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,153,51), 2));
                     alertPassword.setText("Password harus diisi");
                 } else {
                     password.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240,240,240), 2));
                     alertPassword.setText("");
                 }
             }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LoginForm = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        title = new javax.swing.JLabel();
        closeWindow = new javax.swing.JLabel();
        minimize = new javax.swing.JLabel();
        subtitle = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        submit = new javax.swing.JButton();
        alertUsername = new javax.swing.JLabel();
        alertPassword = new javax.swing.JLabel();
        alertCheck = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(390, 540));
        setUndecorated(true);
        setResizable(false);

        LoginForm.setBackground(new java.awt.Color(0, 51, 102));
        LoginForm.setForeground(new java.awt.Color(0, 0, 0));
        LoginForm.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                LoginFormMouseDragged(evt);
            }
        });
        LoginForm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                LoginFormMousePressed(evt);
            }
        });

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\jordan-45-white.png")); // NOI18N

        title.setFont(new java.awt.Font("Google Sans", 1, 24)); // NOI18N
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("ADMIN");

        closeWindow.setBackground(new java.awt.Color(0, 102, 204));
        closeWindow.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        closeWindow.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\cancel.png")); // NOI18N
        closeWindow.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 204), 10));
        closeWindow.setOpaque(true);
        closeWindow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeWindowMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeWindowMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeWindowMouseExited(evt);
            }
        });

        minimize.setBackground(new java.awt.Color(0, 102, 204));
        minimize.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minimize.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\NikeShop\\src\\assets\\minus.png")); // NOI18N
        minimize.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 204), 10));
        minimize.setOpaque(true);
        minimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minimizeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                minimizeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                minimizeMouseExited(evt);
            }
        });

        subtitle.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        subtitle.setForeground(new java.awt.Color(255, 255, 255));
        subtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        subtitle.setText("Log in to your account as admin");

        username.setBackground(new java.awt.Color(240, 240, 240));
        username.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        username.setForeground(new java.awt.Color(0, 0, 0));
        username.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        username.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240), 2));

        usernameLabel.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        usernameLabel.setForeground(new java.awt.Color(255, 255, 255));
        usernameLabel.setText("Username");

        passwordLabel.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        passwordLabel.setForeground(new java.awt.Color(255, 255, 255));
        passwordLabel.setText("Password");

        password.setBackground(new java.awt.Color(240, 240, 240));
        password.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        password.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        password.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240), 2));

        submit.setBackground(new java.awt.Color(0, 102, 204));
        submit.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        submit.setForeground(new java.awt.Color(255, 255, 255));
        submit.setText("Sign In");
        submit.setBorder(null);
        submit.setBorderPainted(false);
        submit.setContentAreaFilled(false);
        submit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        submit.setOpaque(true);
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitActionPerformed(evt);
            }
        });

        alertUsername.setFont(new java.awt.Font("Google Sans", 0, 10)); // NOI18N
        alertUsername.setForeground(new java.awt.Color(255, 153, 51));

        alertPassword.setFont(new java.awt.Font("Google Sans", 0, 10)); // NOI18N
        alertPassword.setForeground(new java.awt.Color(255, 153, 51));

        alertCheck.setBackground(new java.awt.Color(255, 153, 51));
        alertCheck.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        alertCheck.setForeground(new java.awt.Color(255, 255, 255));
        alertCheck.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alertCheck.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51), 0));
        alertCheck.setOpaque(true);

        javax.swing.GroupLayout LoginFormLayout = new javax.swing.GroupLayout(LoginForm);
        LoginForm.setLayout(LoginFormLayout);
        LoginFormLayout.setHorizontalGroup(
            LoginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginFormLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(minimize, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(closeWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(LoginFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(LoginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(LoginFormLayout.createSequentialGroup()
                .addGap(176, 176, 176)
                .addComponent(logo)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(LoginFormLayout.createSequentialGroup()
                .addGroup(LoginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LoginFormLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(LoginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(alertCheck, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                            .addComponent(usernameLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passwordLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(username, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(password, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(alertUsername, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(alertPassword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)))
                    .addGroup(LoginFormLayout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(submit, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LoginFormLayout.setVerticalGroup(
            LoginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginFormLayout.createSequentialGroup()
                .addGroup(LoginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minimize)
                    .addComponent(closeWindow))
                .addGap(18, 18, 18)
                .addComponent(logo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoginFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LoginFormLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(subtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(title))
                .addGap(18, 18, 18)
                .addComponent(alertCheck)
                .addGap(18, 18, 18)
                .addComponent(usernameLabel)
                .addGap(4, 4, 4)
                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alertUsername)
                .addGap(8, 8, 8)
                .addComponent(passwordLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alertPassword)
                .addGap(55, 55, 55)
                .addComponent(submit, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(99, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LoginForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LoginForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void closeWindowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeWindowMouseClicked
        System.exit(0);
    }//GEN-LAST:event_closeWindowMouseClicked

    private void closeWindowMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeWindowMouseEntered
        closeWindow.setBackground(new java.awt.Color(0,51,153));
        closeWindow.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,153), 10));
    }//GEN-LAST:event_closeWindowMouseEntered

    private void closeWindowMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeWindowMouseExited
        closeWindow.setBackground(new java.awt.Color(0,102,204));
        closeWindow.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,102,204), 10));
    }//GEN-LAST:event_closeWindowMouseExited

    private void minimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeMouseClicked
        this.setState(1);
    }//GEN-LAST:event_minimizeMouseClicked

    private void minimizeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeMouseEntered
        minimize.setBackground(new java.awt.Color(0,51,153));
        minimize.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,51,153), 10));
    }//GEN-LAST:event_minimizeMouseEntered

    private void minimizeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeMouseExited
        minimize.setBackground(new java.awt.Color(0,102,204));
        minimize.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,102,204), 10));
    }//GEN-LAST:event_minimizeMouseExited

    private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);

        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }
    
    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i < bytes.length ;i++) {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
    
    private void submitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitActionPerformed
        if(!username.getText().equals("") && !String.valueOf(password.getPassword()).equals("")) {
            alertUsername.setText("");
            alertPassword.setText("");
            password.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240), 2));
            username.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240), 2));
            
            try {
                sql = "SELECT * FROM admin WHERE username='" + username.getText() + "'";
                rs = stat.executeQuery(sql);
                if(rs.next()) {
                    boolean passMatched = validatePassword(String.valueOf(password.getPassword()), rs.getString("password"));
                    
                    if(passMatched) {
                        this.setVisible(false);
                        Dashboard dbd = new Dashboard();
                        dbd.userSession(username.getText());
                        dbd.setVisible(true);
                    } else {
                        alertCheck.setText("Username atau password salah");
                        alertCheck.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,153,51), 10));
                    }
                } else {
                    alertCheck.setText("Username tidak ditemukan");
                    alertCheck.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,153,51), 10));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                //
            }
        } else {
            if(username.getText().equals("")) {
                alertUsername.setText("Username harus diisi");
                username.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,153,51), 2));
            } else {
                alertUsername.setText("");
                username.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240), 2));
            }

            if(String.valueOf(password.getPassword()).equals("")) {
                alertPassword.setText("Password harus diisi");
                password.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255,153,51), 2));
            } else {
                alertPassword.setText("");
                password.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240), 2));
            }
        }
    }//GEN-LAST:event_submitActionPerformed

    private void LoginFormMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoginFormMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x-xx, y-yy);
    }//GEN-LAST:event_LoginFormMouseDragged

    private void LoginFormMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoginFormMousePressed
        xx = evt.getX();
        yy = evt.getY();
    }//GEN-LAST:event_LoginFormMousePressed

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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel LoginForm;
    private javax.swing.JLabel alertCheck;
    private javax.swing.JLabel alertPassword;
    private javax.swing.JLabel alertUsername;
    private javax.swing.JLabel closeWindow;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel minimize;
    private javax.swing.JPasswordField password;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JButton submit;
    private javax.swing.JLabel subtitle;
    private javax.swing.JLabel title;
    private javax.swing.JTextField username;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}

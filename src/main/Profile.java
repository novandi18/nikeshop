/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Novandi Ramadhan
 */
public class Profile extends javax.swing.JFrame {
    Connection con;
    Statement stat;
    PreparedStatement ps;
    ResultSet rs;
    String sql, usernameSession, backupName, backupEmail, backupPN, backupAlamat;
    private static final String email_pattern = 
    "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    /**
     * Creates new form Profile
     */
    public Profile() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setIconImage(new ImageIcon(getClass().getResource("../assets/jordan.png")).getImage());
        this.setTitle("Profile");
        
        Config DB = new Config();
        DB.connect();
        con = DB.conn;
        stat = DB.stm;
        
        submitName.setVisible(false);
        submitEmail.setVisible(false);
        submitPN.setVisible(false);
        submitAlamat.setVisible(false);
        
        cancelName.setVisible(false);
        cancelEmail.setVisible(false);
        cancelPN.setVisible(false);
        cancelAlamat.setVisible(false);
    }
    
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
    
    public static String currencyID(int nominal) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getNumberInstance(localeID);
        return formatRupiah.format((int) nominal);
    }
    
    public void getUsers(String username) {
        usernameSession = username;
        
        try {
            String cekOvo = "SELECT no_ovo FROM users WHERE username = ?";
            ps = con.prepareStatement(cekOvo);
            ps.setString(1, username);
            rs = ps.executeQuery();
            boolean ada = false;
            while(rs.next()) {
                ada = !rs.getString("no_ovo").equals("");
            }
            
            sql = "SELECT "
                    + "u.fullname AS FullName, "
                    + "u.username AS Username, "
                    + "u.email AS Email, "
                    + "u.no_telp AS NoTelp, "
                    + "u.address AS Address, "
                    + "u.no_ovo AS noOvo, "
                    + "u.saldo AS Saldo"
                    + (ada == true ? ", o.saldo_ovo AS OvoSaldo" : "")
                    + " FROM users u "
                    + (ada == true ? "JOIN ovo o ON o.no_ovo = u.no_ovo AND u.username = ?" : "WHERE u.username = ?");
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            
            while(rs.next()) {
                fullNameF.setText(rs.getString("FullName"));
                usernameF.setText(rs.getString("Username"));
                emailF.setText(rs.getString("Email"));
                pnF.setText(rs.getString("NoTelp"));
                alamatF.setText(rs.getString("Address"));
                String saldoRp = currencyID(Integer.parseInt(rs.getString("Saldo")));
                saldoF.setText("Rp. " + saldoRp);
                
                if(rs.getString("noOvo").equals("")) {
                    noOvoF.setText("Nomor OVO belum disambungkan");
                    saldoOvoF.setText("");
                    deleteOvo.setVisible(false);
                } else {
                    String ovoRp = currencyID(Integer.parseInt(rs.getString("ovoSaldo")));
                    noOvoF.setText(rs.getString("noOvo"));
                    saldoOvoF.setText("Rp. " + ovoRp);
                }
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
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        currentPass = new javax.swing.JPasswordField();
        newPass = new javax.swing.JPasswordField();
        confirmPass = new javax.swing.JPasswordField();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        fullNameF = new javax.swing.JTextField();
        changeName = new javax.swing.JButton();
        submitName = new javax.swing.JButton();
        cancelName = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        usernameF = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        changeEmail = new javax.swing.JButton();
        emailF = new javax.swing.JTextField();
        submitEmail = new javax.swing.JButton();
        cancelEmail = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        changePN = new javax.swing.JButton();
        pnF = new javax.swing.JTextField();
        submitPN = new javax.swing.JButton();
        cancelPN = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        changeAlamat = new javax.swing.JButton();
        submitAlamat = new javax.swing.JButton();
        cancelAlamat = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        alamatF = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        changeAlamat1 = new javax.swing.JButton();
        saldoF = new javax.swing.JTextField();
        noOvoF = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        changeOvo = new javax.swing.JButton();
        saldoOvoF = new javax.swing.JTextField();
        deleteOvo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));

        headerPanel.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setIcon(new javax.swing.ImageIcon("D:\\Project\\Java\\GUI\\Nike\\src\\assets\\logo-white.png")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Google Sans", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Profile");

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Refresh");
        jButton2.setContentAreaFilled(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setOpaque(true);

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addComponent(jLabel1)
                .addGap(150, 150, 150)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addGap(184, 184, 184)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(115, 115, 115))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel5.setFont(new java.awt.Font("Google Sans", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Ganti Password");

        jLabel9.setBackground(new java.awt.Color(51, 51, 51));
        jLabel9.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Password Sekarang");

        jLabel10.setBackground(new java.awt.Color(51, 51, 51));
        jLabel10.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("Password Baru");

        jLabel11.setBackground(new java.awt.Color(51, 51, 51));
        jLabel11.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));
        jLabel11.setText("Konfirmasi Password Baru");

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Submit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        currentPass.setBackground(new java.awt.Color(204, 204, 204));
        currentPass.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        currentPass.setForeground(new java.awt.Color(0, 0, 0));
        currentPass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        newPass.setBackground(new java.awt.Color(204, 204, 204));
        newPass.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        newPass.setForeground(new java.awt.Color(0, 0, 0));
        newPass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        confirmPass.setBackground(new java.awt.Color(204, 204, 204));
        confirmPass.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        confirmPass.setForeground(new java.awt.Color(0, 0, 0));
        confirmPass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(152, 152, 152)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(confirmPass, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(newPass, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9)
                    .addComponent(jLabel5)
                    .addComponent(currentPass, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentPass, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newPass, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confirmPass, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(242, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText("Nama Lengkap");

        fullNameF.setEditable(false);
        fullNameF.setBackground(new java.awt.Color(255, 255, 255));
        fullNameF.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        fullNameF.setForeground(new java.awt.Color(0, 0, 0));
        fullNameF.setText("Nama");
        fullNameF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        fullNameF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullNameFActionPerformed(evt);
            }
        });

        changeName.setBackground(new java.awt.Color(0, 204, 51));
        changeName.setForeground(new java.awt.Color(255, 255, 255));
        changeName.setText("Change");
        changeName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51), 2));
        changeName.setContentAreaFilled(false);
        changeName.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        changeName.setOpaque(true);
        changeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeNameActionPerformed(evt);
            }
        });

        submitName.setBackground(new java.awt.Color(0, 0, 0));
        submitName.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        submitName.setForeground(new java.awt.Color(255, 255, 255));
        submitName.setText("Submit");
        submitName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitNameActionPerformed(evt);
            }
        });

        cancelName.setBackground(new java.awt.Color(204, 0, 0));
        cancelName.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        cancelName.setForeground(new java.awt.Color(255, 255, 255));
        cancelName.setText("Cancel");
        cancelName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNameActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("Username");

        usernameF.setEditable(false);
        usernameF.setBackground(new java.awt.Color(255, 255, 255));
        usernameF.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        usernameF.setForeground(new java.awt.Color(0, 0, 0));
        usernameF.setText("Username");
        usernameF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        jLabel6.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setText("Email");

        changeEmail.setBackground(new java.awt.Color(0, 204, 51));
        changeEmail.setForeground(new java.awt.Color(255, 255, 255));
        changeEmail.setText("Change");
        changeEmail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51), 2));
        changeEmail.setContentAreaFilled(false);
        changeEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        changeEmail.setOpaque(true);
        changeEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeEmailActionPerformed(evt);
            }
        });

        emailF.setEditable(false);
        emailF.setBackground(new java.awt.Color(255, 255, 255));
        emailF.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        emailF.setForeground(new java.awt.Color(0, 0, 0));
        emailF.setText("Email");
        emailF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        submitEmail.setBackground(new java.awt.Color(0, 0, 0));
        submitEmail.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        submitEmail.setForeground(new java.awt.Color(255, 255, 255));
        submitEmail.setText("Submit");
        submitEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitEmailActionPerformed(evt);
            }
        });

        cancelEmail.setBackground(new java.awt.Color(204, 0, 0));
        cancelEmail.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        cancelEmail.setForeground(new java.awt.Color(255, 255, 255));
        cancelEmail.setText("Cancel");
        cancelEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelEmailActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("Nomor Telepon");

        changePN.setBackground(new java.awt.Color(0, 204, 51));
        changePN.setForeground(new java.awt.Color(255, 255, 255));
        changePN.setText("Change");
        changePN.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51), 2));
        changePN.setContentAreaFilled(false);
        changePN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        changePN.setOpaque(true);
        changePN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePNActionPerformed(evt);
            }
        });

        pnF.setEditable(false);
        pnF.setBackground(new java.awt.Color(255, 255, 255));
        pnF.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        pnF.setForeground(new java.awt.Color(0, 0, 0));
        pnF.setText("Nomor Telepon");
        pnF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        submitPN.setBackground(new java.awt.Color(0, 0, 0));
        submitPN.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        submitPN.setForeground(new java.awt.Color(255, 255, 255));
        submitPN.setText("Submit");
        submitPN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitPNActionPerformed(evt);
            }
        });

        cancelPN.setBackground(new java.awt.Color(204, 0, 0));
        cancelPN.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        cancelPN.setForeground(new java.awt.Color(255, 255, 255));
        cancelPN.setText("Cancel");
        cancelPN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelPNActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(102, 102, 102));
        jLabel8.setText("Alamat");

        changeAlamat.setBackground(new java.awt.Color(0, 204, 51));
        changeAlamat.setForeground(new java.awt.Color(255, 255, 255));
        changeAlamat.setText("Change");
        changeAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51), 2));
        changeAlamat.setContentAreaFilled(false);
        changeAlamat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        changeAlamat.setOpaque(true);
        changeAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeAlamatActionPerformed(evt);
            }
        });

        submitAlamat.setBackground(new java.awt.Color(0, 0, 0));
        submitAlamat.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        submitAlamat.setForeground(new java.awt.Color(255, 255, 255));
        submitAlamat.setText("Submit");
        submitAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitAlamatActionPerformed(evt);
            }
        });

        cancelAlamat.setBackground(new java.awt.Color(204, 0, 0));
        cancelAlamat.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        cancelAlamat.setForeground(new java.awt.Color(255, 255, 255));
        cancelAlamat.setText("Cancel");
        cancelAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAlamatActionPerformed(evt);
            }
        });

        alamatF.setEditable(false);
        alamatF.setBackground(new java.awt.Color(204, 204, 204));
        alamatF.setColumns(20);
        alamatF.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        alamatF.setForeground(new java.awt.Color(0, 0, 0));
        alamatF.setLineWrap(true);
        alamatF.setRows(5);
        jScrollPane1.setViewportView(alamatF);

        jLabel12.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(102, 102, 102));
        jLabel12.setText("Saldoku");

        changeAlamat1.setBackground(new java.awt.Color(0, 204, 51));
        changeAlamat1.setForeground(new java.awt.Color(255, 255, 255));
        changeAlamat1.setText("Isi Ulang");
        changeAlamat1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51), 2));
        changeAlamat1.setContentAreaFilled(false);
        changeAlamat1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        changeAlamat1.setOpaque(true);
        changeAlamat1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeAlamat1ActionPerformed(evt);
            }
        });

        saldoF.setEditable(false);
        saldoF.setBackground(new java.awt.Color(255, 255, 255));
        saldoF.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        saldoF.setForeground(new java.awt.Color(0, 0, 0));
        saldoF.setText("10000");
        saldoF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        saldoF.setFocusable(false);

        noOvoF.setEditable(false);
        noOvoF.setBackground(new java.awt.Color(204, 204, 204));
        noOvoF.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        noOvoF.setForeground(new java.awt.Color(0, 0, 0));
        noOvoF.setText("085156066785");
        noOvoF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        noOvoF.setFocusable(false);

        jLabel13.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("OVO");

        changeOvo.setBackground(new java.awt.Color(0, 204, 51));
        changeOvo.setForeground(new java.awt.Color(255, 255, 255));
        changeOvo.setText("Change");
        changeOvo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51), 2));
        changeOvo.setContentAreaFilled(false);
        changeOvo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        changeOvo.setOpaque(true);
        changeOvo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeOvoActionPerformed(evt);
            }
        });

        saldoOvoF.setEditable(false);
        saldoOvoF.setBackground(new java.awt.Color(255, 255, 255));
        saldoOvoF.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        saldoOvoF.setForeground(new java.awt.Color(0, 0, 0));
        saldoOvoF.setText("10000");
        saldoOvoF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        saldoOvoF.setFocusable(false);

        deleteOvo.setBackground(new java.awt.Color(204, 0, 0));
        deleteOvo.setForeground(new java.awt.Color(255, 255, 255));
        deleteOvo.setText("Putuskan");
        deleteOvo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0), 2));
        deleteOvo.setContentAreaFilled(false);
        deleteOvo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteOvo.setOpaque(true);
        deleteOvo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOvoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeAlamat1))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeOvo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteOvo, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(saldoF)
                            .addComponent(noOvoF, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                            .addComponent(saldoOvoF))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(fullNameF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(submitName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelName))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                .addComponent(submitAlamat)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelAlamat))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(changeAlamat))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(changePN))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(pnF)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(submitPN)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cancelPN))
                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(changeName))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                    .addComponent(jLabel6)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(changeEmail))
                                                .addComponent(emailF, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(submitEmail)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cancelEmail))
                                        .addComponent(usernameF, javax.swing.GroupLayout.Alignment.LEADING)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(13, 13, 13))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(changeName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fullNameF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(changeEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(changePN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pnF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitPN, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelPN, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(changeAlamat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(submitAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cancelAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(changeAlamat1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saldoF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(changeOvo)
                    .addComponent(deleteOvo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noOvoF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saldoOvoF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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

    private void changeNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeNameActionPerformed
        backupName = fullNameF.getText();
        fullNameF.setEditable(true);
        submitName.setVisible(true);
        cancelName.setVisible(true);
        changeName.setVisible(false);
        fullNameF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    }//GEN-LAST:event_changeNameActionPerformed

    private void cancelNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNameActionPerformed
        fullNameF.setText(backupName);
        fullNameF.setEditable(false);
        submitName.setVisible(false);
        cancelName.setVisible(false);
        changeName.setVisible(true);
    }//GEN-LAST:event_cancelNameActionPerformed

    private void changeEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeEmailActionPerformed
        backupEmail = emailF.getText();
        emailF.setEditable(true);
        submitEmail.setVisible(true);
        cancelEmail.setVisible(true);
        changeEmail.setVisible(false);
        emailF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    }//GEN-LAST:event_changeEmailActionPerformed

    private void cancelEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelEmailActionPerformed
        emailF.setText(backupEmail);
        emailF.setEditable(false);
        submitEmail.setVisible(false);
        cancelEmail.setVisible(false);
        changeEmail.setVisible(true);
    }//GEN-LAST:event_cancelEmailActionPerformed

    private void cancelPNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelPNActionPerformed
        pnF.setText(backupPN);
        pnF.setEditable(false);
        submitPN.setVisible(false);
        cancelPN.setVisible(false);
        changePN.setVisible(true);
    }//GEN-LAST:event_cancelPNActionPerformed

    private void changePNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePNActionPerformed
        backupPN = pnF.getText();
        pnF.setEditable(true);
        submitPN.setVisible(true);
        cancelPN.setVisible(true);
        changePN.setVisible(false);
        pnF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    }//GEN-LAST:event_changePNActionPerformed

    private void changeAlamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeAlamatActionPerformed
        backupAlamat = alamatF.getText();
        alamatF.setEditable(true);
        submitAlamat.setVisible(true);
        cancelAlamat.setVisible(true);
        changeAlamat.setVisible(false);
        alamatF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    }//GEN-LAST:event_changeAlamatActionPerformed

    private void cancelAlamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelAlamatActionPerformed
        alamatF.setText(backupAlamat);
        alamatF.setEditable(false);
        submitAlamat.setVisible(false);
        cancelAlamat.setVisible(false);
        changeAlamat.setVisible(true);
    }//GEN-LAST:event_cancelAlamatActionPerformed

    private void fullNameFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullNameFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fullNameFActionPerformed

    private void submitNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitNameActionPerformed
        if(fullNameF.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Nama harus diisi");
        } else {
            try {
                sql = "UPDATE users SET fullname = ? WHERE username = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, fullNameF.getText());
                ps.setString(2, usernameSession);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(null, "Nama berhasil diubah");
                
                backupName = fullNameF.getText();
                fullNameF.setEditable(false);
                submitName.setVisible(false);
                cancelName.setVisible(false);
                changeName.setVisible(true);
                fullNameF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
            } catch(SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_submitNameActionPerformed

    private void submitPNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitPNActionPerformed
        if(pnF.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Nomor Telpon harus diisi");
        } else {
            if(pnF.getText().matches("[0-9]+")) {
                try {
                    sql = "UPDATE users SET no_telp = ? WHERE username = ?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, pnF.getText());
                    ps.setString(2, usernameSession);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Nomor Telpon berhasil diubah");

                    backupPN = pnF.getText();
                    pnF.setEditable(false);
                    submitPN.setVisible(false);
                    cancelPN.setVisible(false);
                    changePN.setVisible(true);
                    pnF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
                } catch(SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nomor Telpon harus angka");
            }
        }
    }//GEN-LAST:event_submitPNActionPerformed

    private void submitEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitEmailActionPerformed
        if(emailF.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Email harus diisi");
        } else {
            if(emailF.getText().matches(email_pattern)) {
                try {
                    sql = "SELECT email FROM users WHERE email = ?";
                    ps = con.prepareStatement(sql);
                    ps.setString(1, emailF.getText());
                    rs = ps.executeQuery();
                    boolean emailExists = false;
                    while(rs.next()) {
                        emailExists = true;
                    }
                    
                    if(emailExists == false) {
                        sql = "UPDATE users SET email = ? WHERE username = ?";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, emailF.getText());
                        ps.setString(2, usernameSession);
                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Email berhasil diubah");

                        backupEmail = emailF.getText();
                        emailF.setEditable(false);
                        submitEmail.setVisible(false);
                        cancelEmail.setVisible(false);
                        changeEmail.setVisible(true);
                        emailF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
                    } else {
                        JOptionPane.showMessageDialog(null, "Email sudah ada yang pakai");
                    }
                } catch(SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nomor Telpon harus angka");
            }
        }
    }//GEN-LAST:event_submitEmailActionPerformed

    private void submitAlamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitAlamatActionPerformed
        if(alamatF.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Alamat harus diisi");
        } else {
            try {
                sql = "UPDATE users SET address = ? WHERE username = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, alamatF.getText());
                ps.setString(2, usernameSession);
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(null, "Alamat berhasil diubah");
                
                backupAlamat = alamatF.getText();
                alamatF.setEditable(false);
                submitAlamat.setVisible(false);
                cancelAlamat.setVisible(false);
                changeAlamat.setVisible(true);
                alamatF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
            } catch(SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_submitAlamatActionPerformed

    private void changeAlamat1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeAlamat1ActionPerformed
       TopupSaldo ts = new TopupSaldo();
       ts.letTopup(usernameSession);
    }//GEN-LAST:event_changeAlamat1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(String.valueOf(currentPass.getPassword()).equals("") && (String.valueOf(newPass.getPassword()).equals("") && String.valueOf(confirmPass.getPassword()).equals(""))) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi");
        } else {
            try {
                sql = "SELECT password FROM users WHERE username = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, usernameSession);
                rs = ps.executeQuery();
                if(rs.next()) {
                    boolean passMatched = validatePassword(String.valueOf(currentPass.getPassword()), rs.getString("password"));
                    
                    if(passMatched) {
                        if(String.valueOf(newPass.getPassword()).equals("") && String.valueOf(confirmPass.getPassword()).equals("")) {
                            JOptionPane.showMessageDialog(null, "Field Password Baru dan Konfirmasi Password harus diisi");
                        } else {
                            if(String.valueOf(newPass.getPassword()).equals(String.valueOf(confirmPass.getPassword()))) {
                                String passHashed = hashPassword(String.valueOf(newPass.getPassword()));
                                String updatePass = "UPDATE users SET password = ? WHERE username = ?";
                                ps = con.prepareStatement(updatePass);
                                ps.setString(1, passHashed);
                                ps.setString(2, usernameSession);
                                ps.executeUpdate();

                                JOptionPane.showMessageDialog(null, "Password berhasil diubah! mohon login kembali dengan password yang baru");
                                Home h = new Home();
                                Product p = new Product();
                                Order o = new Order();
                                Login l = new Login();

                                this.setVisible(false);
                                h.setVisible(false);
                                p.setVisible(false);
                                o.setVisible(false);

                                l.setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(null, "Password Baru dan Konfirmasi Password Baru tidak sama");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Field Password Sekarang tidak sesuai dengan yang di database");
                    }
                }
                
            } catch(SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
//                Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void changeOvoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeOvoActionPerformed
        try {
            sql = "SELECT no_ovo FROM users WHERE username = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, usernameSession);
            rs = ps.executeQuery();
            
            while(rs.next()) {
                if(rs.getString("no_ovo").equals("")) {
                    ConnectOVO co = new ConnectOVO();
                    co.setVisible(true);
                    co.connectOvo(usernameSession);
                } else {
                    PinOVO pino = new PinOVO();
                    pino.setVisible(true);
                    pino.checkPin(usernameSession, "change");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }//GEN-LAST:event_changeOvoActionPerformed

    private void deleteOvoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteOvoActionPerformed
        PinOVO pino = new PinOVO();
        pino.setVisible(true);
        pino.checkPin(usernameSession, "delete");
    }//GEN-LAST:event_deleteOvoActionPerformed

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
            java.util.logging.Logger.getLogger(Profile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Profile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Profile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Profile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Profile().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea alamatF;
    private javax.swing.JButton cancelAlamat;
    private javax.swing.JButton cancelEmail;
    private javax.swing.JButton cancelName;
    private javax.swing.JButton cancelPN;
    private javax.swing.JButton changeAlamat;
    private javax.swing.JButton changeAlamat1;
    private javax.swing.JButton changeEmail;
    private javax.swing.JButton changeName;
    private javax.swing.JButton changeOvo;
    private javax.swing.JButton changePN;
    private javax.swing.JPasswordField confirmPass;
    private javax.swing.JPasswordField currentPass;
    private javax.swing.JButton deleteOvo;
    private javax.swing.JTextField emailF;
    private javax.swing.JTextField fullNameF;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPasswordField newPass;
    private javax.swing.JTextField noOvoF;
    private javax.swing.JTextField pnF;
    private javax.swing.JTextField saldoF;
    private javax.swing.JTextField saldoOvoF;
    private javax.swing.JButton submitAlamat;
    private javax.swing.JButton submitEmail;
    private javax.swing.JButton submitName;
    private javax.swing.JButton submitPN;
    private javax.swing.JTextField usernameF;
    // End of variables declaration//GEN-END:variables
}

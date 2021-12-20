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
        leftPanel = new javax.swing.JPanel();
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
        topupSaldo = new javax.swing.JButton();
        saldoF = new javax.swing.JTextField();
        noOvoF = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        changeOvo = new javax.swing.JButton();
        saldoOvoF = new javax.swing.JTextField();
        deleteOvo = new javax.swing.JButton();
        rightPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        currentPass = new javax.swing.JPasswordField();
        newPass = new javax.swing.JPasswordField();
        confirmPass = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(906, 685));
        setPreferredSize(new java.awt.Dimension(906, 685));

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
        jButton2.setBorder(null);
        jButton2.setContentAreaFilled(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setOpaque(true);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addComponent(jLabel1)
                .addGap(150, 150, 150)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
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

        leftPanel.setBackground(new java.awt.Color(255, 255, 255));

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
        changeName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                changeNameMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                changeNameMouseExited(evt);
            }
        });
        changeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeNameActionPerformed(evt);
            }
        });

        submitName.setBackground(new java.awt.Color(0, 102, 153));
        submitName.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        submitName.setForeground(new java.awt.Color(255, 255, 255));
        submitName.setText("Submit");
        submitName.setBorder(null);
        submitName.setBorderPainted(false);
        submitName.setContentAreaFilled(false);
        submitName.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        submitName.setOpaque(true);
        submitName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitNameMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitNameMouseExited(evt);
            }
        });
        submitName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitNameActionPerformed(evt);
            }
        });

        cancelName.setBackground(new java.awt.Color(204, 204, 204));
        cancelName.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        cancelName.setForeground(new java.awt.Color(0, 0, 0));
        cancelName.setText("Cancel");
        cancelName.setBorder(null);
        cancelName.setContentAreaFilled(false);
        cancelName.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelName.setOpaque(true);
        cancelName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelNameMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelNameMouseExited(evt);
            }
        });
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
        changeEmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                changeEmailMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                changeEmailMouseExited(evt);
            }
        });
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

        submitEmail.setBackground(new java.awt.Color(0, 102, 153));
        submitEmail.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        submitEmail.setForeground(new java.awt.Color(255, 255, 255));
        submitEmail.setText("Submit");
        submitEmail.setBorder(null);
        submitEmail.setContentAreaFilled(false);
        submitEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        submitEmail.setOpaque(true);
        submitEmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitEmailMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitEmailMouseExited(evt);
            }
        });
        submitEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitEmailActionPerformed(evt);
            }
        });

        cancelEmail.setBackground(new java.awt.Color(204, 204, 204));
        cancelEmail.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        cancelEmail.setForeground(new java.awt.Color(0, 0, 0));
        cancelEmail.setText("Cancel");
        cancelEmail.setBorder(null);
        cancelEmail.setContentAreaFilled(false);
        cancelEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelEmail.setOpaque(true);
        cancelEmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelEmailMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelEmailMouseExited(evt);
            }
        });
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
        changePN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                changePNMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                changePNMouseExited(evt);
            }
        });
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

        submitPN.setBackground(new java.awt.Color(0, 102, 153));
        submitPN.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        submitPN.setForeground(new java.awt.Color(255, 255, 255));
        submitPN.setText("Submit");
        submitPN.setBorder(null);
        submitPN.setContentAreaFilled(false);
        submitPN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        submitPN.setOpaque(true);
        submitPN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitPNMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitPNMouseExited(evt);
            }
        });
        submitPN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitPNActionPerformed(evt);
            }
        });

        cancelPN.setBackground(new java.awt.Color(204, 204, 204));
        cancelPN.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        cancelPN.setForeground(new java.awt.Color(0, 0, 0));
        cancelPN.setText("Cancel");
        cancelPN.setBorder(null);
        cancelPN.setContentAreaFilled(false);
        cancelPN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelPN.setOpaque(true);
        cancelPN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelPNMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelPNMouseExited(evt);
            }
        });
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
        changeAlamat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                changeAlamatMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                changeAlamatMouseExited(evt);
            }
        });
        changeAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeAlamatActionPerformed(evt);
            }
        });

        submitAlamat.setBackground(new java.awt.Color(0, 102, 153));
        submitAlamat.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        submitAlamat.setForeground(new java.awt.Color(255, 255, 255));
        submitAlamat.setText("Submit");
        submitAlamat.setBorder(null);
        submitAlamat.setContentAreaFilled(false);
        submitAlamat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        submitAlamat.setOpaque(true);
        submitAlamat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitAlamatMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitAlamatMouseExited(evt);
            }
        });
        submitAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitAlamatActionPerformed(evt);
            }
        });

        cancelAlamat.setBackground(new java.awt.Color(204, 204, 204));
        cancelAlamat.setFont(new java.awt.Font("Google Sans", 0, 12)); // NOI18N
        cancelAlamat.setForeground(new java.awt.Color(0, 0, 0));
        cancelAlamat.setText("Cancel");
        cancelAlamat.setBorder(null);
        cancelAlamat.setContentAreaFilled(false);
        cancelAlamat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelAlamat.setOpaque(true);
        cancelAlamat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelAlamatMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelAlamatMouseExited(evt);
            }
        });
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

        topupSaldo.setBackground(new java.awt.Color(0, 204, 51));
        topupSaldo.setForeground(new java.awt.Color(255, 255, 255));
        topupSaldo.setText("Isi Ulang");
        topupSaldo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 51), 2));
        topupSaldo.setContentAreaFilled(false);
        topupSaldo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        topupSaldo.setOpaque(true);
        topupSaldo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                topupSaldoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                topupSaldoMouseExited(evt);
            }
        });
        topupSaldo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topupSaldoActionPerformed(evt);
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
        changeOvo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                changeOvoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                changeOvoMouseExited(evt);
            }
        });
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

        deleteOvo.setBackground(new java.awt.Color(255, 51, 51));
        deleteOvo.setForeground(new java.awt.Color(255, 255, 255));
        deleteOvo.setText("Putuskan");
        deleteOvo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51), 2));
        deleteOvo.setContentAreaFilled(false);
        deleteOvo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteOvo.setOpaque(true);
        deleteOvo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteOvoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteOvoMouseExited(evt);
            }
        });
        deleteOvo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOvoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(saldoOvoF, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, leftPanelLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(topupSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, leftPanelLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeOvo, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteOvo, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(saldoF, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(noOvoF, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(174, 174, 174))
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeName, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changePN, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addComponent(usernameF)
                                .addGap(26, 26, 26))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                                    .addComponent(pnF))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(fullNameF, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emailF))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(leftPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(changeEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(leftPanelLayout.createSequentialGroup()
                                        .addComponent(submitAlamat, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cancelAlamat, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                                    .addGroup(leftPanelLayout.createSequentialGroup()
                                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(submitEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                            .addComponent(submitPN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cancelPN, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                                            .addComponent(cancelEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(1, 1, 1))
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addComponent(submitName, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelName, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)))
                        .addGap(70, 70, 70))))
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(changeName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fullNameF, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitName, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelName, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(changeEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(changePN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pnF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitPN, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelPN, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(changeAlamat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(submitAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cancelAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(topupSaldo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saldoF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(changeOvo)
                    .addComponent(deleteOvo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noOvoF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saldoOvoF, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        rightPanel.setBackground(new java.awt.Color(204, 204, 204));
        rightPanel.setForeground(new java.awt.Color(0, 0, 0));

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

        jButton1.setBackground(new java.awt.Color(0, 102, 153));
        jButton1.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Submit");
        jButton1.setBorder(null);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(confirmPass)
                    .addComponent(newPass)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9)
                    .addComponent(jLabel5)
                    .addComponent(currentPass))
                .addGap(28, 28, 28))
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addGap(114, 114, 114))
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addGap(48, 48, 48)
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
                .addGap(59, 59, 59)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(217, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(614, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(81, 81, 81)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
        fullNameF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
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
        emailF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
    }//GEN-LAST:event_cancelEmailActionPerformed

    private void cancelPNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelPNActionPerformed
        pnF.setText(backupPN);
        pnF.setEditable(false);
        submitPN.setVisible(false);
        cancelPN.setVisible(false);
        changePN.setVisible(true);
        pnF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
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

    private void topupSaldoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topupSaldoActionPerformed
       TopupSaldo ts = new TopupSaldo();
       ts.letTopup(usernameSession);
    }//GEN-LAST:event_topupSaldoActionPerformed

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

    private void changeNameMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeNameMouseEntered
        changeName.setBackground(new java.awt.Color(0, 161, 40));
        changeName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 161, 40), 2));
    }//GEN-LAST:event_changeNameMouseEntered

    private void changeNameMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeNameMouseExited
        changeName.setBackground(new java.awt.Color(0,204,51));
        changeName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,204,51), 2));
    }//GEN-LAST:event_changeNameMouseExited

    private void changeEmailMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeEmailMouseEntered
        changeEmail.setBackground(new java.awt.Color(0, 161, 40));
        changeEmail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 161, 40), 2));
    }//GEN-LAST:event_changeEmailMouseEntered

    private void changeEmailMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeEmailMouseExited
        changeEmail.setBackground(new java.awt.Color(0,204,51));
        changeEmail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,204,51), 2));
    }//GEN-LAST:event_changeEmailMouseExited

    private void changePNMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePNMouseEntered
        changePN.setBackground(new java.awt.Color(0, 161, 40));
        changePN.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 161, 40), 2));
    }//GEN-LAST:event_changePNMouseEntered

    private void changePNMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changePNMouseExited
        changePN.setBackground(new java.awt.Color(0,204,51));
        changePN.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,204,51), 2));
    }//GEN-LAST:event_changePNMouseExited

    private void changeAlamatMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeAlamatMouseEntered
        changeAlamat.setBackground(new java.awt.Color(0, 161, 40));
        changeAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 161, 40), 2));
    }//GEN-LAST:event_changeAlamatMouseEntered

    private void changeAlamatMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeAlamatMouseExited
        changeAlamat.setBackground(new java.awt.Color(0,204,51));
        changeAlamat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,204,51), 2));
    }//GEN-LAST:event_changeAlamatMouseExited

    private void topupSaldoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_topupSaldoMouseEntered
        topupSaldo.setBackground(new java.awt.Color(0, 161, 40));
        topupSaldo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 161, 40), 2));
    }//GEN-LAST:event_topupSaldoMouseEntered

    private void topupSaldoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_topupSaldoMouseExited
        topupSaldo.setBackground(new java.awt.Color(0,204,51));
        topupSaldo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,204,51), 2));
    }//GEN-LAST:event_topupSaldoMouseExited

    private void changeOvoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeOvoMouseEntered
        changeOvo.setBackground(new java.awt.Color(0, 161, 40));
        changeOvo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 161, 40), 2));
    }//GEN-LAST:event_changeOvoMouseEntered

    private void changeOvoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeOvoMouseExited
        changeOvo.setBackground(new java.awt.Color(0,204,51));
        changeOvo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,204,51), 2));
    }//GEN-LAST:event_changeOvoMouseExited

    private void submitNameMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitNameMouseEntered
        submitName.setBackground(new java.awt.Color(0, 80, 120));
    }//GEN-LAST:event_submitNameMouseEntered

    private void submitNameMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitNameMouseExited
        submitName.setBackground(new java.awt.Color(0, 102, 153));
    }//GEN-LAST:event_submitNameMouseExited

    private void submitEmailMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitEmailMouseEntered
        submitEmail.setBackground(new java.awt.Color(0, 80, 120));
    }//GEN-LAST:event_submitEmailMouseEntered

    private void submitEmailMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitEmailMouseExited
        submitEmail.setBackground(new java.awt.Color(0, 102, 153));
    }//GEN-LAST:event_submitEmailMouseExited

    private void submitPNMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitPNMouseEntered
        submitPN.setBackground(new java.awt.Color(0, 80, 120));
    }//GEN-LAST:event_submitPNMouseEntered

    private void submitPNMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitPNMouseExited
        submitPN.setBackground(new java.awt.Color(0, 102, 153));
    }//GEN-LAST:event_submitPNMouseExited

    private void submitAlamatMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitAlamatMouseEntered
        submitAlamat.setBackground(new java.awt.Color(0, 80, 120));
    }//GEN-LAST:event_submitAlamatMouseEntered

    private void submitAlamatMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitAlamatMouseExited
        submitAlamat.setBackground(new java.awt.Color(0, 102, 153));
    }//GEN-LAST:event_submitAlamatMouseExited

    private void cancelNameMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelNameMouseEntered
        cancelName.setBackground(new java.awt.Color(171, 171, 171));
    }//GEN-LAST:event_cancelNameMouseEntered

    private void cancelNameMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelNameMouseExited
        cancelName.setBackground(new java.awt.Color(204, 204, 204));
    }//GEN-LAST:event_cancelNameMouseExited

    private void cancelEmailMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelEmailMouseEntered
        cancelEmail.setBackground(new java.awt.Color(171, 171, 171));
    }//GEN-LAST:event_cancelEmailMouseEntered

    private void cancelEmailMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelEmailMouseExited
        cancelEmail.setBackground(new java.awt.Color(204, 204, 204));
    }//GEN-LAST:event_cancelEmailMouseExited

    private void cancelPNMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelPNMouseEntered
        cancelPN.setBackground(new java.awt.Color(171, 171, 171));
    }//GEN-LAST:event_cancelPNMouseEntered

    private void cancelPNMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelPNMouseExited
        cancelPN.setBackground(new java.awt.Color(204, 204, 204));
    }//GEN-LAST:event_cancelPNMouseExited

    private void cancelAlamatMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelAlamatMouseEntered
        cancelAlamat.setBackground(new java.awt.Color(171, 171, 171));
    }//GEN-LAST:event_cancelAlamatMouseEntered

    private void cancelAlamatMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelAlamatMouseExited
        cancelAlamat.setBackground(new java.awt.Color(204, 204, 204));
    }//GEN-LAST:event_cancelAlamatMouseExited

    private void deleteOvoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteOvoMouseEntered
        deleteOvo.setBackground(new java.awt.Color(214, 32, 32));
        deleteOvo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(214, 32, 32), 2));
    }//GEN-LAST:event_deleteOvoMouseEntered

    private void deleteOvoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteOvoMouseExited
        deleteOvo.setBackground(new java.awt.Color(255, 51, 51));
        deleteOvo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51), 2));

    }//GEN-LAST:event_deleteOvoMouseExited

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        getUsers(usernameSession);
    }//GEN-LAST:event_jButton2ActionPerformed

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Profile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPasswordField newPass;
    private javax.swing.JTextField noOvoF;
    private javax.swing.JTextField pnF;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JTextField saldoF;
    private javax.swing.JTextField saldoOvoF;
    private javax.swing.JButton submitAlamat;
    private javax.swing.JButton submitEmail;
    private javax.swing.JButton submitName;
    private javax.swing.JButton submitPN;
    private javax.swing.JButton topupSaldo;
    private javax.swing.JTextField usernameF;
    // End of variables declaration//GEN-END:variables
}

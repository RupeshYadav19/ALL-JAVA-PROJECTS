import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Image;
import javax.swing.ImageIcon;

public class SignIn extends javax.swing.JFrame {

    public SignIn() {
        initComponents();
    }

    private void initComponents() {

        jButton6 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtemail = new javax.swing.JTextField();
        btnlogin = new javax.swing.JButton();
        txtpassword = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/close icon.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6);
        jButton6.setBounds(1316, 0, 50, 31);

        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel4);
        jLabel4.setBounds(377, 118, 36, 31);

        txtemail.setFont(new java.awt.Font("Sitka Display", 1, 18)); // NOI18N
        txtemail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtemailActionPerformed(evt);
            }
        });
        txtemail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtemailKeyPressed(evt);
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtemailKeyReleased(evt);
            }

            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtemailKeyTyped(evt);
            }
        });
        getContentPane().add(txtemail);
        txtemail.setBounds(600, 360, 264, 41);

        btnlogin.setBackground(new java.awt.Color(255, 51, 51));
        btnlogin.setFont(new java.awt.Font("Sitka Display", 1, 14)); // NOI18N
        btnlogin.setForeground(new java.awt.Color(255, 255, 255));
        btnlogin.setText("Login now");
        btnlogin.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                btnloginMouseMoved(evt);
            }
        });
        btnlogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnloginMouseEntered(evt);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnloginMouseExited(evt);
            }
        });
        btnlogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnloginActionPerformed(evt);
            }
        });
        getContentPane().add(btnlogin);
        btnlogin.setBounds(580, 500, 122, 30);

        txtpassword.setFont(new java.awt.Font("Tw Cen MT", 1, 24)); // NOI18N
        txtpassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtpasswordKeyPressed(evt);
            }
        });
        getContentPane().add(txtpassword);
        txtpassword.setBounds(600, 420, 264, 40);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Login now");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(610, 310, 130, 30);

        jLabel2.setFont(new java.awt.Font("Sitka Display", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Password");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(500, 420, 77, 30);

        jLabel1.setFont(new java.awt.Font("Sitka Display", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("User ID");
        getContentPane().add(jLabel1);
        jLabel3.setBounds(510, 370, 80, 30);

        ImageIcon icon3 = new ImageIcon(getClass().getResource("/img/login page.png"));
        Image img3 = icon3.getImage().getScaledInstance(1370, 770, Image.SCALE_SMOOTH);
        jLabel3.setIcon(new ImageIcon(img3));
        getContentPane().add(jLabel3);
        jLabel3.setBounds(0, 0, 1370, 770);

        setSize(1370, 770);
        setLocationRelativeTo(null);
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        int yes = JOptionPane.showConfirmDialog(this, "Are you really Close this application?", "Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (yes == JOptionPane.YES_OPTION) {
            System.exit(0);
        }

    }

    private void txtemailActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void txtemailKeyPressed(java.awt.event.KeyEvent evt) {
    }

    private void txtemailKeyReleased(java.awt.event.KeyEvent evt) {

    }

    private void txtemailKeyTyped(java.awt.event.KeyEvent evt) {

    }

    private void txtpasswordKeyPressed(java.awt.event.KeyEvent evt) {

    }

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {

    }

    private void btnloginMouseMoved(java.awt.event.MouseEvent evt) {

    }

    private void btnloginMouseEntered(java.awt.event.MouseEvent evt) {

    }

    private void btnloginMouseExited(java.awt.event.MouseEvent evt) {

    }

    private void btnloginActionPerformed(java.awt.event.ActionEvent evt) {
        PreparedStatement pst;
        ResultSet rs;
        Connection c = Connect.ConnectToDB();
        if (c == null) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failed! Please check your credentials and MySQL server.");
            return;
        }
        try {
            pst = c.prepareStatement("SELECT * FROM library.login where userid=? AND password=?");
            pst.setString(1, txtemail.getText());
            pst.setString(2, new String(txtpassword.getPassword()));
            rs = pst.executeQuery();
            if (rs.next()) {
                new home().setVisible(true);
                dispose();
            } else
                JOptionPane.showMessageDialog(this, "Please Enter Valid ID and Password");
        } catch (SQLException ex) {
            Logger.getLogger(SignIn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SignIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SignIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SignIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SignIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SignIn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnlogin;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField txtemail;
    private javax.swing.JPasswordField txtpassword;
    // End of variables declaration//GEN-END:variables
}

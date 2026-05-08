import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Image;
import javax.swing.ImageIcon;

public class ReturnBook extends javax.swing.JFrame {
    Connection c = null;
    PreparedStatement pst;
    ResultSet rs;

    public ReturnBook() {
        initComponents();
    }

    public void clear() {
        txtbookid.setText("");
        txtbookname.setText("");
        txtduedate.setText("");
        txtissuedate.setText("");
        txtstudentid.setText("");
        txtstudentname.setText("");
    }

    private void initComponents() {

        jButton6 = new javax.swing.JButton();
        txtduedate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtissuedate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtbookname = new javax.swing.JTextField();
        txtbookid = new javax.swing.JTextField();
        txtstudentid = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        txtstudentname = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

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
        jButton6.setBounds(1088, 0, 50, 31);

        txtduedate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtduedate);
        txtduedate.setBounds(340, 560, 350, 40);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Book ID");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(120, 180, 270, 50);

        txtissuedate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtissuedate);
        txtissuedate.setBounds(340, 480, 350, 40);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Student ID");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(120, 260, 270, 50);

        jButton1.setBackground(new java.awt.Color(204, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(242, 242, 242));
        jButton1.setText("Return");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(330, 640, 130, 40);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Issue Date");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(120, 490, 270, 50);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Book Name");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(120, 410, 220, 50);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Due Date");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(120, 560, 270, 50);

        txtbookname.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtbookname);
        txtbookname.setBounds(340, 410, 350, 40);

        txtbookid.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtbookid);
        txtbookid.setBounds(340, 180, 350, 40);

        txtstudentid.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtstudentid);
        txtstudentid.setBounds(340, 260, 350, 40);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(242, 242, 242));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/isue.jpg"))); // NOI18N
        jLabel1.setText("Return Book");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 40, 240, 60);

        jButton2.setBackground(new java.awt.Color(204, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(242, 242, 242));
        jButton2.setText("Search");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(696, 267, 80, 30);

        txtstudentname.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtstudentname);
        txtstudentname.setBounds(340, 340, 350, 40);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Student Name");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(120, 340, 220, 50);

        ImageIcon iconRet = new ImageIcon(getClass().getResource("/img/All Page Backgraound.png"));
        Image imgRet = iconRet.getImage().getScaledInstance(1140, 770, Image.SCALE_SMOOTH);
        jLabel4.setIcon(new ImageIcon(imgRet));
        getContentPane().add(jLabel4);
        jLabel4.setBounds(0, -4, 1140, 770);

        setSize(1140, 770);
        setLocationRelativeTo(null);
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        c = Connect.ConnectToDB();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed!");
            return;
        }
        try {
            pst = c.prepareStatement("SELECT * FROM library.book where studentid=?");
            pst.setString(1, txtstudentid.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                txtbookname.setText(rs.getString("name"));
                txtbookid.setText(rs.getString("id"));
                txtissuedate.setText(rs.getString("issue"));
                txtduedate.setText(rs.getString("due"));
            } else
                JOptionPane.showMessageDialog(this, "No book issued to this Student ID");

            pst = c.prepareStatement("SELECT * FROM library.student where id=?");
            pst.setString(1, txtstudentid.getText());
            rs = pst.executeQuery();
            if (rs.next())
                txtstudentname.setText(rs.getString("name"));

        } catch (SQLException ex) {
            Logger.getLogger(SignIn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        c = Connect.ConnectToDB();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed!");
            return;
        }
        if (txtbookid.getText().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Please enter Student ID and Search it again");
            txtstudentid.requestFocus();
        } else {
            try {
                pst = c.prepareStatement(
                        "UPDATE `library`.`book` SET `status` = 'NotIssued', `issue` = '', `due` = '', `studentid` = '' WHERE (`id` = ?)");
                pst.setString(1, txtbookid.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Return Successfully");
                clear();
            } catch (SQLException ex) {
                Logger.getLogger(SignIn.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            java.util.logging.Logger.getLogger(ReturnBook.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReturnBook.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReturnBook.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReturnBook.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReturnBook().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField txtbookid;
    private javax.swing.JTextField txtbookname;
    private javax.swing.JTextField txtduedate;
    private javax.swing.JTextField txtissuedate;
    private javax.swing.JTextField txtstudentid;
    private javax.swing.JTextField txtstudentname;
}

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class IssueBook extends javax.swing.JFrame {
    PreparedStatement pst;
    ResultSet rs;
    Connection c = null;

    public IssueBook() {
        initComponents();
        SimpleDateFormat dat = new SimpleDateFormat("dd/MM/yyyy ");
        Date d = new Date();
        txtissuedate.setText(dat.format(d));

    }

    public void clear() {
        txtbookname.setText("");
        txtduedate.setText("");
        txtissuedate.setText("");
        txtstudentid.setText("");
        txtid.setText("");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnissue = new javax.swing.JButton();
        txtid = new javax.swing.JTextField();
        txtstudentid = new javax.swing.JTextField();
        txtbookname = new javax.swing.JTextField();
        txtissuedate = new javax.swing.JTextField();
        txtduedate = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(242, 242, 242));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/isue.jpg"))); // NOI18N
        jLabel1.setText("Issue Book");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(40, 60, 200, 70);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Book ID");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(140, 200, 250, 40);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Student ID");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(140, 280, 240, 40);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Book Name");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(140, 360, 250, 40);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Issue Date");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(140, 450, 240, 40);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Due Date");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(140, 540, 240, 40);

        btnissue.setBackground(new java.awt.Color(204, 0, 0));
        btnissue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnissue.setForeground(new java.awt.Color(242, 242, 242));
        btnissue.setText("Issue Book");
        btnissue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnissueActionPerformed(evt);
            }
        });
        getContentPane().add(btnissue);
        btnissue.setBounds(260, 630, 130, 40);

        txtid.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(txtid);
        txtid.setBounds(320, 200, 350, 40);

        txtstudentid.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(txtstudentid);
        txtstudentid.setBounds(320, 280, 350, 40);

        txtbookname.setEditable(false);
        txtbookname.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(txtbookname);
        txtbookname.setBounds(320, 360, 350, 40);

        txtissuedate.setEditable(false);
        txtissuedate.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(txtissuedate);
        txtissuedate.setBounds(320, 450, 350, 40);

        txtduedate.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        getContentPane().add(txtduedate);
        txtduedate.setBounds(320, 540, 350, 40);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/close icon.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(1090, 0, 51, 40);

        jButton1.setBackground(new java.awt.Color(204, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(242, 242, 242));
        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(676, 204, 80, 30);

        ImageIcon icon7 = new ImageIcon(getClass().getResource("/img/All Page Backgraound.png"));
        Image img7 = icon7.getImage().getScaledInstance(1140, 770, Image.SCALE_SMOOTH);
        jLabel7.setIcon(new ImageIcon(img7));
        getContentPane().add(jLabel7);
        jLabel7.setBounds(0, 0, 1140, 770);

        setSize(1140, 770);
        setLocationRelativeTo(null);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void btnissueActionPerformed(java.awt.event.ActionEvent evt) {
        c = Connect.ConnectToDB();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed!");
            return;
        }
        if (txtid.getText().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Please enter Book ID and Search it again");
            txtid.requestFocus();
        } else {
            try {
                pst = c.prepareStatement(
                        "UPDATE `library`.`book` SET `status` = 'Issued', issue = ?, due = ?,studentid =? WHERE (`id` = ?)");
                pst.setString(1, txtissuedate.getText());
                pst.setString(2, txtduedate.getText());
                pst.setString(3, txtstudentid.getText());
                pst.setString(4, txtid.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book Issued");
                clear();
            } catch (SQLException ex) {
                Logger.getLogger(SignIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

        try {
            pst = c.prepareStatement("SELECT * FROM library.book where id=?");
            pst.setString(1, txtid.getText());
            rs = pst.executeQuery();
            if (rs.next())
                txtbookname.setText(rs.getString("name"));
            else
                JOptionPane.showMessageDialog(this, "Please Enter Valied Book ID");
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
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IssueBook.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IssueBook().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnissue;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField txtbookname;
    private javax.swing.JTextField txtduedate;
    private javax.swing.JTextField txtid;
    private javax.swing.JTextField txtissuedate;
    private javax.swing.JTextField txtstudentid;
    // End of variables declaration//GEN-END:variables
}

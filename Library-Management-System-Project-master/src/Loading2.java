import java.awt.Image;
import javax.swing.ImageIcon;

public class Loading2 extends javax.swing.JFrame {

    /**
     * Creates new form Loading2
     */
    public Loading2() {
        initComponents();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    try {
                        jProgressBar1.setValue(i);
                        Thread.sleep(50);
                        if (jProgressBar1.getString().equals("100%"))
                            new SignIn().setVisible(true);
                        if (jProgressBar1.getString().equals("50%")) {
                            jLabel.setText("Loading Modules.....");

                        }
                        if (jProgressBar1.getString().equals("25%")) {
                            jLabel.setText("Connecting Database....");
                            // jLabel1.setForeground(Color.WHITE);
                        }
                        if (jProgressBar1.getString().equals("95%"))
                            jLabel.setText("Launching Aplication....");
                    } catch (InterruptedException ex) {
                        // Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        t.start();
    }

    
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 255, 51));
        setUndecorated(true);
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(0, 255, 0));
        jPanel1.setLayout(null);

        jLabel1.setBounds(10, 10, 630, 400);

        ImageIcon iconL2 = new ImageIcon(getClass().getResource("/img/Picsart_23-10-30_17-47-04-022.jpg"));
        Image imgL2 = iconL2.getImage().getScaledInstance(630, 400, Image.SCALE_SMOOTH);
        jLabel1.setIcon(new ImageIcon(imgL2));
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 10, 630, 400);

        jProgressBar1.setStringPainted(true);
        jPanel1.add(jProgressBar1);
        jProgressBar1.setBounds(0, 440, 650, 16);
        jPanel1.add(jLabel);
        jLabel.setBounds(400, 420, 247, 22);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 650, 460);

        setSize(650, 460);
        setLocationRelativeTo(null);
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
            java.util.logging.Logger.getLogger(Loading2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Loading2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Loading2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Loading2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Loading2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables
}

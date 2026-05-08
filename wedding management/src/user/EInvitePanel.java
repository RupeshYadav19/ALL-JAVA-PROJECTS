package user;

import models.User;
import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.print.*;

/**
 * EInvitePanel — digital wedding invitation creator with live preview.
 */
public class EInvitePanel extends JPanel {
    private final User user;
    private JTextField bride, groom, venue, date, time, city;
    private JTextField rsvpPhone, hashtag;
    private JComboBox<String> themeCombo;
    private JPanel previewPanel;

    private static final Color[] PALETTE_BG  = {new Color(0xFFF5EE), new Color(0x2C,0x1B,0x12), new Color(0xF0,0xE6,0xFF)};
    private static final Color[] PALETTE_FG  = {new Color(0x7D,0x35,0x45), Color.WHITE, new Color(0x4A,0x0A,0x8A)};
    private static final String[] THEME_NAMES= {"Classic Rose Gold","Royal Dark","Purple Dream"};

    public EInvitePanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(16,0));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("💌  Digital E-Invite Creator"), BorderLayout.NORTH);

        // ── Left: form ────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(16,18,16,18));
        form.setPreferredSize(new Dimension(340,0));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(5,5,5,5); gc.gridwidth=2;

        bride   = addF(form,gc,0,"Bride's Name *");
        groom   = addF(form,gc,1,"Groom's Name *");
        venue   = addF(form,gc,2,"Venue / Banquet Hall");
        city    = addF(form,gc,3,"City");
        date    = addF(form,gc,4,"Date (DD MMMM YYYY, e.g. 14 April 2026)");
        time    = addF(form,gc,5,"Time (e.g. 7:00 PM onwards)");
        rsvpPhone= addF(form,gc,6,"RSVP Phone");
        hashtag = addF(form,gc,7,"Wedding Hashtag (e.g. #SunitaShyamKiShadi)");

        gc.gridy=16; form.add(new JLabel("Theme"),gc);
        gc.gridy=17;
        themeCombo = UIUtils.styledCombo(THEME_NAMES);
        form.add(themeCombo,gc);

        gc.gridy=18;
        JButton previewBtn = UIUtils.primaryButton("👁 Preview");
        JButton printBtn   = UIUtils.secondaryButton("🖨 Print / Save");
        JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        btnRow.setOpaque(false);
        btnRow.add(previewBtn); btnRow.add(printBtn);
        form.add(btnRow,gc);
        previewBtn.addActionListener(e -> refreshPreview());
        printBtn.addActionListener(e -> printInvite());

        // ── Right: preview ────────────────────────────────────────────────────
        previewPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int theme=themeCombo.getSelectedIndex();
                g2.setColor(PALETTE_BG[theme]);
                g2.fillRoundRect(10,10,getWidth()-20,getHeight()-20,20,20);

                // Decorative border
                g2.setColor(PALETTE_FG[theme]);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(20,20,getWidth()-40,getHeight()-40,14,14);

                // Floral decoration
                g2.setFont(new Font("Segoe UI Emoji",Font.PLAIN,28));
                g2.drawString("🌸",30,58); g2.drawString("🌸",getWidth()-65,58);
                g2.drawString("🌸",30,getHeight()-30); g2.drawString("🌸",getWidth()-65,getHeight()-30);

                int y=85;
                g2.setColor(PALETTE_FG[theme]);
                // Sub head
                g2.setFont(new Font("Segoe UI",Font.ITALIC,13));
                drawCentered(g2,"With the Blessings of our Families",y); y+=26;
                // Names
                g2.setFont(new Font("Segoe UI",Font.BOLD,30));
                drawCentered(g2,(bride.getText().isEmpty()?"Bride":bride.getText())+" 💞 "+(groom.getText().isEmpty()?"Groom":groom.getText()),y); y+=42;
                // Divider
                g2.setFont(new Font("Segoe UI",Font.PLAIN,12));
                drawCentered(g2,"✦ ────────────────── ✦",y); y+=28;
                // Invite you
                g2.setFont(new Font("Segoe UI",Font.ITALIC,16));
                drawCentered(g2,"cordially invite you to be part of their Wedding Celebrations",y); y+=28;
                // Date / venue
                g2.setFont(new Font("Segoe UI",Font.BOLD,15));
                drawCentered(g2,"📅 "+(date.getText().isEmpty()?"Date TBD":date.getText()),y); y+=22;
                drawCentered(g2,"⏰ "+(time.getText().isEmpty()?"Time TBD":time.getText()),y); y+=22;
                drawCentered(g2,"📍 "+(venue.getText().isEmpty()?"Venue TBD":venue.getText()+", "+city.getText()),y); y+=30;
                // RSVP
                g2.setFont(new Font("Segoe UI",Font.PLAIN,12));
                drawCentered(g2,"RSVP: "+(!rsvpPhone.getText().isEmpty()?rsvpPhone.getText():"—"),y); y+=22;
                if(!hashtag.getText().isEmpty()){
                    g2.setFont(new Font("Segoe UI",Font.BOLD,14));
                    drawCentered(g2,hashtag.getText(),y);
                }
            }
            void drawCentered(Graphics2D g2,String text,int y){
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(text,(getWidth()-fm.stringWidth(text))/2,y);
            }
        };
        previewPanel.setBackground(UIUtils.CREAM);
        previewPanel.setPreferredSize(new Dimension(480,580));

        JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,UIUtils.scrollPane(form),previewPanel);
        split.setDividerLocation(360); split.setResizeWeight(0.4);
        add(split,BorderLayout.CENTER);
        refreshPreview();
    }

    private void refreshPreview() { previewPanel.repaint(); }

    private void printInvite() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((g,pf,page) -> {
            if(page>0) return Printable.NO_SUCH_PAGE;
            ((Graphics2D)g).translate(pf.getImageableX(),pf.getImageableY());
            previewPanel.paint(g);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try { job.print(); } catch(PrinterException ex) { JOptionPane.showMessageDialog(this,"Print error: "+ex.getMessage()); }
        }
    }

    private JTextField addF(JPanel p,GridBagConstraints gc,int row,String label){
        gc.gridy=row*2; p.add(new JLabel(label),gc);
        gc.gridy=row*2+1; JTextField f=UIUtils.styledField(22); p.add(f,gc); return f;
    }
}

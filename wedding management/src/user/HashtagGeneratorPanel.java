package user;

import models.User;
import utils.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.util.*;

/**
 * HashtagGeneratorPanel — generates wedding hashtags from names.
 */
public class HashtagGeneratorPanel extends JPanel {
    private final User user;
    private JTextField brideF, groomF;
    private JPanel hashtagGrid;

    private static final String[] TEMPLATES = {
        "#%SH%name%",         // SHadi
        "#%WR%wedding",       // initials+Wedding
        "#%BR%And%GR%",       // BrideAndGroom
        "#%BR%%GR%Weds",
        "#%BR%%GR%Forever",
        "#%BR%%GR%ki2026Shadi",
        "#%BR%%GR%ShadiMubarak",
        "#%WR%2026",
        "#%GR%KiBaraat",
        "#%BR%KiVidaai",
        "#%BR%meets%GR%",
        "#%WR%WeddingDiaries"
    };

    public HashtagGeneratorPanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,16));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("🏷  Wedding Hashtag Generator"), BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(UIUtils.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(16,24,16,24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(8,8,8,8);

        gc.gridx=0; gc.gridy=0; inputPanel.add(new JLabel("Bride's Name:"),gc);
        gc.gridx=1; brideF=UIUtils.styledField(20); inputPanel.add(brideF,gc);
        gc.gridx=0; gc.gridy=1; inputPanel.add(new JLabel("Groom's Name:"),gc);
        gc.gridx=1; groomF=UIUtils.styledField(20); inputPanel.add(groomF,gc);
        gc.gridx=0; gc.gridy=2; gc.gridwidth=2;
        JButton genBtn = UIUtils.primaryButton("✨ Generate Hashtags");
        inputPanel.add(genBtn,gc);

        hashtagGrid = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        hashtagGrid.setBackground(UIUtils.WHITE);

        genBtn.addActionListener(e -> generateHashtags());

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(inputPanel,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(hashtagGrid),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
    }

    private void generateHashtags() {
        String bride = brideF.getText().trim().replaceAll("\\s+","");
        String groom = groomF.getText().trim().replaceAll("\\s+","");
        if (bride.isEmpty()||groom.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Enter both names."); return;
        }
        String brideFirst = Character.toUpperCase(bride.charAt(0))+bride.substring(1);
        String groomFirst = Character.toUpperCase(groom.charAt(0))+groom.substring(1);
        String initials = (bride.substring(0,1)+groom.substring(0,1)).toUpperCase();
        String combined = brideFirst+groomFirst;
        String shadhi = brideFirst+groomFirst+"KiShadi";

        hashtagGrid.removeAll();
        for (String tmpl : TEMPLATES) {
            String tag = tmpl
                .replace("%BR%", brideFirst)
                .replace("%GR%", groomFirst)
                .replace("%WR%", initials)
                .replace("%SH%name%", shadhi);
            addHashtagChip(tag, randomColor());
        }
        // Add a few custom creative ones
        addHashtagChip("#LoveStory"+brideFirst+groomFirst, UIUtils.ROSE_GOLD);
        addHashtagChip("#"+brideFirst+"And"+groomFirst+"InLove", new Color(0x8E,0x44,0xAD));
        addHashtagChip("#"+combined+"WedBliss", UIUtils.SUCCESS);

        hashtagGrid.revalidate(); hashtagGrid.repaint();
    }

    private void addHashtagChip(String tag, Color bg) {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT,6,6));
        chip.setBackground(bg);
        chip.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel lbl = new JLabel(tag);
        lbl.setFont(UIUtils.FONT_SUBHEAD);
        lbl.setForeground(Color.WHITE);
        JButton copy = new JButton("📋");
        copy.setFont(new Font("Segoe UI Emoji",Font.PLAIN,12));
        copy.setBorderPainted(false); copy.setContentAreaFilled(false);
        copy.setForeground(Color.WHITE);
        copy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        copy.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(tag),null);
            JOptionPane.showMessageDialog(this,"Copied: "+tag,"Copied! 📋",JOptionPane.INFORMATION_MESSAGE);
        });
        chip.add(lbl); chip.add(copy);
        hashtagGrid.add(chip);
    }

    private Color randomColor() {
        Color[] colors = {UIUtils.ROSE_GOLD,UIUtils.DEEP_BROWN,new Color(0x8E,0x44,0xAD),
            new Color(0x27,0x6E,0xBC),UIUtils.SUCCESS,new Color(0xC0,0x39,0x2B)};
        return colors[new Random().nextInt(colors.length)];
    }
}

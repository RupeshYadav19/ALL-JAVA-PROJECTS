package user;

import dao.VendorDAO;
import models.User;
import models.Vendor;
import utils.UIUtils;
import utils.VendorCard;
import utils.StarRatingPanel;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * BrowseVendorsPanel — WedMeGood-inspired vendor search with cards.
 */
public class BrowseVendorsPanel extends JPanel {
    private final User user;
    private final VendorDAO dao = new VendorDAO();
    private JPanel cardsPanel;
    private JComboBox<String> catCombo;
    private JTextField cityField;
    private JSlider budgetSlider;
    private JComboBox<String> sortCombo;
    private JComboBox<String> ratingCombo;
    private List<Vendor> vendors;

    public BrowseVendorsPanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,10));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("🔍  Find Vendors"), BorderLayout.NORTH);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filterBar.setBackground(UIUtils.WHITE);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0,0xD0,0xBF)),
            BorderFactory.createEmptyBorder(8,12,8,12)));

        String[] cats = {"All","Photographer","Makeup Artist","Caterer","Decorator","Mehndi Artist","DJ","Choreographer","Venue","Bridal Wear","Groom Wear","Invitation","Jewellery","Wedding Planner","Trousseau Packer","Transport","Wedding Cake"};
        catCombo = UIUtils.styledCombo(cats);
        catCombo.setPreferredSize(new Dimension(160,32));
        cityField = UIUtils.styledField(14);
        cityField.setPreferredSize(new Dimension(130,32));

        budgetSlider = new JSlider(0, 500000, 500000);
        budgetSlider.setOpaque(false);
        budgetSlider.setPreferredSize(new Dimension(140,32));
        JLabel budgetLbl = new JLabel("Max: ₹5L");
        budgetLbl.setFont(UIUtils.FONT_SMALL);
        budgetSlider.addChangeListener(e -> budgetLbl.setText("Max: ₹"+String.format("%,.0f",budgetSlider.getValue())));

        String[] ratings = {"Any","4+ ★","3+ ★","2+ ★"};
        ratingCombo = UIUtils.styledCombo(ratings);
        ratingCombo.setPreferredSize(new Dimension(90,32));

        String[] sorts = {"Rating","price_asc","price_desc","reviews"};
        String[] sortLabels = {"Top Rated","Price: Low→High","Price: High→Low","Most Reviewed"};
        sortCombo = new JComboBox<>(sortLabels);
        sortCombo.setFont(UIUtils.FONT_BODY);
        sortCombo.setPreferredSize(new Dimension(160,32));

        JButton searchBtn = UIUtils.primaryButton("🔍 Search");
        JButton clearBtn  = UIUtils.secondaryButton("Clear");

        filterBar.add(new JLabel("Category:")); filterBar.add(catCombo);
        filterBar.add(new JLabel("City:"));     filterBar.add(cityField);
        filterBar.add(new JLabel("Budget:"));   filterBar.add(budgetSlider); filterBar.add(budgetLbl);
        filterBar.add(new JLabel("Rating:"));   filterBar.add(ratingCombo);
        filterBar.add(new JLabel("Sort:"));     filterBar.add(sortCombo);
        filterBar.add(searchBtn);               filterBar.add(clearBtn);

        // Cards panel
        cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        cardsPanel.setBackground(UIUtils.WHITE);
        JScrollPane sp = UIUtils.scrollPane(cardsPanel);

        searchBtn.addActionListener(e -> performSearch(sorts[sortCombo.getSelectedIndex()]));
        clearBtn.addActionListener(e -> { catCombo.setSelectedIndex(0); cityField.setText(""); budgetSlider.setValue(500000); loadAll(); });

        JPanel body = new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(filterBar, BorderLayout.NORTH);
        body.add(sp, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
        loadAll();
    }

    private void loadAll() {
        try { vendors = dao.getAll(); renderCards(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
    }

    private void performSearch(String sort) {
        try {
            String cat  = (String) catCombo.getSelectedItem();
            String city = cityField.getText().trim();
            double max  = budgetSlider.getValue();
            double minR = switch(ratingCombo.getSelectedIndex()) {
                case 1 -> 4.0; case 2 -> 3.0; case 3 -> 2.0; default -> 0.0;
            };
            vendors = dao.search(cat, city, 0, max == 0 ? Integer.MAX_VALUE : max, minR, sort);
            renderCards();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
    }

    private void renderCards() {
        cardsPanel.removeAll();
        if (vendors.isEmpty()) {
            JLabel empty = new JLabel("No vendors found matching your criteria.", SwingConstants.CENTER);
            empty.setFont(UIUtils.FONT_BODY); empty.setForeground(Color.GRAY);
            cardsPanel.add(empty);
        }
        for (Vendor v : vendors) {
            JPanel wrapper = new JPanel(new BorderLayout(0,4));
            wrapper.setBackground(UIUtils.CARD_BG);
            wrapper.setBorder(BorderFactory.createLineBorder(new Color(0xE0,0xD0,0xBF),1));
            wrapper.setPreferredSize(new Dimension(226, 240));

            VendorCard card = new VendorCard(v.getBusinessName(),v.getCategory(),v.getCity(),
                v.getRating(),v.getStartingPrice(),v.isVerified());
            card.setPreferredSize(new Dimension(224,180));

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER,4,2));
            btnRow.setOpaque(false);
            JButton viewBtn = UIUtils.secondaryButton("View");
            JButton bookBtn = UIUtils.primaryButton(user.getUserId() > 0 ? "Book Now" : "Login to Book");

            viewBtn.addActionListener(e -> showVendorDetail(v));
            bookBtn.addActionListener(e -> openBookingSlot(v));

            btnRow.add(viewBtn); btnRow.add(bookBtn);
            wrapper.add(card, BorderLayout.CENTER);
            wrapper.add(btnRow, BorderLayout.SOUTH);
            cardsPanel.add(wrapper);
        }
        cardsPanel.revalidate(); cardsPanel.repaint();
    }

    private void showVendorDetail(Vendor v) {
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), v.getBusinessName(), true);
        dlg.setSize(560, 480);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UIUtils.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(20,28,20,28));

        JLabel name=new JLabel(v.getBusinessName()); name.setFont(UIUtils.FONT_TITLE); name.setForeground(UIUtils.DEEP_BROWN);
        JLabel cat=new JLabel("📌 "+v.getCategory()+" | 📍 "+v.getCity()+(v.getLocality()!=null?", "+v.getLocality():"")); cat.setFont(UIUtils.FONT_BODY);
        JPanel stars=new StarRatingPanel((int)Math.round(v.getRating()),false);
        stars.setPreferredSize(new Dimension(120,22));
        JLabel ratingTxt=new JLabel(String.format("  %.1f ★ (%d reviews)",v.getRating(),v.getReviewCount())); ratingTxt.setFont(UIUtils.FONT_BODY);
        JPanel ratingRow=new JPanel(new FlowLayout(FlowLayout.LEFT,4,0)); ratingRow.setOpaque(false);
        ratingRow.add(stars); ratingRow.add(ratingTxt);
        JLabel price=new JLabel("💰 Starting from ₹"+String.format("%,.0f",v.getStartingPrice())); price.setFont(UIUtils.FONT_SUBHEAD); price.setForeground(UIUtils.DEEP_BROWN);
        JLabel verif=new JLabel(v.isVerified()?"✔ Verified Vendor":"Not Yet Verified"); verif.setFont(UIUtils.FONT_SMALL); verif.setForeground(v.isVerified()?UIUtils.SUCCESS:Color.GRAY);
        JLabel award=new JLabel(v.isAwardWinner()?"🏆 Award Winner":""); award.setFont(UIUtils.FONT_SMALL); award.setForeground(new Color(0xFF,0xA0,0x00));

        JTextArea desc=new JTextArea(v.getDescription()!=null?v.getDescription():"No description available.");
        desc.setFont(UIUtils.FONT_BODY); desc.setWrapStyleWord(true); desc.setLineWrap(true);
        desc.setEditable(false); desc.setBackground(UIUtils.WHITE);
        desc.setMaximumSize(new Dimension(500,100));

        JButton bookBtn=UIUtils.primaryButton("Book This Vendor");
        bookBtn.addActionListener(e -> { dlg.dispose(); openBookingSlot(v); });

        p.add(name); p.add(Box.createRigidArea(new Dimension(0,6)));
        p.add(cat);  p.add(Box.createRigidArea(new Dimension(0,6)));
        p.add(ratingRow); p.add(Box.createRigidArea(new Dimension(0,4)));
        p.add(price); p.add(verif); p.add(award);
        p.add(Box.createRigidArea(new Dimension(0,10)));
        p.add(UIUtils.headingLabel("About")); p.add(desc);
        p.add(Box.createRigidArea(new Dimension(0,14))); p.add(bookBtn);

        dlg.setContentPane(UIUtils.scrollPane(p));
        dlg.setVisible(true);
    }

    private void openBookingSlot(Vendor v) {
        JOptionPane.showMessageDialog(this,"Go to 'Browse Events' tab to book an event.\nThis vendor's services will be available as add-ons.","Book Vendor",JOptionPane.INFORMATION_MESSAGE);
    }
}

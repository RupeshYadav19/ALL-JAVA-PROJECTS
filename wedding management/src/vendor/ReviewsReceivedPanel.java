package vendor;

import dao.ReviewDAO;
import models.Review;
import models.Vendor;
import utils.UIUtils;
import utils.StarRatingPanel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ReviewsReceivedPanel extends JPanel {
    private final ReviewDAO dao = new ReviewDAO();
    private final Vendor vendor;
    private DefaultTableModel model;

    public ReviewsReceivedPanel(Vendor vendor) {
        this.vendor=vendor;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("⭐  Reviews Received"), BorderLayout.NORTH);

        JPanel top=new JPanel(new BorderLayout(0,8));
        top.setOpaque(false);

        // Overall rating display
        if(vendor!=null){
            JPanel ratingRow=new JPanel(new FlowLayout(FlowLayout.LEFT,10,4));
            ratingRow.setOpaque(false);
            JLabel avgLbl=new JLabel("Overall Rating: "+String.format("%.1f",vendor.getRating()));
            avgLbl.setFont(new Font("Segoe UI",Font.BOLD,20));
            avgLbl.setForeground(UIUtils.DEEP_BROWN);
            StarRatingPanel stars=new StarRatingPanel((int)Math.round(vendor.getRating()),false);
            JLabel revCount=new JLabel("("+vendor.getReviewCount()+" reviews)");
            revCount.setFont(UIUtils.FONT_BODY);
            revCount.setForeground(Color.GRAY);
            ratingRow.add(avgLbl); ratingRow.add(stars); ratingRow.add(revCount);
            top.add(ratingRow,BorderLayout.NORTH);

            // Distribution bars
            try {
                int[] dist=dao.getRatingDistribution(vendor.getVendorId());
                int total=vendor.getReviewCount()>0?vendor.getReviewCount():1;
                JPanel distPanel=new JPanel();
                distPanel.setLayout(new BoxLayout(distPanel,BoxLayout.Y_AXIS));
                distPanel.setOpaque(false);
                for(int i=5;i>=1;i--){
                    JPanel row=new JPanel(new BorderLayout(8,0));
                    row.setOpaque(false); row.setMaximumSize(new Dimension(500,22));
                    JLabel lbl=new JLabel(i+" ★");
                    lbl.setFont(UIUtils.FONT_SMALL); lbl.setPreferredSize(new Dimension(36,18));
                    JProgressBar bar=new JProgressBar(0,total);
                    bar.setValue(dist[i]);
                    bar.setForeground(i>=4?UIUtils.SUCCESS:i==3?UIUtils.WARNING:UIUtils.DANGER);
                    bar.setBackground(new Color(0xEE,0xDD,0xCC));
                    bar.setBorderPainted(false);
                    JLabel cnt=new JLabel(" "+dist[i]);
                    cnt.setFont(UIUtils.FONT_SMALL);
                    row.add(lbl,BorderLayout.WEST); row.add(bar,BorderLayout.CENTER); row.add(cnt,BorderLayout.EAST);
                    distPanel.add(row); distPanel.add(Box.createRigidArea(new Dimension(0,4)));
                }
                top.add(distPanel,BorderLayout.CENTER);
            } catch (Exception ex){ top.add(new JLabel("Error: "+ex.getMessage()),BorderLayout.CENTER); }
        }

        // Table
        String[] cols={"Reviewer","Rating","Review","Date","Approved"};
        model=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable table=new JTable(model);
        UIUtils.styleTable(table);
        loadData();

        JPanel body=new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(top,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
    }

    private void loadData(){
        model.setRowCount(0);
        if(vendor==null) return;
        try{
            List<Review> reviews=dao.getByVendor(vendor.getVendorId(),false);
            for(Review r:reviews){
                model.addRow(new Object[]{
                    r.getReviewerName()!=null?r.getReviewerName():"—",
                    "★".repeat(r.getRating()),
                    r.getReviewText()!=null?r.getReviewText():"",
                    r.getCreatedAt()!=null?r.getCreatedAt().toString().substring(0,10):"",
                    r.isApproved()?"✔":"Pending"
                });
            }
        }catch(Exception ex){JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage());}
    }
}

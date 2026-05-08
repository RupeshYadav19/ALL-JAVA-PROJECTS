package vendor;

import dao.BookingDAO;
import dao.NotificationDAO;
import models.Booking;
import models.User;
import models.Vendor;
import utils.UIUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class BookingRequestsPanel extends JPanel {
    private final BookingDAO dao = new BookingDAO();
    private final User user;
    private final Vendor vendor;
    private DefaultTableModel model;
    private JTable table;
    private List<Booking> bookings;

    public BookingRequestsPanel(User user, Vendor vendor) {
        this.user=user; this.vendor=vendor;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("📋  Booking Requests"), BorderLayout.NORTH);

        JPanel toolbar=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        toolbar.setOpaque(false);
        JButton acceptBtn  = UIUtils.successButton("✔ Accept");
        JButton declineBtn = UIUtils.dangerButton("✘ Decline");
        JButton detailBtn  = UIUtils.secondaryButton("👁 Details");
        JButton refreshBtn = UIUtils.secondaryButton("↻ Refresh");
        toolbar.add(acceptBtn); toolbar.add(declineBtn); toolbar.add(detailBtn); toolbar.add(refreshBtn);

        String[] cols={"Booking ID","Couple","Event","Date","Guests","Amount","Status"};
        model=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(model);
        UIUtils.styleTable(table);
        loadData();

        acceptBtn.addActionListener(e->changeStatus("approved",null));
        declineBtn.addActionListener(e->{
            String reason=JOptionPane.showInputDialog(this,"Reason for declining:");
            changeStatus("rejected",reason);
        });
        detailBtn.addActionListener(e->showDetails());
        refreshBtn.addActionListener(e->loadData());

        JPanel body=new JPanel(new BorderLayout(0,8));
        body.setOpaque(false);
        body.add(toolbar,BorderLayout.NORTH);
        body.add(UIUtils.scrollPane(table),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
    }

    private void loadData(){
        model.setRowCount(0);
        try{
            bookings=dao.getByUser(user.getUserId()); // In real app: filter by vendor
            for(Booking b:bookings){
                model.addRow(new Object[]{b.getBookingId(),b.getUserName(),b.getEventName(),
                    b.getEventDate(),b.getGuestCount(),
                    "₹"+String.format("%,.0f",b.getTotalPrice()),b.getStatus().toUpperCase()});
            }
        }catch(Exception ex){JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage());}
    }

    private void changeStatus(String status, String reason){
        int row=table.getSelectedRow(); if(row<0) return;
        try{
            dao.updateStatus(bookings.get(row).getBookingId(),status,reason);
            // Send notification to user
            new NotificationDAO().insert(bookings.get(row).getUserId(),"Booking "+status.toUpperCase(),
                "Your booking has been "+status+" by the vendor.","booking");
            loadData();
        }catch(Exception ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
    }

    private void showDetails(){
        int row=table.getSelectedRow(); if(row<0) return;
        Booking b=bookings.get(row);
        JOptionPane.showMessageDialog(this,
            "Booking #"+b.getBookingId()+"\nCouple: "+b.getUserName()+
            "\nEvent: "+b.getEventName()+"\nDate: "+b.getEventDate()+
            "\nGuests: "+b.getGuestCount()+"\nCeremonies: "+b.getCeremonyTypes()+
            "\nSpecial Requests: "+(b.getSpecialRequests()!=null?b.getSpecialRequests():"None"),
            "Booking Details",JOptionPane.INFORMATION_MESSAGE);
    }
}

package Hotel.Management.System;

// import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class Room extends JFrame {
    JTable table;
    JButton back;

    Room() {
        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 890, 590);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        add(panel);

        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("icon/roomm.png"));
        Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        ImageIcon imageIcon1 = new ImageIcon(image);
        JLabel label = new JLabel(imageIcon1);
        label.setBounds(600, 200, 200, 200);
        panel.add(label);

        table = new JTable();
        table.setBounds(10, 40, 500, 400);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(33, 37, 41));
        panel.add(table);

        try {
            DbConnection c = new DbConnection();
            String RoomInfo = "select * from room";
            ResultSet resultSet = c.statement.executeQuery(RoomInfo);
            table.setModel(TableHelper.resultSetToTableModel(resultSet));
        } catch (Exception e) {
            e.printStackTrace();
        }

        back = new JButton("BACK");
        back.setBackground(new Color(108, 117, 125));
        back.setForeground(Color.BLACK);
        back.setBounds(200, 500, 120, 30);
        panel.add(back);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JLabel room = new JLabel("Room No.");
        room.setBounds(12, 15, 80, 19);
        room.setForeground(new Color(0, 102, 204));
        room.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(room);

        JLabel availability = new JLabel("Availability");
        availability.setBounds(119, 15, 80, 19);
        availability.setForeground(new Color(0, 102, 204));
        availability.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(availability);

        JLabel Clean = new JLabel("Clean Status");
        Clean.setBounds(216, 15, 150, 19);
        Clean.setForeground(new Color(0, 102, 204));
        Clean.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(Clean);

        JLabel Price = new JLabel("Price");
        Price.setBounds(330, 15, 80, 19);
        Price.setForeground(new Color(0, 102, 204));
        Price.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(Price);

        JLabel Bed = new JLabel("Bed Type");
        Bed.setBounds(417, 15, 80, 19);
        Bed.setForeground(new Color(0, 102, 204));
        Bed.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(Bed);

        setUndecorated(true);
        setLayout(null);
        setLocation(500, 100);
        setSize(900, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Room();
    }
}
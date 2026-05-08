package Hotel.Management.System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Date;

public class CheckOut extends JFrame {
    CheckOut() {
        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 790, 390);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        add(panel);

        JLabel label = new JLabel("Check-Out");
        label.setBounds(100, 20, 100, 30);
        label.setFont(new Font("Tahoma", Font.PLAIN, 20));
        label.setForeground(new Color(0, 102, 204));
        panel.add(label);

        JLabel UserId = new JLabel("Customer Id");
        UserId.setBounds(30, 80, 150, 30);
        UserId.setFont(new Font("Tahoma", Font.BOLD, 14));
        UserId.setForeground(new Color(33, 37, 41));
        panel.add(UserId);

        Choice Customer = new Choice();
        Customer.setBounds(200, 80, 150, 25);
        panel.add(Customer);

        JLabel roomNum = new JLabel("Room Number");
        roomNum.setBounds(30, 130, 150, 30);
        roomNum.setFont(new Font("Tahoma", Font.BOLD, 14));
        roomNum.setForeground(new Color(33, 37, 41));
        panel.add(roomNum);

        JLabel labelRoomnumber = new JLabel();
        labelRoomnumber.setBounds(200, 130, 150, 30);
        labelRoomnumber.setFont(new Font("Tahoma", Font.BOLD, 14));
        labelRoomnumber.setForeground(new Color(33, 37, 41));
        panel.add(labelRoomnumber);

        JLabel checkintime = new JLabel("Check-In Time");
        checkintime.setBounds(30, 180, 150, 30);
        checkintime.setFont(new Font("Tahoma", Font.BOLD, 14));
        checkintime.setForeground(new Color(33, 37, 41));
        panel.add(checkintime);

        JLabel labelcheckintime = new JLabel();
        labelcheckintime.setBounds(200, 180, 200, 30);
        labelcheckintime.setFont(new Font("Tahoma", Font.BOLD, 14));
        labelcheckintime.setForeground(new Color(33, 37, 41));
        panel.add(labelcheckintime);

        JLabel checkouttime = new JLabel("Check-Out Time");
        checkouttime.setBounds(30, 230, 150, 30);
        checkouttime.setFont(new Font("Tahoma", Font.BOLD, 14));
        checkouttime.setForeground(new Color(33, 37, 41));
        panel.add(checkouttime);

        Date date = new Date();
        JLabel labelcheckouttime = new JLabel("" + date);
        labelcheckouttime.setBounds(200, 230, 200, 30);
        labelcheckouttime.setFont(new Font("Tahoma", Font.BOLD, 14));
        labelcheckouttime.setForeground(new Color(33, 37, 41));
        panel.add(labelcheckouttime);

        try {
            DbConnection c = new DbConnection();
            ResultSet resultSet = c.statement.executeQuery("select * from customer");
            while (resultSet.next()) {
                Customer.add(resultSet.getString("number"));
            }
        } catch (Exception E) {
            E.printStackTrace();
        }

        JButton checkOut = new JButton(" Check-Out");
        checkOut.setBounds(30, 300, 120, 30);
        checkOut.setForeground(Color.BLACK);
        checkOut.setBackground(new Color(220, 53, 69)); // Using Red for Check-Out (destructive action)
        panel.add(checkOut);
        checkOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DbConnection cv = new DbConnection();
                    cv.statement
                            .executeUpdate("delete from customer where number = '" + Customer.getSelectedItem() + "'");
                    cv.statement.executeUpdate("update room set availability = 'Available' where roomnumber = '"
                            + labelRoomnumber.getText() + "'");
                    JOptionPane.showMessageDialog(null, "Done");
                    setVisible(false);
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        });

        JButton check = new JButton("Check");
        check.setBounds(300, 300, 120, 30);
        check.setForeground(Color.BLACK);
        check.setBackground(new Color(0, 102, 204));
        panel.add(check);
        check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DbConnection c = new DbConnection();
                    ResultSet resultSet = c.statement
                            .executeQuery("select * from customer where number = '" + Customer.getSelectedItem() + "'");
                    while (resultSet.next()) {
                        labelRoomnumber.setText(resultSet.getString("room"));
                        labelcheckintime.setText(resultSet.getString("checkintime"));
                    }
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        });

        JButton back = new JButton("Back");
        back.setBounds(170, 300, 120, 30);
        back.setForeground(Color.BLACK);
        back.setBackground(new Color(108, 117, 125));
        panel.add(back);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        setUndecorated(true);
        setLayout(null);
        setSize(800, 400);
        setLocation(500, 210);
        setVisible(true);
    }

    public static void main(String[] args) {
        new CheckOut();
    }
}
package Hotel.Management.System;

// import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class CustomerInfo extends JFrame {
    CustomerInfo() {
        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 890, 590);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        add(panel);

        JTable table = new JTable();
        table.setBounds(10, 40, 900, 450);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(33, 37, 41));
        panel.add(table);

        try {
            DbConnection c = new DbConnection();
            String q = "select * from customer";
            ResultSet resultSet = c.statement.executeQuery(q);
            table.setModel(TableHelper.resultSetToTableModel(resultSet));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel id = new JLabel("ID");
        id.setBounds(31, 11, 100, 14);
        id.setForeground(new Color(0, 102, 204));
        id.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(id);

        JLabel number = new JLabel("Number");
        number.setBounds(150, 11, 100, 14);
        number.setForeground(new Color(0, 102, 204));
        number.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(number);

        JLabel name = new JLabel("Name");
        name.setBounds(270, 11, 100, 14);
        name.setForeground(new Color(0, 102, 204));
        name.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(name);

        JLabel gender = new JLabel("Gender");
        gender.setBounds(360, 11, 100, 14);
        gender.setForeground(new Color(0, 102, 204));
        gender.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(gender);

        JLabel country = new JLabel("Country");
        country.setBounds(480, 11, 100, 25);
        country.setForeground(new Color(0, 102, 204));
        country.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(country);

        JLabel room = new JLabel("Room");
        room.setBounds(600, 11, 100, 14);
        room.setForeground(new Color(0, 102, 204));
        room.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(room);

        JLabel Time = new JLabel("CI Time");
        Time.setBounds(680, 11, 100, 14);
        Time.setForeground(new Color(0, 102, 204));
        Time.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(Time);

        JLabel Deposit = new JLabel("Deposit");
        Deposit.setBounds(800, 11, 100, 25);
        Deposit.setForeground(new Color(0, 102, 204));
        Deposit.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(Deposit);

        JButton back = new JButton("Back");
        back.setBounds(450, 510, 120, 30);
        back.setBackground(new Color(108, 117, 125));
        back.setForeground(Color.BLACK);
        panel.add(back);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        setUndecorated(true);
        setLayout(null);
        setSize(900, 600);
        setLocation(500, 100);
        setVisible(true);
    }

    public static void main(String[] args) {
        new CustomerInfo();
    }
}
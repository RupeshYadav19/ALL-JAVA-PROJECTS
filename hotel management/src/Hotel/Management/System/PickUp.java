package Hotel.Management.System;

// import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class PickUp extends JFrame {
    PickUp() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBounds(5, 5, 790, 590);
        panel.setLayout(null);
        add(panel);

        JLabel pus = new JLabel("Pick Up Service");
        pus.setBounds(90, 11, 160, 25);
        pus.setForeground(new Color(0, 102, 204));
        pus.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(pus);

        JLabel TOC = new JLabel("Type of Car");
        TOC.setBounds(32, 97, 89, 14);
        TOC.setForeground(new Color(33, 37, 41));
        TOC.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(TOC);

        Choice c = new Choice();
        c.setBounds(123, 94, 150, 25);
        panel.add(c);

        try {
            DbConnection C = new DbConnection();
            ResultSet resultSet = C.statement.executeQuery("select * from driver");
            while (resultSet.next()) {
                c.add(resultSet.getString("carname"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable table = new JTable();
        table.setBounds(10, 233, 800, 250);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(33, 37, 41));
        panel.add(table);

        try {
            DbConnection C = new DbConnection();
            String q = "select * from driver";
            ResultSet resultSet = C.statement.executeQuery(q);
            table.setModel(TableHelper.resultSetToTableModel(resultSet));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel name = new JLabel("Name");
        name.setBounds(24, 208, 46, 14);
        name.setForeground(new Color(0, 102, 204));
        panel.add(name);

        JLabel age = new JLabel("Age");
        age.setBounds(165, 208, 46, 14);
        age.setForeground(new Color(0, 102, 204));
        panel.add(age);

        JLabel gender = new JLabel("Gender");
        gender.setBounds(264, 208, 46, 14);
        gender.setForeground(new Color(0, 102, 204));
        panel.add(gender);

        JLabel company = new JLabel("Company");
        company.setBounds(366, 208, 100, 14);
        company.setForeground(new Color(0, 102, 204));
        panel.add(company);

        JLabel Carname = new JLabel("Car Name");
        Carname.setBounds(486, 208, 100, 14);
        Carname.setForeground(new Color(0, 102, 204));
        panel.add(Carname);

        JLabel available = new JLabel("Available");
        available.setBounds(600, 208, 100, 14);
        available.setForeground(new Color(0, 102, 204));
        panel.add(available);

        JLabel loacation = new JLabel("Loacation");
        loacation.setBounds(700, 208, 100, 14);
        loacation.setForeground(new Color(0, 102, 204));
        panel.add(loacation);

        JButton display = new JButton("Display");
        display.setBounds(200, 500, 120, 30);
        display.setBackground(new Color(0, 102, 204));
        display.setForeground(Color.BLACK);
        panel.add(display);

        display.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String q = "select * from driver where carname = '" + c.getSelectedItem() + "'";
                try {
                    DbConnection c = new DbConnection();
                    ResultSet resultSet = c.statement.executeQuery(q);
                    table.setModel(TableHelper.resultSetToTableModel(resultSet));
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        });

        JButton Back = new JButton("Back");
        Back.setBounds(420, 500, 120, 30);
        Back.setBackground(new Color(108, 117, 125));
        Back.setForeground(Color.BLACK);
        panel.add(Back);
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        setLayout(null);
        setSize(800, 600);
        setLocation(500, 100);
        setVisible(true);
    }

    public static void main(String[] args) {
        new PickUp();
    }
}
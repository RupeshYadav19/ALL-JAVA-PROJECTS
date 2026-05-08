package Hotel.Management.System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
// import net.proteanit.sql.DbUtils;

public class ManagerInfo extends JFrame {
    ManagerInfo() {
        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 990, 590);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        add(panel);

        JTable table = new JTable();
        table.setBounds(10, 34, 980, 450);
        table.setForeground(new Color(33, 37, 41));
        table.setBackground(Color.WHITE);
        panel.add(table);

        try {
            DbConnection c = new DbConnection();
            String q = "select * from Employee where job = 'Manager'";
            ResultSet resultSet = c.statement.executeQuery(q);
            table.setModel(TableHelper.resultSetToTableModel(resultSet));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton back = new JButton("BACK");
        back.setBounds(350, 500, 120, 30);
        back.setBackground(new Color(108, 117, 125));
        back.setForeground(Color.BLACK);
        panel.add(back);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JLabel name = new JLabel("Name");
        name.setBounds(41, 11, 70, 19);
        name.setForeground(new Color(0, 102, 204));
        name.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(name);

        JLabel Age = new JLabel("Age");
        Age.setBounds(159, 11, 70, 19);
        Age.setForeground(new Color(0, 102, 204));
        Age.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(Age);

        JLabel gender = new JLabel("Gender");
        gender.setBounds(273, 11, 70, 19);
        gender.setForeground(new Color(0, 102, 204));
        gender.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(gender);

        JLabel job = new JLabel("Job");
        job.setBounds(416, 11, 70, 19);
        job.setForeground(new Color(0, 102, 204));
        job.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(job);

        JLabel salary = new JLabel("Salary");
        salary.setBounds(536, 11, 70, 19);
        salary.setForeground(new Color(0, 102, 204));
        salary.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(salary);

        JLabel phone = new JLabel("Phone");
        phone.setBounds(656, 11, 70, 19);
        phone.setForeground(new Color(0, 102, 204));
        phone.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(phone);

        JLabel gmail = new JLabel("Gmail");
        gmail.setBounds(786, 11, 70, 19);
        gmail.setForeground(new Color(0, 102, 204));
        gmail.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(gmail);

        JLabel aadhar = new JLabel("Aadhar");
        aadhar.setBounds(896, 11, 70, 19);
        aadhar.setForeground(new Color(0, 102, 204));
        aadhar.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(aadhar);

        setUndecorated(true);
        setLayout(null);
        setLocation(430, 100);
        setSize(1000, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ManagerInfo();
    }
}
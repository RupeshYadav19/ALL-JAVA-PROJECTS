package Hotel.Management.System;

// import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class SearchRoom extends JFrame implements ActionListener {
    JCheckBox checkBox;
    Choice choice;
    JTable table;
    JButton add, back;

    SearchRoom() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBounds(5, 5, 690, 490);
        panel.setLayout(null);
        add(panel);

        JLabel searchForRoom = new JLabel("Search For Room");
        searchForRoom.setBounds(250, 11, 186, 31);
        searchForRoom.setForeground(new Color(0, 102, 204));
        searchForRoom.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(searchForRoom);

        JLabel rbt = new JLabel("Room Bed Type:");
        rbt.setBounds(50, 73, 120, 20);
        rbt.setForeground(new Color(33, 37, 41));
        rbt.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(rbt);

        JLabel rn = new JLabel("Room Number");
        rn.setBounds(23, 162, 150, 20);
        rn.setForeground(new Color(0, 102, 204));
        rn.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(rn);

        JLabel available = new JLabel("Availability");
        available.setBounds(175, 162, 150, 20);
        available.setForeground(new Color(0, 102, 204));
        available.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(available);

        JLabel price = new JLabel("Price");
        price.setBounds(458, 162, 150, 20);
        price.setForeground(new Color(0, 102, 204));
        price.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(price);

        JLabel BT = new JLabel("Bed Type");
        BT.setBounds(580, 162, 150, 20);
        BT.setForeground(new Color(0, 102, 204));
        BT.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(BT);

        JLabel SS = new JLabel("Clean Status");
        SS.setBounds(306, 162, 150, 20);
        SS.setForeground(new Color(0, 102, 204));
        SS.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(SS);

        checkBox = new JCheckBox("Only Display Available");
        checkBox.setBounds(400, 69, 205, 23);
        checkBox.setForeground(new Color(33, 37, 41));
        checkBox.setBackground(Color.WHITE);
        panel.add(checkBox);

        choice = new Choice();
        choice.add("Single Bed");
        choice.add("Double Bed");
        choice.setBounds(170, 70, 120, 20);
        panel.add(choice);

        table = new JTable();
        table.setBounds(0, 187, 700, 150);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(33, 37, 41));
        panel.add(table);

        try {
            DbConnection c = new DbConnection();
            String q = "select * from room";
            ResultSet resultSet = c.statement.executeQuery(q);
            table.setModel(TableHelper.resultSetToTableModel(resultSet));
        } catch (Exception e) {
            e.printStackTrace();
        }

        add = new JButton("Search");
        add.setBounds(200, 400, 120, 30);
        add.setBackground(new Color(0, 102, 204));
        add.setForeground(Color.BLACK);
        add.addActionListener(this);
        panel.add(add);

        back = new JButton("Back");
        back.setBounds(380, 400, 120, 30);
        back.setBackground(new Color(108, 117, 125));
        back.setForeground(Color.BLACK);
        back.addActionListener(this);
        panel.add(back);

        setUndecorated(true);
        setLayout(null);
        setLocation(500, 200);
        setSize(700, 500);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            String Q = "select * from Room where bed_type = '" + choice.getSelectedItem() + "'";
            String Q1 = "select * from Room where availability = 'Available' And bed_type = '"
                    + choice.getSelectedItem() + "'";
            try {
                DbConnection c = new DbConnection();
                ResultSet resultSet = c.statement.executeQuery(Q);
                table.setModel(TableHelper.resultSetToTableModel(resultSet));
                if (checkBox.isSelected()) {
                    ResultSet resultSet1 = c.statement.executeQuery(Q1);
                    table.setModel(TableHelper.resultSetToTableModel(resultSet1));
                }
            } catch (Exception E) {
                E.printStackTrace();
            }
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new SearchRoom();
    }
}
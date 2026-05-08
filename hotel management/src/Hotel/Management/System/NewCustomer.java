package Hotel.Management.System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Date;

public class NewCustomer extends JFrame implements ActionListener {
    JComboBox<String> comboBox;
    JTextField textFieldNumber, TextName, TextCountry, TextDeposite;
    JRadioButton r1, r2;
    ButtonGroup genderGroup;
    Choice c1;
    JLabel date;

    JButton add, back;

    NewCustomer() {

        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 840, 540);
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel);

        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("icon/customer.png"));
        Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        ImageIcon imageIcon1 = new ImageIcon(image);
        JLabel imglabel = new JLabel(imageIcon1);
        imglabel.setBounds(550, 150, 200, 200);
        panel.add(imglabel);

        JLabel labelName = new JLabel("NEW CUSTOMER FORM");
        labelName.setBounds(118, 11, 260, 53);
        labelName.setFont(new Font("Tahoma", Font.BOLD, 20));
        labelName.setForeground(new Color(0, 102, 204));
        panel.add(labelName);

        JLabel labelID = new JLabel("ID :");
        labelID.setBounds(35, 76, 200, 14);
        labelID.setForeground(new Color(33, 37, 41));
        labelID.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(labelID);

        comboBox = new JComboBox<>(new String[] { "Passport", "Aadhar Card", "Voter Id", "Driving License" });
        comboBox.setBounds(271, 73, 150, 20);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(new Color(33, 37, 41));
        comboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(comboBox);

        JLabel labelNumber = new JLabel("Number :");
        labelNumber.setBounds(35, 111, 200, 14);
        labelNumber.setForeground(new Color(33, 37, 41));
        labelNumber.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(labelNumber);
        textFieldNumber = new JTextField();
        textFieldNumber.setBounds(271, 111, 150, 20);
        panel.add(textFieldNumber);

        JLabel labelname = new JLabel("Name :");
        labelname.setBounds(35, 151, 200, 14);
        labelname.setForeground(new Color(33, 37, 41));
        labelname.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(labelname);
        TextName = new JTextField();
        TextName.setBounds(271, 151, 150, 20);
        panel.add(TextName);

        JLabel labelGender = new JLabel("Gender :");
        labelGender.setBounds(35, 191, 200, 14);
        labelGender.setForeground(new Color(33, 37, 41));
        labelGender.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(labelGender);

        r1 = new JRadioButton("Male");
        r1.setFont(new Font("Tahoma", Font.BOLD, 14));
        r1.setForeground(new Color(33, 37, 41));
        r1.setBackground(Color.WHITE);
        r1.setBounds(271, 191, 80, 12);
        panel.add(r1);

        r2 = new JRadioButton("Female");
        r2.setFont(new Font("Tahoma", Font.BOLD, 14));
        r2.setForeground(new Color(33, 37, 41));
        r2.setBackground(Color.WHITE);
        r2.setBounds(350, 191, 80, 12);
        panel.add(r2);

        genderGroup = new ButtonGroup();
        genderGroup.add(r1);
        genderGroup.add(r2);

        JLabel labelCountry = new JLabel("Country :");
        labelCountry.setBounds(35, 231, 200, 14);
        labelCountry.setForeground(new Color(33, 37, 41));
        labelCountry.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(labelCountry);
        TextCountry = new JTextField();
        TextCountry.setBounds(271, 231, 150, 20);
        panel.add(TextCountry);

        JLabel labelRoom = new JLabel("Allocated Room Number :");
        labelRoom.setBounds(35, 274, 200, 14);
        labelRoom.setForeground(new Color(33, 37, 41));
        labelRoom.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(labelRoom);

        c1 = new Choice();
        try {

            DbConnection c = new DbConnection();
            ResultSet resultSet = c.statement.executeQuery("select * from room");
            while (resultSet.next()) {
                c1.add(resultSet.getString("roomnumber"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        c1.setBounds(271, 274, 150, 20);
        c1.setFont(new Font("Tahoma", Font.BOLD, 14));
        c1.setForeground(new Color(33, 37, 41));
        c1.setBackground(Color.WHITE);
        panel.add(c1);

        JLabel labelCIS = new JLabel("Checked-In :");
        labelCIS.setBounds(35, 316, 200, 14);
        labelCIS.setForeground(new Color(33, 37, 41));
        labelCIS.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(labelCIS);

        Date date1 = new Date();

        date = new JLabel("" + date1);
        date.setBounds(271, 316, 200, 14);
        date.setForeground(new Color(33, 37, 41));
        date.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(date);

        JLabel labelDeposite = new JLabel("Deposite :");
        labelDeposite.setBounds(35, 359, 200, 14);
        labelDeposite.setForeground(new Color(33, 37, 41));
        labelDeposite.setFont(new Font("Tahoma", Font.PLAIN, 14));
        panel.add(labelDeposite);
        TextDeposite = new JTextField();
        TextDeposite.setBounds(271, 359, 150, 20);
        panel.add(TextDeposite);

        add = new JButton("ADD");
        add.setBounds(100, 430, 120, 30);
        add.setForeground(Color.BLACK);
        add.setBackground(new Color(0, 102, 204));
        add.addActionListener(this);
        panel.add(add);

        back = new JButton("BACK");
        back.setBounds(260, 430, 120, 30);
        back.setForeground(Color.BLACK);
        back.setBackground(new Color(108, 117, 125));
        back.addActionListener(this);
        panel.add(back);

        setLayout(null);
        setLocation(500, 150);
        setSize(850, 550);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            DbConnection c = new DbConnection();
            String radioBTn = null;
            if (r1.isSelected()) {
                radioBTn = "Male";
            } else if (r2.isSelected()) {
                radioBTn = "Female";
            }

            String s1 = (String) comboBox.getSelectedItem();
            String s2 = textFieldNumber.getText();
            String s3 = TextName.getText();
            String s4 = radioBTn;
            String s5 = TextCountry.getText();
            String s6 = c1.getSelectedItem();
            String s7 = date.getText();
            String s8 = TextDeposite.getText();

            try {

                String q = "insert into customer values ('" + s1 + "', '" + s2 + "','" + s3 + "','" + s4 + "', '" + s5
                        + "', '" + s6 + "', '" + s7 + "', '" + s8 + "')";
                String q1 = "update room set availability = 'Occupied' where roomnumber = '" + s6 + "'";
                c.statement.executeUpdate(q);
                c.statement.executeUpdate(q1);
                JOptionPane.showMessageDialog(null, "added Successfully");
                setVisible(false);

            } catch (Exception E) {
                E.printStackTrace();
            }
        } else {
            setVisible(false);
        }

    }

    public static void main(String[] args) {
        new NewCustomer();
    }
}
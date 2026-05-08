package Hotel.Management.System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class addDriver extends JFrame implements ActionListener {
    JTextField nameText, ageText, carCText, carNText, locText;
    JComboBox<String> comboBox, comboBox1;
    JButton add, back;

    addDriver() {
        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 890, 490);
        panel.setBackground(Color.BLUE);
        panel.setLayout(null);
        add(panel);

        JLabel label = new JLabel("ADD DRIVERS");
        label.setBounds(194, 10, 200, 22);
        label.setForeground(new Color(67, 12, 244));
        label.setFont(new Font("Tahoma", Font.BOLD, 22));
        panel.add(label);

        JLabel name = new JLabel("NAME");
        name.setBounds(64, 70, 102, 22);
        name.setFont(new Font("Tahoma", Font.BOLD, 14));
        name.setForeground(new Color(33, 37, 41));
        panel.add(name);

        nameText = new JTextField();
        nameText.setBounds(174, 70, 156, 20);
        nameText.setForeground(new Color(33, 37, 41));
        nameText.setFont(new Font("Tahoma", Font.BOLD, 14));
        nameText.setBackground(Color.WHITE);
        panel.add(nameText);

        JLabel age = new JLabel("AGE");
        age.setBounds(64, 110, 102, 22);
        age.setFont(new Font("Tahoma", Font.BOLD, 14));
        age.setForeground(new Color(33, 37, 41));
        panel.add(age);

        ageText = new JTextField();
        ageText.setBounds(174, 110, 156, 20);
        ageText.setForeground(new Color(33, 37, 41));
        ageText.setFont(new Font("Tahoma", Font.BOLD, 14));
        ageText.setBackground(Color.WHITE);
        panel.add(ageText);

        JLabel gender = new JLabel("GENDER");
        gender.setBounds(64, 150, 102, 22);
        gender.setFont(new Font("Tahoma", Font.BOLD, 14));
        gender.setForeground(new Color(33, 37, 41));
        panel.add(gender);

        comboBox = new JComboBox<>(new String[] { "Male", "Female" });
        comboBox.setBounds(176, 150, 154, 20);
        comboBox.setForeground(new Color(33, 37, 41));
        comboBox.setFont(new Font("Tahoma", Font.BOLD, 14));
        comboBox.setBackground(Color.WHITE);
        panel.add(comboBox);

        JLabel carC = new JLabel("CAR COMPANY");
        carC.setBounds(64, 190, 110, 22);
        carC.setFont(new Font("Tahoma", Font.BOLD, 14));
        carC.setForeground(new Color(33, 37, 41));
        panel.add(carC);

        carCText = new JTextField();
        carCText.setBounds(174, 190, 156, 20);
        carCText.setForeground(new Color(33, 37, 41));
        carCText.setFont(new Font("Tahoma", Font.BOLD, 14));
        carCText.setBackground(Color.WHITE);
        panel.add(carCText);

        JLabel carN = new JLabel("CAR NAME");
        carN.setBounds(64, 230, 102, 22);
        carN.setFont(new Font("Tahoma", Font.BOLD, 14));
        carN.setForeground(new Color(33, 37, 41));
        panel.add(carN);

        carNText = new JTextField();
        carNText.setBounds(174, 230, 156, 20);
        carNText.setForeground(new Color(33, 37, 41));
        carNText.setFont(new Font("Tahoma", Font.BOLD, 14));
        carNText.setBackground(Color.WHITE);
        panel.add(carNText);

        JLabel available = new JLabel("AVAILABLE");
        available.setBounds(64, 270, 102, 22);
        available.setFont(new Font("Tahoma", Font.BOLD, 14));
        available.setForeground(new Color(33, 37, 41));
        panel.add(available);

        comboBox1 = new JComboBox<>(new String[] { "YES", "NO" });
        comboBox1.setBounds(176, 270, 154, 20);
        comboBox1.setForeground(new Color(33, 37, 41));
        comboBox1.setFont(new Font("Tahoma", Font.BOLD, 14));
        comboBox1.setBackground(Color.WHITE);
        panel.add(comboBox1);

        JLabel loc = new JLabel("LOCATION");
        loc.setBounds(64, 310, 102, 22);
        loc.setFont(new Font("Tahoma", Font.BOLD, 14));
        loc.setForeground(new Color(33, 37, 41));
        panel.add(loc);

        locText = new JTextField();
        locText.setBounds(174, 310, 156, 20);
        locText.setForeground(new Color(33, 37, 41));
        locText.setFont(new Font("Tahoma", Font.BOLD, 14));
        locText.setBackground(Color.WHITE);
        panel.add(locText);

        add = new JButton("ADD");
        add.setBounds(64, 380, 111, 33);
        add.setBackground(new Color(0, 102, 204));
        add.setForeground(Color.BLACK);
        add.addActionListener(this);
        panel.add(add);

        back = new JButton("BACK");
        back.setBounds(198, 380, 111, 33);
        back.setBackground(new Color(108, 117, 125));
        back.setForeground(Color.BLACK);
        back.addActionListener(this);
        panel.add(back);

        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("icon/license.png"));
        Image image = imageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT);
        ImageIcon imageIcon1 = new ImageIcon(image);
        JLabel label1 = new JLabel(imageIcon1);
        label1.setBounds(500, 60, 300, 300);
        panel.add(label1);

        setUndecorated(true);
        setLocation(20, 200);
        setLayout(null);
        setSize(900, 500);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            String name = nameText.getText();
            String age = ageText.getText();
            String gender = (String) comboBox.getSelectedItem();
            String company = carCText.getText();
            String carname = carNText.getText();
            String available = (String) comboBox1.getSelectedItem();
            String location = locText.getText();
            try {
                DbConnection c = new DbConnection();
                String q = "insert into driver values('" + name + "','" + age + "', '" + gender + "', '" + company
                        + "', '" + carname + "','" + available + "', '" + location + "')";
                c.statement.executeUpdate(q);
                JOptionPane.showMessageDialog(null, "Driver Added");
                setVisible(false);
            } catch (Exception E) {
                E.printStackTrace();
            }
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new addDriver();
    }
}
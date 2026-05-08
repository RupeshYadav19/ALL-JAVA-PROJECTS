package Hotel.Management.System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddEmployee extends JFrame implements ActionListener {
    JTextField nameText, ageText, salaryText, phoneText, aadharText, emailText;
    JRadioButton radioButtonM, radioButtonF;
    ButtonGroup genderGroup;
    JComboBox<String> comboBox;
    JButton add, back;

    AddEmployee() {
        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 890, 490);
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel);

        JLabel name = new JLabel("NAME");
        name.setBounds(60, 30, 150, 27);
        name.setFont(new Font("serif", Font.BOLD, 17));
        name.setForeground(new Color(33, 37, 41));
        panel.add(name);

        nameText = new JTextField();
        nameText.setBounds(200, 30, 150, 27);
        nameText.setBackground(Color.WHITE);
        nameText.setFont(new Font("Tahoma", Font.BOLD, 14));
        nameText.setForeground(new Color(33, 37, 41));
        panel.add(nameText);

        JLabel Age = new JLabel("AGE");
        Age.setBounds(60, 80, 150, 27);
        Age.setFont(new Font("serif", Font.BOLD, 17));
        Age.setForeground(new Color(33, 37, 41));
        panel.add(Age);

        ageText = new JTextField();
        ageText.setBounds(200, 80, 150, 27);
        ageText.setBackground(Color.WHITE);
        ageText.setFont(new Font("Tahoma", Font.BOLD, 14));
        ageText.setForeground(new Color(33, 37, 41));
        panel.add(ageText);

        JLabel gender = new JLabel("GENDER");
        gender.setBounds(60, 120, 150, 27);
        gender.setFont(new Font("serif", Font.BOLD, 17));
        gender.setForeground(new Color(33, 37, 41));
        panel.add(gender);

        radioButtonM = new JRadioButton("MALE");
        radioButtonM.setBounds(200, 120, 70, 27);
        radioButtonM.setBackground(Color.WHITE);
        radioButtonM.setFont(new Font("Tahoma", Font.BOLD, 14));
        radioButtonM.setForeground(new Color(33, 37, 41));
        panel.add(radioButtonM);

        radioButtonF = new JRadioButton("FEMALE");
        radioButtonF.setBounds(280, 120, 100, 27);
        radioButtonF.setBackground(Color.WHITE);
        radioButtonF.setFont(new Font("Tahoma", Font.BOLD, 14));
        radioButtonF.setForeground(new Color(33, 37, 41));
        panel.add(radioButtonF);

        genderGroup = new ButtonGroup();
        genderGroup.add(radioButtonM);
        genderGroup.add(radioButtonF);

        JLabel job = new JLabel("JOB");
        job.setBounds(60, 170, 150, 27);
        job.setFont(new Font("serif", Font.BOLD, 17));
        job.setForeground(new Color(33, 37, 41));
        panel.add(job);

        comboBox = new JComboBox<>(new String[] { "Front Desk", "Housekeeping", "Kitchen Staff", "Room Service",
                "Manager", "Accountant", "Chef" });
        comboBox.setBackground(Color.WHITE);
        comboBox.setBounds(200, 170, 150, 30);
        comboBox.setFont(new Font("Tahoma", Font.BOLD, 14));
        comboBox.setForeground(new Color(33, 37, 41));
        panel.add(comboBox);

        JLabel salary = new JLabel("SALARY");
        salary.setBounds(60, 220, 150, 27);
        salary.setFont(new Font("serif", Font.BOLD, 17));
        salary.setForeground(new Color(33, 37, 41));
        panel.add(salary);

        salaryText = new JTextField();
        salaryText.setBounds(200, 220, 150, 27);
        salaryText.setBackground(Color.WHITE);
        salaryText.setFont(new Font("Tahoma", Font.BOLD, 14));
        salaryText.setForeground(new Color(33, 37, 41));
        panel.add(salaryText);

        JLabel phone = new JLabel("PHONE");
        phone.setBounds(60, 270, 150, 27);
        phone.setFont(new Font("serif", Font.BOLD, 17));
        phone.setForeground(new Color(33, 37, 41));
        panel.add(phone);

        phoneText = new JTextField();
        phoneText.setBounds(200, 270, 150, 27);
        phoneText.setBackground(Color.WHITE);
        phoneText.setFont(new Font("Tahoma", Font.BOLD, 14));
        phoneText.setForeground(new Color(33, 37, 41));
        panel.add(phoneText);

        JLabel aadhar = new JLabel("AADHAR");
        aadhar.setBounds(60, 320, 150, 27);
        aadhar.setFont(new Font("serif", Font.BOLD, 17));
        aadhar.setForeground(new Color(33, 37, 41));
        panel.add(aadhar);

        aadharText = new JTextField();
        aadharText.setBounds(200, 320, 150, 27);
        aadharText.setBackground(Color.WHITE);
        aadharText.setFont(new Font("Tahoma", Font.BOLD, 14));
        aadharText.setForeground(new Color(33, 37, 41));
        panel.add(aadharText);

        JLabel email = new JLabel("EMAIL");
        email.setBounds(60, 370, 150, 27);
        email.setFont(new Font("serif", Font.BOLD, 17));
        email.setForeground(new Color(33, 37, 41));
        panel.add(email);

        emailText = new JTextField();
        emailText.setBounds(200, 370, 150, 27);
        emailText.setBackground(Color.WHITE);
        emailText.setFont(new Font("Tahoma", Font.BOLD, 14));
        emailText.setForeground(new Color(33, 37, 41));
        panel.add(emailText);

        JLabel AED = new JLabel("ADD EMPLOYEE DETAILS");
        AED.setBounds(450, 24, 445, 35);
        AED.setFont(new Font("Tahoma", Font.BOLD, 31));
        AED.setForeground(new Color(0, 102, 204));
        panel.add(AED);

        add = new JButton("ADD");
        add.setBounds(80, 420, 100, 30);
        add.setBackground(new Color(0, 102, 204));
        add.setForeground(Color.BLACK);
        add.addActionListener(this);
        panel.add(add);

        back = new JButton("BACK");
        back.setBounds(200, 420, 100, 30);
        back.setBackground(new Color(108, 117, 125));
        back.setForeground(Color.BLACK);
        back.addActionListener(this);
        panel.add(back);

        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("icon/addemp.png"));
        Image image = imageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT);
        ImageIcon imageIcon1 = new ImageIcon(image);
        JLabel label = new JLabel(imageIcon1);
        label.setBounds(500, 100, 300, 300);
        panel.add(label);

        setUndecorated(true);
        setLocation(60, 160);
        setLayout(null);
        setSize(900, 500);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            String name = nameText.getText();
            String age = ageText.getText();
            String salary = salaryText.getText();
            String phone = phoneText.getText();
            String email = emailText.getText();
            String aadhar = aadharText.getText();
            String job = (String) comboBox.getSelectedItem();
            String gender = null;
            if (radioButtonM.isSelected()) {
                gender = "Male";
            } else if (radioButtonF.isSelected()) {
                gender = " Female";
            }
            try {
                DbConnection c = new DbConnection();
                String q = "insert into employee values('" + name + "', '" + age + "', '" + gender + "', '" + job
                        + "', '" + salary + "','" + phone + "', '" + email + "', '" + aadhar + "')";
                c.statement.executeUpdate(q);
                JOptionPane.showMessageDialog(null, "Employee Added");
                setVisible(false);
            } catch (Exception E) {
                E.printStackTrace();
            }
        } else {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new AddEmployee();
    }
}
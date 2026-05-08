package user;

import dao.UserDAO;
import models.User;
import utils.UIUtils;
import utils.PasswordUtils;
import utils.ValidationUtils;
import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    private final User user;
    private final UserDAO dao = new UserDAO();

    public ProfilePanel(User user) {
        this.user = user;
        setBackground(UIUtils.CREAM);
        setLayout(new BorderLayout(0,12));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        build();
    }

    private void build() {
        add(UIUtils.createHeaderBar("👤  My Profile"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(6,6,6,6); gc.gridwidth=2;

        gc.gridy=0; form.add(new JLabel("Full Name *"),gc);
        gc.gridy=1; JTextField nameF=UIUtils.styledField(28);
        nameF.setText(user.getFullName()!=null?user.getFullName():""); form.add(nameF,gc);

        gc.gridy=2; form.add(new JLabel("Email (read-only)"),gc);
        gc.gridy=3; JTextField emailF=UIUtils.styledField(28);
        emailF.setText(user.getEmail()!=null?user.getEmail():"");
        emailF.setEditable(false); emailF.setBackground(new Color(0xF0,0xEE,0xEB));
        form.add(emailF,gc);

        gc.gridy=4; form.add(new JLabel("Phone Number"),gc);
        gc.gridy=5; JTextField phoneF=UIUtils.styledField(14);
        phoneF.setText(user.getPhone()!=null?user.getPhone():""); form.add(phoneF,gc);

        gc.gridy=6; form.add(new JLabel("City"),gc);
        gc.gridy=7; JTextField cityF=UIUtils.styledField(16);
        cityF.setText(user.getCity()!=null?user.getCity():""); form.add(cityF,gc);

        gc.gridy=8; form.add(new JLabel("─────── Change Password ───────"),gc);
        gc.gridy=9; form.add(new JLabel("Current Password"),gc);
        gc.gridy=10; JPasswordField curPass=UIUtils.styledPasswordField(20); form.add(curPass,gc);
        gc.gridy=11; form.add(new JLabel("New Password"),gc);
        gc.gridy=12; JPasswordField newPass=UIUtils.styledPasswordField(20); form.add(newPass,gc);
        gc.gridy=13; form.add(new JLabel("Confirm New Password"),gc);
        gc.gridy=14; JPasswordField cnfPass=UIUtils.styledPasswordField(20); form.add(cnfPass,gc);

        gc.gridy=15;
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        btns.setOpaque(false);
        JButton saveBtn = UIUtils.primaryButton("💾 Save Changes");
        JButton pwdBtn  = UIUtils.secondaryButton("🔒 Change Password");
        btns.add(saveBtn); btns.add(pwdBtn);
        form.add(btns,gc);

        saveBtn.addActionListener(e -> {
            String name=nameF.getText().trim();
            String phone=phoneF.getText().trim();
            if(!ValidationUtils.isNotEmpty(name)){JOptionPane.showMessageDialog(this,"Name required.");return;}
            if(!phone.isEmpty()&&!ValidationUtils.isValidPhone(phone)){JOptionPane.showMessageDialog(this,"Invalid phone number.");return;}
            user.setFullName(name); user.setPhone(phone); user.setCity(cityF.getText().trim());
            try { dao.updateUser(user); JOptionPane.showMessageDialog(this,"Profile updated ✔"); }
            catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });

        pwdBtn.addActionListener(e -> {
            String cur=new String(curPass.getPassword());
            String np=new String(newPass.getPassword());
            String cnf=new String(cnfPass.getPassword());
            if(cur.isEmpty()||np.isEmpty()){JOptionPane.showMessageDialog(this,"Fill current and new passwords.");return;}
            if(!np.equals(cnf)){JOptionPane.showMessageDialog(this,"New passwords don't match.");return;}
            if(np.length()<6){JOptionPane.showMessageDialog(this,"Password must be at least 6 chars.");return;}
            if(!PasswordUtils.hash(cur).equals(user.getPasswordHash())){JOptionPane.showMessageDialog(this,"Current password is incorrect.");return;}
            user.setPasswordHash(PasswordUtils.hash(np));
            try { dao.updateUser(user); JOptionPane.showMessageDialog(this,"Password changed ✔"); curPass.setText(""); newPass.setText(""); cnfPass.setText(""); }
            catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });

        add(UIUtils.scrollPane(form),BorderLayout.CENTER);
    }
}

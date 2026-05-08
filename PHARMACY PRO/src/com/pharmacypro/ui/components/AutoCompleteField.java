package com.pharmacypro.ui.components;

import javax.swing.JList;
import javax.swing.JWindow;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class AutoCompleteField extends PlaceholderTextField {
    private JWindow popup;
    private JList<String> list;
    private ActionListener selectAction;

    public AutoCompleteField(String placeholder) {
        super(placeholder);
        popup = new JWindow();
        list = new JList<>();
        popup.add(new JScrollPane(list));

        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { showPopup(); }
            public void removeUpdate(DocumentEvent e) { showPopup(); }
            public void changedUpdate(DocumentEvent e) { showPopup(); }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && popup.isVisible()) {
                    list.setSelectedIndex(0);
                    list.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER && popup.isVisible() && list.getSelectedIndex() != -1) {
                    setText(list.getSelectedValue());
                    popup.setVisible(false);
                    if (selectAction != null) selectAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                }
            }
        });

        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    setText(list.getSelectedValue());
                    popup.setVisible(false);
                    if (selectAction != null) selectAction.actionPerformed(new ActionEvent(list, ActionEvent.ACTION_PERFORMED, null));
                    AutoCompleteField.this.requestFocus();
                }
            }
        });
    }

    public void setSelectAction(ActionListener action) {
        this.selectAction = action;
    }

    private void showPopup() {
        if (getText().isEmpty()) {
            popup.setVisible(false);
            return;
        }
        // DB query logic would be triggered here in real use, for now mock:
        String[] data = {"Item 1", "Item 2"}; 
        list.setListData(data);
        popup.pack();
        popup.setLocation(getLocationOnScreen().x, getLocationOnScreen().y + getHeight());
        popup.setVisible(true);
    }
}

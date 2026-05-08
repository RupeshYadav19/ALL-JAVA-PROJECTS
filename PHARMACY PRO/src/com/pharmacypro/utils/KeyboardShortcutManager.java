package com.pharmacypro.utils;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class KeyboardShortcutManager {
    public static void register(JComponent comp, String actionName, int keyCode, int modifiers, ActionListener action) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
        comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionName);
        comp.getActionMap().put(actionName, new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                action.actionPerformed(e);
            }
        });
    }

    public static void register(JComponent comp, String actionName, int keyCode, ActionListener action) {
        register(comp, actionName, keyCode, 0, action);
    }
}

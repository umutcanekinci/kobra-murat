package common.graphics.ui;

import javax.swing.JTextField;

public class TextField extends JTextField {

    public TextField() {
        super();
        setFont(new java.awt.Font("Arial", 0, 20));
        setForeground(new java.awt.Color(255, 255, 255));
        setBackground(new java.awt.Color(0, 0, 0));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        setPreferredSize(new java.awt.Dimension(200, 60));
    }

    public TextField(String text) {
        this();
        setText(text);
    }

    public TextField(String text, java.awt.event.ActionListener listener) {
        this(text);
        addActionListener(listener);
    }

}

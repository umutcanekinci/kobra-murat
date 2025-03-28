package common.graphics.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class Button extends JButton {
    private static final Color COLOR = new Color(84, 148, 20);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.BLACK;

    public Button(String text) {
        super(text);
        setRequestFocusEnabled(false);
        setBackground(COLOR);
        setForeground(TEXT_COLOR);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
        setFont(new Font("Arial", Font.BOLD, 20));
        setPreferredSize(new Dimension(200, 100));
    }

    public Button(String text, ActionListener listener) {
        this(text);
        addActionListener(listener);
    }
}

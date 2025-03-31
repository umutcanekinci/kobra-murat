package common.graphics.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Button extends ButtonGradient {
    private static final Color COLOR = new Color(84, 148, 21);
    private static final Color COLOR2 = new Color(141, 198, 63);

    public Button(String text) {
        super();
        setText(text);
        setFont(new Font("Arial", Font.BOLD, 50));
        setRequestFocusEnabled(false);
        setColor1(COLOR);
        setColor2(COLOR2);
        setPreferredSize(new Dimension(700, 170));
        
        //setForeground(TEXT_COLOR);
        //setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        //setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
        //setFont(new Font("Arial", Font.BOLD, 20));
        
    }

    public Button(String text, ActionListener listener) {
        this(text);
        addActionListener(listener);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    listener.actionPerformed(null);
                }
            }
        });
    }
}

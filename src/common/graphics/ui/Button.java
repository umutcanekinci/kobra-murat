package common.graphics.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import common.Utils;

public class Button extends ButtonGradient {

    public static final Dimension SIZE = new Dimension(700, 170);
    public static final Font FONT = new Font("Lato",Font.BOLD, 50);
    private static final Color COLOR = new Color(84, 148, 21);
    private static final Color COLOR2 = new Color(141, 198, 63);

    public Button(String text) {
        super();
        setText(text);
        setFont(FONT);
        setRequestFocusEnabled(false);
        setColor1(COLOR);
        setColor2(COLOR2);
        setPreferredSize(Utils.scale(SIZE));
    }

    public Button(String text, ActionListener listener) {
        this(text);
        setAction(listener);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)
                    listener.actionPerformed(null);
            }
        });
    }

    public void setAction(ActionListener action) {
        for (ActionListener al : getActionListeners()) {
            if (al != null) {
                removeActionListener(al);
            }
        }
        addActionListener(action);
    }
}

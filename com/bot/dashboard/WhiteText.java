package com.bot.dashboard;

import javax.swing.*;
import java.awt.*;

public class WhiteText extends JLabel {

    public WhiteText(String text) {
        super("<html><body>" + text + "</body></html>");
        setOpaque(false);
        setForeground(Color.white);
    }

}

package com.bot.dashboard;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    public Frame() {
        super("Trading Bot");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(960, 540));
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(new Dashboard(), SwingConstants.CENTER);
        setVisible(true);
    }

}

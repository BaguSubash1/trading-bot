package com.bot.dashboard;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JPanel {

    public Dashboard() {
        super(new GridBagLayout());
        setBackground(Colors.BACKGROUND);
        setPreferredSize(new Dimension(960, 540));

        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.insets = new Insets(4, 4, 2, 4);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridwidth = 2;
        add(new Nav(), c);

        c.insets = new Insets(2, 4, 4, 2);
        c.gridy = 1;
        c.weightx = 0.25;
        c.weighty = 1.0;
        c.gridwidth = 1;
        add(new Info(), c);

        c.insets = new Insets(2, 2, 4, 4);
        c.gridx = 1;
        c.weightx = 0.75;
        add(new CandleDisplay(), c);
    }

}

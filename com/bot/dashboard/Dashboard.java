package com.bot.dashboard;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JPanel {

    public Dashboard() {
        super(new GridLayout(2, 2, 4, 4));
        setSize(new Dimension(952, 532));

        setBackground(Colors.BACKGROUND);

    }

}

package com.bot.dashboard;

import javax.swing.*;
import java.awt.*;

public class Nav extends Panel {

    public Nav() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(0, 24));

        add(new WhiteText("TradEZ"));
    }

}

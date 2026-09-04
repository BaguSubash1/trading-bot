package com.bot.dashboard;

import javax.swing.*;
import java.awt.*;

public class Info extends Panel {

    private JPanel info_panel = null;

    public Info() {

        final JTextField ticker_field = new JTextField("Ticker");
        add(ticker_field);

        ticker_field.addActionListener(action -> {
            System.out.println(action.getActionCommand());
            ErrorDispatch.dispatch_error(action.getActionCommand());
        });

    }

}

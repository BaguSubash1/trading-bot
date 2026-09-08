package com.bot.dashboard;

import com.bot.Main;
import com.bot.util.Queryable;

import javax.swing.*;
import java.awt.*;

public class Info extends Panel {

    private JPanel info_panel = null;

    public Info() {

        setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        final JTextField ticker_field = new JTextField("Ticker");
        add(ticker_field, c);

        ticker_field.addActionListener(action -> {
            final String ticker_name = action.getActionCommand();

            ErrorDispatch.wrap_operation(() -> Main.massive.get_ticker(ticker_name, Queryable.NONE), ticker -> {
                if(info_panel != null) {
                    remove(info_panel);
                }

                info_panel = new JPanel();
                info_panel.setLayout(new GridBagLayout());
                info_panel.setOpaque(false);

                final WhiteText title = new WhiteText(ticker.get("results").get("name").get(String.class));
                title.setFont(title.getFont().deriveFont(Font.BOLD));

                final String currency_name = ticker.get("results").get("currency_name").get(String.class);
                final WhiteText currency = new WhiteText(currency_name);
                currency.setFont(currency.getFont().deriveFont(Font.ITALIC));

                final WhiteText description = new WhiteText(ticker.get("results").get("description").get(String.class));

                info_panel.add(title, c);
                info_panel.add(currency, c);
                info_panel.add(description, c);
                add(info_panel, c);

                Dashboard.replace_candles(new CandleDisplay(ticker_name, currency_name));
                Dashboard.update();
            });
        });

    }

}

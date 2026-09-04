package com.bot.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Frame extends JFrame {

    public Frame() {
        super("Trading Bot");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(960, 540));

        final JLayeredPane layered_pane = new JLayeredPane();
        setContentPane(layered_pane);

        final Dashboard dashboard = new Dashboard();
        layered_pane.add(dashboard, JLayeredPane.DEFAULT_LAYER);
        layered_pane.add(ErrorDispatch.panel, JLayeredPane.POPUP_LAYER);

        layered_pane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                final int width = layered_pane.getWidth();
                final int height = layered_pane.getHeight();

                dashboard.setBounds(0, 0, width, height);
                ErrorDispatch.panel.setBounds(width - 204, 4, 200, height - 8);
            }
        });

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}

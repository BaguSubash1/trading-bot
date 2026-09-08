package com.bot.dashboard;

import com.bot.Main;
import com.bot.massive.MassiveClient;
import com.bot.util.JSON;
import com.bot.util.Queryable;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;

public class CandleDisplay extends Panel {

    private record Candle(double open, double high, double low, double close) {

        public Candle(JSON<?> json) {
            this(
                    json.get("o").any_double(),
                    json.get("h").any_double(),
                    json.get("l").any_double(),
                    json.get("c").any_double());
        }

    }

    private final ArrayList<Candle> candles = new ArrayList<>();
    private double min_price;
    private double max_price;
    private String currency_name;
    private String date;
    private int mouse_x = 0;
    private int mouse_y = 0;

    private static final int DAY_DELAY = 5;

    public CandleDisplay(String ticker, String currency_name) {

        this.currency_name = currency_name;
        date = LocalDate.now().minusDays(DAY_DELAY).toString();

        if(ticker == null) {
            return;
        }

        ErrorDispatch.wrap_operation(() -> Main.massive.get_custom_bars(
                ticker, 30, MassiveClient.TimeSpan.Minute, date, date, new Queryable() {}), bars -> {
            final long bar_count = bars.get("resultsCount").get(Long.class);
            candles.ensureCapacity((int) bar_count);
            final JSON<?> results = bars.get("results");

            min_price = Double.MAX_VALUE;
            max_price = Double.MIN_VALUE;

            for(int i = 0; i < bar_count; i++) {
                final Candle candle = new Candle(results.get(i));
                candles.add(candle);

                min_price = Math.min(candle.low(), min_price);
                max_price = Math.max(candle.high(), max_price);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                mouse_x = event.getX();
                mouse_y = event.getY();
                repaint();
            }
        });

    }

    private int price_to_pixel(double price) {
        final int PADDING = 50;
        return (int) ((1 - (price - min_price) / (max_price - min_price)) * (getHeight() - PADDING * 2)) + PADDING;
    }

    private double pixel_to_price(int pixel) {
        final int PADDING = 50;
        double full_price = (1 - (double) (pixel - PADDING) / (getHeight() - PADDING * 2)) * (max_price - min_price) + min_price;
        return (double) Math.round(full_price * 100) / 100;
    }

    private void draw_average_lines(Graphics2D g2, int i, Candle candle, int WIDTH, int SPACING, int CANDLE_X) {
        if(i == 0) return;

        final int PREV_X = (i - 1) * (WIDTH + SPACING) + WIDTH / 2;
        final Candle prev_candle = candles.get(i - 1);

        g2.setColor(Colors.HL_AVERAGE_LINE);
        g2.drawLine(PREV_X, price_to_pixel((prev_candle.high() + prev_candle.low()) / 2),
                CANDLE_X, price_to_pixel((candle.high() + candle.low()) / 2));

        g2.setColor(Colors.OC_AVERAGE_LINE);
        g2.drawLine(PREV_X, price_to_pixel((prev_candle.open() + prev_candle.close()) / 2),
                CANDLE_X, price_to_pixel((candle.open() + candle.close()) / 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Colors.BORDER);
        g2.setFont(new Font("Arial", Font.ITALIC, 6));
        for(int i = 0; i < 20; i++) {
            int y = i * getHeight() / 20;
            g2.drawLine(0, y, getWidth(), y);
            g2.drawString(Double.toString(pixel_to_price(y)), 2, y + 8);
        }

        g2.setColor(Color.gray);
        g2.setFont(new Font("Arial", Font.ITALIC, 8));
        g2.drawString(date + " (" + currency_name + ")", 4, 12);

        if(candles.isEmpty()) {
            return;
        }

        final int max_candles = Math.min(getWidth() / 4, candles.size());

        final int SPACING = 1;
        final int WIDTH = getWidth() / max_candles - SPACING;

        for(int i = 0; i < max_candles; i++) {
            final Candle candle = candles.get(i);
            final int CANDLE_X = i * (WIDTH + SPACING) + WIDTH / 2;

            if(candle.low() == candle.high()) {
                g2.setColor(Colors.NO_TRADE);
                g2.drawLine(CANDLE_X, 0, CANDLE_X, getHeight());

                draw_average_lines(g2, i, candle, WIDTH, SPACING, CANDLE_X);
                continue;
            }

            g2.setColor(candle.close() > candle.open() ? Color.green : Color.red);

            g2.drawLine(
                    CANDLE_X, price_to_pixel(candle.high()),
                    CANDLE_X, price_to_pixel(candle.low()));

            final int bouse = price_to_pixel(candle.close());
            final int touse = price_to_pixel(candle.open());
            g2.fillRect(
                    i * (WIDTH + SPACING), Math.min(bouse, touse),
                    WIDTH, Math.abs(bouse - touse));

            draw_average_lines(g2, i, candle, WIDTH, SPACING, CANDLE_X);
        }

        g.setColor(Color.gray);
        g2.setFont(new Font("Arial", Font.PLAIN, 8));
        g.drawString(pixel_to_price(mouse_y) + " " + currency_name, mouse_x + 4, mouse_y - 4);
        g.drawLine(mouse_x, 0, mouse_x, getHeight());

        g.setColor(Colors.BORDER);
        g.drawLine(0, mouse_y, getWidth(), mouse_y);
    }

}

package com.bot.dashboard;

import com.bot.Main;
import com.bot.massive.MassiveClient;
import com.bot.util.JSON;
import com.bot.util.Queryable;

import java.awt.*;
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

    private final int DAY_DELAY = 5;

    public CandleDisplay(String ticker) {

        if(ticker == null) {
            return;
        }

        final String query_date = LocalDate.now().minusDays(DAY_DELAY).toString();

        ErrorDispatch.wrap_operation(() -> Main.massive.get_custom_bars(
                ticker, 30, MassiveClient.TimeSpan.Minute, query_date, query_date, new Queryable() {}), bars -> {
            final long bar_count = bars.get("resultsCount").get(Long.class);
            candles.ensureCapacity((int) bar_count);
            final JSON<?> results = bars.get("results");

            for(int i = 0; i < bar_count; i++) {
                candles.add(new Candle(results.get(i)));
            }
        });

    }

    private int price_to_pixel(double price, double min, double max) {
        final int PADDING = 50;
        return (int) ((1 - (price - min) / (max - min)) * (getHeight() - PADDING * 2)) + PADDING;
    }

    private void draw_average_lines(Graphics2D g2, int i, Candle candle, int WIDTH, int SPACING, int CANDLE_X,
                                   double min, double max) {
        if(i == 0) return;

        final int PREV_X = (i - 1) * (WIDTH + SPACING) + WIDTH / 2;
        final Candle prev_candle = candles.get(i - 1);

        g2.setColor(Colors.HL_AVERAGE_LINE);
        g2.drawLine(PREV_X, price_to_pixel((prev_candle.high() + prev_candle.low()) / 2, min, max),
                CANDLE_X, price_to_pixel((candle.high() + candle.low()) / 2, min, max));

        g2.setColor(Colors.OC_AVERAGE_LINE);
        g2.drawLine(PREV_X, price_to_pixel((prev_candle.open() + prev_candle.close()) / 2, min, max),
                CANDLE_X, price_to_pixel((candle.open() + candle.close()) / 2, min, max));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(candles.isEmpty()) {
            return;
        }

        final Graphics2D g2 = (Graphics2D) g;
        final int max_candles = Math.min(getWidth() / 4, candles.size());

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for(int i = 0; i < max_candles; i++) {
            min = Math.min(min, candles.get(i).low());
            max = Math.max(max, candles.get(i).high());
        }

        final int SPACING = 1;
        final int WIDTH = getWidth() / max_candles - SPACING;

        for(int i = 0; i < max_candles; i++) {
            final Candle candle = candles.get(i);
            final int CANDLE_X = i * (WIDTH + SPACING) + WIDTH / 2;

            if(candle.close() == candle.open()) {
                g2.setColor(Colors.SUBTLE);
                g2.drawLine(CANDLE_X, 0, CANDLE_X, getHeight());
                g2.fillRect(i * (WIDTH + SPACING), price_to_pixel(candle.low(), min, max) - WIDTH * 2,
                        WIDTH, WIDTH * 4);

                draw_average_lines(g2, i, candle, WIDTH, SPACING, CANDLE_X, min, max);
                continue;
            }

            g2.setColor(candle.close() > candle.open() ? Color.green : Color.red);

            g2.drawLine(
                    CANDLE_X, price_to_pixel(candle.high(), min, max),
                    CANDLE_X, price_to_pixel(candle.low(), min, max));

            final int bouse = price_to_pixel(candle.close(), min, max);
            final int touse = price_to_pixel(candle.open(), min, max);
            g2.fillRect(
                    i * (WIDTH + SPACING), Math.min(bouse, touse),
                    WIDTH, Math.abs(bouse - touse));

            draw_average_lines(g2, i, candle, WIDTH, SPACING, CANDLE_X, min, max);
        }
    }

}

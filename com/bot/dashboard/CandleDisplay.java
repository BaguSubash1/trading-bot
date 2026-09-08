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

    private ArrayList<Candle> candles;

    private final int DAY_DELAY = 5;

    public CandleDisplay(String ticker) {

        if(ticker == null) {
            return;
        }

        final String query_date = LocalDate.now().minusDays(DAY_DELAY).toString();

        ErrorDispatch.wrap_operation(() -> Main.massive.get_custom_bars(
                ticker, 1, MassiveClient.TimeSpan.Hour, query_date, query_date, new Queryable() {}), bars -> {
            final long bar_count = bars.get("resultsCount").get(Long.class);
            candles = new ArrayList<>((int) bar_count);
            final JSON<?> results = bars.get("results");

            for(int i = 0; i < bar_count; i++) {
                candles.add(new Candle(results.get(i)));
            }
        });

    }

}

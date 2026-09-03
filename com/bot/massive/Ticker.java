package com.bot.massive;

import com.bot.util.Queryable;

import java.util.HashMap;

public class Ticker {

    public Boolean active;
    public String cik;
    public String composite_figi;
    public String currency_name;
    public String last_updated_utc;
    public String locale;
    public String market;
    public String name;
    public String primary_exchange;
    public String share_class_figi;
    public String ticker;
    public String type;

    public Ticker(HashMap<String, Object> json) {
        active = (Boolean) json.get("active");
        cik = (String) json.get("cik");
        composite_figi = (String) json.get("composite_figi");
        currency_name = (String) json.get("currency_name");
        last_updated_utc = (String) json.get("last_updated_utc");
        locale = (String) json.get("locale");
        market = (String) json.get("market");
        name = (String) json.get("name");
        primary_exchange = (String) json.get("primary_exchange");
        share_class_figi = (String) json.get("share_class_figi");
        ticker = (String) json.get("ticker");
        type = (String) json.get("type");
    }

}

package com.bot.util;

import java.util.ArrayList;
import java.util.HashMap;

public class JSON<T> {

    private final T value;

    public JSON(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <K> K get(Class<K> _clazz) {
        return (K) value;
    }

    @SuppressWarnings("unchecked")
    public JSON<?> get(String field) {
        if(value instanceof HashMap<?, ?>) {
            return ((HashMap<String, JSON<?>>) value).get(field);
        }
        return new JSON<>(null);
    }

    @SuppressWarnings("unchecked")
    public JSON<?> get(int index) {
        if(value instanceof ArrayList<?>) {
            return ((ArrayList<JSON<?>>) value).get(index);
        }
        return new JSON<>(null);
    }

    public static JSON<?> parse(String json) throws JSONParser.MalformedJSONException {
        JSONParser parser = new JSONParser(json);
        return parser.parse();
    }

}

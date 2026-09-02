package com.bot.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSON {

    public static class MalformedJSONException extends Exception {}
    public static class JSONDatatypeException extends Exception {}

    private static Matcher tokens_of(String json) {
        return Pattern.compile("([{}\\[]:])|(\".*?\")|(\\d+)|(true|false)").matcher(json);
    }

    private static String parse_next(Matcher tokens, Class<String> _clazz, String _object) throws JSONDatatypeException {
        final String unescaped_string = tokens.group(2);
        if(unescaped_string == null) {
            throw new JSONDatatypeException();
        }

        return unescaped_string.substring(1, unescaped_string.length() - 1).translateEscapes();
    }

    private static Integer parse_next(Matcher tokens, Class<Integer> _clazz, Integer _object) throws JSONDatatypeException {
        if(tokens.group(3) == null) {
            throw new JSONDatatypeException();
        }

        return Integer.parseInt(tokens.group(3));
    }

    private static Boolean parse_next(Matcher tokens, Class<Boolean> _clazz, Boolean _object) throws JSONDatatypeException {
        if(tokens.group(4) == null) {
            throw new JSONDatatypeException();
        }

        return tokens.group(4).equals("true");
    }

    private static <T> ArrayList<T> parse_next(Matcher tokens, Class<ArrayList<?>> clazz, ArrayList<T> object) throws MalformedJSONException, JSONDatatypeException {
        if(tokens.group(1) == null || tokens.group(1).charAt(0) != '[') {
            throw new MalformedJSONException();
        }
        final Class<?> item_clazz = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

        while(tokens.find() && (tokens.group(1) == null || tokens.group(1).charAt(0) != ']')) {
            try {
                object.add((T) parse_next(tokens, item_clazz, item_clazz.getConstructor().newInstance()));
            } catch (Exception e) {
                throw new JSONDatatypeException();
            }

            if(!tokens.find() || tokens.group(1) == null || tokens.group(1).charAt(0) != ',') {
                break;
            }
        }

        if(tokens.group(1) == null || tokens.group(1).charAt(0) != ']') {
            throw new MalformedJSONException();
        }

        return object;
    }

    private static <T> T parse_next(Matcher tokens, Class<?> clazz, T object) throws MalformedJSONException, JSONDatatypeException {
        if(tokens.group(1) == null) {
            throw new MalformedJSONException();
        }

        while(tokens.find() && (tokens.group(1) == null || tokens.group(1).charAt(0) != '}')) {
            final String field_name = parse_next(tokens, String.class, null);

            if(!tokens.find() || tokens.group(1) == null || tokens.group(1).charAt(0) != ':') {
                throw new MalformedJSONException();
            }

            try {
                final Field field = clazz.getField(field_name);
                field.set(object, parse_next(tokens, field.getType(), field.getType().getConstructor().newInstance()));
            } catch(Exception e) {
                throw new JSONDatatypeException();
            }

            if(!tokens.find() || tokens.group(1) == null || tokens.group(1).charAt(0) != ',') {
                break;
            }
        }

        if(tokens.group(1) == null || tokens.group(1).charAt(0) != '}') {
            throw new MalformedJSONException();
        }

        return object;
    }

    public static <T> T parse(String json, T object) throws MalformedJSONException, JSONDatatypeException {
        final Matcher tokens = JSON.tokens_of(json);
        if(!tokens.find()) {
            throw new MalformedJSONException();
        }

        return parse_next(tokens, object.getClass(), object);
    }

}

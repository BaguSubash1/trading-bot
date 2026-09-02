package com.bot.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONParser {

    private static final String TOKENS_REGEX =
            /* language=RegExp */ "([{}\\[\\]:])|(\"(?:\\.|.)*?\")|(\\d+)|(true|false)|(\\s+)";

    private final Matcher tokens;

    // TODO: Add extra error handling logic / fields on these exceptions
    public static class MalformedJSONException extends Exception {}
    public static class JSONDatatypeException extends Exception {}

    public JSONParser(String json) {
        tokens = Pattern.compile(TOKENS_REGEX).matcher(json);
    }

    private boolean try_advance(int group) {
        do {
            if(!tokens.find()) {
                return false;
            }
        } while(tokens.group(5) != null);

        return tokens.group(group) != null;
    }

    private String advance(int group) throws MalformedJSONException {
        do {
            if(!tokens.find()) {
                throw new MalformedJSONException();
            }
        } while(tokens.group(5) != null);

        if(tokens.group(group) == null) {
            throw new MalformedJSONException();
        }

        return tokens.group(group);
    }

    private String get(int group) throws MalformedJSONException {
        if(tokens.group(group) == null) {
            throw new MalformedJSONException();
        }

        return tokens.group(group);
    }

    @SuppressWarnings("unchecked")
    private <T> T parse_next(Class<?> clazz) throws MalformedJSONException, JSONDatatypeException {
        if(clazz == String.class) {
            final String unescaped_string = get(2);
            return (T) unescaped_string.substring(1, unescaped_string.length() - 1).translateEscapes();
        }

        if(clazz == Integer.class) {
            return (T)(Integer) Integer.parseInt(get(3));
        }

        if(clazz == Boolean.class) {
            return (T)(Boolean) get(4).equals("true");
        }

        if(clazz == ArrayList.class) {
            if(get(1).charAt(0) != '[') {
                throw new MalformedJSONException();
            }
            final Class<?> item_clazz = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

            final T list;
            try {
                list = (T) clazz.getConstructor().newInstance();
            } catch(Exception e) {
                throw new JSONDatatypeException();
            }

            while(!try_advance(1) || tokens.group(1).charAt(0) != ']') {
                try {
                    ArrayList.class.getMethod("add", item_clazz).invoke(list, parse_next(item_clazz));
                } catch (Exception e) {
                    throw new JSONDatatypeException();
                }

                if(advance(1).charAt(0) != ',') {
                    break;
                }
            }

            if(get(1).charAt(0) != ']') {
                throw new MalformedJSONException();
            }

            return list;
        }

        if(get(1).charAt(0) != '{') {
            throw new MalformedJSONException();
        }

        final T object;
        try {
            object = (T) clazz.getConstructor().newInstance();
        } catch(Exception e) {
            throw new JSONDatatypeException();
        }

        while(!try_advance(1) || tokens.group(1).charAt(0) != '}') {
            final String field_name = parse_next(String.class);

            if(advance(1).charAt(0) != ':') {
                throw new MalformedJSONException();
            }

            advance(0);

            try {
                final Field field = clazz.getField(field_name);
                field.set(object, parse_next(field.getType()));
            } catch(MalformedJSONException | JSONDatatypeException e) {
                throw e;
            } catch(Exception e) {
                throw new JSONDatatypeException();
            }

            if(advance(1).charAt(0) != ',') {
                break;
            }
        }

        if(get(1).charAt(0) != '}') {
            throw new MalformedJSONException();
        }

        return object;
    }

    public <T> T parse_into(Class<?> clazz) throws MalformedJSONException, JSONDatatypeException {
        if(!tokens.find()) {
            throw new MalformedJSONException();
        }

        return parse_next(clazz);
    }

}

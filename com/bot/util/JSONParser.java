package com.bot.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONParser {

    private static final String TOKENS_REGEX =
            /* language=RegExp */ "([{}\\[\\]:,])|(\"(?:\\\\.|.)*?\")|(\\d+(?:\\.\\d+)?(?:e[+-]?\\d+)?)|(true|false)|(\\s+)|(.)";

    private final Matcher tokens;

    // TODO: Add extra error handling logic / fields on this exception
    public static class MalformedJSONException extends Exception {}

    public JSONParser(String json) {
        tokens = Pattern.compile(TOKENS_REGEX).matcher(json);
    }

    private void skip_whitespace() throws MalformedJSONException {
        do {
            if(!tokens.find()) {
                throw new MalformedJSONException();
            }
        } while(tokens.group(5) != null);
    }

    private String get(int group) throws MalformedJSONException {
        if(tokens.group(group) == null) {
            throw new MalformedJSONException();
        }

        return tokens.group(group);
    }

    private String advance(int group) throws MalformedJSONException {
        skip_whitespace();
        if(tokens.group(group) == null) {
            throw new MalformedJSONException();
        }

        return tokens.group(group);
    }

    private JSON<?> parse_next() throws MalformedJSONException {
        if(tokens.group(1) != null) {
            switch(tokens.group(1).charAt(0)) {
                case '{':
                    final HashMap<String, JSON<?>> map = new HashMap<>();

                    while(advance(0).charAt(0) != '}') {
                        String key = get(2);
                        key = key.substring(1, key.length() - 1).translateEscapes();

                        if(advance(1).charAt(0) != ':') {
                            throw new MalformedJSONException();
                        }

                        advance(0);
                        map.put(key, parse_next());

                        if(advance(1).charAt(0) != ',') {
                            break;
                        }
                    }

                    if(get(1).charAt(0) != '}') {
                        throw new MalformedJSONException();
                    }

                    return new JSON<>(map);

                case '[':
                    final ArrayList<Object> array = new ArrayList<>();

                    while(advance(0).charAt(0) != ']') {
                        array.add(parse_next());

                        if(advance(1).charAt(0) != ',') {
                            break;
                        }
                    }

                    if(get(1).charAt(0) != ']') {
                        throw new MalformedJSONException();
                    }

                    return new JSON<>(array);

                default:
                    throw new MalformedJSONException();
            }
        }

        if(tokens.group(2) != null) {
            final String string = tokens.group(2);
            return new JSON<>(string.substring(1, string.length() - 1).translateEscapes());
        }

        if(tokens.group(3) != null) {
            if(tokens.group(3).contains(".")) {
                return new JSON<>(Double.parseDouble(tokens.group(3)));
            }
            return new JSON<>(Long.parseLong(tokens.group(3)));
        }

        if(tokens.group(4) != null) {
            return new JSON<>(tokens.group(4).equals("true"));
        }

        throw new MalformedJSONException();
    }

    public JSON<?> parse() throws MalformedJSONException {
        advance(0);
        return parse_next();
    }

}

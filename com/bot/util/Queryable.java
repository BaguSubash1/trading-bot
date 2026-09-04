package com.bot.util;

import java.lang.reflect.Field;

public interface Queryable {

    Queryable NONE = new Queryable() {};

    private static void append_builder(StringBuilder builder, Queryable query) {
        for(final Field field : query.getClass().getDeclaredFields()) {
            if(field.getName().contains("$")) {
                continue;
            }

            try {
                field.setAccessible(true);

                builder
                        .append(builder.isEmpty() ? '?' : '&')
                        .append(field.getName())
                        .append('=')
                        .append(field.get(query));
            } catch (IllegalAccessException e) {
                // TODO: figure out something better here
                throw new RuntimeException(e);
            }
        }
    }

    default String uri_query_string(Queryable... queries) {
        final StringBuilder builder = new StringBuilder();

        append_builder(builder, this);
        for(Queryable query : queries) {
            append_builder(builder, query);
        }

        System.out.println(builder);
        return builder.toString();
    }

}

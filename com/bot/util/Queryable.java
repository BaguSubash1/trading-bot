package com.bot.util;

import java.lang.reflect.Field;

public interface Queryable {

    default String uri_query_string() {
        final StringBuilder builder = new StringBuilder();

        for(final Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);

                builder
                        .append(builder.isEmpty() ? '?' : '&')
                        .append(field.getName())
                        .append('=')
                        .append(field.get(this));
            } catch (IllegalAccessException e) {
                // TODO: figure out something better here
                throw new RuntimeException(e);
            }
        }

        return builder.toString();
    }

}

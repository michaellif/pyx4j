/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Sep 16, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ConverterUtils {

    public static interface ToStringConverter<E> {

        public String toString(E value);

    }

    public static class StringConverter implements ToStringConverter<String> {

        public String toString(String value) {
            return value;
        }
    }

    public static Object collectionFirstElement(Collection<?> collection) {
        if ((collection == null) || (collection.size() == 0)) {
            return null;
        } else {
            return collection.iterator().next();
        }
    }

    public static <T> List<T> collectionAsList(Collection<T> collection) {
        if (collection instanceof List<?>) {
            return (List<T>) collection;
        } else if (collection == null) {
            return null;
        } else {
            return new ArrayList<T>(collection);
        }
    }

    public static String convertStringCollection(Collection<String> entityCollection) {
        return convertCollection(entityCollection, new StringConverter());
    }

    public static <T> String convertCollection(Collection<T> collection, ToStringConverter<T> converter) {
        if (collection == null) {
            return "";
        }
        StringBuilder messagesBuffer = new StringBuilder();
        LoopCounter c = new LoopCounter(collection);
        for (T m : collection) {
            switch (c.next()) {
            case FIRST:
            case ITEM:
                messagesBuffer.append(converter.toString(m)).append(", ");
                break;
            case SINGLE:
            case LAST:
                messagesBuffer.append(converter.toString(m));
                break;
            }
        }
        return messagesBuffer.toString();
    }
}

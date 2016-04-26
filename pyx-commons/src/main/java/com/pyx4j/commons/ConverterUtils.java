/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 16, 2009
 * @author vlads
 */
package com.pyx4j.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class ConverterUtils {

    public static interface ToStringConverter<E> {

        public String toString(E value);

    }

    public static class StringConverter implements ToStringConverter<String> {

        @Override
        public String toString(String value) {
            return value;
        }
    }

    public static class ObjectConverter<T> implements ToStringConverter<T> {

        @Override
        public String toString(T value) {
            return String.valueOf(value);
        }
    }

    public static class StringViewConverter<T extends IStringView> implements ToStringConverter<T> {

        @Override
        public String toString(T value) {
            if (value != null) {
                return value.getStringView();
            } else {
                return "";
            }
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

    public static String convertStringCollection(Collection<String> stringCollection) {
        return convertCollection(stringCollection, new StringConverter());
    }

    public static String convertStringCollection(Collection<String> stringCollection, String separator) {
        return convertCollection(stringCollection, new StringConverter(), separator);
    }

    public static <T> String convertArray(T[] objectsArray, String separator) {
        return convertCollection(Arrays.asList(objectsArray), new ObjectConverter<T>(), separator);
    }

    public static <T extends IStringView> String convertCollectionStringView(Collection<T> objectsCollection, String separator) {
        return convertCollection(objectsCollection, new StringViewConverter<T>(), separator);
    }

    public static <T> String convertCollection(Collection<T> objectsCollection, String separator) {
        return convertCollection(objectsCollection, new ObjectConverter<T>(), separator);
    }

    public static <T> String convertCollection(Collection<T> collection, ToStringConverter<T> converter) {
        return convertCollection(collection, converter, ", ");
    }

    public static <T> String convertCollection(Collection<T> collection, ToStringConverter<T> converter, String separator) {
        if (collection == null) {
            return "";
        }
        StringBuilder messagesBuffer = new StringBuilder();
        LoopCounter c = new LoopCounter(collection);
        for (T m : collection) {
            switch (c.next()) {
            case FIRST:
            case ITEM:
                messagesBuffer.append(converter.toString(m)).append(separator);
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

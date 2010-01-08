/**
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
 *
 * Created on 15-Oct-06
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class EqualsHelper {

    public static boolean equals(Object value1, Object value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 instanceof String) {
            return CommonsStringUtils.equals((String) value1, value2);
        } else if (value2 instanceof String) {
            return CommonsStringUtils.equals((String) value2, value1);
        } else if (value1 != null) {
            return value1.equals(value2);
        } else {
            return false;
        }
    }

    public static <T> boolean iequals(IEqual<T> value1, T value2) {
        return ((value1 == value2) || ((value1 != null) && value1.iequals(value2)));
    }

    public static boolean equals(Collection<?> value1, Collection<?> value2) {
        if ((value1 == null) ^ (value2 == null)) {
            return true;
        } else {
            if (value1.size() != value2.size()) {
                return false;
            }
            Iterator<?> iter1 = value1.iterator();
            while (iter1.hasNext()) {
                if (!value2.contains(iter1.next())) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean equals(List<?> value1, List<?> value2) {
        if ((value1 == null) ^ (value2 == null)) {
            return true;
        } else {
            if (value1.size() != value2.size()) {
                return false;
            }
            Iterator<?> iter1 = value1.iterator();
            Iterator<?> iter2 = value2.iterator();
            for (; iter1.hasNext() && iter2.hasNext();) {
                if (!iter1.next().equals(iter2.next())) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean equals(Map<?, ?> value1, Map<?, ?> value2) {
        if ((value1 == null) ^ (value2 == null)) {
            return true;
        } else {
            if (value1.size() != value2.size()) {
                return false;
            }
            for (Object key1 : value1.keySet()) {
                Object v2 = value2.get(key1);
                if (v2 == null) {
                    return false;
                }
                if (!v2.equals(value1.get(key1))) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean equals(char[] value1, char[] value2) {
        if ((value1 == null) ^ (value2 == null)) {
            return true;
        } else {
            if (value1.length != value2.length) {
                return false;
            }
            for (int i = 0; i < value1.length; i++) {
                if (value1[i] != value2[i]) {
                    return false;
                }
            }
            return true;
        }
    }
}

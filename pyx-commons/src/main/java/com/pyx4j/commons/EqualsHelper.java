/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
        } else if (value1 instanceof List<?>) {
            return equals((List<?>) value1, (List<?>) value2);
        } else if (value1 instanceof Collection<?>) {
            return equals((Collection<?>) value1, (Collection<?>) value2);
            //        } else if (value1 instanceof IEqual<?>) {
            //            return iequals((IEqual) value1, value2);
        } else if (value1 != null) {
            return value1.equals(value2);
        } else if (value2 != null) {
            return value2.equals(value1);
        } else {
            return false;
        }
    }

    public static boolean classEquals(Object value1, Object value2) {
        if ((value1 == null) || (value2 == null)) {
            return (value1 == null) && (value2 == null);
        } else {
            return value1.getClass().equals(value2.getClass());
        }
    }

    public static <T> boolean iequals(IEqual<T> value1, T value2) {
        return ((value1 == value2) || ((value1 != null) && value1.iequals(value2)));
    }

    public static boolean equals(Collection<?> value1, Collection<?> value2) {
        if (value1 == null) {
            return (value2 == null);
        } else if (value2 == null) {
            return false;
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
        if (value1 == null) {
            return (value2 == null);
        } else if (value2 == null) {
            return false;
        } else {
            if (value1.size() != value2.size()) {
                return false;
            }
            Iterator<?> iter1 = value1.iterator();
            Iterator<?> iter2 = value2.iterator();
            for (; iter1.hasNext() && iter2.hasNext();) {
                if (!equals(iter1.next(), iter2.next())) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean equals(Map<?, ?> value1, Map<?, ?> value2) {
        if (value1 == null) {
            return (value2 == null);
        } else if (value2 == null) {
            return false;
        } else {
            if (value1.size() != value2.size()) {
                return false;
            }
            for (Map.Entry<?, ?> me : value1.entrySet()) {
                Object v2 = value2.get(me.getKey());
                if (v2 == null) {
                    return false;
                }
                if (!v2.equals(me.getValue())) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean equals(char[] value1, char[] value2) {
        if (value1 == null) {
            return (value2 == null);
        } else if (value2 == null) {
            return false;
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

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
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

public class CompareHelper {

    /**
     * Compare null Strings without exceptions
     */
    public static int compareTo(String s1, String s2) {
        // this is also null == null
        if (s1 == s2) {
            return 0;
        }
        // Null in Front
        if (s2 == null) {
            return 1;
        }
        if (s1 == null) {
            return -1;
        }
        return s1.compareTo(s2);
    }

    public static <T> int compareTo(Comparable<T> o1, T o2) {
        // this is also null == null
        if (o1 == o2) {
            return 0;
        }
        // Null in Front
        if (o2 == null) {
            return 1;
        }
        if (o1 == null) {
            return -1;
        }
        return o1.compareTo(o2);
    }

    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

}

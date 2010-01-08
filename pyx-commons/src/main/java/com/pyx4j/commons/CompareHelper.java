/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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

}

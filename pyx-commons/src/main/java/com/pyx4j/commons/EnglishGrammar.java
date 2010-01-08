/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Nov 26, 2008
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

/**
 *
 */
public class EnglishGrammar {

    public static String prefixArticleIndefinite(String word, boolean capital) {
        return articleIndefinite(word, capital) + " " + word;
    }

    public static String articleIndefinite(String word, boolean capital) {
        if ((word == null) || (word.length() == 0)) {
            if (capital) {
                return "A";
            } else {
                return "a";
            }
        } else {
            char c1 = Character.toLowerCase(word.charAt(0));
            switch (c1) {
            case 'h':
                // vowels
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'y':
                if (capital) {
                    return "An";
                } else {
                    return "an";
                }
            default:
                if (capital) {
                    return "A";
                } else {
                    return "a";
                }
            }
        }
    }
}

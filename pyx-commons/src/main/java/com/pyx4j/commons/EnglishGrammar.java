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
 * Created on Nov 26, 2008
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

public class EnglishGrammar {

    public static String classNameToEnglish(String className) {
        if (className.endsWith("DTO")) {
            return className.substring(0, className.length() - 3);
        } else {
            return className;
        }
    }

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

    public static String capitalize(String word) {
        StringBuilder b = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();
        boolean currentWordIsNumbers = false;
        boolean wordStart = true;
        for (char c : word.toCharArray()) {
            if (c == '_') {
                if (currentWord.length() > 0) {
                    b.append(currentWord.toString());
                    currentWord = new StringBuilder();
                    wordStart = true;
                }
                b.append(' ');
            } else if (Character.isDigit(c)) {
                if ((!wordStart) || (!currentWordIsNumbers)) {
                    if (currentWord.length() > 0) {
                        b.append(currentWord.toString());
                        currentWord = new StringBuilder();
                        b.append(' ');
                    }
                    wordStart = true;
                }
                currentWordIsNumbers = true;
                currentWord.append(c);
            } else if (Character.isUpperCase(c)) {
                if ((!wordStart) || (currentWordIsNumbers)) {
                    b.append(currentWord.toString());
                    currentWord = new StringBuilder();
                    b.append(' ');
                    wordStart = true;
                }
                currentWord.append(c);
            } else {
                wordStart = false;
                if (currentWord.length() == 0) {
                    c = Character.toUpperCase(c);
                }
                currentWord.append(c);
            }
        }

        if (currentWord.length() > 0) {
            b.append(currentWord.toString());
        }

        return b.toString();
    }

    public static String deCapitalize(String word) {
        if (Character.isUpperCase(word.charAt(0))) {
            StringBuilder b = new StringBuilder(word);
            b.setCharAt(0, Character.toLowerCase(word.charAt(0)));
            return b.toString();
        } else {
            return word;
        }
    }

}
